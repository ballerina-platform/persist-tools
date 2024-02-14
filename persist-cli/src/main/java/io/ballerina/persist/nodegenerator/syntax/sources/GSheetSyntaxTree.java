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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.clients.GSheetClientSyntax;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.validator.SampleNodeGenerator;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used to generate the syntax tree for google sheet.
 *
 * @since 0.3.1
 */
public class GSheetSyntaxTree implements SyntaxTree {

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getClientSyntax(Module entityModule) throws BalException {
        GSheetClientSyntax gSheetClientSyntax = new GSheetClientSyntax(entityModule);
        NodeList<ImportDeclarationNode> imports = gSheetClientSyntax.getImports();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = gSheetClientSyntax.getConstantVariables();

        Client clientObject = gSheetClientSyntax.getClientObject(entityModule);
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }
        clientObject.addMember(gSheetClientSyntax.getInitFunction(entityModule), true);
        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            ClientResource resource = new ClientResource();
            resource.addFunction(gSheetClientSyntax.getGetFunction(entity), true);
            resource.addFunction(gSheetClientSyntax.getGetByKeyFunction(entity), true);
            resource.addFunction(gSheetClientSyntax.getPostFunction(entity), true);
            resource.addFunction(gSheetClientSyntax.getPutFunction(entity), true);
            resource.addFunction(gSheetClientSyntax.getDeleteFunction(entity), true);
            FunctionDefinitionNode[] functions = createQueryFunction(entity);
            resource.addFunction(functions[0], true);
            resource.addFunction(functions[1], true);
            String entityResourceName = entity.getTableName();
            resource.addFunction(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.EXTERNAL_QUERY_STREAM_METHOD_TEMPLATE, entityResourceName.substring(0, 1).
                            toUpperCase(Locale.ENGLISH) + entityResourceName.substring(1),
                    entity.getEntityName(), BalSyntaxConstants.GOOGLE_SHEETS)), true);
            resourceList.add(resource);
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });
        for (Map.Entry<String, String> entry : gSheetClientSyntax.queryMethodStatement.entrySet()) {
            Function query = new Function(entry.getKey(), SyntaxKind.OBJECT_METHOD_DEFINITION);
            query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PRIVATE,
                    BalSyntaxConstants.KEYWORD_ISOLATED });
            query.addReturns(TypeDescriptor.getSimpleNameReferenceNode("record{}[]|persist:Error"));
            query.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode("record{}"), "value");
            query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                    BalSyntaxConstants.KEYWORD_FIELDS);
            query.addStatement(NodeParser.parseStatement(entry.getValue()));
            clientObject.addMember(query.getFunctionDefinitionNode(), true);
        }
        clientObject.addMember(gSheetClientSyntax.getCloseFunction(), true);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
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
        String content = BalSyntaxConstants.AUTOGENERATED_FILE_COMMENT +
                System.lineSeparator() + System.lineSeparator() +
                BalSyntaxConstants.AUTO_GENERATED_COMMENT +
                System.lineSeparator() +
                BalSyntaxConstants.COMMENT_SHOULD_NOT_BE_MODIFIED +
                System.lineSeparator() + System.lineSeparator() +
                BalSyntaxConstants.CONFIGURABLE_CLIENT_ID +
                BalSyntaxConstants.CONFIGURABLE_CLIENT_SECRET +
                BalSyntaxConstants.CONFIGURABLE_REFRESH_TOKEN +
                BalSyntaxConstants.CONFIGURABLE_WORK_SHEET_ID;
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        io.ballerina.compiler.syntax.tree.SyntaxTree balTree =
                io.ballerina.compiler.syntax.tree.SyntaxTree.from(textDocument);
        return balTree.modifyWith(NodeParser.parseModulePart(content));
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getConfigTomlSyntax(String moduleName) {
        io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> moduleMembers =
                io.ballerina.toml.syntax.tree.AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(moduleName, null));
        moduleMembers = populateConfigNodeList(moduleMembers);
        moduleMembers = BalProjectUtils.addNewLine(moduleMembers, 1);
        io.ballerina.toml.syntax.tree.Token eofToken = io.ballerina.toml.syntax.tree.AbstractNodeFactory.
                createIdentifierToken("");
        DocumentNode documentNode = io.ballerina.toml.syntax.tree.NodeFactory.createDocumentNode(
                moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return io.ballerina.compiler.syntax.tree.SyntaxTree.from(textDocument);
    }

    private static io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> populateConfigNodeList(
            io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> moduleMembers) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.KEYWORD_SHEET_ID, PersistToolsConstants.EMPTY_VALUE, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.KEYWORD_CLIENT_ID, PersistToolsConstants.EMPTY_VALUE, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.KEYWORD_CLIENT_SECRET, PersistToolsConstants.EMPTY_VALUE, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.KEYWORD_REFRESH_TOKEN, PersistToolsConstants.EMPTY_VALUE, null));
        return moduleMembers;
    }

    private FunctionDefinitionNode[] createQueryFunction(Entity entity) {
        String resourceName = entity.getTableName();
        String nameInCamelCase = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) + resourceName.substring(1);
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryOneBuilder = new StringBuilder();

        Function query = new Function(String.format(BalSyntaxConstants.QUERY, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        query.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PRIVATE, BalSyntaxConstants.KEYWORD_ISOLATED });
        query.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_RETURN + "|" +
                BalSyntaxConstants.PERSIST_ERROR));
        query.addRequiredParameter(TypeDescriptor.getArrayTypeDescriptorNode("string"),
                BalSyntaxConstants.KEYWORD_FIELDS);

        Function queryOne = new Function(String.format(BalSyntaxConstants.QUERY_ONE, nameInCamelCase),
                SyntaxKind.OBJECT_METHOD_DEFINITION);
        queryOne.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PRIVATE, BalSyntaxConstants.KEYWORD_ISOLATED});
        queryOne.addReturns(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.QUERY_ONE_RETURN_GSHEET));
        queryOne.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.ANY_DATA),
                BalSyntaxConstants.KEYWORD_KEY);
        createQuery(entity, queryBuilder, queryOneBuilder);
        query.addStatement(NodeParser.parseStatement(queryBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(queryOneBuilder.toString()));
        queryOne.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.QUERY_ONE_RETURN_STATEMENT, entity.getEntityName())));
        return new FunctionDefinitionNode[]{query.getFunctionDefinitionNode(), queryOne.getFunctionDefinitionNode()};
    }

    private void createQuery(Entity entity, StringBuilder queryBuilder, StringBuilder queryOneBuilder) {
        StringBuilder relationalRecordFields = new StringBuilder();
        String entityName = entity.getEntityName();
        String entityResourceName = entity.getTableName();
        String resourceNameInCamelCase = entityResourceName.substring(0, 1).
                toUpperCase(Locale.ENGLISH) + entityResourceName.substring(1);
        StringBuilder streamInitBuilder = new StringBuilder();
        StringBuilder streamSelectBuilder = new StringBuilder();
        String stream = "Stream";
        streamInitBuilder.append(String.format(BalSyntaxConstants.STREAM_PARAM_INIT, entityName, entityResourceName,
                resourceNameInCamelCase));
        List<String> addedEntities = new ArrayList<>();
        for (EntityField fields : entity.getFields()) {
            if (fields.getRelation() != null) {
                Relation relation = fields.getRelation();
                if (relation.isOwner()) {
                    Entity assocEntity = relation.getAssocEntity();
                    String assocEntityName = assocEntity.getEntityName();
                    String assocEntityResourceName = assocEntity.getTableName();
                    String assocResourceNameInCamelCase = assocEntityResourceName.substring(0, 1).
                            toUpperCase(Locale.ENGLISH) + assocEntityResourceName.substring(1);

                    if (!addedEntities.contains(assocEntityResourceName)) {
                        streamInitBuilder.append(String.format(BalSyntaxConstants.STREAM_PARAM_INIT,
                                assocEntityName, assocEntityResourceName, assocResourceNameInCamelCase));
                        addedEntities.add(assocEntityResourceName);
                    }

                    String assocFieldName = fields.getFieldName();
                    streamSelectBuilder.append(String.format(BalSyntaxConstants.G_SHEET_QUERY_OUTER_JOIN,
                            assocFieldName.toLowerCase(Locale.ENGLISH), BalSyntaxConstants.EMPTY_STRING,
                            assocEntityResourceName + stream));
                    streamSelectBuilder.append(BalSyntaxConstants.ON);
                    relationalRecordFields.append(String.format(BalSyntaxConstants.VARIABLE,
                            assocFieldName.toLowerCase(Locale.ENGLISH),
                            assocFieldName.toLowerCase(Locale.ENGLISH)));
                    int i = 0;
                    StringBuilder arrayFields = new StringBuilder();
                    StringBuilder arrayValues = new StringBuilder();
                    arrayFields.append(BalSyntaxConstants.OPEN_BRACKET);
                    arrayValues.append(BalSyntaxConstants.OPEN_BRACKET);
                    for (String references: relation.getReferences()) {
                        if (i > 0) {
                            arrayFields.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                            arrayValues.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                        }
                        arrayFields.append(String.format(BalSyntaxConstants.OBJECT_FIELD,
                                relation.getKeyColumns().get(i).getField()));
                        arrayValues.append(String.format(BalSyntaxConstants.VALUES,
                                assocFieldName.toLowerCase(Locale.ENGLISH), references));
                        i++;
                    }
                    streamSelectBuilder.append(arrayFields.append(BalSyntaxConstants.CLOSE_BRACKET));
                    streamSelectBuilder.append(BalSyntaxConstants.EQUALS);
                    streamSelectBuilder.append(arrayValues.append(BalSyntaxConstants.CLOSE_BRACKET));
                }
            }
        }
        queryBuilder.append(streamInitBuilder);
        String streamParamName =  entityResourceName + stream;
        queryBuilder.append("record{}[] outputArray =").append(
                String.format(BalSyntaxConstants.G_SHEET_QUERY_STATEMENT, "check", BalSyntaxConstants.EMPTY_STRING,
                        streamParamName));
        queryBuilder.append(streamSelectBuilder);
        queryOneBuilder.append(streamInitBuilder);
        queryOneBuilder.append("error? unionResult = ").append(
                String.format(BalSyntaxConstants.G_SHEET_QUERY_STATEMENT, BalSyntaxConstants.EMPTY_STRING,
                        BalSyntaxConstants.EMPTY_STRING, streamParamName));
        StringBuilder keysArray = new StringBuilder();
        if (entity.getKeys().size() > 1) {
            for (EntityField keyField : entity.getKeys()) {
                if (keysArray.length() > 0) {
                    keysArray.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                }
                keysArray.append(String.format("\"%s\"", keyField.getFieldName().replaceAll(BalSyntaxConstants.SPACE
                        , "")));
            }
        } else {
            keysArray.append(String.format("\"%s\"", entity.getKeys().get(0).getFieldName().replaceAll(
                    BalSyntaxConstants.SPACE, "")));
        }
        queryOneBuilder.append(String.format(BalSyntaxConstants.G_SHEET_WHERE_CLAUSE, keysArray));
        queryOneBuilder.append(streamSelectBuilder);
        if (relationalRecordFields.length() > 0) {
            queryBuilder.append(String.format(BalSyntaxConstants.SELECT_QUERY,
                    BalSyntaxConstants.COMMA + System.lineSeparator() +
                    relationalRecordFields.substring(0, relationalRecordFields.length() - 1)));
            queryOneBuilder.append(String.format(BalSyntaxConstants.DO_QUERY,
                    BalSyntaxConstants.COMMA + System.lineSeparator() +
                    relationalRecordFields.substring(0, relationalRecordFields.length() - 1)));
        } else {
            queryBuilder.append(String.format(System.lineSeparator() + BalSyntaxConstants.SELECT_QUERY,
                    BalSyntaxConstants.EMPTY_STRING));
            queryBuilder.append(System.lineSeparator());
            queryOneBuilder.append(String.format(System.lineSeparator() + BalSyntaxConstants.DO_QUERY,
                    BalSyntaxConstants.EMPTY_STRING));
        }
        queryBuilder.append("return outputArray.toStream();").append(System.lineSeparator());
        queryOneBuilder.append(BalSyntaxConstants.IF_STATEMENT);
    }
}
