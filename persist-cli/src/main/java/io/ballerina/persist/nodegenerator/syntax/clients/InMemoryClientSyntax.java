/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
package io.ballerina.persist.nodegenerator.syntax.clients;

import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.QueryMethod;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.IN_MEMORY_TABLE;

/**
 * This class is used to generate the in-memory client syntax tree.
 *
 * @since 0.3.1
 */
public class InMemoryClientSyntax implements ClientSyntax {

    public static final String ENTITY_TYPE = "table";
    public static final String IN_MEMORY_PROCESSOR = "InMemoryProcessor";
    public final List<QueryMethod> queryMethodList = new ArrayList<>();
    private StringBuilder primaryKeysTuple = new StringBuilder();
    private StringBuilder primaryKeysRecord = new StringBuilder();
    private final Module entityModule;

    public InMemoryClientSyntax(Module entityModule) {
        this.entityModule = entityModule;
    }

    @Override
    public NodeList<ImportDeclarationNode> getImports() {
        NodeList<ImportDeclarationNode> imports = BalSyntaxUtils.generateImport(this.entityModule);
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                BalSyntaxConstants.PERSIST_MODULE + "." + BalSyntaxConstants.PERSIST_IN_MEMORY,
                null));
        return imports;
    }

    @Override
    public NodeList<ModuleMemberDeclarationNode> getConstantVariables() {
        return BalSyntaxUtils.generateConstantVariables(entityModule);
    }

    @Override
    public Client getClientObject(Module entityModule, String clientName) {
        Client clientObject = BalSyntaxUtils.generateClientSignature(clientName, true, IN_MEMORY_TABLE);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_IN_MEMORY_CLIENT_MAP), true);
        clientObject.addMember(NodeParser.parseObjectMember(""), true);
        return clientObject;
    }

    @Override
    public FunctionDefinitionNode getInitFunction(Module entityModule) {
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(generateInMemoryMetadataRecord(entityModule,
                queryMethodList)));

        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        StringBuilder persistClientMap = new StringBuilder();
        for (Entity entity : entityArray) {
            if (persistClientMap.length() != 0) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }

            String inMemoryClientMapElement = String.format(BalSyntaxConstants.PERSIST_IN_MEMORY_CLIENT_MAP_ELEMENT,
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()),
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()));
            persistClientMap.append(inMemoryClientMapElement);
        }
        init.addStatement(NodeParser.parseStatement(String.format(
                BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE, persistClientMap)));

        return init.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getGetFunction(Entity entity) {
        FunctionDefinitionNode functionNode = BalSyntaxUtils.generateGetFunction(entity, IN_MEMORY_PROCESSOR,
                BalSyntaxConstants.PERSIST_IN_MEMORY);

        String doc = BalSyntaxUtils.createGetResourceDocumentation(entity, ENTITY_TYPE);
        return BalSyntaxUtils.addDocumentationToFunction(functionNode, doc);
    }

    @Override
    public FunctionDefinitionNode getGetByKeyFunction(Entity entity) {
        FunctionDefinitionNode functionNode = BalSyntaxUtils.generateGetByKeyFunction(entity, IN_MEMORY_PROCESSOR,
                BalSyntaxConstants.PERSIST_IN_MEMORY);

        String doc = BalSyntaxUtils.createGetByKeyResourceDocumentation(entity, ENTITY_TYPE);
        return BalSyntaxUtils.addDocumentationToFunction(functionNode, doc);
    }

    public FunctionDefinitionNode getCloseFunction() {
        Function close = BalSyntaxUtils.generateCloseFunction();
        String doc = BalSyntaxUtils.createCloseMethodDocumentation();
        close.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_NIL));
        return close.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getPostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = BalSyntaxUtils.generatePostFunction(entity, primaryKeys, parameterType);
        String doc = BalSyntaxUtils.createPostResourceDocumentation(entity, ENTITY_TYPE);
        create.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        addFunctionBodyToInMemoryPostResource(create, primaryKeys, entity, parameterType);
        return create.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getPutFunction(Entity entity) {
        this.primaryKeysTuple = new StringBuilder();
        this.primaryKeysRecord = new StringBuilder();
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getClientResourceName());
        EntityField primaryKey;
        List<EntityField> keys = entity.getKeys();
        if (keys.size() == 1) {
            primaryKey = keys.get(0);
            primaryKeysTuple.append(String.format("%s", primaryKey.getFieldName()));
            primaryKeysRecord.append(String.format("%s", primaryKey.getFieldName()));
        } else {
            primaryKeysTuple.append(BalSyntaxConstants.OPEN_BRACKET);
            primaryKeysRecord.append(BalSyntaxConstants.OPEN_BRACE);
            int iterator = 0;
            for (EntityField field : keys) {
                if (iterator > 0) {
                    primaryKeysTuple.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                    primaryKeysRecord.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                }
                primaryKeysTuple.append(String.format("%s", field.getFieldName()));
                primaryKeysRecord.append(String.format("%s: %s", field.getFieldName(), field.getFieldName()));
                iterator++;
            }
            primaryKeysTuple.append(BalSyntaxConstants.CLOSE_BRACKET);
            primaryKeysRecord.append(BalSyntaxConstants.CLOSE_BRACE);
        }
        Function update = BalSyntaxUtils.generatePutFunction(entity, path, filterKeys);
        String doc = BalSyntaxUtils.createPutResourceDocumentation(entity, ENTITY_TYPE);
        update.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.LOCK));
        update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.OPEN_BRACE));
        IfElse hasCheck = new IfElse(NodeParser.parseExpression(String.format(BalSyntaxConstants.HAS_NOT_KEY,
                entity.getClientResourceName(),
                primaryKeysTuple)));
        hasCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.HAS_NOT_KEY_ERROR,
                entity.getEntityName(), primaryKeysRecord)));
        update.addIfElseStatement(hasCheck.getIfElseStatementNode());
        String entityNameInLowerCase = entity.getEntityName().toLowerCase(Locale.ENGLISH);
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.GET_UPDATE_RECORD,
                entity.getEntityName(), entityNameInLowerCase, entity.getClientResourceName(), primaryKeysTuple)));
        update.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.UPDATE_RECORD_FIELD_VALUE, entityNameInLowerCase)));
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.PUT_VALUE_TO_MAP,
                entity.getClientResourceName(), entityNameInLowerCase)));
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_STATEMENT,
                entity.getEntityName().toLowerCase(Locale.ENGLISH))));
        update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_BRACE));
        return update.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getDeleteFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getClientResourceName());
        Function delete = BalSyntaxUtils.generateDeleteFunction(entity, path, filterKeys);
        String doc = BalSyntaxUtils.createDeleteResourceDocumentation(entity, ENTITY_TYPE);
        delete.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.LOCK));
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.OPEN_BRACE));
        IfElse hasCheck = new IfElse(NodeParser.parseExpression(String.format(BalSyntaxConstants.HAS_NOT_KEY,
                entity.getClientResourceName(), primaryKeysTuple)));
        hasCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.HAS_NOT_KEY_ERROR,
                entity.getEntityName(), primaryKeysRecord)));
        delete.addIfElseStatement(hasCheck.getIfElseStatementNode());
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETED_OBJECT,
                entity.getClientResourceName(), primaryKeysTuple)));
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_BRACE));
        return delete.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getQueryNativeSQLFunction() {
        throw new UnsupportedOperationException("Query native SQL is not supported for in-memory database");
    }

    @Override
    public FunctionDefinitionNode getExecuteNativeSQLFunction() {
        throw new UnsupportedOperationException("Execute native SQL is not supported for in-memory database");
    }

    private static void addFunctionBodyToInMemoryPostResource(Function create, List<EntityField> primaryKeys,
                                                              Entity entity, String parameterType) {
        StringBuilder forEachStmt = new StringBuilder();
        forEachStmt.append(String.format(BalSyntaxConstants.FOREACH_STMT_START, parameterType));
        forEachStmt.append(BalSyntaxConstants.LOCK);
        forEachStmt.append(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder variableArrayType = new StringBuilder();
        StringBuilder filterKeys = new StringBuilder();
        StringBuilder filterKeysRecord = new StringBuilder();
        EntityField primaryKey;
        if (primaryKeys.size() == 1) {
            primaryKey = primaryKeys.get(0);
            filterKeys.append(String.format(BalSyntaxConstants.FIELD, primaryKey.getFieldName()));
            filterKeysRecord.append(String.format(BalSyntaxConstants.FIELD, primaryKey.getFieldName()));
            variableArrayType.append(String.format(BalSyntaxConstants.VARIABLE_TYPE, primaryKey.getFieldType()));
        } else {
            StringBuilder variableType = new StringBuilder();
            filterKeys.append(BalSyntaxConstants.OPEN_BRACKET);
            filterKeysRecord.append(BalSyntaxConstants.OPEN_BRACE);
            variableType.append(BalSyntaxConstants.OPEN_BRACKET);
            int iterator = 0;
            for (EntityField field : primaryKeys) {
                if (iterator > 0) {
                    filterKeys.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                    variableType.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                    filterKeysRecord.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                }
                filterKeys.append(String.format(BalSyntaxConstants.FIELD, field.getFieldName()));
                filterKeysRecord.append(String.format(BalSyntaxConstants.FIELD_WITH_KEY,
                        field.getFieldName(), field.getFieldName()));
                variableType.append(field.getFieldType());
                iterator++;
            }
            filterKeys.append(BalSyntaxConstants.CLOSE_BRACKET);
            variableType.append(BalSyntaxConstants.CLOSE_BRACKET);
            filterKeysRecord.append(BalSyntaxConstants.CLOSE_BRACE);
            variableArrayType.append(String.format(BalSyntaxConstants.VARIABLE_TYPE, variableType));
        }
        forEachStmt.append(String.format(BalSyntaxConstants.HAS_KEY, entity.getClientResourceName(), filterKeys));
        forEachStmt.append(String.format(BalSyntaxConstants.HAS_KEY_ERROR, entity.getEntityName(), filterKeysRecord));

        forEachStmt.append(String.format("\t" + BalSyntaxConstants.PUT_VALUE_TO_MAP, entity.getClientResourceName(),
                "value.clone()"));
        forEachStmt.append(BalSyntaxConstants.CLOSE_BRACE);
        forEachStmt.append(String.format(BalSyntaxConstants.PUSH_VALUES, filterKeys)).append("}");
        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.CREATE_ARRAY_VAR,
                variableArrayType)));
        create.addStatement(NodeParser.parseStatement(forEachStmt.toString()));
        create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.POST_RETURN));
    }

    private static String generateInMemoryMetadataRecord(Module entityModule, List<QueryMethod> queryMethodList) {
        StringBuilder mapBuilder = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (mapBuilder.length() != 0) {
                mapBuilder.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }

            StringBuilder entityMetaData = new StringBuilder();
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_KEY_FIELDS_TEMPLATE,
                    getPrimaryKeys(entity, true)));
            String resourceName = BalSyntaxUtils.stripEscapeCharacter(entity.getClientResourceName());
            resourceName = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                    resourceName.substring(1).toLowerCase(Locale.ENGLISH);
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_QUERY_TEMPLATE, resourceName));
            StringBuilder associationsMethods = new StringBuilder();
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

                                String associateEntityName = BalSyntaxUtils.stripEscapeCharacter(relation.
                                        getAssocEntity().getClientResourceName());

                                String associateFieldName = BalSyntaxUtils.stripEscapeCharacter(field.getFieldName());
                                String associateFieldNameCamelCase = associateFieldName.substring(0, 1).
                                        toUpperCase(Locale.ENGLISH) + associateFieldName.substring(1).
                                        toLowerCase(Locale.ENGLISH);
                                associationsMethods.append(String.format(
                                        BalSyntaxConstants.METADATA_ASSOCIATIONS_METHODS_TEMPLATE,
                                        "\"" + associateFieldName + "\"", entity.getEntityName().concat(
                                                associateFieldNameCamelCase)));
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
                                QueryMethod queryMethod = new QueryMethod(
                                        "query" + entity.getEntityName().concat(associateFieldNameCamelCase),
                                        relation.getAssocEntity().getEntityName(),
                                        String.format(BalSyntaxConstants.RETURN_STATEMENT_FOR_RELATIONAL_ENTITY,
                                                associateEntityName, conditionStatement)
                                );
                                queryMethodList.add(queryMethod);
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
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()), entityMetaData));
        }
        return String.format(BalSyntaxConstants.IN_MEMORY_METADATA_MAP_TEMPLATE, mapBuilder);
    }

    public static String getPrimaryKeys(Entity entity, boolean addDoubleQuotes) {
        StringBuilder keyFields = new StringBuilder();
        for (EntityField key : entity.getKeys()) {
            if (keyFields.length() != 0) {
                keyFields.append(BalSyntaxConstants.COMMA_WITH_SPACE);
            }
            if (addDoubleQuotes) {
                keyFields.append("\"").append(BalSyntaxUtils.stripEscapeCharacter(key.getFieldName())).append("\"");
            } else {
                keyFields.append(BalSyntaxUtils.stripEscapeCharacter(key.getFieldName()));
            }
        }
        return keyFields.toString();
    }
}
