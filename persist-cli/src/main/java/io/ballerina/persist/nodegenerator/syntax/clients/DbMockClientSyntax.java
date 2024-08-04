/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
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
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.constants.SyntaxTokenConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

public class DbMockClientSyntax implements ClientSyntax {
    private final String dbNamePrefix;
    private final String dbSpecifics;
    private final String initDbClientMethodTemplate;

    private final DbClientSyntax dbClientSyntax;

    public DbMockClientSyntax(Module entityModule) throws BalException {
        this.dbClientSyntax = new DbClientSyntax(entityModule, PersistToolsConstants.SupportedDataSources.H2_DB);
        this.dbNamePrefix = PersistToolsConstants.SupportedDataSources.JDBC;
        this.dbSpecifics = BalSyntaxConstants.H2_SPECIFICS;
        this.initDbClientMethodTemplate = BalSyntaxConstants.JDBC_URL_INIT_DB_CLIENT_WITH_PARAMS;
    }

    @Override
    public NodeList<ImportDeclarationNode> getImports() throws BalException {
        return dbClientSyntax.getImports();
    }

    @Override
    public NodeList<ModuleMemberDeclarationNode> getConstantVariables() {
        return dbClientSyntax.getConstantVariables();
    }

    @Override
    public Client getClientObject(Module entityModule, String clientName) {
        return dbClientSyntax.getClientObject(entityModule, clientName);
    }

    @Override
    public FunctionDefinitionNode getInitFunction(Module entityModule) {
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"),
                "url");
        init.addDefaultableParameter(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                "string"), "user", NodeFactory.createNilLiteralNode(
                        SyntaxTokenConstants.SYNTAX_TREE_OPEN_PAREN, SyntaxTokenConstants.SYNTAX_TREE_CLOSE_PAREN));
        init.addDefaultableParameter(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                "string"), "password", NodeFactory.createNilLiteralNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_PAREN, SyntaxTokenConstants.SYNTAX_TREE_CLOSE_PAREN));
        init.addDefaultableParameter(TypeDescriptor.getOptionalTypeDescriptorNode("jdbc",
                "Options"), "connectionOptions", NodeFactory.createNilLiteralNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_PAREN, SyntaxTokenConstants.SYNTAX_TREE_CLOSE_PAREN));
        init.addStatement(NodeParser.parseStatement(String.format(this.initDbClientMethodTemplate, this.dbNamePrefix)));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.DB_CLIENT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.DB_CLIENT)));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.ADD_CLIENT));
        StringBuilder persistClientMap = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (entity.containsUnsupportedTypes()) {
                continue;
            }
            if (!persistClientMap.isEmpty()) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            String constantName = BalSyntaxUtils.stripEscapeCharacter(BalSyntaxUtils.
                    getStringWithUnderScore(entity.getEntityName()));
            String clientMapElement = String.format(BalSyntaxConstants.PERSIST_CLIENT_MAP_ELEMENT,
                    constantName, constantName, this.dbSpecifics);
            persistClientMap.append(clientMapElement);
        }
        init.addStatement(NodeParser.parseStatement(String.format(
                BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE, persistClientMap)));
        return init.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getGetFunction(Entity entity) {
        return dbClientSyntax.getGetFunction(entity);
    }

    @Override
    public FunctionDefinitionNode getGetByKeyFunction(Entity entity) {
        return dbClientSyntax.getGetByKeyFunction(entity);
    }

    @Override
    public FunctionDefinitionNode getCloseFunction() {
        return dbClientSyntax.getCloseFunction();
    }

    @Override
    public FunctionDefinitionNode getPostFunction(Entity entity) {
        return dbClientSyntax.getPostFunction(entity);
    }

    @Override
    public FunctionDefinitionNode getPutFunction(Entity entity) {
        return dbClientSyntax.getPutFunction(entity);
    }

    @Override
    public FunctionDefinitionNode getDeleteFunction(Entity entity) {
        return dbClientSyntax.getDeleteFunction(entity);
    }

    @Override
    public FunctionDefinitionNode getQueryNativeSQLFunction() {
        return dbClientSyntax.getQueryNativeSQLFunction();
    }

    @Override
    public FunctionDefinitionNode getExecuteNativeSQLFunction() {
        return dbClientSyntax.getExecuteNativeSQLFunction();
    }
}
