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
package io.ballerina.persist.nodegenerator;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.sources.DbModelGenSyntaxTree;
import io.ballerina.persist.nodegenerator.syntax.sources.DbSyntaxTree;
import io.ballerina.persist.nodegenerator.syntax.sources.GSheetSyntaxTree;
import io.ballerina.persist.nodegenerator.syntax.sources.InMemorySyntaxTree;
import io.ballerina.persist.nodegenerator.syntax.sources.RedisSyntaxTree;
import io.ballerina.persist.nodegenerator.syntax.utils.AppScriptUtils;
import io.ballerina.persist.nodegenerator.syntax.utils.SqlScriptUtils;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.syntax.tree.TableArrayNode;
import io.ballerina.toml.syntax.tree.TableNode;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.GOOGLE_SHEETS_SCHEMA_FILE;
import static io.ballerina.persist.PersistToolsConstants.SQL_SCHEMA_FILE;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.H2_DB;

/**
 * This class is used to generate the all files to data source type.
 *
 * @since 0.3.1
 */
public class SourceGenerator {

    private static final String persistTypesBal = "persist_types.bal";
    private static final String persistClientBal = "persist_client.bal";
    private static final String persistModelBal = "model.bal";
    private static final String NEW_LINE = System.lineSeparator();
    private final String sourcePath;
    private final String moduleNameWithPackageName;
    private final Path generatedSourceDirPath;
    private final Module entityModule;
    private final boolean eagerLoading;

    public SourceGenerator(String sourcePath, Path generatedSourceDirPath, String moduleNameWithPackageName,
                           Module entityModule) {
        this(sourcePath, generatedSourceDirPath, moduleNameWithPackageName, entityModule, false);
    }

    public SourceGenerator(String sourcePath, Path generatedSourceDirPath, String moduleNameWithPackageName,
                           Module entityModule, boolean eagerLoading) {
        this.sourcePath = sourcePath;
        this.moduleNameWithPackageName = moduleNameWithPackageName;
        this.entityModule = entityModule;
        this.generatedSourceDirPath = generatedSourceDirPath;
        this.eagerLoading = eagerLoading;
    }

    public void createDbModel() throws BalException {
        DbModelGenSyntaxTree dbModelGenSyntaxTree = new DbModelGenSyntaxTree();
        addModelFile(dbModelGenSyntaxTree.getDataModels(entityModule),
                this.generatedSourceDirPath.resolve(persistModelBal).toAbsolutePath(),
                this.moduleNameWithPackageName);
    }

    private void addModelFile(SyntaxTree syntaxTree, Path path, String moduleName) throws BalException {
        try {
            writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), path);
        } catch (FormatterException | IOException e) {
            throw new BalException(String.format("could not write the records for the `%s` data model " +
                    "to the model.bal file.", moduleName) + e.getMessage());
        }
    }

    public void createDbSources(String datasource) throws BalException {
        DbSyntaxTree dbSyntaxTree = new DbSyntaxTree();
        try {
            addDataSourceConfigBalFile(this.generatedSourceDirPath, BalSyntaxConstants.PATH_DB_CONFIGURATION_BAL_FILE,
                    dbSyntaxTree.getDataStoreConfigSyntax(datasource));
            addConfigTomlFile(this.sourcePath, dbSyntaxTree.getConfigTomlSyntax(
                    this.moduleNameWithPackageName, datasource), this.moduleNameWithPackageName);
            addDataTypesBalFile(dbSyntaxTree.getDataTypesSyntax(entityModule),
                    this.generatedSourceDirPath.resolve(persistTypesBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addClientFile(dbSyntaxTree.getClientSyntax(entityModule, datasource, this.eagerLoading),
                    this.generatedSourceDirPath.resolve(persistClientBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addSqlScriptFile(this.entityModule.getModuleName(),
                    SqlScriptUtils.generateSqlScript(this.entityModule.getEntityMap().values(), datasource),
                    generatedSourceDirPath);
        } catch (BalException e) {
            throw new BalException(e.getMessage());
        }
    }

    public void createTestDataSources(String testDatastore) throws BalException {
        if (testDatastore.equals(H2_DB)) {
            DbSyntaxTree dbSyntaxTree = new DbSyntaxTree();
            addClientFile(dbSyntaxTree.getTestClientSyntax(entityModule),
                    this.generatedSourceDirPath.resolve("persist_test_client.bal").toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addTestInitFile(dbSyntaxTree.getTestInitSyntax(SqlScriptUtils.
                            generateSqlScript(this.entityModule.getEntityMap().values(), testDatastore)),
                    this.generatedSourceDirPath.resolve("persist_test_init.bal").toAbsolutePath());
        } else {
            InMemorySyntaxTree inMemorySyntaxTree = new InMemorySyntaxTree();
            addClientFile(inMemorySyntaxTree.getTestClientSyntax(entityModule),
            this.generatedSourceDirPath.resolve("persist_test_client.bal").toAbsolutePath(),
            this.moduleNameWithPackageName);
        }

    }

    public void createRedisSources() throws BalException {
        RedisSyntaxTree redisSyntaxTree = new RedisSyntaxTree();
        try {
            addDataSourceConfigBalFile(this.generatedSourceDirPath, BalSyntaxConstants.PATH_DB_CONFIGURATION_BAL_FILE,
            redisSyntaxTree.getDataStoreConfigSyntax());
            addConfigTomlFile(this.sourcePath, redisSyntaxTree.getConfigTomlSyntax(
                    this.moduleNameWithPackageName), this.moduleNameWithPackageName);
            addDataTypesBalFile(redisSyntaxTree.getDataTypesSyntax(this.entityModule),
                    this.generatedSourceDirPath.resolve(persistTypesBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addClientFile(redisSyntaxTree.getClientSyntax(this.entityModule),
                    this.generatedSourceDirPath.resolve(persistClientBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
        } catch (BalException e) {
            throw new BalException(e.getMessage());
        }
    }

    public void createInMemorySources() throws BalException {
        InMemorySyntaxTree inMemorySyntaxTree = new InMemorySyntaxTree();
        try {
            createGeneratedDirectory(this.generatedSourceDirPath);
            addDataTypesBalFile(inMemorySyntaxTree.getDataTypesSyntax(this.entityModule),
                    this.generatedSourceDirPath.resolve(persistTypesBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addClientFile(inMemorySyntaxTree.getClientSyntax(this.entityModule),
                    this.generatedSourceDirPath.resolve(persistClientBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
        } catch (BalException e) {
            throw new BalException(e.getMessage());
        }
    }

    public void createGSheetSources() throws BalException {
        GSheetSyntaxTree gSheetSyntaxTree = new GSheetSyntaxTree();
        try {
            addDataSourceConfigBalFile(this.generatedSourceDirPath,
                    BalSyntaxConstants.PATH_SHEET_CONFIGURATION_BAL_FILE, gSheetSyntaxTree.getDataStoreConfigSyntax());
            addConfigTomlFile(this.sourcePath, gSheetSyntaxTree.getConfigTomlSyntax(this.moduleNameWithPackageName),
                    this.moduleNameWithPackageName);
            addDataTypesBalFile(gSheetSyntaxTree.getDataTypesSyntax(entityModule),
                    this.generatedSourceDirPath.resolve(persistTypesBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addClientFile(gSheetSyntaxTree.getClientSyntax(entityModule),
                    this.generatedSourceDirPath.resolve(persistClientBal).toAbsolutePath(),
                    this.moduleNameWithPackageName);
            addGoogleScriptFile(this.entityModule.getModuleName(),
                    AppScriptUtils.generateJavaScriptFile(this.entityModule.getEntityMap().values()),
                    generatedSourceDirPath);
        } catch (BalException e) {
            throw new BalException(e.getMessage());
        }
    }
    
    private void addDataSourceConfigBalFile(Path generatedSourceDirPath, String fileName, SyntaxTree syntaxTree)
            throws BalException {
        Path configFilePath = generatedSourceDirPath.resolve(fileName);
        if (!Files.exists(configFilePath)) {
            try {
                writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), configFilePath.toAbsolutePath());
            } catch (Exception e) {
                throw new BalException("failed to generate the persist_db_config.bal file. "
                        + e.getMessage());
            }
        }
    }

    private void addConfigTomlFile(String sourcePath, SyntaxTree syntaxTree, String moduleName)
            throws BalException {
        Path configPath = Paths.get(sourcePath, PersistToolsConstants.CONFIG_SCRIPT_FILE).toAbsolutePath();
        try {
            if (!Files.exists(configPath.toAbsolutePath())) {
                writeOutputFile(syntaxTree.toSourceCode(), configPath);
            } else {
                writeOutputFile(getUpdateConfigTomlSyntax(configPath, moduleName, syntaxTree).toSourceCode(),
                        configPath);
            }
        } catch (IOException e) {
            throw new BalException("could not update Config.toml file inside the Ballerina project. " + e.getMessage());
        }
    }

    private void addDataTypesBalFile(SyntaxTree syntaxTree, Path path, String moduleName) throws BalException {
        try {
            writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), path);
        } catch (FormatterException | IOException e) {
            throw new BalException(String.format("could not write the type code for the `%s` data model " +
                    "to the persist_types.bal file.", moduleName) + e.getMessage());
        }
    }

    private void addClientFile(SyntaxTree syntaxTree, Path path, String moduleName) throws BalException {
        try {
            writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), path);
        } catch (FormatterException | IOException e) {
            throw new BalException(String.format("could not write the client code for the `%s` data model " +
                    "to the persist_client.bal file.", moduleName) + e.getMessage());
        }
    }

    private void addTestInitFile(SyntaxTree syntaxTree, Path path) throws BalException {
        try {
            writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), path);
        } catch (FormatterException | IOException e) {
            throw new BalException(String.format("could not write the db initialization scripts to the `%s` file. %s",
                    path.getFileName(), e.getMessage()));
        }
    }
    
    private void createGeneratedDirectory(Path path) throws BalException {
        if (Objects.nonNull(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new BalException(String.format("could not create the parent directories of output path %s. %s",
                                path, e.getMessage()));
            }
        }
    }

    private void writeOutputFile(String syntaxTree, Path outPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(outPath.toString(), StandardCharsets.UTF_8)) {
            writer.println(syntaxTree);
        }
    }

    public static void addSqlScriptFile(String moduleName, String[] sqlScripts, Path filePath) throws BalException {
        Path path = Paths.get(String.valueOf(filePath), SQL_SCHEMA_FILE);
        StringBuilder sqlScript = new StringBuilder();
        sqlScript.append(PersistToolsConstants.SqlScriptComments.AUTOGENERATED_FILE_COMMENT).append(NEW_LINE)
                .append(NEW_LINE);
        sqlScript.append(String.format(PersistToolsConstants.SqlScriptComments.AUTO_GENERATED_COMMENT_WITH_REASON,
                moduleName)).append(NEW_LINE);
        sqlScript.append(PersistToolsConstants.SqlScriptComments.COMMENT_SHOULD_BE_VERIFIED_AND_EXECUTED)
                .append(NEW_LINE).append(NEW_LINE);
        for (String script : sqlScripts) {
            sqlScript.append(script).append(NEW_LINE);
        }
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.writeString(path, sqlScript);
        } catch (IOException e) {
            throw new BalException(String.format("could not write the SQL script to the %s file. %s",
                    SQL_SCHEMA_FILE, e.getMessage()));
        }
    }

    private static void addGoogleScriptFile(String moduleName, String gsScripts, Path filePath) throws BalException {
        Path path = Paths.get(String.valueOf(filePath), GOOGLE_SHEETS_SCHEMA_FILE);
        StringBuilder gsScript = new StringBuilder();
        gsScript.append(PersistToolsConstants.AppScriptComments.AUTOGENERATED_FILE_COMMENT).append(NEW_LINE)
                .append(NEW_LINE);
        gsScript.append(String.format(PersistToolsConstants.AppScriptComments.AUTO_GENERATED_COMMENT_WITH_REASON,
                moduleName)).append(NEW_LINE);
        gsScript.append(PersistToolsConstants.AppScriptComments.COMMENT_SHOULD_BE_VERIFIED_AND_EXECUTED)
                .append(NEW_LINE).append(NEW_LINE);
            gsScript.append(gsScripts);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.writeString(path, gsScript);
        } catch (IOException e) {
            throw new BalException(String.format("could not write the google AppScript code to the %s file. %s",
                    GOOGLE_SHEETS_SCHEMA_FILE, e.getMessage()));
        }
    }

    private SyntaxTree getUpdateConfigTomlSyntax(Path configPath, String moduleName, SyntaxTree newConfigSyntaxTree)
            throws IOException {
        boolean configExists = false;
        io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> moduleMembers =
                io.ballerina.toml.syntax.tree.AbstractNodeFactory.createEmptyNodeList();
        Path fileNamePath = configPath.getFileName();
        TextDocument configDocument = TextDocuments.from(Files.readString(configPath));
        if (Objects.nonNull(fileNamePath)) {
            io.ballerina.toml.syntax.tree.SyntaxTree syntaxTree = io.ballerina.toml.syntax.tree.SyntaxTree.from(
                    configDocument, fileNamePath.toString());
            DocumentNode rootNote = syntaxTree.rootNode();
            io.ballerina.toml.syntax.tree.NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();

            for (DocumentMemberDeclarationNode member : nodeList) {
                if (member instanceof KeyValueNode) {
                    moduleMembers = moduleMembers.add(member);
                } else if (member instanceof TableNode node) {
                    moduleMembers = moduleMembers.add(member);
                    if (node.identifier().toSourceCode().trim().equals(moduleName)) {
                        configExists = true;
                    }
                } else if (member instanceof TableArrayNode) {
                    moduleMembers = moduleMembers.add(member);
                }
            }
        }
        io.ballerina.toml.syntax.tree.Token eofToken = io.ballerina.toml.syntax.tree.AbstractNodeFactory.
                createIdentifierToken("");
        DocumentNode documentNode = io.ballerina.toml.syntax.tree.NodeFactory.createDocumentNode(
                moduleMembers, eofToken);
        TextDocument textDocument;
        if (!configExists) {
            textDocument = TextDocuments.from(documentNode.toSourceCode() + NEW_LINE +
                    newConfigSyntaxTree.toSourceCode());
        } else {
            textDocument = TextDocuments.from(documentNode.toSourceCode());
        }
        return SyntaxTree.from(textDocument);
    }
}
