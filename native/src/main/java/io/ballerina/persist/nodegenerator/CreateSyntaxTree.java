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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static io.ballerina.persist.PersistToolsConstants.DEFAULT_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_HOST;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PORT;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_USER;

/**
 * Class to create syntax tree for Config.toml.
 */
public class CreateSyntaxTree {

    private static String[] nodeMap = {"host", "port", "user", "database", "password"};
    private static String[] defaultValues = {"localhost", "3306", "root", "", ""};

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
        ArrayList<String> existingNodes = new ArrayList<String>();
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
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
                    } else {
                        if (node.identifier().toString().trim().equals("port")) {
                            existingNodes.add(node.identifier().toString().trim());
                            moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(
                                    node.identifier().toString().trim(),
                                    defaultValues[indexOf(nodeMap,
                                            node.identifier().toString().trim())], null));
                        } else {
                            existingNodes.add(node.identifier().toString().trim());
                            moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                                    node.identifier().toString().trim(),
                                    defaultValues[indexOf(nodeMap,
                                            node.identifier().toString().trim())], null));
                        }
                    }
                } else {
                    moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
                }
            } else if (member instanceof TableNode || member instanceof TableArrayNode) {
                if (!existingNodes.isEmpty() || !moduleMembers.isEmpty()) {
                    moduleMembers = addNewLine(moduleMembers, 2);
                    moduleMembers = poulateRemaining(moduleMembers, existingNodes);
                    moduleMembers = addNewLine(moduleMembers, 1);
                    isTableEntry = true;
                    moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
                } else {
                    isTableEntry = true;
                    moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
                }
            }
        }
        if (existingNodes.size() != 5) {
            moduleMembers = addNewLine(moduleMembers, 2);
            moduleMembers = poulateRemaining(moduleMembers, existingNodes);
        }

        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        SyntaxTree syntaxTreeFinal = SyntaxTree.from(textDocument);
        return syntaxTreeFinal;
    }


    private static boolean isDatabaseConfigurationEntry(KeyNode key) {
        switch (key.toString().trim()) {
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
    private static int indexOf(String[] arr, String key) {
        int index = 0;
        for (Object member : arr) {
            if (key.equals((String) member)) {
                return index;
            }
            index += 1;
        }
        return -1;
    }

    private static NodeList populateNodeList(NodeList moduleMembers, boolean create) {
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

    private static NodeList addNewLine(NodeList moduleMembers, int n) {
        for (int i = 0; i < n; i++) {
            moduleMembers = moduleMembers.add(AbstractNodeFactory.createIdentifierToken("\n"));
        }
        return moduleMembers;
    }

    private static NodeList poulateRemaining(NodeList moduleMembers, ArrayList existingNodes) {
        for (Object key : nodeMap) {
            if (!existingNodes.contains((String) key)) {
                if (((String) key).equals("port")) {
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV((String) key,
                            defaultValues[indexOf(nodeMap, (String) key)], null));
                    existingNodes.add((String) key);
                } else {
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV((String) key,
                            defaultValues[indexOf(nodeMap, (String) key)], null));
                    existingNodes.add((String) key);
                }
            }
        }
        return moduleMembers;
    }
}
