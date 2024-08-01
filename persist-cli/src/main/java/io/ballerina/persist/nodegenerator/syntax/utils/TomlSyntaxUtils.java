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

package io.ballerina.persist.nodegenerator.syntax.utils;

import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.projects.util.ProjectConstants;
import io.ballerina.toml.syntax.tree.AbstractNodeFactory;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.syntax.tree.NodeList;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import io.ballerina.toml.syntax.tree.TableArrayNode;
import io.ballerina.toml.syntax.tree.TableNode;
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

/**
 * Class to create syntax tree for Config.toml.
 *
 * @since 0.1.0
 */
public class TomlSyntaxUtils {

    public static final String REGEX_TOML_TABLE_NAME_SPLITTER = "\\.";

    private TomlSyntaxUtils() {
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
                    if (tableName.startsWith(PersistToolsConstants.PERSIST_CONFIG_PATTERN)) {
                        String[] nameParts = tableName.split(REGEX_TOML_TABLE_NAME_SPLITTER);
                        if (nameParts.length > 3 && PersistToolsConstants.SUPPORTED_DB_PROVIDERS.
                                contains(nameParts[3])) {
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

    public static HashMap<String, String> readBallerinaTomlConfig(Path configPath) throws BalException {
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument);
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            boolean dbConfigExists = false;
            HashMap<String, String> persistConfig = new HashMap<>();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableArrayNode node) {
                    String tableName = node.identifier().toSourceCode().trim();
                    if (tableName.equals(PersistToolsConstants.PERSIST_TOOL_CONFIG)) {
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
                        "add [tool.persist] table with persist configurations.");
            } else if (!persistConfig.containsKey("targetModule") || !persistConfig.containsKey("options.datastore")) {
                throw new BalException("the persist configurations does not exist under [persist] table.");
            }
            return persistConfig;
        } catch (IOException e) {
            throw new BalException("error while reading persist configurations. " + e.getMessage());
        }
    }

    public static String readPackageName(String sourcePath) throws BalException {
        try {
            TextDocument configDocument = TextDocuments.from(Files.readString(Paths.get(sourcePath,
                    ProjectConstants.BALLERINA_TOML)));
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument);
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableNode) {
                    TableNode node = (TableNode) member;
                    String tableName = node.identifier().toSourceCode().trim();
                    if (tableName.equals(PersistToolsConstants.KEYWORD_PACKAGE)) {
                        NodeList<KeyValueNode> fields = node.fields();
                        for (KeyValueNode field : fields) {
                            if (field.identifier().toSourceCode().trim()
                                    .equals(PersistToolsConstants.KEYWORD_NAME)) {
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

    public static ConfigDeclaration getConfigDeclaration(Path configPath, NativeDependency dependency)
            throws IOException {
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        NodeList<DocumentMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        TableArrayNode dependencyNode = null;
        TableArrayNode testDependencyNode = null;
        boolean persistConfigExists = false;
        if (Objects.nonNull(fileNamePath)) {
            SyntaxTree syntaxTree = SyntaxTree.from(configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof TableArrayNode node) {
                    if (node.identifier().toSourceCode().trim().equals(PersistToolsConstants.PERSIST_TOOL_CONFIG)) {
                        persistConfigExists = true;
                    } else if (node.identifier().toSourceCode().trim().equals("platform.java17.dependency")) {
                        NodeList<KeyValueNode> fields = ((TableArrayNode) member).fields();
                        for (KeyValueNode field : fields) {
                            String value = field.value().toSourceCode().trim();
                            if (field.identifier().toSourceCode().trim().equals(
                                    PersistToolsConstants.TomlFileConstants.KEYWORD_ARTIFACT_ID)) {
                                if (value.substring(1, value.length() - 1).equals(
                                        String.format(PersistToolsConstants.TomlFileConstants.ARTIFACT_ID,
                                                dependency.artifactId()))) {
                                    dependencyNode = node;
                                }
                                if (dependency.testArtifactId() != null &&
                                        value.substring(1, value.length() - 1).equals(
                                                String.format(PersistToolsConstants.TomlFileConstants.ARTIFACT_ID,
                                                dependency.testArtifactId()))) {
                                    testDependencyNode = node;
                                }
                            }
                            if (dependencyNode != null &&
                                    ((dependency.testArtifactId() != null) == (testDependencyNode != null))) {
                                break;
                            }
                        }
                    }
                    moduleMembers = moduleMembers.add(member);
                } else {
                    moduleMembers = moduleMembers.add(member);
                }
            }
        }
        return new ConfigDeclaration(moduleMembers, dependencyNode, testDependencyNode, persistConfigExists);
    }

    public record ConfigDeclaration(
            NodeList<DocumentMemberDeclarationNode> moduleMembers,
            TableArrayNode dependencyNode,
            TableArrayNode testDependencyNode,
            boolean persistConfigExists) {
    }

    public static NativeDependency getDependencyConfig(String datasource, String testDatasource) {
        String artifactId = BalSyntaxConstants.PERSIST_MODULE + "." + datasource;
        if (datasource.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB) ||
                datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB) ||
                datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB) ||
                datasource.equals(PersistToolsConstants.SupportedDataSources.H2_DB)) {
            artifactId = BalSyntaxConstants.PERSIST_MODULE + "." + BalSyntaxConstants.SQL;
        }
        String testArtifactId = null;
        if (Objects.nonNull(testDatasource)) {
            testArtifactId = PersistToolsConstants.SupportedDataSources.H2_DB.equals(testDatasource) ?
                    BalSyntaxConstants.PERSIST_MODULE + "." + BalSyntaxConstants.SQL :
                    BalSyntaxConstants.PERSIST_MODULE + "." + testDatasource;
        }
        return new NativeDependency(artifactId, testArtifactId);
    }

    public record NativeDependency(String artifactId, String testArtifactId) {
    }

    public static void validateDependency(TableArrayNode node, String datasource)
            throws BalException {
        NodeList<KeyValueNode> fields = node.fields();
        for (KeyValueNode field : fields) {
            String value = field.value().toSourceCode().trim().replaceAll("\"", "");
            if (field.identifier().toSourceCode().trim().equals(
                    PersistToolsConstants.TomlFileConstants.KEYWORD_VERSION) &&
                    !(value.equals(getPersistVersion(datasource)))) {
                throw new BalException("the 'Ballerina.toml' file is already updated with the Persist client native " +
                        "dependency but the version is different from the current version. Please remove the " +
                        "existing dependency and try again.");
            }
        }
    }

    public static NodeList<DocumentMemberDeclarationNode> populatePersistDependency(
            NodeList<DocumentMemberDeclarationNode> moduleMembers, String artifactID, String datasource)
            throws BalException {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.TomlFileConstants.KEYWORD_GROUP_ID,
                datasource.equals(PersistToolsConstants.SupportedDataSources.REDIS)
                ?  PersistToolsConstants.TomlFileConstants.PERSIST_LIB_GROUP_ID
                : PersistToolsConstants.TomlFileConstants.PERSIST_GROUP_ID, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.TomlFileConstants.KEYWORD_ARTIFACT_ID,
                String.format(PersistToolsConstants.TomlFileConstants.ARTIFACT_ID, artifactID), null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV(
                PersistToolsConstants.TomlFileConstants.KEYWORD_VERSION,
                getPersistVersion(datasource), null));
        return moduleMembers;
    }

    private static String getPersistVersion(String datasource) throws BalException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream(
                PersistToolsConstants.TomlFileConstants.VERSION_PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return switch (datasource) {
                case PersistToolsConstants.SupportedDataSources.MYSQL_DB,
                        PersistToolsConstants.SupportedDataSources.MSSQL_DB,
                        PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB,
                        PersistToolsConstants.SupportedDataSources.H2_DB ->
                        properties.get(PersistToolsConstants.TomlFileConstants.PERSIST_SQL_VERSION).toString();
                case PersistToolsConstants.SupportedDataSources.IN_MEMORY_TABLE ->
                        properties.get(PersistToolsConstants.TomlFileConstants.PERSIST_IN_MEMORY_VERSION).toString();
                case PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS ->
                        properties.get(
                                PersistToolsConstants.TomlFileConstants.PERSIST_GOOGLE_SHEETS_VERSION).toString();
                case PersistToolsConstants.SupportedDataSources.REDIS ->
                        properties.get(PersistToolsConstants.TomlFileConstants.PERSIST_REDIS_VERSION).toString();
                default -> throw new BalException("ERROR: invalid datasource: " + datasource);
            };
        } catch (IOException e) {
            throw new BalException("ERROR: couldn't read the version.properties file. " + e.getMessage());
        }
    }
}
