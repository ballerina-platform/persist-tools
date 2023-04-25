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

import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
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
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.components.syntax.client.CommonSyntax;
import io.ballerina.persist.components.syntax.client.InMemoryClientSyntax;
import io.ballerina.persist.components.syntax.objects.SyntaxTreeGenerator;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used to generate the syntax tree for in-memory.
 *
 * @since 0.3.1
 */
public class InMemorySyntaxTree implements SyntaxTreeGenerator {

    @Override
    public SyntaxTree getClientSyntax(Module entityModule) throws BalException {
        InMemoryClientSyntax dbClientSyntax = new InMemoryClientSyntax(entityModule);
        NodeList<ImportDeclarationNode> imports = dbClientSyntax.getImports();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = dbClientSyntax.getConstantVariables();
        for (Entity entity : entityModule.getEntityMap().values()) {
            moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                    String.format(BalSyntaxConstants.TABLE_PARAMETER_INIT_TEMPLATE, entity.getEntityName(),
                            Utils.getPrimaryKeys(entity, false), entity.getResourceName(), "table[]")));
        }
        Client clientObject = dbClientSyntax.getClient(entityModule);
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
            FunctionDefinitionNode[] functions = createQueryFunction(entity);
            resource.addFunction(functions[0], true);
            resource.addFunction(functions[1], true);
            resourceList.add(resource);
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });

        for (Map.Entry<String, String> entry : dbClientSyntax.queryMethodStatement.entrySet()) {
            Function query = new Function(entry.getKey(), SyntaxKind.OBJECT_METHOD_DEFINITION);
            query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PRIVATE });
            query.addReturns(TypeDescriptor.getSimpleNameReferenceNode("record{}[]"));
            query.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("record{}"), "value");
            query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                    BalSyntaxConstants.KEYWORD_FIELDS);
            query.addStatement(NodeParser.parseStatement(entry.getValue()));
            clientObject.addMember(query.getFunctionDefinitionNode(), true);
        }
        clientObject.addMember(dbClientSyntax.getCloseFunction(), true);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return CommonSyntax.generateSyntaxTree(imports, moduleMembers);
    }

    @Override
    public SyntaxTree getDataTypesSyntax(Module entityModule) throws BalException {
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() != 0) {
            return Utils.generateTypeSyntaxTree(entityModule);
        }
        return null;
    }

    @Override
    public SyntaxTree getDataStoreConfigSyntax() {
        return null;
    }

    @Override
    public SyntaxTree getConfigTomlSyntax(String moduleName) {
        return null;
    }

    @Override
    public SyntaxTree getUpdateConfigTomlSyntax(Path configPath, String moduleName) {
        return null;
    }

    private static FunctionDefinitionNode[] createQueryFunction(Entity entity) {
        String resourceName = entity.getResourceName();
        String nameInCamelCase = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) + resourceName.substring(1);
        StringBuilder queryBuilder = new StringBuilder(String.format(BalSyntaxConstants.QUERY_STATEMENT, resourceName));
        Function query = new Function(String.format(BalSyntaxConstants.QUERY, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PRIVATE });
        query.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_RETURN));
        query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                BalSyntaxConstants.KEYWORD_FIELDS);

        StringBuilder queryOneBuilder = new StringBuilder(String.format(BalSyntaxConstants.QUERY_ONE_FROM_STATEMENT,
                resourceName));
        queryOneBuilder.append(String.format(BalSyntaxConstants.QUERY_ONE_WHERE_CLAUSE,
                Utils.getStringWithUnderScore(entity.getEntityName())));
        Function queryOne = new Function(String.format(BalSyntaxConstants.QUERY_ONE, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        queryOne.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PRIVATE });
        queryOne.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_ONE_RETURN));
        queryOne.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("anydata"),
                BalSyntaxConstants.KEYWORD_KEY);

        createQuery(entity, queryBuilder, queryOneBuilder);
        query.addStatement(NodeParser.parseStatement(queryBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(queryOneBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(BalSyntaxConstants.QUERY_ONE_RETURN_STATEMENT));
        return new FunctionDefinitionNode[]{query.getFunctionDefinitionNode(), queryOne.getFunctionDefinitionNode()};
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
}
