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
package io.ballerina.persist.components.syntax;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used to generate the in-memory client syntax tree.
 *
 * @since 0.3.1
 */
public class InMemoryClient implements SyntaxGenerator {

    private final Map<String, String> queryMethodStatement = new HashMap<>();
    private StringBuilder primaryKeys = new StringBuilder();
    private final Module entityModule;
    public InMemoryClient(Module entityModule) {
        this.entityModule = entityModule;
    }

    @Override
    public SyntaxTree getClientSyntax() throws BalException {
        NodeList<ImportDeclarationNode> imports = CommonSyntax.generateImport(this.entityModule);
        NodeList<ModuleMemberDeclarationNode> moduleMembers = CommonSyntax.generateConstantVariables(entityModule);
        for (Entity entity : entityModule.getEntityMap().values()) {
            moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                    String.format(BalSyntaxConstants.TABLE_PARAMETER_INIT_TEMPLATE, entity.getEntityName(),
                            getPrimaryKeys(entity, false), entity.getResourceName(), "table[]")));
        }
        Client clientObject = generateClient(entityModule);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return CommonSyntax.generateSyntaxTree(imports, moduleMembers);
    }

    @Override
    public Client generateClient(Module entityModule) throws BalException {
        Client clientObject = CommonSyntax.generateClientSignature();
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_IN_MEMORY_CLIENT), true);
        clientObject.addMember(NodeParser.parseObjectMember(""), true);
        addTableParameterInit(entityModule, clientObject);

        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }

        Function init = generateInitFunction(entityModule);
        clientObject.addMember(init.getFunctionDefinitionNode(), true);

        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            resourceList.add(generateClientResource(entity));
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });

        for (Map.Entry<String, String> entry : this.queryMethodStatement.entrySet()) {
            Function query = new Function(entry.getKey(), SyntaxKind.OBJECT_METHOD_DEFINITION);
            query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
            query.addReturns(TypeDescriptor.getSimpleNameReferenceNode("record{}[]"));
            query.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("record{}"), "value");
            query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                    BalSyntaxConstants.KEYWORD_FIELDS);
            query.addStatement(NodeParser.parseStatement(entry.getValue()));
            clientObject.addMember(query.getFunctionDefinitionNode(), true);
        }
        clientObject.addMember(generateCloseFunction().getFunctionDefinitionNode(), true);
        return clientObject;
    }

    @Override
    public Function generateInitFunction(Module entityModule) {
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

    @Override
    public ClientResource generateClientResource(Entity entity) {
        String className = "InMemoryProcessor";
        ClientResource resource  = CommonSyntax.generateClientResource(entity, className);

        Function create = generatePostFunction(entity);
        resource.addFunction(create.getFunctionDefinitionNode(), true);

        Function update = generatePutFunction(entity);
        resource.addFunction(update.getFunctionDefinitionNode(), true);

        Function delete = generateDeleteFunction(entity);
        resource.addFunction(delete.getFunctionDefinitionNode(), true);

        Function[] functions = createQueryFunction(entity);
        resource.addFunction(functions[0].getFunctionDefinitionNode(), true);
        resource.addFunction(functions[1].getFunctionDefinitionNode(), true);
        return resource;
    }

    public Function generateCloseFunction() {
        Function close = CommonSyntax.generateCloseFunction();
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_NIL));
        return close;
    }

    @Override
    public Function generatePostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = CommonSyntax.generatePostFunction(entity, primaryKeys, parameterType);
        addFunctionBodyToInMemoryPostResource(create, primaryKeys, entity, parameterType);
        return create;
    }

    @Override
    public Function generatePutFunction(Entity entity) {
        this.primaryKeys = new StringBuilder();
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
        Function update = CommonSyntax.generatePutFunction(entity, path, filterKeys);
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

    @Override
    public Function generateDeleteFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function delete = CommonSyntax.generateDeleteFunction(entity, path, filterKeys);
        IfElse hasCheck = new IfElse(NodeParser.parseExpression(String.format(BalSyntaxConstants.HAS_NOT_KEY,
                entity.getResourceName(), primaryKeys)));
        hasCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.HAS_NOT_KEY_ERROR,
                primaryKeys)));
        delete.addIfElseStatement(hasCheck.getIfElseStatementNode());
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETED_OBJECT,
                entity.getResourceName(), primaryKeys)));
        return delete;
    }

    private static void addTableParameterInit(Module entityModule, Client clientObject) {
        for (Entity entity : entityModule.getEntityMap().values()) {
            clientObject.addMember(NodeParser.parseObjectMember(String.format(
                    BalSyntaxConstants.TABLE_PARAMETER_INIT_TEMPLATE,
                    entity.getEntityName(), getPrimaryKeys(entity, false), entity.getResourceName(),
                    entity.getResourceName())), false);
        }
    }

    private static String getPrimaryKeys(Entity entity, boolean addDoubleQuotes) {
        StringBuilder keyFields = new StringBuilder();
        for (EntityField key : entity.getKeys()) {
            if (keyFields.length() != 0) {
                keyFields.append(BalSyntaxConstants.COMMA_SPACE);
            }
            if (addDoubleQuotes) {
                keyFields.append("\"").append(Utils.stripEscapeCharacter(key.getFieldName())).append("\"");
            } else {
                keyFields.append(Utils.stripEscapeCharacter(key.getFieldName()));
            }
        }
        return keyFields.toString();
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

    private static String getEntityNameConstant(String entityName) {
        StringBuilder outputString = new StringBuilder();
        String[] splitedStrings = Utils.stripEscapeCharacter(entityName).split(
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

    private static Function[] createQueryFunction(Entity entity) {
        String resourceName = entity.getResourceName();
        String nameInCamelCase = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) + resourceName.substring(1);
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
            String resourceName = Utils.stripEscapeCharacter(entity.getResourceName());
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
                                String associateEntityName = Utils.stripEscapeCharacter(relation.getAssocEntity().
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
}
