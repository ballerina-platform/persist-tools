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
import io.ballerina.compiler.syntax.tree.ArrayDimensionNode;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
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
import java.util.Collection;
import java.util.HashMap;
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

    public static SyntaxTree generateDbClientSyntaxTree(Module entityModule) throws BalException {
        NodeList<ImportDeclarationNode> imports = addImport(entityModule);
        imports = imports.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                PersistToolsConstants.SupportDataSources.MYSQL_DB, null));

        NodeList<ModuleMemberDeclarationNode> moduleMembers = addConstantVariables(entityModule);

        Client clientObject = createDBClient(entityModule);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return createSyntaxTree(imports, moduleMembers);
    }

    public static SyntaxTree generateInMemoryClientSyntaxTree(Module entityModule) throws BalException {
        NodeList<ImportDeclarationNode> imports = addImport(entityModule);

        NodeList<ModuleMemberDeclarationNode> moduleMembers = addConstantVariables(entityModule);

        for (Entity entity : entityModule.getEntityMap().values()) {
            moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                    String.format(BalSyntaxConstants.TABLE_PARAMETER_INIT_TEMPLATE, entity.getEntityName(),
                            getPrimaryKeys(entity, false), entity.getResourceName(), "table[]")));
        }

        Client clientObject = createInMemoryClient(entityModule);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return createSyntaxTree(imports, moduleMembers);
    }

    private static SyntaxTree createSyntaxTree(NodeList<ImportDeclarationNode> imports,
                                               NodeList<ModuleMemberDeclarationNode> moduleMembers) {
        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);
        return balTree.modifyWith(modulePartNode);
    }

    private static NodeList<ImportDeclarationNode> addImport(Module entityModule) {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        MinutiaeList commentMinutiaeList = createCommentMinutiaeList(String.format(
                BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON, entityModule.getModuleName()));
        imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment(BalSyntaxConstants.KEYWORD_BALLERINA,
                BalSyntaxConstants.PERSIST_MODULE, commentMinutiaeList, null));
        imports = imports.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                BalSyntaxConstants.KEYWORD_JBALLERINA_JAVA_PREFIX, null));
        return imports;
    }

    private static NodeList<ModuleMemberDeclarationNode> addConstantVariables(Module entityModule) {
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        for (Entity entity : entityModule.getEntityMap().values()) {
            moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(String.format(
                    "const %s = \"%s\";", getEntityNameConstant(entity.getEntityName()),
                    entity.getResourceName())));
        }
        return moduleMembers;
    }

    private static Client createDBClient(Module entityModule) throws BalException {
        Client clientObject = createClient();
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_DB_CLIENT), true);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_DB_CLIENT_MAP), true);
        clientObject.addMember(generateMetadataRecord(entityModule), true);

        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }
        Function init = createDbInitFunction(entityArray);
        clientObject.addMember(init.getFunctionDefinitionNode(), true);
        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            resourceList.add(createClientResource(entity));
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });
        clientObject.addMember(createClientCloseFunction().getFunctionDefinitionNode(), true);
        return clientObject;
    }

    private static Client createInMemoryClient(Module entityModule) throws BalException {
        Client clientObject = createClient();
        Map<String, String> queryMethodStatement = new HashMap<>();
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_IN_MEMORY_CLIENT), true);
        clientObject.addMember(NodeParser.parseObjectMember(""), true);
        addTableParameterInit(entityModule, clientObject);

        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }

        Function init = createInMemoryInitFunction(entityModule, queryMethodStatement);
        clientObject.addMember(init.getFunctionDefinitionNode(), true);

        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            resourceList.add(createInMemoryClientResource(entity));
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });

        for (Map.Entry<String, String> entry : queryMethodStatement.entrySet()) {
            Function query = new Function(entry.getKey(), SyntaxKind.OBJECT_METHOD_DEFINITION);
            query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
            query.addReturns(TypeDescriptor.getSimpleNameReferenceNode("record{}[]"));
            query.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("record{}"), "value");
            query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                    BalSyntaxConstants.KEYWORD_FIELDS);
            query.addStatement(NodeParser.parseStatement(entry.getValue()));
            clientObject.addMember(query.getFunctionDefinitionNode(), true);
        }
        clientObject.addMember(createInMemoryClientCloseFunction().getFunctionDefinitionNode(), true);
        return clientObject;
    }

    private static ClientResource createInMemoryClientResource(Entity entity) {
        ClientResource resource = new ClientResource();
        StringBuilder filterKeys = new StringBuilder();
        String className = "InMemoryProcessor";
        resource.addFunction(createGetFunction(entity, className), true);
        resource.addFunction(createGetByKeyFunction(entity, className), true);

        Function create = createInMemoryPostFunction(entity);
        resource.addFunction(create.getFunctionDefinitionNode(), true);

        Function update = createInMemoryPutFunction(entity, filterKeys);
        resource.addFunction(update.getFunctionDefinitionNode(), true);

        Function delete = createInMemoryDeleteFunction(entity, filterKeys);
        resource.addFunction(delete.getFunctionDefinitionNode(), true);

        Function[] functions = createInMemoryQueryFunction(entity);
        resource.addFunction(functions[0].getFunctionDefinitionNode(), true);
        resource.addFunction(functions[1].getFunctionDefinitionNode(), true);

        return resource;
    }

    private static Client createClient() {
        Client clientObject = new Client("Client", true);
        clientObject.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_CLIENT });
        clientObject.addMember(NodeFactory.createTypeReferenceNode(
                AbstractNodeFactory.createToken(SyntaxKind.ASTERISK_TOKEN),
                NodeFactory.createQualifiedNameReferenceNode(
                        NodeFactory.createIdentifierToken(
                                BalSyntaxConstants.InheritedTypeReferenceConstants.PERSIST_MODULE_NAME),
                        AbstractNodeFactory.createToken(SyntaxKind.COLON_TOKEN),
                        NodeFactory.createIdentifierToken(
                                BalSyntaxConstants.InheritedTypeReferenceConstants.ABSTRACT_PERSIST_CLIENT)),
                AbstractNodeFactory.createToken(SyntaxKind.SEMICOLON_TOKEN)), false);
        return clientObject;
    }

    private static Node generateMetadataRecord(Module entityModule) {
        StringBuilder mapBuilder = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (mapBuilder.length() != 0) {
                mapBuilder.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            StringBuilder entityMetaData = new StringBuilder();
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_ENTITY_NAME_TEMPLATE,
                    stripEscapeCharacter(entity.getEntityName())));
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_TABLE_NAME_TEMPLATE,
                    stripEscapeCharacter(entity.getEntityName())));
            StringBuilder fieldMetaData = new StringBuilder();
            StringBuilder associateFieldMEtaData = new StringBuilder();
            boolean relationsExists = false;
            for (EntityField field : entity.getFields()) {
                if (field.getRelation() != null) {
                    relationsExists = true;
                    StringBuilder foreignKeyFields = new StringBuilder();
                    if (field.getRelation().isOwner()) {
                        if (fieldMetaData.length() != 0) {
                            fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                        }
                        for (Relation.Key key : field.getRelation().getKeyColumns()) {
                            if (foreignKeyFields.length() != 0) {
                                foreignKeyFields.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                            }
                            foreignKeyFields.append(String.format(BalSyntaxConstants.METADATA_RECORD_FIELD_TEMPLATE,
                                    key.getField(), stripEscapeCharacter(key.getField())));
                        }
                    }
                    fieldMetaData.append(foreignKeyFields);
                    Entity associatedEntity = field.getRelation().getAssocEntity();
                    for (EntityField associatedEntityField : associatedEntity.getFields()) {
                        if (associatedEntityField.getRelation() == null) {
                            if (associateFieldMEtaData.length() != 0) {
                                associateFieldMEtaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                            }
                            associateFieldMEtaData.append(String.format((field.isArrayType() ? "\"%s[]" : "\"%s") +
                                            BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE,
                                    field.getFieldName(),
                                    associatedEntityField.getFieldName(), stripEscapeCharacter(field.getFieldName()),
                                    stripEscapeCharacter(associatedEntityField.getFieldName())));
                        } else {
                            if (associatedEntityField.getRelation().isOwner()) {
                                for (Relation.Key key : associatedEntityField.getRelation().getKeyColumns()) {
                                    if (associateFieldMEtaData.length() != 0) {
                                        associateFieldMEtaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                                    }
                                    associateFieldMEtaData.append(String.format((field.isArrayType() ?
                                                    "\"%s[]" : "\"%s") + BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE,
                                            field.getFieldName(), key.getField(),
                                            stripEscapeCharacter(field.getFieldName()),
                                            stripEscapeCharacter(key.getField())));
                                }
                            }
                        }
                    }
                } else {
                    if (fieldMetaData.length() != 0) {
                        fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                    }
                    fieldMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_FIELD_TEMPLATE,
                            field.getFieldName(), stripEscapeCharacter(field.getFieldName())));
                }
            }
            if (associateFieldMEtaData.length() > 1) {
                fieldMetaData.append(",");
                fieldMetaData.append(associateFieldMEtaData);
            }
            entityMetaData.append(String.format(BalSyntaxConstants.FIELD_METADATA_TEMPLATE, fieldMetaData));
            entityMetaData.append(BalSyntaxConstants.COMMA_SPACE);

            StringBuilder keyFields = new StringBuilder();
            for (EntityField key : entity.getKeys()) {
                if (keyFields.length() != 0) {
                    keyFields.append(BalSyntaxConstants.COMMA_SPACE);
                }
                keyFields.append("\"").append(stripEscapeCharacter(key.getFieldName())).append("\"");
            }
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_KEY_FIELD_TEMPLATE, keyFields));
            if (relationsExists) {
                entityMetaData.append(BalSyntaxConstants.COMMA_SPACE);
                String joinMetaData = getJoinMetaData(entity);
                entityMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_TEMPLATE, joinMetaData));
            }

            mapBuilder.append(String.format(BalSyntaxConstants.METADATA_RECORD_ELEMENT_TEMPLATE,
                    getEntityNameConstant(entity.getEntityName()), entityMetaData));
        }
        return NodeParser.parseObjectMember(String.format(BalSyntaxConstants.METADATA_RECORD_TEMPLATE, mapBuilder));
    }

    private static void addTableParameterInit(Module entityModule, Client clientObject) {
        for (Entity entity : entityModule.getEntityMap().values()) {
            clientObject.addMember(NodeParser.parseObjectMember(String.format(
                    BalSyntaxConstants.TABLE_PARAMETER_INIT_TEMPLATE,
                    entity.getEntityName(), getPrimaryKeys(entity, false), entity.getResourceName(),
                    entity.getResourceName())), false);
        }
    }

    private static String getJoinMetaData(Entity entity) {
        StringBuilder joinMetaData = new StringBuilder();
        for (EntityField entityField : entity.getFields()) {
            StringBuilder refColumns = new StringBuilder();
            StringBuilder joinColumns = new StringBuilder();
            if (entityField.getRelation() != null) {
                String relationType = "persist:ONE_TO_ONE";
                Entity associatedEntity = entityField.getRelation().getAssocEntity();
                for (EntityField associatedEntityField : associatedEntity.getFields()) {
                    if (associatedEntityField.getFieldType().equals(entity.getEntityName())) {
                        if (associatedEntityField.isArrayType() && !entityField.isArrayType()) {
                            relationType = "persist:ONE_TO_MANY";
                        } else if (!associatedEntityField.isArrayType() && entityField.isArrayType()) {
                            relationType = "persist:MANY_TO_ONE";
                        } else if (associatedEntityField.isArrayType() && entityField.isArrayType()) {
                            relationType = "persist:MANY_TO_MANY";
                        }
                    }
                }
                if (joinMetaData.length() > 0) {
                    joinMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                }
                for (Relation.Key key : entityField.getRelation().getKeyColumns()) {
                    if (joinColumns.length() > 0) {
                        joinColumns.append(",");
                    }
                    if (refColumns.length() > 0) {
                        refColumns.append(",");
                    }
                    refColumns.append(String.format(BalSyntaxConstants.COLUMN_ARRAY_ENTRY_TEMPLATE,
                            key.getReference()));
                    joinColumns.append(String.format(BalSyntaxConstants.COLUMN_ARRAY_ENTRY_TEMPLATE, key.getField()));
                }
                joinMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_FIELD_TEMPLATE,
                        entityField.getFieldName(), entityField.getFieldType(),
                        entityField.getFieldName(), entityField.getFieldType(), refColumns,
                        joinColumns, relationType));
            }
        }
        return joinMetaData.toString();
    }

    private static ClientResource createClientResource(Entity entity) {
        ClientResource resource = new ClientResource();
        String className = "MySQLProcessor";
        resource.addFunction(createGetFunction(entity, className), true);

        resource.addFunction(createGetByKeyFunction(entity, className), true);

        Function create = createPostFunction(entity);
        resource.addFunction(create.getFunctionDefinitionNode(), true);

        Function update = createPutFunction(entity);
        resource.addFunction(update.getFunctionDefinitionNode(), true);

        Function delete = createDeleteFunction(entity);
        resource.addFunction(delete.getFunctionDefinitionNode(), true);

        return resource;
    }

    private static Function createDbInitFunction(Collection<Entity> entityArray) {
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.INIT_DB_CLIENT_WITH_PARAMS));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.DB_CLIENT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.DB_CLIENT)));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.ADD_CLIENT));
        StringBuilder persistClientMap = new StringBuilder();
        for (Entity entity : entityArray) {
            if (persistClientMap.length() != 0) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            persistClientMap.append(String.format(BalSyntaxConstants.PERSIST_CLIENT_MAP_ELEMENT,
                    getEntityNameConstant(entity.getEntityName()), getEntityNameConstant(entity.getEntityName())));
        }
        init.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE,
                persistClientMap)));
        return init;
    }

    private static Function createInMemoryInitFunction(Module entityModule,
                                                       Map<String, String> queryMethodStatement) {
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(generateInMemoryMetadataRecord(entityModule,
                queryMethodStatement)));
        StringBuilder persistClientMap = new StringBuilder();
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        for (Entity entity : entityArray) {
            if (persistClientMap.length() != 0) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            persistClientMap.append(String.format(BalSyntaxConstants.PERSIST_IN_MEMORY_CLIENT_MAP_ELEMENT,
                    getEntityNameConstant(entity.getEntityName()), getEntityNameConstant(entity.getEntityName())));
        }
        init.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE,
                persistClientMap)));
        return init;
    }

    private static String generateInMemoryMetadataRecord(Module entityModule,
                                                         Map<String, String> queryMethodStatement) {
        StringBuilder mapBuilder = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (mapBuilder.length() != 0) {
                mapBuilder.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }

            StringBuilder entityMetaData = new StringBuilder();
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_KEY_FIELDS_TEMPLATE, getPrimaryKeys(entity,
                    true)));
            String resourceName = stripEscapeCharacter(entity.getResourceName());
            resourceName = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                    resourceName.substring(1).toLowerCase(Locale.ENGLISH);
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_QUERY_TEMPLATE, resourceName));
            StringBuilder associationsMethods = new StringBuilder();
            String finalResourceName = resourceName;
            boolean hasAssociationMethod = false;
            for (EntityField field : entity.getFields()) {
                if (field.getRelation() != null) {
                    Relation relation = field.getRelation();
                    if (!relation.isOwner() && relation.getRelationType().equals(Relation.RelationType.MANY)) {
                        Entity assEntity = relation.getAssocEntity();
                        for (EntityField entityField : assEntity.getFields()) {
                            if (entityField.getRelation() != null && entityField.getFieldType().
                                    equals(entity.getEntityName()) && entityField.getRelation().
                                    getRelationType().equals(Relation.RelationType.ONE)) {
                                hasAssociationMethod = true;
                                if (associationsMethods.length() != 0) {
                                    associationsMethods.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                                }
                                String associateEntityName = stripEscapeCharacter(relation.getAssocEntity().
                                        getResourceName());
                                String associateEntityNameCamelCase = associateEntityName.substring(0, 1).
                                        toUpperCase(Locale.ENGLISH) + associateEntityName.substring(1).
                                        toLowerCase(Locale.ENGLISH);
                                associationsMethods.append(String.format(
                                        BalSyntaxConstants.METADATA_ASSOCIATIONS_METHODS_TEMPLATE,
                                        "\"" + associateEntityName + "\"", finalResourceName.concat(
                                                associateEntityNameCamelCase)));
                                int referenceIndex = 0;
                                StringBuilder conditionStatement = new StringBuilder();
                                for (String reference : relation.getReferences()) {
                                    if (conditionStatement.length() > 1) {
                                        conditionStatement.append(BalSyntaxConstants.AND);
                                    }
                                    conditionStatement.append(String.format(BalSyntaxConstants.CONDITION_STATEMENT,
                                            reference, entity.getKeys().get(referenceIndex).getFieldName()));
                                    referenceIndex++;
                                }
                                queryMethodStatement.put("query" +
                                                finalResourceName.concat(associateEntityNameCamelCase),
                                        String.format(BalSyntaxConstants.RETURN_STATEMENT_FOR_RELATIONAL_ENTITY,
                                                associateEntityName, conditionStatement)
                             );
                                break;
                            }
                        }
                    }
                }
            }
            if (hasAssociationMethod) {
                entityMetaData.append(String.format(BalSyntaxConstants.METADATA_QUERY_ONE_TEMPLATE, resourceName));
                entityMetaData.append(String.format(BalSyntaxConstants.IN_MEMORY_ASSOC_METHODS_TEMPLATE,
                        associationsMethods));
            } else {
                String queryOne = String.format(BalSyntaxConstants.METADATA_QUERY_ONE_TEMPLATE, resourceName);
                queryOne = queryOne.substring(0, queryOne.length() - 1);
                entityMetaData.append(queryOne);
            }
            mapBuilder.append(String.format(BalSyntaxConstants.METADATA_RECORD_ELEMENT_TEMPLATE,
                    getEntityNameConstant(entity.getEntityName()), entityMetaData));
        }
        return String.format(BalSyntaxConstants.IN_MEMORY_METADATA_MAP_TEMPLATE, mapBuilder);
    }

    private static String getPrimaryKeys(Entity entity, boolean addDoubleQuotes) {
        StringBuilder keyFields = new StringBuilder();
        for (EntityField key : entity.getKeys()) {
            if (keyFields.length() != 0) {
                keyFields.append(BalSyntaxConstants.COMMA_SPACE);
            }
            if (addDoubleQuotes) {
                keyFields.append("\"").append(stripEscapeCharacter(key.getFieldName())).append("\"");
            } else {
                keyFields.append(stripEscapeCharacter(key.getFieldName()));
            }
        }
        return keyFields.toString();
    }

    private static Function createClientCloseFunction() {
        Function close = createCloseSignature();
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.PERSIST_CLIENT_CLOSE_STATEMENT));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.RESULT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.RESULT)));
        close.addIfElseStatement(errorCheck.getIfElseStatementNode());
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_RESULT));
        return close;
    }

    private static Function createInMemoryClientCloseFunction() {
        Function close = createCloseSignature();
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_NIL));
        return close;
    }

    private static Function createCloseSignature() {
        Function close = new Function(BalSyntaxConstants.CLOSE, SyntaxKind.OBJECT_METHOD_DEFINITION);
        close.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
        close.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        return close;
    }

    private static Function createInMemoryPostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = createPostSignature(entity, primaryKeys, parameterType);
        addFunctionBodyToInMemoryPostResource(create, primaryKeys, entity, parameterType);
        return create;
    }

    private static Function createPostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = createPostSignature(entity, primaryKeys, parameterType);
        addFunctionBodyToPostResource(create, primaryKeys, getEntityNameConstant(entity.getEntityName()),
                parameterType);
        return create;
    }

    private static Function createPostSignature(Entity entity, List<EntityField> primaryKeys, String parameterType) {
        Function create = new Function(BalSyntaxConstants.POST, SyntaxKind.RESOURCE_ACCESSOR_DEFINITION);
        NodeList<Node> resourcePaths = AbstractNodeFactory.createEmptyNodeList();
        resourcePaths = resourcePaths.add(AbstractNodeFactory.createIdentifierToken(entity.getResourceName()));
        create.addRelativeResourcePaths(resourcePaths);
        create.addRequiredParameter(
                TypeDescriptor.getArrayTypeDescriptorNode(parameterType), BalSyntaxConstants.KEYWORD_VALUE);
        create.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_ISOLATED, BalSyntaxConstants.KEYWORD_RESOURCE });
        addReturnsToPostResourceSignature(create, primaryKeys);
        return create;
    }

    private static StringBuilder addFunctionBodyToInMemoryPostResource(Function create, List<EntityField> primaryKeys,
                                                                       Entity entity, String parameterType) {
        StringBuilder forEachStmt = new StringBuilder();
        forEachStmt.append(String.format(BalSyntaxConstants.FOREACH_STMT_START, parameterType));
        StringBuilder variableArrayType = new StringBuilder();
        StringBuilder filterKeys = new StringBuilder();
        EntityField primaryKey;
        if (primaryKeys.size() == 1) {
            primaryKey = primaryKeys.get(0);
            filterKeys.append(String.format(BalSyntaxConstants.FIELD, primaryKey.getFieldName()));
            variableArrayType.append(String.format(BalSyntaxConstants.VARIABLE_TYPE, primaryKey.getFieldType()));
        } else {
            StringBuilder variableType = new StringBuilder();
            filterKeys.append(BalSyntaxConstants.OPEN_BRACKET);
            variableType.append(BalSyntaxConstants.OPEN_BRACKET);
            int iterator = 0;
            for (EntityField field : primaryKeys) {
                if (iterator > 0) {
                    filterKeys.append(BalSyntaxConstants.COMMA_SPACE);
                    variableType.append(BalSyntaxConstants.COMMA_SPACE);
                }
                filterKeys.append(String.format(BalSyntaxConstants.FIELD, field.getFieldName()));
                variableType.append(field.getFieldType());
                iterator++;
            }
            filterKeys.append(BalSyntaxConstants.CLOSE_BRACKET);
            variableType.append(BalSyntaxConstants.CLOSE_BRACKET);
            variableArrayType.append(String.format(BalSyntaxConstants.VARIABLE_TYPE, variableType));
        }
        forEachStmt.append(String.format(BalSyntaxConstants.HAS_KEY, entity.getResourceName(), filterKeys));
        forEachStmt.append(String.format(BalSyntaxConstants.HAS_KEY_ERROR, filterKeys));

        forEachStmt.append(String.format("\t" + BalSyntaxConstants.PUT_VALUE_TO_MAP, entity.getResourceName(),
                "value"));
        forEachStmt.append(String.format(BalSyntaxConstants.PUSH_VALUES, filterKeys)).append("}");
        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.CREATE_ARRAY_VAR,
                variableArrayType)));
        create.addStatement(NodeParser.parseStatement(forEachStmt.toString()));
        create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.POST_RETURN));
        return filterKeys;
    }

    private static void addReturnsToPostResourceSignature(Function create, List<EntityField> primaryKeys) {
        ArrayDimensionNode arrayDimensionNode = NodeFactory.createArrayDimensionNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACKET,
                null,
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACKET);
        NodeList<ArrayDimensionNode> dimensionList = NodeFactory.createNodeList(arrayDimensionNode);
        List<Node> typeTuple = new ArrayList<>();
        if (primaryKeys.size() > 1) {
            primaryKeys.forEach(primaryKey -> {
                if (!typeTuple.isEmpty()) {
                    typeTuple.add(NodeFactory.createToken(SyntaxKind.COMMA_TOKEN));
                }
                typeTuple.add(NodeFactory.createSimpleNameReferenceNode(
                        NodeFactory.createIdentifierToken(primaryKey.getFieldType())));
            });
            create.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                    NodeFactory.createArrayTypeDescriptorNode(NodeFactory.createTupleTypeDescriptorNode(
                            NodeFactory.createToken(SyntaxKind.OPEN_BRACKET_TOKEN),
                            NodeFactory.createSeparatedNodeList(typeTuple),
                            NodeFactory.createToken(SyntaxKind.CLOSE_BRACKET_TOKEN)), dimensionList),
                    TypeDescriptor.getQualifiedNameReferenceNode(BalSyntaxConstants.PERSIST_MODULE,
                            BalSyntaxConstants.SPECIFIC_ERROR)));
        } else {
            create.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                    TypeDescriptor.getArrayTypeDescriptorNode(primaryKeys.get(0).getFieldType()),
                    TypeDescriptor.getQualifiedNameReferenceNode(BalSyntaxConstants.PERSIST_MODULE,
                            BalSyntaxConstants.SPECIFIC_ERROR)));

        }
    }

    private static void addFunctionBodyToPostResource(Function create, List<EntityField> primaryKeys,
                                                      String tableName, String parameterType) {
        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.CREATE_SQL_RESULTS,
                tableName)));
        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_CREATED_KEY,
                parameterType)));
        StringBuilder filterKeys = new StringBuilder();
        for (int i = 0; i < primaryKeys.size(); i++) {
            filterKeys.append("inserted.").append(primaryKeys.get(i).getFieldName());
            if (i < primaryKeys.size() - 1) {
                filterKeys.append(",");
            }
        }
        if (primaryKeys.size() == 1) {
            filterKeys = new StringBuilder(BalSyntaxConstants.SELECT_WITH_SPACE + filterKeys +
                    BalSyntaxConstants.SEMICOLON);
        } else {
            filterKeys = new StringBuilder(BalSyntaxConstants.SELECT_WITH_SPACE + BalSyntaxConstants.OPEN_BRACKET +
                    filterKeys + BalSyntaxConstants.CLOSE_BRACKET + BalSyntaxConstants.SEMICOLON);
        }
        create.addStatement(NodeParser.parseStatement(filterKeys.toString()));
    }

    private static FunctionDefinitionNode createGetByKeyFunction(Entity entity, String className) {
        StringBuilder keyBuilder = new StringBuilder();
        for (EntityField keyField : entity.getKeys()) {
            if (keyBuilder.length() > 0) {
                keyBuilder.append("/");
            }
            keyBuilder.append(BalSyntaxConstants.OPEN_BRACKET);
            keyBuilder.append(keyField.getFieldType());
            keyBuilder.append(BalSyntaxConstants.SPACE);
            keyBuilder.append(keyField.getFieldName());
            keyBuilder.append(BalSyntaxConstants.CLOSE_BRACKET);
        }

        return (FunctionDefinitionNode) NodeParser.parseObjectMember(
                String.format(BalSyntaxConstants.EXTERNAL_GET_BY_KEY_METHOD_TEMPLATE,
                        entity.getResourceName(), keyBuilder, entity.getEntityName(), className));
    }

    private static FunctionDefinitionNode createGetFunction(Entity entity, String className) {
        return (FunctionDefinitionNode) NodeParser.parseObjectMember(
                String.format(BalSyntaxConstants.EXTERNAL_GET_METHOD_TEMPLATE,
                        entity.getResourceName(), entity.getEntityName(), className));
    }

    private static Function createPutFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function update = createPutSignature(entity, path, filterKeys);
        if (entity.getKeys().size() > 1) {
            update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY,
                    getEntityNameConstant(entity.getEntityName()),
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE))));
        } else {
            update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY,
                    getEntityNameConstant(entity.getEntityName()), entity.getKeys().stream().findFirst().get()
                            .getFieldName())));
        }
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RETURN_UPDATE_QUERY,
                path)));
        return update;
    }

    private static Function createInMemoryPutFunction(Entity entity, StringBuilder primaryKeys) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        EntityField primaryKey;
        List<EntityField> keys = entity.getKeys();
        if (keys.size() == 1) {
            primaryKey = keys.get(0);
            primaryKeys.append(String.format("%s", primaryKey.getFieldName()));
        } else {
            primaryKeys.append(BalSyntaxConstants.OPEN_BRACKET);
            int iterator = 0;
            for (EntityField field : keys) {
                if (iterator > 0) {
                    primaryKeys.append(BalSyntaxConstants.COMMA_SPACE);
                }
                primaryKeys.append(String.format("%s", field.getFieldName()));
                iterator++;
            }
            primaryKeys.append(BalSyntaxConstants.CLOSE_BRACKET);
        }
        Function update = createPutSignature(entity, path, filterKeys);
        IfElse hasCheck = new IfElse(NodeParser.parseExpression(String.format(BalSyntaxConstants.HAS_NOT_KEY,
                entity.getResourceName(),
                primaryKeys)));
        hasCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.HAS_NOT_KEY_ERROR,
                primaryKeys)));
            update.addIfElseStatement(hasCheck.getIfElseStatementNode());
        String entityNameInLowerCase = entity.getEntityName().toLowerCase(Locale.ENGLISH);
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.GET_UPDATE_RECORD,
                entity.getEntityName(), entityNameInLowerCase, entity.getResourceName(), primaryKeys)));
        update.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.UPDATE_RECORD_FIELD_VALUE, entityNameInLowerCase)));
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.PUT_VALUE_TO_MAP,
                entity.getResourceName(), entityNameInLowerCase)));
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_STATEMENT,
                entity.getEntityName().toLowerCase(Locale.ENGLISH))));
        return update;
    }

    private static Function createPutSignature(Entity entity, StringBuilder path, StringBuilder filterKeys) {
        Function update = new Function(BalSyntaxConstants.PUT, SyntaxKind.RESOURCE_ACCESSOR_DEFINITION);
        update.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_ISOLATED, BalSyntaxConstants.KEYWORD_RESOURCE });
        update.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(
                String.format(BalSyntaxConstants.UPDATE_RECORD, entity.getEntityName())), BalSyntaxConstants.VALUE);
        NodeList<Node> resourcePaths = AbstractNodeFactory.createEmptyNodeList();
        resourcePaths = getResourcePath(resourcePaths, entity.getKeys(), filterKeys, path, entity.getResourceName());
        update.addRelativeResourcePaths(resourcePaths);
        update.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getQualifiedNameReferenceNode(BalSyntaxConstants.PERSIST_MODULE,
                        BalSyntaxConstants.SPECIFIC_ERROR)));
        return update;
    }

    private static Function createDeleteFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function delete = createDeleteSignature(entity, path, filterKeys);
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.GET_OBJECT_QUERY,
                entity.getEntityName(), path)));
        if (entity.getKeys().size() > 1) {
            delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY,
                    getEntityNameConstant(entity.getEntityName()),
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE))));
        } else {
            delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY,
                    getEntityNameConstant(entity.getEntityName()), entity.getKeys().stream().findFirst().get()
                            .getFieldName())));
        }
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_DELETED_OBJECT));
        return delete;
    }

    private static Function createInMemoryDeleteFunction(Entity entity, StringBuilder primaryKeys) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function delete = createDeleteSignature(entity, path, filterKeys);
        IfElse hasCheck = new IfElse(NodeParser.parseExpression(String.format(BalSyntaxConstants.HAS_NOT_KEY,
                entity.getResourceName(), primaryKeys)));
        hasCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.HAS_NOT_KEY_ERROR,
                primaryKeys)));
        delete.addIfElseStatement(hasCheck.getIfElseStatementNode());
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETED_OBJECT,
                entity.getResourceName(), primaryKeys)));
        return delete;
    }

    private static Function createDeleteSignature(Entity entity, StringBuilder path, StringBuilder filterKeys) {
        Function delete = new Function(BalSyntaxConstants.DELETE, SyntaxKind.RESOURCE_ACCESSOR_DEFINITION);
        delete.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_ISOLATED, BalSyntaxConstants.KEYWORD_RESOURCE });
        NodeList<Node> resourcePaths = AbstractNodeFactory.createEmptyNodeList();
        resourcePaths = getResourcePath(resourcePaths, entity.getKeys(), filterKeys, path, entity.getResourceName());
        delete.addRelativeResourcePaths(resourcePaths);
        delete.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getQualifiedNameReferenceNode(BalSyntaxConstants.PERSIST_MODULE,
                        BalSyntaxConstants.SPECIFIC_ERROR)));
        return delete;
    }

    private static Function[] createInMemoryQueryFunction(Entity entity) {
        String nameInCamelCase = getCamelCase(entity.getResourceName());
        String resourceName = entity.getResourceName();
        StringBuilder queryBuilder = new StringBuilder(String.format(BalSyntaxConstants.QUERY_STATEMENT, resourceName));
        Function query = new Function(String.format(BalSyntaxConstants.QUERY, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
        query.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_RETURN));
        query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                BalSyntaxConstants.KEYWORD_FIELDS);

        StringBuilder queryOneBuilder = new StringBuilder(String.format(BalSyntaxConstants.QUERY_ONE_FROM_STATEMENT,
                resourceName));
        queryOneBuilder.append(String.format(BalSyntaxConstants.QUERY_ONE_WHERE_CLAUSE,
                getEntityNameConstant(entity.getEntityName())));
        Function queryOne = new Function(String.format(BalSyntaxConstants.QUERY_ONE, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        queryOne.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
        queryOne.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_ONE_RETURN));
        queryOne.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("anydata"),
                BalSyntaxConstants.KEYWORD_KEY);

        createQuery(entity, queryBuilder, queryOneBuilder);
        query.addStatement(NodeParser.parseStatement(queryBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(queryOneBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(BalSyntaxConstants.QUERY_ONE_RETURN_STATEMENT));
        return new Function[]{query, queryOne};
    }

    private static StringBuilder[] createQuery(Entity entity, StringBuilder queryBuilder,
                                               StringBuilder queryOneBuilder) {
        StringBuilder relationalRecordFields = new StringBuilder();
        for (EntityField fields : entity.getFields()) {
            if (fields.getRelation() != null) {
                Relation relation = fields.getRelation();
                if (relation.isOwner()) {
                    Entity assocEntity = relation.getAssocEntity();
                    String assocEntityName = assocEntity.getEntityName();
                    queryBuilder.append(String.format(BalSyntaxConstants.QUERY_OUTER_JOIN, assocEntityName.
                                    toLowerCase(Locale.ENGLISH), assocEntity.getResourceName()));
                    queryBuilder.append(BalSyntaxConstants.ON);
                    queryOneBuilder.append(String.format(BalSyntaxConstants.QUERY_OUTER_JOIN, assocEntityName.
                            toLowerCase(Locale.ENGLISH), assocEntity.getResourceName()));
                    queryOneBuilder.append(BalSyntaxConstants.ON);
                    relationalRecordFields.append(String.format(BalSyntaxConstants.VARIABLE,
                            assocEntityName.toLowerCase(Locale.ENGLISH),
                            assocEntityName.toLowerCase(Locale.ENGLISH)));
                    int i = 0;
                    StringBuilder arrayFields = new StringBuilder();
                    StringBuilder arrayValues = new StringBuilder();
                    arrayFields.append(BalSyntaxConstants.OPEN_BRACKET);
                    arrayValues.append(BalSyntaxConstants.OPEN_BRACKET);
                    for (String references: relation.getReferences()) {
                        if (i > 0) {
                            arrayFields.append(BalSyntaxConstants.COMMA);
                            arrayValues.append(BalSyntaxConstants.COMMA);
                        }
                        arrayFields.append(String.format(BalSyntaxConstants.OBJECT_FIELD,
                                relation.getKeyColumns().get(i).getField()));
                        arrayValues.append(String.format(BalSyntaxConstants.VALUES,
                                assocEntityName.toLowerCase(Locale.ENGLISH), references));
                        i++;
                    }
                    queryBuilder.append(arrayFields.append(BalSyntaxConstants.CLOSE_BRACKET));
                    queryBuilder.append(BalSyntaxConstants.EQUALS);
                    queryBuilder.append(arrayValues.append(BalSyntaxConstants.CLOSE_BRACKET)).
                            append(System.lineSeparator());
                    queryOneBuilder.append(arrayFields);
                    queryOneBuilder.append(BalSyntaxConstants.EQUALS);
                    queryOneBuilder.append(arrayValues).append(System.lineSeparator());
                }
            }
        }
        if (relationalRecordFields.length() > 0) {
            queryBuilder.append(String.format(BalSyntaxConstants.SELECT_QUERY, "," + System.lineSeparator() +
                    relationalRecordFields.substring(0, relationalRecordFields.length() - 1)));
            queryOneBuilder.append(String.format(BalSyntaxConstants.DO_QUERY, "," + System.lineSeparator() +
                    relationalRecordFields.substring(0, relationalRecordFields.length() - 1)));
        } else {
            queryBuilder.append(String.format(BalSyntaxConstants.SELECT_QUERY, ""));
            queryOneBuilder.append(String.format(BalSyntaxConstants.DO_QUERY, ""));
        }

        return new StringBuilder[]{queryBuilder, queryOneBuilder};
    }

    private static NodeList<Node> getResourcePath(NodeList<Node> resourcePaths, List<EntityField> keys,
                                                  StringBuilder filterKeys, StringBuilder path, String tableName) {
        resourcePaths = resourcePaths.add(AbstractNodeFactory.createIdentifierToken(tableName));
        for (EntityField entry : keys) {
            resourcePaths = resourcePaths.add(AbstractNodeFactory.createToken(SyntaxKind.SLASH_TOKEN));
            resourcePaths = resourcePaths.add(NodeFactory.createResourcePathParameterNode(
                    SyntaxKind.RESOURCE_PATH_SEGMENT_PARAM,
                    AbstractNodeFactory.createToken(SyntaxKind.OPEN_BRACKET_TOKEN),
                    AbstractNodeFactory.createEmptyNodeList(),
                    NodeFactory.createBuiltinSimpleNameReferenceNode(SyntaxKind.STRING_TYPE_DESC,
                            AbstractNodeFactory.createIdentifierToken(entry.getFieldType() +
                                    BalSyntaxConstants.SPACE)),
                    null,
                    AbstractNodeFactory.createIdentifierToken(entry.getFieldName()),
                    AbstractNodeFactory.createToken(SyntaxKind.CLOSE_BRACKET_TOKEN)));
            filterKeys.append(BalSyntaxConstants.DOUBLE_QUOTE).append(stripEscapeCharacter(entry.getFieldName()))
                    .append(BalSyntaxConstants.DOUBLE_QUOTE).append(BalSyntaxConstants.COLON).
                    append(entry.getFieldName()).append(BalSyntaxConstants.COMMA_SPACE);
            path.append(BalSyntaxConstants.BACK_SLASH).append(BalSyntaxConstants.OPEN_BRACKET).
                    append(entry.getFieldName()).append(BalSyntaxConstants.CLOSE_BRACKET);
        }
        return resourcePaths;
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

    private static ImportDeclarationNode getImportDeclarationNode(String orgName, String moduleName,
                                                                  ImportPrefixNode prefix) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
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
                prefix,
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
                imports.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA, modulePrefix, null));
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
            StringBuilder commentBuilder = new StringBuilder();
            commentBuilder.append(BalSyntaxConstants.AUTOGENERATED_FILE_COMMENT);
            commentBuilder.append("\n\n");
            commentBuilder.append(String.format(BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON, moduleName));
            commentBuilder.append("\n");
            commentBuilder.append(BalSyntaxConstants.COMMENT_SHOULD_NOT_BE_MODIFIED);
            commentBuilder.append("\n\n");
            commentBuilder.append("public type %s record {| %s |};");
            return NodeParser.parseModuleMemberDeclaration(String.format(commentBuilder.toString(),
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

    private static String getEntityNameConstant(String entityName) {
        StringBuilder outputString = new StringBuilder();
        String[] splitedStrings = stripEscapeCharacter(entityName).split(
                BalSyntaxConstants.REGEX_FOR_SPLIT_BY_CAPITOL_LETTER);
        for (String splitedString : splitedStrings) {
            if (outputString.length() != 0) {
                outputString.append(BalSyntaxConstants.UNDERSCORE);
            }
            outputString.append(splitedString.toUpperCase(Locale.ENGLISH));
        }
        if (entityName.startsWith(BalSyntaxConstants.SINGLE_QUOTE)) {
            return BalSyntaxConstants.SINGLE_QUOTE + outputString;
        }
        return outputString.toString();
    }

    private static String stripEscapeCharacter(String fieldName) {
        return fieldName.startsWith(BalSyntaxConstants.SINGLE_QUOTE) ? fieldName.substring(1) : fieldName;
    }

    private static String getCamelCase(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
    }
}
