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
package io.ballerina.persist.nodegenerator.syntax.sources;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.clients.RedisClientSyntax;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.constants.SyntaxTokenConstants;
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

public class RedisSyntaxTree implements SyntaxTree  {

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getClientSyntax(Module entityModule)
            throws BalException {
        RedisClientSyntax redisClientSyntax = new RedisClientSyntax(entityModule);
        NodeList<ImportDeclarationNode> imports = redisClientSyntax.getImports();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = redisClientSyntax.getConstantVariables();

        Client clientObject = redisClientSyntax.getClientObject(entityModule);
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }
        clientObject.addMember(redisClientSyntax.getInitFunction(entityModule), true);
        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            ClientResource resource = new ClientResource();
            resource.addFunction(redisClientSyntax.getGetFunction(entity), true);
            resource.addFunction(redisClientSyntax.getGetByKeyFunction(entity), true);
            resource.addFunction(redisClientSyntax.getPostFunction(entity), true);
            resource.addFunction(redisClientSyntax.getPutFunction(entity), true);
            resource.addFunction(redisClientSyntax.getDeleteFunction(entity), true);
            resourceList.add(resource);
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });
        clientObject.addMember(redisClientSyntax.getCloseFunction(), true);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return BalSyntaxUtils.generateSyntaxTree(imports, moduleMembers);
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getDataTypesSyntax(Module entityModule) {
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() != 0) {
            return BalSyntaxUtils.generateTypeSyntaxTree(entityModule, 
            PersistToolsConstants.SupportedDataSources.REDIS);
        }
        return null;
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getDataStoreConfigSyntax() {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        MinutiaeList commentMinutiaeList = createCommentMinutiaeList(String.
                format(BalSyntaxConstants.AUTO_GENERATED_COMMENT));
        imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment("redis", commentMinutiaeList));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(BalSyntaxConstants.REDIS_CONFIG));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(BalSyntaxConstants.CACHE_CONFIG));

        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        io.ballerina.compiler.syntax.tree.SyntaxTree balTree =
                io.ballerina.compiler.syntax.tree.SyntaxTree.from(textDocument);
        return balTree.modifyWith(modulePartNode);
    }

    @Override
    public io.ballerina.compiler.syntax.tree.SyntaxTree getConfigTomlSyntax(String moduleName) {
        io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> moduleMembers =
                io.ballerina.toml.syntax.tree.AbstractNodeFactory.createEmptyNodeList();
        // moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(moduleName + ".redis", null));
        moduleMembers = populateConfigNodeList(moduleName, moduleMembers);
        moduleMembers = BalProjectUtils.addNewLine(moduleMembers, 1);
        io.ballerina.toml.syntax.tree.Token eofToken = io.ballerina.toml.syntax.tree.AbstractNodeFactory.
                createIdentifierToken("");
        DocumentNode documentNode = io.ballerina.toml.syntax.tree.NodeFactory.createDocumentNode(
                moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return io.ballerina.compiler.syntax.tree.SyntaxTree.from(textDocument);
    }

    private static ImportDeclarationNode getImportDeclarationNodeWithAutogeneratedComment(
            String datasource, MinutiaeList commentMinutiaeList) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.KEYWORD_BALLERINAX);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(datasource);
        SeparatedNodeList<IdentifierToken> moduleNodeList = AbstractNodeFactory
                .createSeparatedNodeList(moduleNameToken);
        Token importToken = NodeFactory.createToken(SyntaxKind.IMPORT_KEYWORD,
                commentMinutiaeList, NodeFactory.createMinutiaeList(AbstractNodeFactory
                        .createWhitespaceMinutiae(BalSyntaxConstants.SPACE)));
        return NodeFactory.createImportDeclarationNode(
                importToken,
                importOrgNameNode,
                moduleNodeList,
                null,
                SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON);
    }

    private static io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> populateConfigNodeList(
        String moduleName, io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> moduleMembers) {
        String connectionUri = PersistToolsConstants.DBConfigs.REDIS.CONNECTION_URI;
        String maxAge = PersistToolsConstants.DBConfigs.REDIS.MAX_AGE;
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(moduleName + ".connectionConfig", null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.DBConfigs.KEY_CONNECTION, connectionUri, null));
        
        moduleMembers = BalProjectUtils.addNewLine(moduleMembers, 1);
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(moduleName + ".cacheConfig", null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(
                PersistToolsConstants.DBConfigs.KEY_MAX_AGE, maxAge, null));
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
