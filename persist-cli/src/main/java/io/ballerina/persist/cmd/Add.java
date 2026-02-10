/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.persist.utils.FileUtils;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.toml.syntax.tree.AbstractNodeFactory;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.NodeFactory;
import io.ballerina.toml.syntax.tree.NodeList;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import io.ballerina.toml.syntax.tree.Token;
import io.ballerina.toml.validator.SampleNodeGenerator;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.BAL_PERSIST_ADD_CMD;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.getConfigDeclaration;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.getDependencyConfig;
import static io.ballerina.persist.utils.BalProjectUtils.printTestClientUsageSteps;
import static io.ballerina.persist.utils.BalProjectUtils.validateDatastore;
import static io.ballerina.persist.utils.BalProjectUtils.validateTestDatastore;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

@CommandLine.Command(name = "add", description = "Initialize the persistence layer in the Ballerina project.")
public class Add implements BLauncherCmd {

    private static final PrintStream errStream = System.err;
    private static final String COMMAND_IDENTIFIER = "persist-add";

    private final String sourcePath;

    @CommandLine.Option(names = { "-h", "--help" }, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = { "--datastore" })
    private String datastore;

    @CommandLine.Option(names = { "--module" })
    private String module;

    @CommandLine.Option(names = { "--id" }, description = "ID for the generated Ballerina client")
    private String id;

    @CommandLine.Option(names = { "--test-datastore" }, description = "Test data store for the " +
            "generated Ballerina client")
    private String testDatastore;

    @CommandLine.Option(names = { "--eager-loading" }, hidden = true, description = "Enable eager loading to return " +
            "arrays instead of streams for get-all methods")
    private boolean eagerLoading;

    @CommandLine.Option(names = { "--with-init-params" }, hidden = true, description = "Use init parameters instead" +
            " of configurables")
    private boolean initParams;

    @CommandLine.Option(names = { "--model" })
    private String model;

    public Add() {
        this("");
    }

    public Add(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public void execute() {
        String packageName;

        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER, Add.class.getClassLoader());
            errStream.println(commandUsageInfo);
            return;
        }
        try {
            if (Objects.isNull(datastore)) {
                datastore = PersistToolsConstants.SupportedDataSources.IN_MEMORY_TABLE;
            } else {
                validateDatastore(datastore);
            }
            validateTestDatastore(datastore, testDatastore);

            // Validate eager loading is only used with SQL datastores
            eagerLoading = Utils.validateEagerLoading(datastore, eagerLoading, errStream);

            try {
                packageName = TomlSyntaxUtils.readPackageName(this.sourcePath);
            } catch (BalException e) {
                errStream.println(e.getMessage());
                return;
            }
            String moduleNameWithPackage = validateAndProcessModule(packageName, module);
            createDefaultClientId();
            String modelPath = (model != null && !model.trim().isEmpty())
                    ? "persist/" + model + "/model.bal"
                    : "persist/model.bal";
            String syntaxTree = updateBallerinaToml(Paths.get(this.sourcePath, BALLERINA_TOML),
                    moduleNameWithPackage, datastore, testDatastore, eagerLoading, initParams, id, modelPath);
            FileUtils.writeToTargetFile(syntaxTree,
                    Paths.get(sourcePath, BALLERINA_TOML).toAbsolutePath().toString());
            createPersistDirectoryIfNotExists();
            createDefaultSchemaBalFile();
            errStream.printf("Integrated the generation of persist client and entity types into the package " +
                    "build process." + System.lineSeparator());
            errStream.println(System.lineSeparator() + "Next steps:");
            errStream.println("1. Define your data model in \"" + modelPath + "\".");
            errStream.println("2. Execute `bal build` to generate the persist client during package build.");

            if (Objects.nonNull(testDatastore)) {
                errStream.printf(System.lineSeparator() +
                        "The test client for the %s datastore will be generated in the %s module.%n",
                        testDatastore, module);
                printTestClientUsageSteps(testDatastore, packageName, module);
            }
        } catch (BalException | IOException e) {
            errStream.printf("ERROR: %s%n", e.getMessage());
        }
    }

    /**
     * Method to update the Ballerina.toml with persist tool configurations.
     */
    String updateBallerinaToml(Path tomlPath, String module, String datastore, String testDatastore,
            boolean eagerLoading, boolean initParams, String id, String modelPath)
            throws BalException, IOException {
        TomlSyntaxUtils.NativeDependency dependency = getDependencyConfig(datastore, testDatastore);
        TomlSyntaxUtils.ConfigDeclaration declaration = getConfigDeclaration(tomlPath, dependency);

        if (declaration.persistClientIds().contains(id)) {
            throw new BalException(String.format("a build option with the provided ID '%s' already exists in " +
                    "the Ballerina.toml. Please provide a unique ID using the --id option.", id));
        }

        NodeList<DocumentMemberDeclarationNode> moduleMembers = declaration.moduleMembers();
        if (!moduleMembers.isEmpty()) {
            moduleMembers = BalProjectUtils.addNewLine(moduleMembers, 1);
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createTableArray(
                    PersistToolsConstants.PERSIST_TOOL_CONFIG, null));
            moduleMembers = populateBallerinaNodeList(moduleMembers, module, datastore, testDatastore,
                    eagerLoading, initParams, id, modelPath);
            moduleMembers = BalProjectUtils.addNewLine(moduleMembers, 1);
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        DocumentNode documentNode = NodeFactory.createDocumentNode(moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(documentNode.toSourceCode());
        return SyntaxTree.from(textDocument).toSourceCode();
    }

    private static NodeList<DocumentMemberDeclarationNode> populateBallerinaNodeList(
            NodeList<DocumentMemberDeclarationNode> moduleMembers, String module, String dataStore,
            String testDatastore, boolean eagerLoading, boolean initParams, String id, String modelPath) {
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("id", id, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("targetModule", module, null));
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("options.datastore", dataStore, null));
        if (testDatastore != null) {
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("options.testDatastore",
                    testDatastore, null));
        }
        if (eagerLoading) {
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createBooleanKV("options.eagerLoading",
                    true, null));
        }
        if (initParams) {
            moduleMembers = moduleMembers.add(SampleNodeGenerator.createBooleanKV("options.withInitParams",
                    true, null));
        }
        moduleMembers = moduleMembers.add(SampleNodeGenerator.createStringKV("filePath", modelPath, null));
        return moduleMembers;
    }

    @Override
    public String getName() {
        return BAL_PERSIST_ADD_CMD;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }

    private String validateAndProcessModule(String packageName, String module) throws BalException {
        if (Objects.nonNull(module)) {
            if (!ProjectUtils.validateModuleName(module)) {
                throw new BalException(String.format("invalid module name : '%s' :" + System.lineSeparator() +
                        "module name can only contain alphanumerics, underscores and periods", module));
            } else if (!ProjectUtils.validateNameLength(module)) {
                throw new BalException(String.format("invalid module name : '%s' :" + System.lineSeparator() +
                        "maximum length of module name is 256 characters", module));
            }
        }
        return Objects.isNull(module) ? packageName
                : String.format("%s.%s", packageName.replaceAll("\"", ""),
                        module.replaceAll("\"", ""));
    }

    private void createPersistDirectoryIfNotExists() throws IOException {
        Path persistDirPath = Paths.get(sourcePath, PERSIST_DIRECTORY);
        if (!Files.exists(persistDirPath)) {
            Files.createDirectory(persistDirPath.toAbsolutePath());
        }
    }

    private void createDefaultSchemaBalFile() throws IOException, BalException {
        Path persistPath = Paths.get(sourcePath, PERSIST_DIRECTORY);

        if (model != null && !model.isBlank()) {
            // Create subdirectory model
            try {
                BalProjectUtils.validateModelName(model);
                Path modelDir = persistPath.resolve(model);

                if (Files.exists(modelDir)) {
                    // Directory exists, check if model.bal exists
                    Path modelFile = modelDir.resolve("model.bal");
                    if (!Files.exists(modelFile)) {
                        FileUtils.generateSchemaBalFile(modelDir);
                    }
                } else {
                    // Create directory and model file
                    Files.createDirectory(modelDir);
                    FileUtils.generateSchemaBalFile(modelDir);
                }
            } catch (BalException e) {
                throw new BalException("invalid model name '" + model + "'. " + e.getMessage());
            }
        } else {
            // Create root model (backward compatible)
            Path modelFile = persistPath.resolve("model.bal");
            if (!Files.exists(modelFile)) {
                FileUtils.generateSchemaBalFile(persistPath);
            }
        }
    }

    private void createDefaultClientId() {
        if (id == null || id.isBlank()) {
            if (model == null || model.isBlank()) {
                id = "generate-db-client";
            } else {
                id = "generate-" + model.toLowerCase(Locale.ENGLISH).trim() + "-client";
            }
        }
    }
}
