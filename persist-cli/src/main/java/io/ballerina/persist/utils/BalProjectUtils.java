/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.persist.utils;

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.EnumDeclarationNode;
import io.ballerina.compiler.syntax.tree.EnumMemberNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.OptionalTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.RecordFieldNode;
import io.ballerina.compiler.syntax.tree.RecordTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.cmd.Utils;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Enum;
import io.ballerina.persist.models.EnumMember;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.models.SQLType;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;
import io.ballerina.projects.BuildOptions;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.toml.syntax.tree.AbstractNodeFactory;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.NodeList;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.ballerina.compiler.syntax.tree.SyntaxKind.QUALIFIED_NAME_REFERENCE;
import static io.ballerina.persist.PersistToolsConstants.GENERATE_CMD_FILE;
import static io.ballerina.persist.PersistToolsConstants.TARGET_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.SqlTypes.CHAR;
import static io.ballerina.persist.PersistToolsConstants.SqlTypes.VARCHAR;
import static io.ballerina.persist.PersistToolsConstants.UNSUPPORTED_TYPE_COMMENT_START;
import static io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils.isAnnotationFieldArrayType;
import static io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils.isAnnotationFieldStringType;
import static io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils.isAnnotationPresent;
import static io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils.readStringArrayValueFromAnnotation;
import static io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils.readStringValueFromAnnotation;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */
public class BalProjectUtils {

    private BalProjectUtils() {}
    private static final PrintStream errStream = System.out;

    public static Module getEntities(Path schemaFile) throws BalException {
        Path schemaFilename = schemaFile.getFileName();
        String moduleName;
        if (schemaFilename != null) {
            moduleName = schemaFilename.toString().substring(0, schemaFilename.toString().lastIndexOf('.'));
        } else {
            throw new BalException("the model definition file name is invalid.");
        }
        Module.Builder moduleBuilder = Module.newBuilder(moduleName);

        try {
            SyntaxTree balSyntaxTree = SyntaxTree.from(TextDocuments.from(Files.readString(schemaFile)));
            populateEnums(moduleBuilder, balSyntaxTree);
            populateEntities(moduleBuilder, balSyntaxTree);
            Module entityModule = moduleBuilder.build();
            inferEnumDetails(entityModule);
            inferRelationDetails(entityModule);
            return entityModule;
        } catch (IOException | BalException | RuntimeException e) {
            throw new BalException(e.getMessage());
        }
    }

    public static void updateToml(String sourcePath, String datastore, String module) throws BalException, IOException {
        String sourceContent = "[[tool.persist]]" + System.lineSeparator() +
                "options.datastore = \"" + datastore + "\"" + System.lineSeparator() +
                "module = \"" + module + "\"";
        Path generatedCmdOutPath = Paths.get(sourcePath, TARGET_DIRECTORY, GENERATE_CMD_FILE);
        Utils.writeToTargetFile(sourceContent, generatedCmdOutPath.toAbsolutePath().toString());
    }

    public static void validateSchemaFile(Path schemaPath) throws BalException {
        BuildOptions.BuildOptionsBuilder buildOptionsBuilder = BuildOptions.builder();
        buildOptionsBuilder.setOffline(true);
        SingleFileProject buildProject = SingleFileProject.load(schemaPath.toAbsolutePath(),
                buildOptionsBuilder.build());
        Package currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        if (diagnosticResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append(String.format("the model definition file(%s) has errors.", schemaPath.getFileName()));
            int validErrors = 0;
            for (Diagnostic diagnostic : diagnosticResult.errors()) {
                errorMessage.append(System.lineSeparator());
                errorMessage.append(diagnostic);
                validErrors += 1;
            }
            if (validErrors > 0) {
                throw new BalException(errorMessage.toString());
            }
        }
    }

    public static Project buildDriverFile(Path driverPath) throws BalException {
        BuildOptions.BuildOptionsBuilder buildOptionsBuilder = BuildOptions.builder();
        buildOptionsBuilder.setOffline(true);
        SingleFileProject buildProject =  SingleFileProject.load(driverPath.toAbsolutePath(),
                buildOptionsBuilder.build());
        Package currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        if (diagnosticResult.hasErrors()) {
            throw new BalException("failed to build the driver file.");
        }
        return buildProject;
    }

    public static void validateBallerinaProject(Path projectPath) throws BalException {
        Optional<Path> ballerinaToml;
        try (Stream<Path> stream = Files.list(projectPath)) {
            ballerinaToml = stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .filter(Objects::nonNull)
                    .filter(file -> BALLERINA_TOML.equals(file.toString()))
                    .findFirst();
        } catch (IOException e) {
            throw new BalException(String.format("ERROR: invalid Ballerina package directory: %s, " +
                    "%s.%n", projectPath.toAbsolutePath(), e.getMessage()));
        }
        if (ballerinaToml.isEmpty()) {
            throw new BalException(String.format("ERROR: invalid Ballerina package directory: %s, " +
                    "cannot find 'Ballerina.toml' file.%n", projectPath.toAbsolutePath()));
        }
    }

    public static NodeList<DocumentMemberDeclarationNode> addNewLine(NodeList moduleMembers, int n) {
        for (int i = 0; i < n; i++) {
            moduleMembers = moduleMembers.add(AbstractNodeFactory.createIdentifierToken(System.lineSeparator()));
        }
        return moduleMembers;
    }

    public static void populateEntities(Module.Builder moduleBuilder, SyntaxTree balSyntaxTree) throws IOException,
            BalException {
        ModulePartNode rootNote = balSyntaxTree.rootNode();
        io.ballerina.compiler.syntax.tree.NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
        rootNote.imports().stream().filter(importNode -> importNode.orgName().isPresent() && importNode.orgName().get()
                        .orgName().text().equals(BalSyntaxConstants.KEYWORD_BALLERINA) &&
                        importNode.moduleName().stream().anyMatch(node -> node.text().equals(
                                BalSyntaxConstants.KEYWORD_PERSIST)))
                .findFirst().orElseThrow(() -> new BalException(
                        "no `import ballerina/persist as _;` statement found.."));
        for (ImportDeclarationNode importDeclarationNode: rootNote.imports()) {
            if (importDeclarationNode.moduleName().get(0).text().equals(BalSyntaxConstants.CONSTRAINT) &&
                    importDeclarationNode.orgName().isPresent() && importDeclarationNode.orgName().get()
                    .orgName().text().equals(BalSyntaxConstants.KEYWORD_BALLERINA)) {
                moduleBuilder.addImportModulePrefix(BalSyntaxConstants.CONSTRAINT);
            }
        }
        for (ModuleMemberDeclarationNode moduleNode : nodeList) {
            if (moduleNode.kind() != SyntaxKind.TYPE_DEFINITION) {
                continue;
            }
            TypeDefinitionNode typeDefinitionNode = (TypeDefinitionNode) moduleNode;
            Entity.Builder entityBuilder = Entity.newBuilder(typeDefinitionNode.typeName().text().trim());
            List<EntityField> keyArray = new ArrayList<>();
            RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) moduleNode)
                    .typeDescriptor();
            Optional<MetadataNode> entityMetadataNode = typeDefinitionNode.metadata();
            entityBuilder.setTableName(typeDefinitionNode.typeName().text().trim());
            entityMetadataNode.ifPresent(value -> entityBuilder.setTableName(
                    readStringValueFromAnnotation(
                            new BalSyntaxUtils.AnnotationUtilRecord(value.annotations().stream().toList(),
                            BalSyntaxConstants.SQL_DB_NAME_ANNOTATION_NAME,
                            BalSyntaxConstants.ANNOTATION_VALUE_FIELD)
                    )
            ));
            if (recordDesc.toSourceCode().contains(UNSUPPORTED_TYPE_COMMENT_START)) {
                errStream.println("WARNING the entity '" + entityBuilder.getEntityName() + "' contains " +
                        "unsupported data types. client api for this entity will not be generated.");
                entityBuilder.setContainsUnsupportedTypes(true);
            }
            for (Node node : recordDesc.fields()) {
                EntityField.Builder fieldBuilder;
                RecordFieldNode fieldNode = (RecordFieldNode) node;
                fieldBuilder = EntityField.newBuilder(fieldNode.fieldName().text().trim());
                TypeDescriptorNode type;
                Node fieldType = fieldNode.typeName();
                if (fieldType instanceof OptionalTypeDescriptorNode) {
                    fieldBuilder.setOptionalType(true);
                    fieldType = ((OptionalTypeDescriptorNode) fieldType).typeDescriptor();
                }
                if (fieldType instanceof ArrayTypeDescriptorNode) {
                    type = ((ArrayTypeDescriptorNode) fieldType).memberTypeDesc();
                    fieldBuilder.setArrayType(true);
                } else {
                    type = (TypeDescriptorNode) fieldType;
                }
                String fType = getType(type, fieldNode.fieldName().text().trim());
                String qualifiedNamePrefix = getQualifiedModulePrefix(type);
                fieldBuilder.setType(fType);
                fieldBuilder.setOptionalType(fieldNode.typeName().kind().equals(SyntaxKind.OPTIONAL_TYPE_DESC));
                fieldBuilder.setFieldColumnName(fieldNode.fieldName().text().trim());
                fieldBuilder.setOptionalField(fieldNode.questionMarkToken().isPresent());
                Optional<MetadataNode> metadataNode = fieldNode.metadata();
                metadataNode.ifPresent(value -> {
                    //read the db generated annotation
                    List<AnnotationNode> annotations = value.annotations().stream().toList();
                    boolean dbGenerated = isAnnotationPresent(
                            annotations,
                            BalSyntaxConstants.SQL_GENERATED_ANNOTATION_NAME
                    );
                    fieldBuilder.setIsDbGenerated(dbGenerated);

                    //read the db mapping annotation
                    String fieldColumnName = readStringValueFromAnnotation(
                            new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                            BalSyntaxConstants.SQL_DB_NAME_ANNOTATION_NAME,
                            BalSyntaxConstants.ANNOTATION_VALUE_FIELD)
                    );
                    if (!fieldColumnName.isEmpty()) {
                        fieldBuilder.setFieldColumnName(fieldColumnName);
                    }
                    //read the unique index annotation
                    boolean isUniqueIndexPresent = isAnnotationPresent(
                            annotations,
                            BalSyntaxConstants.SQL_UNIQUE_INDEX_MAPPING_ANNOTATION_NAME
                    );

                    if (isUniqueIndexPresent) {
                        BalSyntaxUtils.AnnotationUtilRecord uniqueIndexAnnot =
                                new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                                        BalSyntaxConstants.SQL_UNIQUE_INDEX_MAPPING_ANNOTATION_NAME,
                                        BalSyntaxConstants.ANNOTATION_NAME_FIELD);
                        if (isAnnotationFieldArrayType(uniqueIndexAnnot)) {
                            List<String> uniqueIndexNames = readStringArrayValueFromAnnotation(uniqueIndexAnnot);
                            uniqueIndexNames.forEach(uniqueIndexName ->
                                    entityBuilder.upsertUniqueIndex(uniqueIndexName, fieldBuilder.build()));
                        } else if (isAnnotationFieldStringType(uniqueIndexAnnot)) {
                            entityBuilder.upsertUniqueIndex(readStringValueFromAnnotation(uniqueIndexAnnot),
                                    fieldBuilder.build());
                        } else {
                            entityBuilder.upsertUniqueIndex("unique_idx_" +
                                    fieldBuilder.getFieldName().toLowerCase(Locale.ENGLISH), fieldBuilder.build());
                        }
                    }
                    //read the index annotation
                    boolean isIndexPresent = isAnnotationPresent(
                            annotations,
                            BalSyntaxConstants.SQL_INDEX_MAPPING_ANNOTATION_NAME
                    );

                    if (isIndexPresent) {
                        BalSyntaxUtils.AnnotationUtilRecord indexAnnot =
                                new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                                        BalSyntaxConstants.SQL_INDEX_MAPPING_ANNOTATION_NAME,
                                        BalSyntaxConstants.ANNOTATION_NAME_FIELD);
                        if (isAnnotationFieldArrayType(indexAnnot)) {
                            List<String> indexNames = readStringArrayValueFromAnnotation(indexAnnot);
                            indexNames.forEach(indexName ->
                                    entityBuilder.upsertIndex(indexName, fieldBuilder.build()));
                        } else if (isAnnotationFieldStringType(indexAnnot)) {
                            entityBuilder.upsertIndex(readStringValueFromAnnotation(indexAnnot),
                                    fieldBuilder.build());
                        } else {
                            entityBuilder.upsertIndex("idx_" +
                                    fieldBuilder.getFieldName().toLowerCase(Locale.ENGLISH), fieldBuilder.build());
                        }
                    }
                    //read the relation annotation
                    List<String> relationRefs = readStringArrayValueFromAnnotation(
                            new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                            BalSyntaxConstants.SQL_RELATION_MAPPING_ANNOTATION_NAME,
                            BalSyntaxConstants.ANNOTATION_KEYS_FIELD)
                    );
                    if (relationRefs != null) {
                        fieldBuilder.setRelationRefs(relationRefs);
                    }
                    // read the varchar annotation
                    String varcharLength = readStringValueFromAnnotation(
                            new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                            BalSyntaxConstants.SQL_VARCHAR_MAPPING_ANNOTATION_NAME,
                            BalSyntaxConstants.ANNOTATION_LENGTH_FIELD)
                    );
                    if (!varcharLength.isEmpty()) {
                        fieldBuilder.setSqlType(
                                new SQLType(
                                        VARCHAR,
                                        null,
                                        null,
                                        0,
                                        0,
                                        Integer.parseInt(varcharLength)));
                    }
                    //read the char annotation
                    String charLength = readStringValueFromAnnotation(
                            new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                            BalSyntaxConstants.SQL_CHAR_MAPPING_ANNOTATION_NAME,
                            BalSyntaxConstants.ANNOTATION_LENGTH_FIELD)
                    );
                    if (!charLength.isEmpty()) {
                        fieldBuilder.setSqlType(
                                new SQLType(
                                        CHAR,
                                        null,
                                        null,
                                        0,
                                        0,
                                        Integer.parseInt(charLength)));
                    }
                    //read the decimal annotation
                    List<String> decimal = readStringArrayValueFromAnnotation(
                            new BalSyntaxUtils.AnnotationUtilRecord(annotations,
                            BalSyntaxConstants.SQL_DECIMAL_MAPPING_ANNOTATION_NAME,
                            BalSyntaxConstants.ANNOTATION_PRECISION_FIELD)
                    );
                    if (decimal != null && decimal.size() == 2) {
                        fieldBuilder.setSqlType(
                                new SQLType(
                                        PersistToolsConstants.SqlTypes.DECIMAL,
                                        null,
                                        null,
                                        Integer.parseInt(decimal.get(0).trim()),
                                        Integer.parseInt(decimal.get(1).trim()),
                                        0)
                        );
                    }
                    fieldBuilder.setAnnotations(annotations);
                });
                EntityField entityField = fieldBuilder.build();
                entityBuilder.addField(entityField);
                if (fieldNode.readonlyKeyword().isPresent()) {
                    keyArray.add(entityField);
                }

                if (qualifiedNamePrefix != null) {
                    moduleBuilder.addImportModulePrefix(qualifiedNamePrefix);
                }
            }
            entityBuilder.setKeys(keyArray);
            Entity entity = entityBuilder.build();
            moduleBuilder.addEntity(entity.getEntityName(), entity);
        }
    }

    public static void populateEnums(Module.Builder moduleBuilder, SyntaxTree balSyntaxTree) throws IOException,
            BalException {
        ModulePartNode rootNote = balSyntaxTree.rootNode();
        io.ballerina.compiler.syntax.tree.NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
        rootNote.imports().stream().filter(importNode -> importNode.orgName().isPresent() && importNode.orgName().get()
                        .orgName().text().equals(BalSyntaxConstants.KEYWORD_BALLERINA) &&
                        importNode.moduleName().stream().anyMatch(node -> node.text().equals(
                                BalSyntaxConstants.KEYWORD_PERSIST)))
                .findFirst().orElseThrow(() -> new BalException(
                        "no `import ballerina/persist as _;` statement found."));

        for (ModuleMemberDeclarationNode moduleNode : nodeList) {
            if (moduleNode.kind() != SyntaxKind.ENUM_DECLARATION) {
                continue;
            }
            EnumDeclarationNode enumDeclarationNode = (EnumDeclarationNode) moduleNode;
            Enum.Builder enumBuilder = Enum.newBuilder(enumDeclarationNode.identifier().text().trim());

            for (Node node: enumDeclarationNode.enumMemberList()) {
                if (!(node instanceof EnumMemberNode)) {
                    continue;
                }
                EnumMemberNode enumMemberNode = (EnumMemberNode) node;
                EnumMember enumMember;
                if (enumMemberNode.constExprNode().isPresent()) {
                    String value = enumMemberNode.constExprNode().get().toSourceCode().trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    enumMember = new EnumMember(enumMemberNode.identifier().text().trim(), value);
                } else {
                    enumMember = new EnumMember(enumMemberNode.identifier().text().trim(), null);
                }
                enumBuilder.addMember(enumMember);
            }
            Enum enumValue = enumBuilder.build();
            moduleBuilder.addEnum(enumValue.getEnumName(), enumValue);
        }
    }

    private static String getType(TypeDescriptorNode typeDesc, String fieldName) throws BalException {
        switch (typeDesc.kind()) {
            case INT_TYPE_DESC:
            case BOOLEAN_TYPE_DESC:
            case DECIMAL_TYPE_DESC:
            case FLOAT_TYPE_DESC:
            case STRING_TYPE_DESC:
            case BYTE_TYPE_DESC:
                return ((BuiltinSimpleNameReferenceNode) typeDesc).name().text();
            case QUALIFIED_NAME_REFERENCE:
                QualifiedNameReferenceNode qualifiedName = (QualifiedNameReferenceNode) typeDesc;
                String modulePrefix = qualifiedName.modulePrefix().text();
                String identifier = qualifiedName.identifier().text();
                return modulePrefix + BalSyntaxConstants.COLON + identifier;
            case SIMPLE_NAME_REFERENCE:
                return ((SimpleNameReferenceNode) typeDesc).name().text();
            case OPTIONAL_TYPE_DESC:
                return getType((TypeDescriptorNode) ((OptionalTypeDescriptorNode) typeDesc).typeDescriptor(),
                        fieldName);
            default:
                throw new BalException(String.format("unsupported data type found for the field `%s`", fieldName));
        }
    }

    private static String getQualifiedModulePrefix(TypeDescriptorNode typeDesc) {
        if (typeDesc.kind() == QUALIFIED_NAME_REFERENCE) {
            QualifiedNameReferenceNode qualifiedName = (QualifiedNameReferenceNode) typeDesc;
            return qualifiedName.modulePrefix().text();
        } else {
            return null;
        }
    }

    public static void inferRelationDetails(Module entityModule) {
        Map<String, Entity> entityMap = entityModule.getEntityMap();
        for (Entity entity : entityMap.values()) {
            List<EntityField> fields = entity.getFields();
            HashMap<String, List<EntityField>> relationFields = new HashMap<>();
            fields.stream().filter(field -> entityMap.get(field.getFieldType()) != null && field.getRelation() == null)
                    .forEach(field -> {
                        if (relationFields.containsKey(field.getFieldType())) {
                            relationFields.get(field.getFieldType()).add(field);
                        } else {
                            List<EntityField> fieldList = new ArrayList<>();
                            fieldList.add(field);
                            relationFields.put(field.getFieldType(), fieldList);
                        }
                    });
            for (List<EntityField> fieldList : relationFields.values()) {
                Entity assocEntity = entityMap.get(fieldList.get(0).getFieldType());
                List<EntityField> assocFields = assocEntity.getFields().stream()
                        .filter(assocField -> assocField.getFieldType()
                                .equals(entity.getEntityName()) && assocField.getRelation() == null).toList();
                for (int i = 0; i < fieldList.size(); i++) {
                    EntityField field = fieldList.get(i);
                    EntityField assocField = assocFields.get(i);
                    if (field.isArrayType() && assocField.isArrayType()) {
                        //both are array types. many-to-many is not supported
                        throw new RuntimeException("unsupported many to many relation between " +
                                entity.getEntityName() + " and " + assocEntity.getEntityName());
                    }
                    if (field.isArrayType() || assocField.isArrayType()) {
                        // one of them is array type -> one-to-many or many-to-many relation
                        if (field.isArrayType()) {
                            // many-to-one -> associated entity is the owner
                            field.setRelation(computeRelation(assocField.getFieldName(), entity, assocEntity, false,
                                    Relation.RelationType.MANY, assocField.getRelationRefs()));
                            assocField.setRelation(computeRelation(assocField.getFieldName(), assocEntity, entity,
                                    true, Relation.RelationType.ONE, assocField.getRelationRefs()));

                        } else {
                            // one-to-many -> entity is the owner
                            field.setRelation(computeRelation(field.getFieldName(), entity, assocEntity, true,
                                    Relation.RelationType.ONE, field.getRelationRefs()));
                            assocField.setRelation(computeRelation(field.getFieldName(), assocEntity, entity,
                                    false, Relation.RelationType.MANY, field.getRelationRefs()));
                        }
                    } else {
                        // one-to-one relation
                        if (field.isOptionalType()) {
                            // associated entity is the owner
                            field.setRelation(computeRelation(assocField.getFieldName(), entity, assocEntity, false,
                                    Relation.RelationType.ONE, assocField.getRelationRefs()));
                            assocField.setRelation(computeRelation(assocField.getFieldName(), assocEntity, entity,
                                    true, Relation.RelationType.ONE, assocField.getRelationRefs()));
                        } else {
                            // entity is the owner
                            field.setRelation(computeRelation(field.getFieldName(), entity, assocEntity, true,
                                    Relation.RelationType.ONE, field.getRelationRefs()));
                            assocField.setRelation(computeRelation(field.getFieldName(), assocEntity, entity,
                                    false, Relation.RelationType.ONE, field.getRelationRefs()));
                        }
                    }
                    if (field.getRelationRefs() != null) {
                        field.getRelationRefs().forEach(entity::removeField);
                    }
                    if (assocField.getRelationRefs() != null) {
                        assocField.getRelationRefs().forEach(assocEntity::removeField);
                    }
                }
            }
        }
    }

    public static void inferEnumDetails(Module entityModule) {
        Map<String, Enum> enumMap = entityModule.getEnumMap();

        for (Entity entity: entityModule.getEntityMap().values()) {
            for (EntityField field: entity.getFields()) {
                if (enumMap.containsKey(field.getFieldType())) {
                    field.setEnum(enumMap.get(field.getFieldType()));
                }
            }
        }
    }


    private static Relation computeRelation(String fieldName, Entity entity, Entity assocEntity, boolean isOwner,
                                            Relation.RelationType relationType, List<String> relationRefs) {
        Relation.Builder relBuilder = new Relation.Builder();
        relBuilder.setAssocEntity(assocEntity);
        if (isOwner) {
            List<Relation.Key> keyColumns = IntStream.range(0, assocEntity.getKeys().size())
                    .mapToObj(i -> {

                        EntityField key = assocEntity.getKeys().get(i);
                        if (!relationRefs.isEmpty()) {
                            String fkField = relationRefs.get(i);
                            EntityField fkEntityField = entity.getFieldByName(fkField);
                            return new Relation.Key(fkField,
                                    fkEntityField.getFieldColumnName(), key.getFieldName(), key.getFieldColumnName(),
                                    key.getFieldType());
                        }
                        String fkField = stripEscapeCharacter(fieldName.toLowerCase(Locale.ENGLISH))
                                + stripEscapeCharacter(key.getFieldName()).substring(0, 1).toUpperCase(Locale.ENGLISH)
                                + stripEscapeCharacter(key.getFieldName()).substring(1);

                        return new Relation.Key(fkField,
                                fkField, key.getFieldName(), key.getFieldColumnName(), key.getFieldType());

                    })
                    .collect(Collectors.toList());
            relBuilder.setOwner(true);
            relBuilder.setRelationType(relationType);
            relBuilder.setKeys(keyColumns);
            relBuilder.setReferences(assocEntity.getKeys().stream().map(EntityField::getFieldName)
                    .collect(Collectors.toList()));
        } else {
            List<Relation.Key> keyColumns = IntStream.range(0, entity.getKeys().size())
                    .mapToObj(i -> {

                        EntityField key = entity.getKeys().get(i);
                        String fkField = stripEscapeCharacter(fieldName.toLowerCase(Locale.ENGLISH))
                                + stripEscapeCharacter(key.getFieldName()).substring(0, 1).toUpperCase(Locale.ENGLISH)
                                + stripEscapeCharacter(key.getFieldName()).substring(1);
                        if (!relationRefs.isEmpty()) {
                            fkField = relationRefs.get(i);
                            EntityField assocField = assocEntity.getFieldByName(fkField);
                            return new Relation.Key(key.getFieldName(), key.getFieldColumnName(), fkField,
                                    assocField.getFieldColumnName(), key.getFieldType());
                        }
                        return new Relation.Key(key.getFieldName(), key.getFieldColumnName(), fkField,
                                fkField, key.getFieldType());
                    })
                    .collect(Collectors.toList());
            relBuilder.setOwner(false);
            relBuilder.setRelationType(relationType);
            relBuilder.setKeys(keyColumns);
            relBuilder.setReferences(keyColumns.stream().map(Relation.Key::getReference).collect(Collectors.toList()));
        }
        return relBuilder.build();
    }

    private static String stripEscapeCharacter(String fieldName) {
        return fieldName.startsWith(BalSyntaxConstants.SINGLE_QUOTE) ? fieldName.substring(1) : fieldName;
    }

    public static Path getSchemaFilePath(String sourcePath) throws BalException {
        List<Path> schemaFilePaths;

        Path persistDir = Paths.get(sourcePath, PersistToolsConstants.PERSIST_DIRECTORY);
        if (!Files.isDirectory(persistDir, LinkOption.NOFOLLOW_LINKS)) {
            throw new BalException("ERROR: the persist directory inside the Ballerina project does not exist. " +
                    "run `bal persist init` to initiate the project before generation");
        }
        try (Stream<Path> stream = Files.list(persistDir)) {
            schemaFilePaths = stream.filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(".bal"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new BalException("ERROR: failed to list the model definition files in the persist directory. "
                    + e.getMessage());
        }

        if (schemaFilePaths.isEmpty()) {
            throw new BalException("ERROR: the persist directory does not contain any model definition file. " +
                    "run `bal persist init` to initiate the project before generation.");
        } else if (schemaFilePaths.size() > 1) {
            throw new BalException("ERROR: the persist directory allows only one model definition file, " +
                    "but contains many files.");
        }

        return schemaFilePaths.get(0);
    }

    public static void validatePullCommandOptions(String datastore, String host, String port, String user,
                                                  String database) throws BalException {
        String nameRegex = "[A-Za-z]\\w*";
        List<String> errors = new ArrayList<>();
        if (datastore == null) {
            errors.add("The datastore type is not provided.");
        } else if (datastore.isEmpty()) {
            errors.add("The datastore type cannot be empty.");
        } else if (!(datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB) || datastore.equals(
                PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB))) {
            errors.add("Unsupported data store: '" + datastore + "'");
        }
        if (host == null) {
            errors.add("The host is not provided.");
        } else if (host.isEmpty()) {
            errors.add("The host cannot be empty.");
        }
        if (port == null) {
            errors.add("The port is not provided.");
        } else if (port.isEmpty()) {
            errors.add("The port cannot be empty.");
        } else if (!Pattern.matches("\\d+", port)) {
            errors.add("The port is invalid. The port should be a number.");
        } else if (Integer.parseInt(port) < 0 || Integer.parseInt(port) > 65535) {
            errors.add("The port is invalid. The port should be in the range of 0 to 65535.");
        }
        if (user == null) {
            errors.add("The user is not provided.");
        } else if (user.isEmpty()) {
            errors.add("The user cannot be empty.");
        }
        if (database == null) {
            errors.add("The database is not provided.");
        } else if (database.isEmpty()) {
            errors.add("The database cannot be empty.");
        } else if (!Pattern.matches(nameRegex, database)) {
            errors.add("The database name is invalid. The database name should start with a letter or underscore (_)" +
                    " and must contain only alphanumeric characters.");
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(System.lineSeparator(), errors));
        }
    }

}
