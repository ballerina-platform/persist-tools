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
import static io.ballerina.persist.PersistToolsConstants.KEY_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.KEY_HOST;
import static io.ballerina.persist.PersistToolsConstants.KEY_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.KEY_PORT;
import static io.ballerina.persist.PersistToolsConstants.KEY_USER;


/**
 * Class to create syntax tree for Config.toml.
 */
public class SyntaxTreeGenerator {
    private static final String[] nodeMap = {KEY_HOST, KEY_PORT, KEY_USER, KEY_DATABASE, KEY_PASSWORD};
    private static final String[] defaultValues = {DEFAULT_HOST, DEFAULT_PORT, DEFAULT_USER, DEFAULT_DATABASE,
            DEFAULT_DATABASE};

    /**
     * Method to create a new Config.toml file with database configurations.
     */
    public static SyntaxTree createToml(String name) {
        NodeList moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(name, null));
        moduleMembers = populateNodeList(moduleMembers);
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    /**
     * Method to update the Config.toml with database configurations.
     */
    public static SyntaxTree updateToml(Path configPath, String name) throws IOException {

        ArrayList<String> existingNodes = new ArrayList<>();
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
        DocumentNode rootNote = syntaxTree.rootNode();
        NodeList nodeList = rootNote.members();

        for (Object member : nodeList) {
            if (member instanceof KeyValueNode) {
                moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
            } else if (member instanceof TableNode) {
                TableNode node = (TableNode) member;
                if (node.identifier().toString().trim().equals(name)) {
                    if (!moduleMembers.isEmpty()) {
                        moduleMembers = addNewLine(moduleMembers, 1);
                    }
                    NodeList<KeyValueNode> subNodeList = node.fields();
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(name, null));
                    for (KeyValueNode subMember : subNodeList) {
                        if (!isDatabaseConfigurationEntry(subMember.identifier())) {
                            moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) subMember);
                        } else {
                            existingNodes.add(subMember.identifier().toString().trim());
                            if (subMember.identifier().toString().trim().equals(KEY_PORT)) {
                                moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(
                                        subMember.identifier().toString().trim(),
                                        defaultValues[indexOf(
                                                subMember.identifier().toString().trim())], null));
                            } else {
                                moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                                        subMember.identifier().toString().trim(),
                                        defaultValues[indexOf(
                                                subMember.identifier().toString().trim())], null));
                            }
                        }
                    }
                    if (existingNodes.size() != 5) {
                        moduleMembers = populateRemaining(moduleMembers, existingNodes);
                    }
                } else {
                    moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
                }
            } else if (member instanceof TableArrayNode) {
                moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) member);
            }
        }
        if (existingNodes.isEmpty()) {
            moduleMembers = addNewLine(moduleMembers, 1);
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(name, null));
            moduleMembers = populateRemaining(moduleMembers, existingNodes);

        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    private static boolean isDatabaseConfigurationEntry(KeyNode key) {
        switch (key.toString().trim()) {
            case KEY_USER:
            case KEY_DATABASE:
            case KEY_PASSWORD:
            case KEY_HOST:
            case KEY_PORT:
                return true;
            default:
                return false;
        }
    }
        private static int indexOf(String key) {
        int index = 0;
        for (String member : SyntaxTreeGenerator.nodeMap) {
            if (key.equals(member)) {
                return index;
            }
            index += 1;
        }
        return -1;
    }

    private static NodeList populateNodeList(NodeList moduleMembers) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_HOST, DEFAULT_HOST, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(KEY_PORT, DEFAULT_PORT, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_USER, DEFAULT_USER, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_PASSWORD, DEFAULT_PASSWORD, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_DATABASE, DEFAULT_DATABASE, null));
        moduleMembers = moduleMembers.add(AbstractNodeFactory.createIdentifierToken(System.lineSeparator()));
        return moduleMembers;
    }

    private static NodeList addNewLine(NodeList moduleMembers, int n) {
        for (int i = 0; i < n; i++) {
            moduleMembers = moduleMembers.add(AbstractNodeFactory.createIdentifierToken(System.lineSeparator()));
        }
        return moduleMembers;
    }

    private static NodeList<DocumentMemberDeclarationNode> populateRemaining(
            NodeList<DocumentMemberDeclarationNode> moduleMembers, ArrayList<String> existingNodes) {
        for (String key : nodeMap) {
            if (!existingNodes.contains(key)) {
                if (key.equals(KEY_PORT)) {
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(key,
                            defaultValues[indexOf(key)], null));
                } else {
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(key,
                            defaultValues[indexOf(key)], null));
                }
                existingNodes.add(key);
            }
        }
        return moduleMembers;
    }
}
