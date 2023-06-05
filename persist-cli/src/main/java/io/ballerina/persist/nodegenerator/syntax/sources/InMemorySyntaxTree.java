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
package io.ballerina.persist.nodegenerator.syntax.sources;

import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.persist.BalException;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.QueryMethod;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.clients.InMemoryClientSyntax;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * This class is used to generate the syntax tree for in-memory.
 *
 * @since 0.3.1
 */
public class InMemorySyntaxTree implements SyntaxTree {

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getClientSyntax(Module entityModule) throws BalException {
        InMemoryClientSyntax dbClientSyntax = new InMemoryClientSyntax(entityModule);
        NodeList<ImportDeclarationNode> imports = dbClientSyntax.getImports();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = dbClientSyntax.getConstantVariables();
        for (Entity entity : entityModule.getEntityMap().values()) {
            moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                    String.format(BalSyntaxConstants.TABLE_PARAMETER_INIT_TEMPLATE, entity.getEntityName(),
                            getPrimaryKeys(entity, false), entity.getResourceName())));
        }
        Client clientObject = dbClientSyntax.getClientObject(entityModule);
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }

        clientObject.addMember(dbClientSyntax.getInitFunction(entityModule), true);

        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            ClientResource resource = new ClientResource();
            resource.addFunction(dbClientSyntax.getGetFunction(entity), true);
            resource.addFunction(dbClientSyntax.getGetByKeyFunction(entity), true);
            resource.addFunction(dbClientSyntax.getPostFunction(entity), true);
            resource.addFunction(dbClientSyntax.getPutFunction(entity), true);
            resource.addFunction(dbClientSyntax.getDeleteFunction(entity), true);
            resourceList.add(resource);
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });

        clientObject.addMember(dbClientSyntax.getCloseFunction(), true);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());

        List<ModuleMemberDeclarationNode> functionsList = new ArrayList<>();
        for (Entity entity : entityArray) {
            functionsList.addAll(List.of(createQueryFunctions(entity)));
        }

        for (QueryMethod queryMethod: dbClientSyntax.queryMethodList) {
            String entityName = queryMethod.getAssociatedEntityName();
            Function query = new Function(queryMethod.getMethodName(), SyntaxKind.OBJECT_METHOD_DEFINITION);

            query.addStatement(NodeParser.parseStatement(getClonedTable(entityModule.getEntityMap().get(entityName))));
            query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_ISOLATED });
            query.addReturns(TypeDescriptor.getSimpleNameReferenceNode("record{}[]"));
            query.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("record{}"), "value");
            query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                    BalSyntaxConstants.KEYWORD_FIELDS);
            query.addStatement(NodeParser.parseStatement(queryMethod.getMethodBody()));
            functionsList.add(query.getFunctionDefinitionNode());
        }

        moduleMembers = moduleMembers.addAll(functionsList);
        return BalSyntaxUtils.generateSyntaxTree(imports, moduleMembers);
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getDataTypesSyntax(Module entityModule) throws BalException {
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() != 0) {
            return BalSyntaxUtils.generateTypeSyntaxTree(entityModule);
        }
        return null;
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getDataStoreConfigSyntax() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getConfigTomlSyntax(String moduleName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static FunctionDefinitionNode[] createQueryFunctions(Entity entity) {
        String resourceName = entity.getResourceName();
        String nameInCamelCase = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) + resourceName.substring(1);
        String clonedTables = getQueryClonedTables(entity);

        StringBuilder queryBuilder = new StringBuilder(String.format(BalSyntaxConstants.QUERY_STATEMENT, resourceName));
        Function query = new Function(String.format(BalSyntaxConstants.QUERY, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_ISOLATED });
        query.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_RETURN));
        query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                BalSyntaxConstants.KEYWORD_FIELDS);
        query.addStatement(NodeParser.parseStatement(clonedTables));

        StringBuilder queryOneBuilder = new StringBuilder(String.format(BalSyntaxConstants.QUERY_ONE_FROM_STATEMENT,
                resourceName));
        queryOneBuilder.append(String.format(BalSyntaxConstants.QUERY_ONE_WHERE_CLAUSE, getPrimaryKeys(entity, true)));
        Function queryOne = new Function(String.format(BalSyntaxConstants.QUERY_ONE, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        queryOne.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_ISOLATED });
        queryOne.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_ONE_RETURN));
        queryOne.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("anydata"),
                BalSyntaxConstants.KEYWORD_KEY);
        queryOne.addStatement(NodeParser.parseStatement(clonedTables));

        createQuery(entity, queryBuilder, queryOneBuilder);
        query.addStatement(NodeParser.parseStatement(queryBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(queryOneBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.QUERY_ONE_RETURN_STATEMENT, entity.getEntityName())));
        return new FunctionDefinitionNode[]{query.getFunctionDefinitionNode(), queryOne.getFunctionDefinitionNode()};
    }

    private static StringBuilder[] createQuery(Entity entity, StringBuilder queryBuilder,
                                               StringBuilder queryOneBuilder) {
        StringBuilder relationalRecordFields = new StringBuilder();
        for (EntityField field : entity.getFields()) {
            if (field.getRelation() != null) {
                Relation relation = field.getRelation();
                if (relation.isOwner()) {
                    Entity assocEntity = relation.getAssocEntity();
                    String fieldName = field.getFieldName();
                    queryBuilder.append(String.format(BalSyntaxConstants.QUERY_OUTER_JOIN,
                            fieldName.toLowerCase(Locale.ENGLISH), assocEntity.getResourceName()));
                    queryBuilder.append(BalSyntaxConstants.ON);
                    queryOneBuilder.append(String.format(BalSyntaxConstants.QUERY_OUTER_JOIN,
                            fieldName.toLowerCase(Locale.ENGLISH), assocEntity.getResourceName()));
                    queryOneBuilder.append(BalSyntaxConstants.ON);
                    relationalRecordFields.append(String.format(BalSyntaxConstants.VARIABLE,
                            fieldName, fieldName.toLowerCase(Locale.ENGLISH)));

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
                                field.getFieldName().toLowerCase(Locale.ENGLISH), references));
                        i++;
                    }
                    queryBuilder.append(arrayFields.append(BalSyntaxConstants.CLOSE_BRACKET));
                    queryBuilder.append(BalSyntaxConstants.EQUALS);
                    queryBuilder.append(arrayValues.append(BalSyntaxConstants.CLOSE_BRACKET));
                    queryOneBuilder.append(arrayFields);
                    queryOneBuilder.append(BalSyntaxConstants.EQUALS);
                    queryOneBuilder.append(arrayValues);
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

    public static String getQueryClonedTables(Entity entity) {
        StringBuilder clonedTables = new StringBuilder();
        ArrayList<Entity> clonedTablesList = new ArrayList<>();

        clonedTables.append(getClonedTable(entity));
        clonedTablesList.add(entity);

        for (EntityField field : entity.getFields()) {
            Relation relation = field.getRelation();
            if (relation == null || !relation.isOwner()) {
                continue;
            }
            Entity assocEntity = relation.getAssocEntity();
            if (clonedTablesList.contains(assocEntity)) {
                continue;
            }

            clonedTables.append(getClonedTable(assocEntity));
            clonedTablesList.add(assocEntity);
        }

        return clonedTables.toString();
    }

    public static String getClonedTable(Entity entity) {
        StringBuilder clonedTable = new StringBuilder();
        clonedTable.append(String.format(BalSyntaxConstants.CLONED_TABLE_INIT_TEMPLATE,
                entity.getEntityName(), getPrimaryKeys(entity, false),
                entity.getResourceName()));
        String clonedTableDeclaration = String.format(BalSyntaxConstants.CLONED_TABLE_DECLARATION_TEMPLATE,
                entity.getResourceName(), entity.getResourceName());
        clonedTable.append(String.format(BalSyntaxConstants.LOCK_TEMPLATE, clonedTableDeclaration));

        return clonedTable.toString();
    }

}
