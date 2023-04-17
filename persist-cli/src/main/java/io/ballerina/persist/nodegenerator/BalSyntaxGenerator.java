/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.nodegenerator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.ImportPrefixNode;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.OptionalTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.RecordFieldNode;
import io.ballerina.compiler.syntax.tree.RecordTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static io.ballerina.compiler.syntax.tree.SyntaxKind.QUALIFIED_NAME_REFERENCE;
import static io.ballerina.persist.nodegenerator.SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON;

/**
 * Class containing methods to create and read ballerina files as syntax trees.
 *
 * @since 0.1.0
 */
public class BalSyntaxGenerator {

    private BalSyntaxGenerator() {
    }

    /**
     * method to read ballerina files.
     */
    public static void populateEntities(Module.Builder moduleBuilder, SyntaxTree balSyntaxTree) throws IOException,
            BalException {
        ModulePartNode rootNote = balSyntaxTree.rootNode();
        NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
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
                            fieldName.toLowerCase(Locale.ENGLISH)
                            + stripEscapeCharacter(key.getFieldName()).substring(0, 1).toUpperCase(Locale.ENGLISH)
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

    public static SyntaxTree generateDatabaseConfigSyntaxTree() {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        MinutiaeList commentMinutiaeList = createCommentMinutiaeList(String.
                format(BalSyntaxConstants.AUTO_GENERATED_COMMENT));
        ImportPrefixNode prefix = NodeFactory.createImportPrefixNode(SyntaxTokenConstants.SYNTAX_TREE_AS,
                AbstractNodeFactory.createToken(SyntaxKind.UNDERSCORE_KEYWORD));
        imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment(
                BalSyntaxConstants.KEYWORD_BALLERINAX, BalSyntaxConstants.MYSQL_DRIVER,
                commentMinutiaeList, prefix));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_PORT));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_HOST));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_USER));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_DATABASE));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_PASSWORD));

        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);

        return balTree.modifyWith(modulePartNode);
    }

    public static String generateSchemaSyntaxTree() throws FormatterException {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        imports = imports.add(NodeParser.parseImportDeclaration("import ballerina/persist as _;"));
        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);

        // output cannot be SyntaxTree as it will overlap with Toml Syntax Tree in Init
        // Command
        return Formatter.format(balTree.modifyWith(modulePartNode).toSourceCode());
    }

    private static ImportDeclarationNode getImportDeclarationNode(String moduleName) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.KEYWORD_BALLERINA);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList = AbstractNodeFactory
                .createSeparatedNodeList(moduleNameToken);

        return NodeFactory.createImportDeclarationNode(
                SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_IMPORT,
                importOrgNameNode,
                moduleNodeList,
                null,
                SYNTAX_TREE_SEMICOLON);
    }

    private static MinutiaeList createCommentMinutiaeList(String comment) {
        return NodeFactory.createMinutiaeList(
                AbstractNodeFactory.createCommentMinutiae(BalSyntaxConstants.AUTOGENERATED_FILE_COMMENT),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createCommentMinutiae(comment),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createCommentMinutiae(BalSyntaxConstants.COMMENT_SHOULD_NOT_BE_MODIFIED),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()));
    }

    private static ImportDeclarationNode getImportDeclarationNodeWithAutogeneratedComment(
            String orgName, String moduleName, MinutiaeList commentMinutiaeList, ImportPrefixNode prefix) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList = AbstractNodeFactory
                .createSeparatedNodeList(moduleNameToken);
        Token importToken = NodeFactory.createToken(SyntaxKind.IMPORT_KEYWORD,
                commentMinutiaeList, NodeFactory.createMinutiaeList(AbstractNodeFactory
                        .createWhitespaceMinutiae(BalSyntaxConstants.SPACE)));
        return NodeFactory.createImportDeclarationNode(
                importToken,
                importOrgNameNode,
                moduleNodeList,
                prefix,
                SYNTAX_TREE_SEMICOLON);
    }

    public static SyntaxTree generateTypeSyntaxTree(Module entityModule) {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        MinutiaeList commentMinutiaeList = createCommentMinutiaeList(String.format(
                BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON, entityModule.getModuleName()));

        for (String modulePrefix : entityModule.getImportModulePrefixes()) {
            if (imports.isEmpty()) {
                imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment(
                        BalSyntaxConstants.KEYWORD_BALLERINA, modulePrefix,
                        commentMinutiaeList, null));
            } else {
                imports.add(getImportDeclarationNode(modulePrefix));
            }
        }
        boolean includeAutoGeneratedComment = imports.isEmpty();
        for (Entity entity : entityModule.getEntityMap().values()) {
            boolean hasRelations = false;
            for (EntityField field : entity.getFields()) {
                if (field.getRelation() != null) {
                    hasRelations = true;
                    break;
                }
            }
            if (includeAutoGeneratedComment) {
                moduleMembers = moduleMembers.add(createEntityRecord(entity, includeAutoGeneratedComment,
                        entityModule.getModuleName()));
                includeAutoGeneratedComment = false;
            } else {
                moduleMembers = moduleMembers.add(createEntityRecord(entity, includeAutoGeneratedComment,
                        entityModule.getModuleName()));
            }
            moduleMembers = moduleMembers.add(createEntityRecordOptionalized(entity));
            if (hasRelations) {
                moduleMembers = moduleMembers.add(createEntityRecordWithRelation(entity));
            }
            moduleMembers = moduleMembers.add(createEntityTargetType(entity, hasRelations));
            moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                    String.format("public type %sInsert %s;", entity.getEntityName(),
                            entity.getEntityName())));
            moduleMembers = moduleMembers.add(createUpdateRecord(entity));
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);

        return balTree.modifyWith(modulePartNode);
    }

    private static ModuleMemberDeclarationNode createEntityRecord(Entity entity, boolean includeAutogeneratedComment,
                                                                  String moduleName) {
        StringBuilder recordFields = new StringBuilder();
        for (EntityField field : entity.getFields()) {
            if (entity.getKeys().stream().anyMatch(key -> key == field)) {
                recordFields.append(BalSyntaxConstants.KEYWORD_READONLY);
                recordFields.append(BalSyntaxConstants.SPACE);
                recordFields.append(field.getFieldType());
                if (field.isArrayType()) {
                    recordFields.append(BalSyntaxConstants.ARRAY);
                }
                recordFields.append(BalSyntaxConstants.SPACE);
                recordFields.append(field.getFieldName());
                recordFields.append(BalSyntaxConstants.SEMICOLON);
                recordFields.append(BalSyntaxConstants.SPACE);
            } else if (field.getRelation() != null) {
                if (field.getRelation().isOwner()) {
                    for (Relation.Key key : field.getRelation().getKeyColumns()) {
                        recordFields.append(key.getType());
                        recordFields.append(BalSyntaxConstants.SPACE);
                        recordFields.append(key.getField());
                        recordFields.append(BalSyntaxConstants.SEMICOLON);
                        recordFields.append(BalSyntaxConstants.SPACE);
                    }
                }
            } else {
                recordFields.append(field.isOptionalType() ? field.getFieldType() + (field.isArrayType() ?
                        BalSyntaxConstants.ARRAY : "") + BalSyntaxConstants.QUESTION_MARK : field.getFieldType() +
                        (field.isArrayType() ? BalSyntaxConstants.ARRAY : ""));
                recordFields.append(BalSyntaxConstants.SPACE);
                recordFields.append(field.getFieldName());
                recordFields.append(BalSyntaxConstants.SEMICOLON);
                recordFields.append(BalSyntaxConstants.SPACE);
            }

        }
        if (includeAutogeneratedComment) {
            String commentBuilder = BalSyntaxConstants.AUTOGENERATED_FILE_COMMENT + System.lineSeparator() +
                    System.lineSeparator() + String.format(BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON,
                    moduleName) + System.lineSeparator() + BalSyntaxConstants.COMMENT_SHOULD_NOT_BE_MODIFIED +
                    System.lineSeparator() + System.lineSeparator() + "public type %s record {| %s |};";
            return NodeParser.parseModuleMemberDeclaration(String.format(commentBuilder,
                    entity.getEntityName().trim(), recordFields));
        }
        return NodeParser.parseModuleMemberDeclaration(String.format("public type %s record {| %s |};",
                entity.getEntityName().trim(), recordFields));
    }


    private static ModuleMemberDeclarationNode createEntityRecordOptionalized(Entity entity) {
        StringBuilder recordFields = new StringBuilder();
        for (EntityField field : entity.getFields()) {
            if (field.getRelation() != null) {
                if (field.getRelation().isOwner()) {
                    for (Relation.Key key : field.getRelation().getKeyColumns()) {
                        recordFields.append(key.getType());
                        recordFields.append(BalSyntaxConstants.SPACE);
                        recordFields.append(key.getField());
                        recordFields.append(BalSyntaxConstants.QUESTION_MARK);
                        recordFields.append(BalSyntaxConstants.SEMICOLON);
                        recordFields.append(BalSyntaxConstants.SPACE);
                    }
                }
            } else {
                recordFields.append(field.isOptionalType() ? field.getFieldType() + (field.isArrayType() ?
                        BalSyntaxConstants.ARRAY : "") + BalSyntaxConstants.QUESTION_MARK : field.getFieldType() +
                        (field.isArrayType() ? BalSyntaxConstants.ARRAY : ""));
                recordFields.append(BalSyntaxConstants.SPACE);
                recordFields.append(field.getFieldName());
                recordFields.append(BalSyntaxConstants.QUESTION_MARK);
                recordFields.append(BalSyntaxConstants.SEMICOLON);
                recordFields.append(BalSyntaxConstants.SPACE);
            }

        }
        return NodeParser.parseModuleMemberDeclaration(String.format("public type %sOptionalized record {| %s |};",
                entity.getEntityName().trim(), recordFields));
    }

    private static ModuleMemberDeclarationNode createEntityRecordWithRelation(Entity entity) {
        StringBuilder recordFields = new StringBuilder();
        recordFields.append(String.format("*%sOptionalized;", entity.getEntityName()));
        for (EntityField field : entity.getFields()) {
            if (field.getRelation() != null) {
                recordFields.append(String.format("%sOptionalized", field.getFieldType()));
                if (field.isArrayType()) {
                    recordFields.append("[]");
                }
                recordFields.append(BalSyntaxConstants.SPACE);
                recordFields.append(field.getFieldName());
                recordFields.append(BalSyntaxConstants.QUESTION_MARK);
                recordFields.append(BalSyntaxConstants.SEMICOLON);
                recordFields.append(BalSyntaxConstants.SPACE);
            }

        }
        return NodeParser.parseModuleMemberDeclaration(String.format("public type %sWithRelations record {| %s |};",
                entity.getEntityName().trim(), recordFields));
    }


    private static ModuleMemberDeclarationNode createEntityTargetType(Entity entity, boolean hasRelations) {
        return NodeParser.parseModuleMemberDeclaration(String.format("public type %sTargetType " +
                        "typedesc<%s>;",
                entity.getEntityName().trim(), entity.getEntityName().trim() +
                        (hasRelations ? "WithRelations" : "Optionalized")));
    }

    private static ModuleMemberDeclarationNode createUpdateRecord(Entity entity) {
        StringBuilder recordFields = new StringBuilder();
        for (EntityField field : entity.getFields()) {
            if (entity.getKeys().stream().noneMatch(key -> key == field)) {
                if (field.getRelation() != null) {
                    if (field.getRelation().isOwner()) {
                        for (Relation.Key key : field.getRelation().getKeyColumns()) {
                            recordFields.append(key.getType());
                            recordFields.append(" ");
                            recordFields.append(key.getField());
                            recordFields.append(BalSyntaxConstants.QUESTION_MARK);
                            recordFields.append(BalSyntaxConstants.SEMICOLON);
                            recordFields.append(BalSyntaxConstants.SPACE);
                        }
                    }
                } else {
                    recordFields.append(field.isOptionalType()
                            ? field.getFieldType() + (field.isArrayType() ? BalSyntaxConstants.ARRAY : "") +
                            BalSyntaxConstants.QUESTION_MARK : field.getFieldType() + (field.isArrayType() ?
                            BalSyntaxConstants.ARRAY : ""));
                    recordFields.append(BalSyntaxConstants.SPACE);
                    recordFields.append(field.getFieldName());
                    recordFields.append(BalSyntaxConstants.QUESTION_MARK);
                    recordFields.append(BalSyntaxConstants.SEMICOLON);
                    recordFields.append(BalSyntaxConstants.SPACE);
                }
            }

        }
        return NodeParser.parseModuleMemberDeclaration(String.format("public type %sUpdate record {| %s |};",
                entity.getEntityName().trim(), recordFields));
    }

    private static String stripEscapeCharacter(String fieldName) {
        return fieldName.startsWith(BalSyntaxConstants.SINGLE_QUOTE) ? fieldName.substring(1) : fieldName;
    }
}
