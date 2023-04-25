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
package io.ballerina.persist.nodegenerator.syntax;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.ImportPrefixNode;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;
import io.ballerina.persist.nodegenerator.syntax.client.DbClientSyntax;
import io.ballerina.persist.nodegenerator.syntax.objects.SyntaxTreeGenerator;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.validator.SampleNodeGenerator;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used to generate the syntax tree for database.
 *
 * @since 0.3.1
 */
public class DbSyntaxTree implements SyntaxTreeGenerator {

    @Override
    public SyntaxTree getClientSyntax(Module entityModule) throws BalException {
        DbClientSyntax dbClientSyntax = new DbClientSyntax(entityModule);
        NodeList<ImportDeclarationNode> imports = dbClientSyntax.getImports();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = dbClientSyntax.getConstantVariables();

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
            resourceList.add(resource);
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });
        clientObject.addMember(dbClientSyntax.getCloseFunction(), true);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return CommonSyntax.generateSyntaxTree(imports, moduleMembers);
    }

    @Override
    public SyntaxTree getDataTypesSyntax(Module entityModule) {
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() != 0) {
            return CommonSyntax.generateTypeSyntaxTree(entityModule);
        }
        return null;
    }

    @Override
    public SyntaxTree getDataStoreConfigSyntax() {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        MinutiaeList commentMinutiaeList = createCommentMinutiaeList(String.
                format(BalSyntaxConstants.AUTO_GENERATED_COMMENT));
        ImportPrefixNode prefix = NodeFactory.createImportPrefixNode(SyntaxTokenConstants.SYNTAX_TREE_AS,
                AbstractNodeFactory.createToken(SyntaxKind.UNDERSCORE_KEYWORD));
        imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment(
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

    @Override
    public SyntaxTree getConfigTomlSyntax(String moduleName) {
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
        return SyntaxTree.from(textDocument);
    }

    private static ImportDeclarationNode getImportDeclarationNodeWithAutogeneratedComment(
            MinutiaeList commentMinutiaeList, ImportPrefixNode prefix) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.KEYWORD_BALLERINAX);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.MYSQL_DRIVER);
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
                SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON);
    }

    private static io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> populateConfigNodeList(
            io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> moduleMembers) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(PersistToolsConstants.KEY_HOST,
                PersistToolsConstants.DEFAULT_HOST, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(PersistToolsConstants.KEY_PORT,
                PersistToolsConstants.DEFAULT_PORT, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(PersistToolsConstants.KEY_USER,
                PersistToolsConstants.DEFAULT_USER, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(PersistToolsConstants.KEY_PASSWORD,
                PersistToolsConstants.DEFAULT_PASSWORD, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(PersistToolsConstants.KEY_DATABASE,
                PersistToolsConstants.DEFAULT_DATABASE, null));
        return moduleMembers;
    }

    protected static MinutiaeList createCommentMinutiaeList(String comment) {
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
}
