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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.SUPPORTED_DB_PROVIDERS;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.BAL_EXTENSION;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

@CommandLine.Command(
        name = "add",
        description = "Initialize the persistence layer in the Ballerina project.")
public class Add implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-add";

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--datastore"})
    private String datastore;

    @CommandLine.Option(names = {"--module"})
    private String module;

    @CommandLine.Option(names = {"--id"}, description = "ID for the generated Ballerina client")
    private String id;

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
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(getName());
            errStream.println(commandUsageInfo);
            return;
        }
        if (datastore == null) {
            datastore = PersistToolsConstants.SupportedDataSources.IN_MEMORY_TABLE;
        } else if (!SUPPORTED_DB_PROVIDERS.contains(datastore)) {
            errStream.printf("ERROR: the persist layer supports one of data stores: %s" +
                    ". but found '%s' datasource.%n", Arrays.toString(SUPPORTED_DB_PROVIDERS.toArray()), datastore);
            return;
        }
        try {
            packageName = TomlSyntaxUtils.readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }
        if (module == null) {
            module = packageName;
        } else {
            module = module.replaceAll("\"", "");
        }
        if (!ProjectUtils.validateModuleName(module)) {
            errStream.println("ERROR: invalid module name : '" + module + "' :\n" +
                    "module name can only contain alphanumerics, underscores and periods");
            return;
        } else if (!ProjectUtils.validateNameLength(module)) {
            errStream.println("ERROR: invalid module name : '" + module + "' :\n" +
                    "maximum length of module name is 256 characters");
            return;
        }
        if (!module.equals(packageName)) {
            module = String.format("%s.%s", packageName.replaceAll("\"", ""), module);
        }
        try {
            Path projectPath = Paths.get(sourcePath);
            validateBallerinaProject(projectPath);
            createDefaultClientId();
            String syntaxTree = TomlSyntaxUtils.updateBallerinaToml(Paths.get(this.sourcePath, BALLERINA_TOML), module,
                    datastore, false, id);
            Utils.writeOutputString(syntaxTree,
                    Paths.get(this.sourcePath, BALLERINA_TOML).toAbsolutePath().toString());
            Path persistDirPath = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
            if (!Files.exists(persistDirPath)) {
                try {
                    Files.createDirectory(persistDirPath.toAbsolutePath());
                } catch (IOException e) {
                    errStream.println("ERROR: failed to create the persist directory. " + e.getMessage());
                    return;
                }
            }
            List<String> schemaFiles;
            try (Stream<Path> stream = Files.list(persistDirPath)) {
                schemaFiles = stream.filter(file -> !Files.isDirectory(file))
                        .map(Path::getFileName)
                        .filter(Objects::nonNull)
                        .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(BAL_EXTENSION))
                        .map(file -> file.toString().replace(BAL_EXTENSION, ""))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                errStream.println("ERROR: failed to list model definition files in the persist directory. "
                        + e.getMessage());
                return;
            }
            if (schemaFiles.size() > 1) {
                errStream.println("ERROR: the persist directory allows only one model definition file, " +
                        "but contains many files.");
                return;
            }
            if (schemaFiles.size() == 0) {
                try {
                    Utils.generateSchemaBalFile(persistDirPath);
                } catch (BalException e) {
                    errStream.println("ERROR: failed to create the model definition file in persist directory. "
                            + e.getMessage());
                    return;
                }
            }
        } catch (BalException | IOException e) {
            errStream.printf("ERROR: Failed to generate types and client for the definition file(%s). %s%n",
                    "Ballerina.toml", e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "add";
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

    private static void validateBallerinaProject(Path projectPath) throws BalException {
        Optional<Path> ballerinaToml = Optional.empty();
        try (Stream<Path> stream = Files.list(projectPath)) {
            ballerinaToml = stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .filter(Objects::nonNull)
                    .filter(file -> BALLERINA_TOML.equals(file.toString()))
                    .findFirst();
        } catch (IOException e) {
            errStream.printf("ERROR: Invalid Ballerina package", projectPath.toAbsolutePath(), e.getMessage());
        }
        if (ballerinaToml.isEmpty()) {
            throw new BalException(String.format("ERROR: invalid Ballerina package directory: %s, " +
                    "cannot find 'Ballerina.toml' file.%n", projectPath.toAbsolutePath()));
        }
    }

    private void createDefaultClientId() {
        if (id == null || id.isBlank()) {
            id = "generate-db-client";
        }
    }
}
