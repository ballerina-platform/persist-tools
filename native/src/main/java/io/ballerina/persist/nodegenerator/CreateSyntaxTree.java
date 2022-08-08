/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.persist.nodegenerator;

import io.ballerina.toml.syntax.tree.AbstractNodeFactory;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.KeyNode;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.syntax.tree.NodeFactory;
import io.ballerina.toml.syntax.tree.NodeList;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import io.ballerina.toml.syntax.tree.TableArrayNode;
import io.ballerina.toml.syntax.tree.TableNode;
import io.ballerina.toml.syntax.tree.Token;
import io.ballerina.toml.validator.SampleNodeGenerator;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.ballerina.persist.PersistToolsConstants.DEFAULT_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_HOST;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PORT;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PROVIDER;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_USER;

/**
 * Class to create syntax tree for Config.toml.
 */
public class CreateSyntaxTree {
    private static final PrintStream outStream = System.out;

    /**
     * Method to create a new Config.toml file with database configurations.
     */
    public static SyntaxTree createToml() {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = populateNodeList(moduleMembers, true);
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        SyntaxTree syntaxTree = SyntaxTree.from(textDocument);
        return syntaxTree;
    }

    /**
     * Method to update the Config.toml with database configurations.
     */
    public static SyntaxTree updateToml(Path configPath) throws IOException {
        boolean isTableEntry = false;
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = populateNodeList(moduleMembers, false);
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
        DocumentNode rootNote = (DocumentNode) syntaxTree.rootNode();
        NodeList nodeList = rootNote.members();

        for (Object member : nodeList) {
            if (member instanceof KeyValueNode) {
                KeyValueNode node = (KeyValueNode) member;
                if (!isTableEntry) {
                    if (!isDatabaseConfigurationEntry(node.identifier())) {
                        moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
                    }
                } else {
                    moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
                }
            } else if (member instanceof TableNode || member instanceof TableArrayNode) {
                isTableEntry = true;
                moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
            }
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        SyntaxTree syntaxTreeFinal = SyntaxTree.from(textDocument);
        return syntaxTreeFinal;
    }

    private static boolean isDatabaseConfigurationEntry(KeyNode key) {
        switch (key.toString().trim()) {
            case "provider":
            case "user":
            case "database":
            case "password":
            case "host":
            case "port":
                return true;
            default:
                return false;
        }
    }

    private static NodeList populateNodeList(NodeList moduleMembers, boolean create) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("provider", DEFAULT_PROVIDER, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("host", DEFAULT_HOST, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV("port", DEFAULT_PORT, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("user", DEFAULT_USER, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("password", DEFAULT_PASSWORD, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("database", DEFAULT_DATABASE, null));
        if (!create) {
            moduleMembers = moduleMembers.add(AbstractNodeFactory.createIdentifierToken("\n"));
        }
        return moduleMembers;
    }
}
