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

import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
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
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.projects.BuildOptions;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.compiler.syntax.tree.SyntaxKind.QUALIFIED_NAME_REFERENCE;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */
public class BalProjectUtils {

    private BalProjectUtils() {}

    private static final PrintStream errStream = System.err;

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
            populateEntities(moduleBuilder, balSyntaxTree);
            Module entityModule = moduleBuilder.build();
            inferRelationDetails(entityModule);
            return entityModule;
        } catch (IOException | BalException | RuntimeException e) {
            throw new BalException(e.getMessage());
        }
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

    /**
     * method to read ballerina files.
     */
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

        Entity.Builder entityBuilder;
        for (ModuleMemberDeclarationNode moduleNode : nodeList) {
            if (moduleNode.kind() != SyntaxKind.TYPE_DEFINITION) {
                continue;
            }
            TypeDefinitionNode typeDefinitionNode = (TypeDefinitionNode) moduleNode;
            entityBuilder = Entity.newBuilder(typeDefinitionNode.typeName().text().trim());

            List<EntityField> keyArray = new ArrayList<>();
            RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) moduleNode)
                    .typeDescriptor();
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
            fields.stream().filter(field -> entityMap.get(field.getFieldType()) != null)
                    .forEach(field -> {
                        String fieldType = field.getFieldType();
                        Entity assocEntity = entityMap.get(fieldType);
                        if (field.getRelation() == null) {
                            // this branch only handles one-to-one or one-to-many or many-to-many with no relation
                            // annotations
                            assocEntity.getFields().stream().filter(assocfield -> assocfield.getFieldType()
                                            .equals(entity.getEntityName()))
                                    .filter(assocfield -> assocfield.getRelation() == null).forEach(assocfield -> {
                                        // skip if the relation is already set for the entity field.
                                        if (field.getRelation() != null) {
                                            return;
                                        }
                                        // one-to-many or many-to-many with no relation annotations
                                        if (field.isArrayType() && assocfield.isArrayType()) {
                                            throw new RuntimeException("unsupported many to many relation between " +
                                                    entity.getEntityName() + " and " + assocEntity.getEntityName());
                                        }
                                        // one-to-one
                                        if (!field.isArrayType() && !assocfield.isArrayType()) {
                                            if (!field.isOptionalType() && assocfield.isOptionalType()) {
                                                field.setRelation(computeRelation(field.getFieldName(), entity,
                                                        assocEntity, true, Relation.RelationType.ONE));
                                                assocfield.setRelation(computeRelation(field.getFieldName(),
                                                        assocEntity, entity, false, Relation.RelationType.ONE));
                                            } else if (field.isOptionalType() && !assocfield.isOptionalType()) {
                                                field.setRelation(computeRelation(field.getFieldName(), entity,
                                                        assocEntity, false, Relation.RelationType.ONE));
                                                assocfield.setRelation(computeRelation(field.getFieldName(),
                                                        assocEntity, entity, true, Relation.RelationType.ONE));
                                            } else {
                                                throw new RuntimeException("unsupported ownership annotation " +
                                                        "in the relation between " + entity.getEntityName() +
                                                        " and " + assocEntity.getEntityName());
                                            }
                                        } else {
                                            if (field.isArrayType() && field.isOptionalType()) {
                                                // one-to-many relation. associated entity is the owner.
                                                // first param should be always owner entities field name
                                                field.setRelation(computeRelation(assocfield.getFieldName(), entity,
                                                        assocEntity, false, Relation.RelationType.MANY));
                                                assocfield.setRelation(computeRelation(assocfield.getFieldName(),
                                                        assocEntity, entity, true, Relation.RelationType.ONE));
                                            } else if (field.isArrayType() || field.getFieldType().equals("byte")) {
                                                field.setRelation(null);
                                            } else {
                                                // one-to-many relation. entity is the owner.
                                                // one-to-one relation. entity is the owner.
                                                // first param should be always owner entities field name
                                                field.setRelation(computeRelation(field.getFieldName(), entity,
                                                        assocEntity, true, Relation.RelationType.ONE));
                                                assocfield.setRelation(computeRelation(field.getFieldName(),
                                                        assocEntity, entity, false, Relation.RelationType.MANY));
                                            }
                                        }
                                    });
                        } else if (field.getRelation() != null && field.getRelation().isOwner()) {
                            field.getRelation().setRelationType(
                                    field.isArrayType() ? Relation.RelationType.MANY : Relation.RelationType.ONE);
                            field.getRelation().setAssocEntity(assocEntity);
                            List<Relation.Key> keyColumns = field.getRelation().getKeyColumns();
                            if (keyColumns == null || keyColumns.size() == 0) {
                                keyColumns = assocEntity.getKeys().stream()
                                        .map(key -> new Relation.Key(
                                                assocEntity.getEntityName().toLowerCase(Locale.ENGLISH)
                                                        + stripEscapeCharacter(key.getFieldName()).substring(0, 1)
                                                        .toUpperCase(Locale.ENGLISH)
                                                        + stripEscapeCharacter(key.getFieldName()).substring(1),
                                                key.getFieldName(), key.getFieldType()))
                                        .collect(Collectors.toList());
                                field.getRelation().setKeyColumns(keyColumns);
                            }
                            List<String> references = field.getRelation().getReferences();
                            if (references == null || references.size() == 0) {
                                field.getRelation().setReferences(assocEntity.getKeys().stream()
                                        .map(EntityField::getFieldName)
                                        .collect(Collectors.toList()));
                            }

                            // create bidirectional mapping for associated entity
                            Relation.Builder assocRelBuilder = Relation.newBuilder();
                            assocRelBuilder.setOwner(false);
                            assocRelBuilder.setAssocEntity(entity);

                            List<Relation.Key> assockeyColumns = assocEntity
                                    .getKeys().stream().map(key -> new Relation.Key(key.getFieldName(),
                                            assocEntity.getEntityName().toLowerCase(Locale.ENGLISH)
                                                    + stripEscapeCharacter(key.getFieldName()).substring(0, 1)
                                                    .toUpperCase(Locale.ENGLISH)
                                                    + stripEscapeCharacter(key.getFieldName()).substring(1),
                                            key.getFieldType()))
                                    .collect(Collectors.toList());
                            assocRelBuilder.setKeys(assockeyColumns);
                            assocRelBuilder.setReferences(assockeyColumns.stream().map(Relation.Key::getReference)
                                    .collect(Collectors.toList()));
                            assocEntity.getFields().stream().filter(assocfield -> assocfield.getFieldType()
                                    .equals(entity.getEntityName())).forEach(
                                    assocField -> {
                                        assocRelBuilder.setRelationType(
                                                assocField.isArrayType() ? Relation.RelationType.MANY
                                                        : Relation.RelationType.ONE);
                                        assocField.setRelation(assocRelBuilder.build());
                                    });
                        }
                    });
        }
    }

    private static Relation computeRelation(String fieldName, Entity entity, Entity assocEntity, boolean isOwner,
                                            Relation.RelationType relationType) {
        Relation.Builder relBuilder = new Relation.Builder();
        relBuilder.setAssocEntity(assocEntity);
        if (isOwner) {
            List<Relation.Key> keyColumns = assocEntity.getKeys().stream().map(key ->
                    new Relation.Key(stripEscapeCharacter(fieldName.toLowerCase(Locale.ENGLISH))
                            + stripEscapeCharacter(key.getFieldName()).substring(0, 1).toUpperCase(Locale.ENGLISH)
                            + stripEscapeCharacter(key.getFieldName()).substring(1), key.getFieldName(),
                            key.getFieldType())).collect(Collectors.toList());
            relBuilder.setOwner(true);
            relBuilder.setRelationType(relationType);
            relBuilder.setKeys(keyColumns);
            relBuilder.setReferences(assocEntity.getKeys().stream().map(EntityField::getFieldName)
                    .collect(Collectors.toList()));
        } else {
            List<Relation.Key> keyColumns = entity.getKeys().stream().map(key -> new Relation.Key(key.getFieldName(),
                            fieldName.toLowerCase(Locale.ENGLISH) +
                                    stripEscapeCharacter(key.getFieldName()).substring(0, 1).toUpperCase(Locale.ENGLISH)
                                    + stripEscapeCharacter(key.getFieldName()).substring(1),
                            key.getFieldType()))
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

    public static Path getSchemaFilePath(String sourcePath) {
        List<Path> schemaFilePaths;

        Path persistDir = Paths.get(sourcePath, PersistToolsConstants.PERSIST_DIRECTORY);
        if (!Files.isDirectory(persistDir, LinkOption.NOFOLLOW_LINKS)) {
            errStream.println("ERROR: the persist directory inside the Ballerina project does not exist. " +
                    "run `bal persist init` to initiate the project before generation");
            return null;
        }
        try (Stream<Path> stream = Files.list(persistDir)) {
            schemaFilePaths = stream.filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(".bal"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            errStream.println("ERROR: failed to list the model definition files in the persist directory. "
                    + e.getMessage());
            return null;
        }

        if (schemaFilePaths.isEmpty()) {
            errStream.println("ERROR: the persist directory does not contain any model definition file. " +
                    "run `bal persist init` to initiate the project before generation.");
            return null;
        } else if (schemaFilePaths.size() > 1) {
            errStream.println("ERROR: the persist directory allows only one model definition file, " +
                    "but contains many files.");
            return null;
        }
        
        return schemaFilePaths.get(0);
    }
}
