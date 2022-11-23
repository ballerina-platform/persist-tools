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

import static io.ballerina.persist.PersistToolsConstants.DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DATABASE_PLACEHOLDER;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_HOST;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PORT;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_USER;
import static io.ballerina.persist.PersistToolsConstants.HOST;
import static io.ballerina.persist.PersistToolsConstants.HOST_PLACEHOLDER;
import static io.ballerina.persist.PersistToolsConstants.KEYWORD_PROVIDER;
import static io.ballerina.persist.PersistToolsConstants.KEY_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.KEY_HOST;
import static io.ballerina.persist.PersistToolsConstants.KEY_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.KEY_PORT;
import static io.ballerina.persist.PersistToolsConstants.KEY_USER;
import static io.ballerina.persist.PersistToolsConstants.MYSQL;
import static io.ballerina.persist.PersistToolsConstants.PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.PASSWORD_PLACEHOLDER;
import static io.ballerina.persist.PersistToolsConstants.PORT;
import static io.ballerina.persist.PersistToolsConstants.PORT_PLACEHOLDER;
import static io.ballerina.persist.PersistToolsConstants.USER;
import static io.ballerina.persist.PersistToolsConstants.USER_PLACEHOLDER;


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
    public static SyntaxTree createConfigToml(String name) {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(name, null));
        moduleMembers = populateNodeList(moduleMembers);
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }
    public static SyntaxTree createPesistToml(String name) {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEYWORD_PROVIDER, MYSQL, null));
        moduleMembers = addNewLine(moduleMembers, 1);
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(DATABASE, null));
        moduleMembers = populatePersistNodeList(moduleMembers, name);
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    public static HashMap<String, TableNode> getConfigs(Path configPath)
            throws BalException {
        Path fileNamePath = configPath.getFileName();
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            HashMap<String, TableNode> configs = new HashMap<>();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    String tableName = node.identifier().toSourceCode().trim();
                    configs.put(tableName, node);
                }
            }
            return configs;
        } catch (IOException e) {
            throw new BalException("Error while reading configurations. ");
        }
    }

    public static void  populateConfigurations(ArrayList<String> templatedEntry, HashMap<String, String>
            persistConfigurations, HashMap<String, TableNode> configs) throws BalException {
        for (String configKey : templatedEntry) {
            boolean configExists = false;
            String[] placeHolderValues = persistConfigurations.get(configKey).replaceAll("\"", "").replaceAll("}", "")
                    .split("\\.");
            String relatedKey =  placeHolderValues[placeHolderValues.length - 1];
            String placeHolderTable = persistConfigurations.get(configKey)
                    .replaceAll("\"\\$\\{", "").replaceAll("}\"", "").replaceAll("\\." + relatedKey, "");
            if (configs.containsKey(placeHolderTable)) {
                TableNode tableNode = configs.get(placeHolderTable);
                NodeList<KeyValueNode> subNodeList = tableNode.fields();
                for (KeyValueNode subMember : subNodeList) {
                    if (subMember.identifier().toSourceCode().trim().equals(relatedKey)) {
                        persistConfigurations.put(configKey,
                                subMember.value().toSourceCode().trim());
                        configExists = true;
                    }
                }
            }
            if (!configExists) {
                throw new BalException(
                        String.format("Persist.toml configuration template %s is not found in Config.toml ",
                                persistConfigurations.get(configKey).replaceAll("\"", "")));
            }

        }
    }

    public static HashMap<String, String> readPersistToml(Path configPath) throws BalException {
        HashMap<String, String> values = new HashMap<>();
        Path fileNamePath = configPath.getFileName();
        try {
            boolean persistConfigs = false;
            TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    if (node.identifier().toSourceCode().trim().equals(DATABASE)) {
                        persistConfigs = true;
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
            if (!persistConfigs) {
                throw new BalException("Persist client related config doesn't exist in Persist.toml.\n" +
                        "You should add [database] table with configurations values or placeholders");
            } else if (values.isEmpty() || values.size() < 5 || (!values.containsKey(DATABASE)
                    || !values.containsKey(USER) || !values.containsKey(HOST) || !values.containsKey(PASSWORD) ||
                    !values.containsKey(PORT))) {
                throw new BalException("Database is not configured properly\n" +
                        "You should give the correct database configurations with database name to create tables");
            } else {
                return values;
            }
        } catch (IOException e) {
            throw new BalException("Error while reading configurations. ");
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
                            moduleMembers = moduleMembers.add(subMember);
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

    private static NodeList<DocumentMemberDeclarationNode> populatePersistNodeList(
            NodeList<DocumentMemberDeclarationNode> moduleMembers, String projectName) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_HOST, String.format(HOST_PLACEHOLDER,
                projectName), null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_PORT, String.format(PORT_PLACEHOLDER,
                projectName), null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_USER, String.format(USER_PLACEHOLDER,
                projectName), null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_PASSWORD,
                String.format(PASSWORD_PLACEHOLDER, projectName), null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_DATABASE,
                String.format(DATABASE_PLACEHOLDER, projectName), null));
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
