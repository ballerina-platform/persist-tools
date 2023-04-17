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
import io.ballerina.persist.PersistToolsConstants;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import static io.ballerina.persist.PersistToolsConstants.DEFAULT_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_HOST;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_PORT;
import static io.ballerina.persist.PersistToolsConstants.DEFAULT_USER;
import static io.ballerina.persist.PersistToolsConstants.KEYWORD_NAME;
import static io.ballerina.persist.PersistToolsConstants.KEYWORD_PACKAGE;
import static io.ballerina.persist.PersistToolsConstants.KEY_DATABASE;
import static io.ballerina.persist.PersistToolsConstants.KEY_HOST;
import static io.ballerina.persist.PersistToolsConstants.KEY_PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.KEY_PORT;
import static io.ballerina.persist.PersistToolsConstants.KEY_USER;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_CONFIG_PATTERN;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.SUPPORTED_DB_PROVIDERS;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

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

    public static String createConfigToml(String moduleName) {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(moduleName, null));
        moduleMembers = populateConfigNodeList(moduleMembers);
        moduleMembers = addNewLine(moduleMembers, 1);
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument).toSourceCode();
    }

    public static PersistConfiguration readDatabaseConfigurations(Path configPath)
            throws BalException {
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
                    if (tableName.startsWith(PERSIST_CONFIG_PATTERN)) {
                        String[] nameParts = tableName.split(REGEX_TOML_TABLE_NAME_SPLITTER);
                        if (nameParts.length > 3 && SUPPORTED_DB_PROVIDERS.contains(nameParts[3])) {
                            configuration.setProvider(nameParts[3]);
                            dbConfigExists = true;
                            DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(
                                    "model", node.fields());
                            configuration.setDbConfig(databaseConfiguration);
                        } else {
                            throw new BalException("database is not configured properly. " +
                                    "give correct database configurations with " +
                                    "database name to create tables.");
                        }
                    }

                }
            }
            if (!dbConfigExists) {
                throw new BalException("the persist tool config doesn't exist in the Ballerina.toml. " +
                        "add [persist.model.storage.<provider>] table with db configurations.");
            }
            return configuration;
        } catch (IOException e) {
            throw new BalException("error while reading configurations. " + e.getMessage());
        }
    }

    public static HashMap<String, String> readPersistConfigurations(Path configPath)
            throws BalException {
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument);
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            boolean dbConfigExists = false;
            HashMap<String, String> persistConfig = new HashMap<>();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    String tableName = node.identifier().toSourceCode().trim();
                    if (tableName.equals(PERSIST_DIRECTORY)) {
                        dbConfigExists = true;
                        for (KeyValueNode field : node.fields()) {
                            persistConfig.put(field.identifier().toSourceCode().trim(),
                                    field.value().toSourceCode().trim().replaceAll("\"", ""));
                        }
                    }

                }
            }
            if (!dbConfigExists) {
                throw new BalException("the persist config doesn't exist in the Ballerina.toml. " +
                        "add [persist] table with persist configurations.");
            } else if (!persistConfig.containsKey("module") || !persistConfig.containsKey("datastore")) {
                throw new BalException("the persist configurations does not exist under [persist] table.");
            }
            return persistConfig;
        } catch (IOException e) {
            throw new BalException("error while reading persist configurations. " + e.getMessage());
        }
    }


    public static String readPackageName(String sourcePath) throws BalException {
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(Paths.get(sourcePath, BALLERINA_TOML)));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument);
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    String tableName = node.identifier().toSourceCode().trim();
                    if (tableName.equals(KEYWORD_PACKAGE)) {
                        NodeList<KeyValueNode> fields = node.fields();
                        for (KeyValueNode field : fields) {
                            if (field.identifier().toSourceCode().trim()
                                    .equals(KEYWORD_NAME)) {
                                return field.value().toSourceCode().trim().replaceAll("\"", "");
                            }
                        }
                    }

                }
            }
            throw new BalException("ERROR: couldn't find the package name in Ballerina.toml file.");
        } catch (IOException e) {
            throw new BalException("ERROR: couldn't read the Ballerina.toml file. " + e.getMessage());
        }
    }

    /**
     * Method to update the Ballerina.toml with database configurations and persist dependency.
     */
    public static String updateBallerinaToml(Path configPath, String module, String datasource) throws IOException,
            BalException {
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        if (Objects.nonNull(fileNamePath)) {
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();

            for (DocumentMemberDeclarationNode member : nodeList) {
                boolean dependencyExists = false;
                if (member instanceof KeyValueNode) {
                    moduleMembers = moduleMembers.add(member);
                } else if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    if (node.identifier().toSourceCode().trim().equals(PERSIST_DIRECTORY)) {
                        throw new BalException("persist configuration already exists in the Ballerina.toml. " +
                                "remove the existing configuration and try again.");
                    } else {
                        moduleMembers = moduleMembers.add(member);
                    }
                } else if (member instanceof TableArrayNode) {
                    TableArrayNode tableArray = (TableArrayNode) member;
                    if (tableArray.identifier().toSourceCode().trim().equals("platform.java11.dependency")) {
                        NodeList<KeyValueNode> fields = ((TableArrayNode) member).fields();
                        for (KeyValueNode field : fields) {
                            String value = field.value().toSourceCode().trim();
                            if (field.identifier().toSourceCode().trim().equals(
                                    PersistToolsConstants.TomlFileConstants.KEYWORD_ARTIFACT_ID) &&
                                    (value).substring(1, value.length() - 1).equals(
                                            PersistToolsConstants.TomlFileConstants.ARTIFACT_ID)) {
                                dependencyExists = true;
                                break;
                            }
                        }
                    }
                    if (!dependencyExists) {
                        moduleMembers = moduleMembers.add(member);
                    }
                }
            }
            moduleMembers = addNewLine(moduleMembers, 1);
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(
                    PERSIST_DIRECTORY, null));
            moduleMembers = populateBallerinaNodeList(moduleMembers, module, datasource);
            moduleMembers = addNewLine(moduleMembers, 1);
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createTableArray(
                    BalSyntaxConstants.PERSIST_DEPENDENCY, null));
            moduleMembers = populatePersistDependency(moduleMembers);
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument).toSourceCode();
    }

    public static String updateConfigToml(Path configPath, String moduleName)
            throws IOException {
        boolean configExists = false;
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
                    moduleMembers = moduleMembers.add(member);
                    if (node.identifier().toSourceCode().trim().equals(moduleName)) {
                        configExists = true;
                        break;
                    }
                } else if (member instanceof TableArrayNode) {
                    moduleMembers = moduleMembers.add(member);
                }
            }
            if (!configExists) {
                moduleMembers = addNewLine(moduleMembers, 1);
                moduleMembers = moduleMembers.add(SampleNodeGenerator.createTable(moduleName, null));
                moduleMembers = populateConfigNodeList(moduleMembers);
            }
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument).toSourceCode();
    }

    private static NodeList<DocumentMemberDeclarationNode> populateConfigNodeList(
            NodeList<DocumentMemberDeclarationNode> moduleMembers) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_HOST, DEFAULT_HOST, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createNumericKV(KEY_PORT, DEFAULT_PORT, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_USER, DEFAULT_USER, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_PASSWORD, DEFAULT_PASSWORD, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(KEY_DATABASE, DEFAULT_DATABASE, null));
        return moduleMembers;
    }

    private static NodeList<DocumentMemberDeclarationNode> populateBallerinaNodeList(
            NodeList<DocumentMemberDeclarationNode> moduleMembers, String module, String dataStore) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("datastore", dataStore, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("module", module, null));
        return moduleMembers;
    }

    private static NodeList<DocumentMemberDeclarationNode> populatePersistDependency(
            NodeList<DocumentMemberDeclarationNode> moduleMembers) throws BalException {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.TomlFileConstants.KEYWORD_GROUP_ID,
                PersistToolsConstants.TomlFileConstants.PERSIST_GROUP_ID, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.TomlFileConstants.KEYWORD_ARTIFACT_ID,
                PersistToolsConstants.TomlFileConstants.ARTIFACT_ID, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.TomlFileConstants.KEYWORD_VERSION,
                getPersistVersion(), null));
        return moduleMembers;
    }

    private static String getPersistVersion() throws BalException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream(
                PersistToolsConstants.TomlFileConstants.VERSION_PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.get(PersistToolsConstants.TomlFileConstants.PERSIST_VERSION).toString();
        } catch (IOException e) {
            throw new BalException("ERROR: couldn't read the version.properties file. " + e.getMessage());
        }
    }

    private static NodeList<DocumentMemberDeclarationNode> addNewLine(NodeList moduleMembers, int n) {
        for (int i = 0; i < n; i++) {
            moduleMembers = moduleMembers.add(AbstractNodeFactory.createIdentifierToken(System.lineSeparator()));
        }
        return moduleMembers;
    }
}
