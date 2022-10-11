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

import io.ballerina.persist.objects.BalException;
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
import java.util.HashMap;

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
import static io.ballerina.persist.PersistToolsConstants.MYSQL;
import static io.ballerina.persist.nodegenerator.SyntaxTreeConstants.ARTIFACT_ID;
import static io.ballerina.persist.nodegenerator.SyntaxTreeConstants.GROUP_ID;
import static io.ballerina.persist.nodegenerator.SyntaxTreeConstants.GROUP_ID_KEYWORD;
import static io.ballerina.persist.nodegenerator.SyntaxTreeConstants.JAVA_11_DEPENDANCY;
import static io.ballerina.persist.nodegenerator.SyntaxTreeConstants.VERSION;


/**
 * Class to create syntax tree for Config.toml.
 *
 * @since 0.1.0
 */
public class SyntaxTreeGenerator {
    private static final String[] nodeMap = {KEY_HOST, KEY_PORT, KEY_USER, KEY_DATABASE, KEY_PASSWORD};
    private static final String[] defaultValues = {
            DEFAULT_HOST,
            DEFAULT_PORT,
            DEFAULT_USER,
            DEFAULT_DATABASE,
            DEFAULT_DATABASE
    };

    /**
     * Method to create a new Config.toml file with database configurations.
     */
    public static SyntaxTree createToml(String name) {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(name, null));
        moduleMembers = populateNodeList(moduleMembers);
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    public static HashMap<String, String> readToml(Path configPath, String name) throws BalException {
        HashMap<String, String> values = new HashMap<>();
        Path fileNamePath = configPath.getFileName();
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            for (Object member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    if (node.identifier().toSourceCode().trim().equals(name)) {
                        NodeList<KeyValueNode> subNodeList = node.fields();
                        for (KeyValueNode subMember : subNodeList) {
                            if (isDatabaseConfigurationEntry(subMember.identifier())) {
                                values.put(subMember.identifier().toSourceCode().trim(),
                                        subMember.value().toSourceCode().trim());
                            }
                        }
                    }

                }
            }
            if (values.isEmpty() || values.size() < 5 || (!values.containsKey("database")
                    || !values.containsKey("user") || !values.containsKey("host") || !values.containsKey("password") ||
                    !values.containsKey("port"))) {
                throw new BalException("Error occurred while reading Config.Toml file");
            } else {
                return values;
            }
        } catch (IOException e) {
            throw new BalException("Error while reading configurations");
        }
    }

    /**
     * Method to update the Config.toml with database configurations.
     */
    public static SyntaxTree updateConfigToml(Path configPath, String name) throws IOException {

        ArrayList<String> existingNodes = new ArrayList<>();
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
        DocumentNode rootNote = syntaxTree.rootNode();
        NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();

        for (DocumentMemberDeclarationNode member : nodeList) {
            if (member instanceof KeyValueNode) {
                moduleMembers = moduleMembers.add(member);
            } else if (member instanceof TableNode) {
                TableNode node = (TableNode) member;
                if (node.identifier().toSourceCode().trim().equals(name)) {
                    if (!moduleMembers.isEmpty()) {
                        moduleMembers = addNewLine(moduleMembers, 1);
                    }
                    NodeList<KeyValueNode> subNodeList = node.fields();
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(name, null));
                    for (KeyValueNode subMember : subNodeList) {
                        if (!isDatabaseConfigurationEntry(subMember.identifier())) {
                            moduleMembers = moduleMembers.add((DocumentMemberDeclarationNode) subMember);
                        } else {
                            existingNodes.add(subMember.identifier().toSourceCode().trim());
                            if (subMember.identifier().toSourceCode().trim().equals(KEY_PORT)) {
                                moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(
                                        subMember.identifier().toSourceCode().trim(),
                                        defaultValues[indexOf(
                                                subMember.identifier().toSourceCode().trim())], null));
                            } else {
                                moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                                        subMember.identifier().toSourceCode().trim(),
                                        defaultValues[indexOf(
                                                subMember.identifier().toSourceCode().trim())], null));
                            }
                        }
                    }
                    if (existingNodes.size() != 5) {
                        if (existingNodes.size() > 0) {
                            moduleMembers = addNewLine(moduleMembers, 1);
                        }
                        moduleMembers = populateRemaining(moduleMembers, existingNodes);
                    }
                } else {
                    moduleMembers = moduleMembers.add(member);
                }
            } else if (member instanceof TableArrayNode) {
                moduleMembers = moduleMembers.add(member);
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

    public static SyntaxTree updateBallerinaToml(Path balPAth) throws IOException {

        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = balPAth.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(balPAth));
        SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
        DocumentNode rootNote = syntaxTree.rootNode();
        NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
        boolean mysqlDriverExists = false;

        for (DocumentMemberDeclarationNode member : nodeList) {
            if (member instanceof KeyValueNode) {
                moduleMembers = moduleMembers.add(member);
            } else if (member instanceof TableNode) {
                moduleMembers = moduleMembers.add(member);
            } else if (member instanceof TableArrayNode) {
                if (((TableArrayNode) member).identifier().toSourceCode().contains(JAVA_11_DEPENDANCY)) {
                    NodeList<KeyValueNode> fields = ((TableArrayNode) member).fields();
                    for (KeyValueNode keyValueNode : fields) {
                        if ((keyValueNode.identifier()).toSourceCode().contains(GROUP_ID_KEYWORD) &&
                                (keyValueNode.value()).toSourceCode().contains(MYSQL)) {
                            mysqlDriverExists = true;
                            break;
                        }
                    }
                    if (!mysqlDriverExists) {
                        moduleMembers = moduleMembers.add(member);
                    } else {
                        moduleMembers = addNewLine(moduleMembers, 1);
                        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTableArray(
                                JAVA_11_DEPENDANCY, null));
                        moduleMembers = moduleMembers.add(GROUP_ID);
                        moduleMembers = moduleMembers.add(ARTIFACT_ID);
                        moduleMembers = moduleMembers.add(VERSION);
                    }
                } else {
                    moduleMembers = moduleMembers.add(member);
                }
            }

        }
        if (!mysqlDriverExists) {
            moduleMembers = addNewLine(moduleMembers, 1);
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createTableArray(
                    JAVA_11_DEPENDANCY, null));
            moduleMembers = moduleMembers.add(GROUP_ID);
            moduleMembers = moduleMembers.add(ARTIFACT_ID);
            moduleMembers = moduleMembers.add(VERSION);
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    private static boolean isDatabaseConfigurationEntry(KeyNode key) {
        switch (key.toSourceCode().trim()) {
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
        for (String member : nodeMap) {
            if (key.equals(member)) {
                return index;
            }
            index += 1;
        }
        return -1;
    }

    private static NodeList<DocumentMemberDeclarationNode> populateNodeList(
            NodeList<DocumentMemberDeclarationNode> moduleMembers) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_HOST, DEFAULT_HOST, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(KEY_PORT, DEFAULT_PORT, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_USER, DEFAULT_USER, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_PASSWORD, DEFAULT_PASSWORD, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_DATABASE, DEFAULT_DATABASE, null));
        return moduleMembers;
    }

    private static NodeList<DocumentMemberDeclarationNode> addNewLine(NodeList moduleMembers, int n) {
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
