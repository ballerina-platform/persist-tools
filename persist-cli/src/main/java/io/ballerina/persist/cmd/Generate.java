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
package io.ballerina.persist.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.NodeList;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import io.ballerina.toml.syntax.tree.TableNode;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.getConfigDeclaration;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.getDependencyConfig;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.populateNativeDependencyConfig;
import static io.ballerina.persist.utils.BalProjectUtils.printTestClientUsageSteps;
import static io.ballerina.persist.utils.BalProjectUtils.validateDatastore;
import static io.ballerina.persist.utils.BalProjectUtils.validateTestDatastore;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

/**
 * This Class implements the `persist generate` command in Ballerina persist-tool.
 *
 * @since 0.1.0
 */
@CommandLine.Command(
        name = "generate",
        description = "Generate Ballerina client object for the entity."
)

public class Generate implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-generate";

    public Generate() {
        this("");
    }

    public Generate(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--module"})
    private String module;

    @CommandLine.Option(names = {"--datastore"})
    private String datastore;

    @CommandLine.Option(names = {"--test-datastore"}, description = "Test data store for the " +
            "generated Ballerina client")
    private String testDatastore;

    @CommandLine.Option(names = {"--eager-loading"}, hidden = true, description = "Enable eager loading to return arrays " +
            "instead of streams for get-all methods")
    private boolean eagerLoading;

    @Override
    public void execute() {
        Path generatedSourceDirPath;
        Module entityModule;
        Path schemaFilePath;
        String packageName;
        String moduleNameWithPackage;
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER,
                    Generate.class.getClassLoader());
            errStream.println(commandUsageInfo);
            return;
        }
        if (Objects.isNull(datastore) || datastore.isBlank()) {
            errStream.println("ERROR: datastore is required");
            return;
        } else {
            try {
                validateDatastore(datastore);
            } catch (BalException e) {
                errStream.printf("ERROR: %s%n", e.getMessage());
                return;
            }
        }
        try {
            validateTestDatastore(datastore, testDatastore);
        } catch (BalException e) {
            errStream.printf("ERROR: %s%n", e.getMessage());
            return;
        }

        // Validate eager loading is only used with SQL datastores
        eagerLoading = Utils.validateEagerLoading(datastore, eagerLoading, errStream);

        Path projectPath = Paths.get(sourcePath);
        try {
            BalProjectUtils.validateBallerinaProject(projectPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            packageName = TomlSyntaxUtils.readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        if (module == null) {
            generatedSourceDirPath = Paths.get(this.sourcePath);
            module = packageName;
        } else {
            generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.MODULES_SOURCE_DIRECTORY);
            module = module.replaceAll("\"", "");
        }
        moduleNameWithPackage = (packageName.equals(module)) ? packageName : packageName + "." + module;

        boolean hasPersistConfig;
        try {
            hasPersistConfig = hasPersistConfig(Paths.get(this.sourcePath, "Ballerina.toml"));
        } catch (BalException e) {
            errStream.println("ERROR: The project does not contain a toml file");
            return;
        }
        // Check if there are previous persist configurations.
        if (hasPersistConfig) {
            errStream.println("The behavior of the `bal persist generate` command has been updated starting " +
                    "from Ballerina update 09.");
            errStream.println("You now have the following options for code generation:");
            errStream.println(System.lineSeparator() + "- `bal persist add --datastore <datastore> --module <module>`" +
                    ": This command adds an entry to \"Ballerina.toml\" to integrate code generation with the " +
                    "package build process.");
            errStream.println("- `bal persist generate --datastore <datastore> --module <module>`: This command " +
                    "performs a one-time generation of the client.");
            errStream.println(System.lineSeparator() + "If you choose to proceed with the `bal persist generate` " +
                    "command, please follow these steps:");
            errStream.println(System.lineSeparator() + "1. Remove the [persist] configuration in the " +
                    "\"Ballerina.toml\" file.");
            errStream.println("2. Delete any previously generated source files, if they exist.");
            errStream.println("3. Re-execute the `bal persist generate --datastore <datastore> --module <module>`" +
                    " command.");
            errStream.println(System.lineSeparator() + "If you have any questions or need further assistance, refer" +
                    " to the updated documentation.");
            return;
        }
        if (!ProjectUtils.validateModuleName(moduleNameWithPackage)) {
            errStream.println("ERROR: invalid module name : '" + module + "' :\n" +
                    "module name can only contain alphanumerics, underscores and periods");
            return;
        } else if (!ProjectUtils.validateNameLength(moduleNameWithPackage)) {
            errStream.println("ERROR: invalid module name : '" + module + "' :\n" +
                    "maximum length of module name is 256 characters");
            return;
        }
        if (!module.equals(packageName)) {
            generatedSourceDirPath = generatedSourceDirPath.resolve(module);
        }

        if (Files.isDirectory(Paths.get(sourcePath, PersistToolsConstants.PERSIST_TOOL_CONFIG,
                PersistToolsConstants.MIGRATIONS)) &&
                !datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
            errStream.println("ERROR: regenerating the client with a different datastore after executing " +
                    "the migrate command is not permitted. please remove the migrations directory within the " +
                    "persist directory and try executing the command again.");
            return;
        }

        if (datastore.equals(PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS)) {
            errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for Google Sheets data store " +
                    "is currently an experimental feature, and its behavior may be subject to change in future " +
                    "releases." + System.lineSeparator());
        }

        if (datastore.equals(PersistToolsConstants.SupportedDataSources.REDIS)) {
            errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for Redis data store " +
                    "is currently an experimental feature, and its behavior may be subject to change in future " +
                    "releases." + System.lineSeparator());
        }

        try {
            schemaFilePath =  BalProjectUtils.getSchemaFilePath(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            BalProjectUtils.updateToml(sourcePath, datastore, moduleNameWithPackage);
            String syntaxTree = updateBallerinaToml(Paths.get(this.sourcePath, BALLERINA_TOML),
                    datastore, testDatastore);
            Utils.writeOutputString(syntaxTree,
                    Paths.get(this.sourcePath, BALLERINA_TOML).toAbsolutePath().toString());
            BalProjectUtils.validateSchemaFile(schemaFilePath);
            Module module = BalProjectUtils.getEntities(schemaFilePath);
            if (module.getEntityMap().isEmpty()) {
                errStream.printf("ERROR: the model definition file(%s) does not contain any entity definition.%n",
                        schemaFilePath.getFileName());
                return;
            }
            entityModule = module;
        } catch (BalException | IOException e) {
            errStream.printf("ERROR: Failed to generate types and client for the definition file(%s). %s%n",
                    schemaFilePath.getFileName(), e.getMessage());
            return;
        }

        if (!Files.exists(generatedSourceDirPath)) {
            try {
                Files.createDirectories(generatedSourceDirPath.toAbsolutePath());
            } catch (IOException e) {
                errStream.println("ERROR: failed to create the generated directory. " + e.getMessage());
                return;
            }
        }
        SourceGenerator sourceCreator = new SourceGenerator(sourcePath, generatedSourceDirPath,
                moduleNameWithPackage, entityModule, eagerLoading);
        try {
            switch (datastore) {
                case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                case PersistToolsConstants.SupportedDataSources.H2_DB:
                    sourceCreator.createDbSources(datastore);
                    break;
                case PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS:
                    sourceCreator.createGSheetSources();
                    break;
                case PersistToolsConstants.SupportedDataSources.REDIS:
                    sourceCreator.createRedisSources();
                    break;
                default:
                    sourceCreator.createInMemorySources();
                    break;
            }
        } catch (BalException e) {
            errStream.printf(String.format(BalSyntaxConstants.ERROR_MSG,
                    datastore, e.getMessage()));
            return;
        }
        errStream.println("Persist client and entity types generated successfully in the " + module +  " directory.");

        if (testDatastore != null) {
            try {
                sourceCreator.createTestDataSources(testDatastore);
            } catch (BalException e) {
                errStream.printf("ERROR: the test data source creation failed. %s%n", e.getMessage());
                return;
            }
            errStream.printf("The test client for the %s datastore is successfully generated in the %s module.%n",
                    testDatastore, module);
            printTestClientUsageSteps(testDatastore, packageName, module);
        }
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {

    }

    /**
     * Method to update the Ballerina.toml with persist native dependency.
     */
    private String updateBallerinaToml(Path tomlPath, String datastore, String testDatastore)
            throws BalException, IOException {
        TomlSyntaxUtils.NativeDependency dependency = getDependencyConfig(datastore, testDatastore);
        TomlSyntaxUtils.ConfigDeclaration declaration = getConfigDeclaration(tomlPath, dependency);
        if (declaration.persistConfigExists()) {
            throw new BalException("persist configuration already exists in the Ballerina.toml. " +
                    "remove the existing configuration and try again.");
        }

        return populateNativeDependencyConfig(datastore, testDatastore, declaration, dependency);
    }

    @Override
    public String getName() {
        return PersistToolsConstants.COMPONENT_IDENTIFIER;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Generate Client objects corresponding to persist entities").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + PersistToolsConstants.COMPONENT_IDENTIFIER + " generate").
                append(System.lineSeparator());
    }

    public static boolean hasPersistConfig(Path configPath) throws BalException {
        TextDocument configDocument = null;
        try {
            configDocument = TextDocuments.from(Files.readString(configPath));
        } catch (IOException e) {
            errStream.println("ERROR: failed to read the toml file: " + e.getMessage());
        }
        SyntaxTree syntaxTree = SyntaxTree.from(configDocument);
        DocumentNode rootNote = syntaxTree.rootNode();
        NodeList<DocumentMemberDeclarationNode> nodeList = rootNote.members();
        boolean dbConfigExists = false;
        for (DocumentMemberDeclarationNode member : nodeList) {
            if (member instanceof TableNode) {
                TableNode node = (TableNode) member;
                String tableName = node.identifier().toSourceCode().trim();
                if (tableName.equals(PERSIST_DIRECTORY)) {
                    dbConfigExists = true;
                    break;
                }
            }
        }
        return dbConfigExists;
    }
}
