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
import io.ballerina.projects.util.ProjectUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.BAL_PERSIST_ADD_CMD;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.BAL_EXTENSION;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.utils.BalProjectUtils.printTestClientUsageSteps;
import static io.ballerina.persist.utils.BalProjectUtils.validateDatastore;
import static io.ballerina.persist.utils.BalProjectUtils.validateTestDatastore;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

@CommandLine.Command(
        name = "add",
        description = "Initialize the persistence layer in the Ballerina project.")
public class Add implements BLauncherCmd {

    private static final PrintStream errStream = System.err;
    private static final String COMMAND_IDENTIFIER = "persist-add";

    private final String sourcePath;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--datastore"})
    private String datastore;

    @CommandLine.Option(names = {"--module"})
    private String module;

    @CommandLine.Option(names = {"--id"}, description = "ID for the generated Ballerina client")
    private String id;

    @CommandLine.Option(names = {"--test-datastore"}, description = "Test data store for the " +
            "generated Ballerina client")
    private String testDatastore;

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
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        try {
            if (Objects.isNull(datastore)) {
                datastore = PersistToolsConstants.SupportedDataSources.IN_MEMORY_TABLE;
            } else {
                validateDatastore(datastore);
            }
            validateTestDatastore(testDatastore);

            try {
                packageName = TomlSyntaxUtils.readPackageName(this.sourcePath);
            } catch (BalException e) {
                errStream.println(e.getMessage());
                return;
            }
            String moduleNameWithPackage = validateAndProcessModule(packageName, module);
            createDefaultClientId();
            String syntaxTree = TomlSyntaxUtils.updateBallerinaToml(Paths.get(this.sourcePath, BALLERINA_TOML),
                    moduleNameWithPackage, datastore, testDatastore, false, id);
            Utils.writeOutputString(syntaxTree,
                    Paths.get(sourcePath, BALLERINA_TOML).toAbsolutePath().toString());
            createPersistDirectoryIfNotExists();
            createDefaultSchemaBalFile();
            errStream.printf("Integrated the generation of persist client and entity types into the package " +
                    "build process." + System.lineSeparator());
            errStream.println(System.lineSeparator() + "Next steps:");
            errStream.println("1. Define your data model in \"persist/model.bal\".");
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
        return Objects.isNull(module) ? packageName :
                String.format("%s.%s", packageName.replaceAll("\"", ""),
                        module.replaceAll("\"", ""));
    }

    private void createPersistDirectoryIfNotExists() throws IOException {
        Path persistDirPath = Paths.get(sourcePath, PERSIST_DIRECTORY);
        if (!Files.exists(persistDirPath)) {
            Files.createDirectory(persistDirPath.toAbsolutePath());
        }
    }

    private void createDefaultSchemaBalFile() throws IOException, BalException {
        List<String> schemaFiles;
        try (Stream<Path> stream = Files.list(Paths.get(sourcePath, PERSIST_DIRECTORY))) {
            schemaFiles = stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .filter(Objects::nonNull)
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(BAL_EXTENSION))
                    .map(file -> file.toString().replace(BAL_EXTENSION, ""))
                    .collect(Collectors.toList());
        }
        if (schemaFiles.size() > 1) {
            throw new BalException("the persist directory allows only one model definition file, " +
                    "but contains many files.");
        }
        if (schemaFiles.isEmpty()) {
            Utils.generateSchemaBalFile(Paths.get(sourcePath, PERSIST_DIRECTORY));
        }
    }

    private void createDefaultClientId() {
        if (id == null || id.isBlank()) {
            id = "generate-db-client";
        }
    }
}
