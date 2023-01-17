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

import io.ballerina.persist.BalException;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.toml.syntax.tree.AbstractNodeFactory;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
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
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_HOST;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PORT;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PROVIDER;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_USER;
import static io.ballerina.persist.PersistToolsConstants.KEY_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.KEY_HOST;
import static io.ballerina.persist.PersistToolsConstants.KEY_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.KEY_PORT;
import static io.ballerina.persist.PersistToolsConstants.KEY_PROVIDER;
import static io.ballerina.persist.PersistToolsConstants.KEY_USER;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.SUPPORTED_DB_PROVIDERS;


/**
 * Class to create syntax tree for Config.toml.
 *
 * @since 0.1.0
 */
public class TomlSyntaxGenerator {

    public static final String REGEX_TOML_TABLE_NAME_SPLITTER = "\\.";

    private TomlSyntaxGenerator() {
    }

    /**
     * Method to create a new Config.toml file with database configurations.
     */

    public static SyntaxTree createConfigToml(ArrayList<String> schemas, String packageName) {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        for (String schema : schemas) {
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(packageName + "." + schema, null));
            moduleMembers = populateConfigNodeList(moduleMembers, false);
            moduleMembers = addNewLine(moduleMembers, 1);
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    public static PersistConfiguration readPersistToml(Path configPath) throws BalException {
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument);
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            PersistConfiguration configuration = new PersistConfiguration();
            boolean dbConfigExists = false;
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    String tableName = node.identifier().toSourceCode().trim();
                    if (tableName.startsWith(DATABASE)) {
                        String[] nameParts = tableName.split(REGEX_TOML_TABLE_NAME_SPLITTER);
                        if (nameParts.length > 1 && SUPPORTED_DB_PROVIDERS.contains(nameParts[1])) {
                            configuration.setProvider(nameParts[1]);
                            dbConfigExists = true;
                            if (nameParts.length == 3 && "shadow".equals(nameParts[2])) {
                                DatabaseConfiguration shadowDbConfiguration = new DatabaseConfiguration(node.fields());
                                configuration.setShadowDbConfig(shadowDbConfiguration);
                            } else {
                                DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(node.fields());
                                configuration.setDbConfig(databaseConfiguration);
                            }
                        } else {
                            throw new BalException("Database is not configured properly\n" +
                                    "You should give the correct database configurations " +
                                    "with database name to create tables.");
                        }
                    }

                }
            }
            if (!dbConfigExists) {
                throw new BalException("The persist tool config doesn't exist in the Persist.toml.\n" +
                        "You should add [database.<provide>] table with db configurations.");
            }
            return configuration;
        } catch (IOException e) {
            throw new BalException("Error while reading configurations. " + e.getMessage());
        }
    }

    /**
     * Method to update the Config.toml with database configurations.
     */
    public static SyntaxTree updateBallerinaToml(Path configPath, ArrayList<String> names) throws IOException {

        ArrayList<String> existingNodes = new ArrayList<>();

        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        if (Objects.nonNull(fileNamePath)) {
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();

            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof KeyValueNode) {
                    moduleMembers = moduleMembers.add(member);
                } else if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    for (String schema : names) {
                        if (node.identifier().toSourceCode().trim().equals(PERSIST_DIRECTORY + "."
                                + schema)) {
                            existingNodes.add(schema);
                            break;
                        }
                    }
                    moduleMembers = moduleMembers.add(member);
                } else if (member instanceof TableArrayNode) {
                    moduleMembers = moduleMembers.add(member);
                }
            }
            if (existingNodes.size() != names.size()) {
                for (String schema : names) {
                    moduleMembers = addNewLine(moduleMembers, 1);
                    if (existingNodes.contains(schema)) {
                        continue;
                    }
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(PERSIST_DIRECTORY + "."
                            + schema, null));
                    moduleMembers = populateConfigNodeList(moduleMembers, true);
                }
            }
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    public static SyntaxTree updateConfigToml(Path configPath, ArrayList<String> names, String packageName)
            throws IOException {

        ArrayList<String> existingNodes = new ArrayList<>();

        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        if (Objects.nonNull(fileNamePath)) {
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();

            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof KeyValueNode) {
                    moduleMembers = moduleMembers.add(member);
                } else if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    for (String schema : names) {
                        if (node.identifier().toSourceCode().trim().equals(packageName + "."
                                + schema)) {
                            existingNodes.add(schema);
                            break;
                        }
                    }
                    moduleMembers = moduleMembers.add(member);
                } else if (member instanceof TableArrayNode) {
                    moduleMembers = moduleMembers.add(member);
                }
            }
            if (existingNodes.size() != names.size()) {
                for (String schema : names) {
                    moduleMembers = addNewLine(moduleMembers, 1);
                    if (existingNodes.contains(schema)) {
                        continue;
                    }
                    moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(packageName + "."
                            + schema, null));
                    moduleMembers = populateConfigNodeList(moduleMembers, false);
                }
            }
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument);
    }

    private static NodeList<DocumentMemberDeclarationNode> populateConfigNodeList(
            NodeList<DocumentMemberDeclarationNode> moduleMembers, boolean includeProvider) {
        if (includeProvider) {
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_PROVIDER, DEFAULT_PROVIDER, null));
        }
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
}
