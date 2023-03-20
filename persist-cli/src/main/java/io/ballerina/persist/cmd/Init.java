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
import io.ballerina.persist.nodegenerator.BalSyntaxGenerator;
import io.ballerina.persist.nodegenerator.TomlSyntaxGenerator;
import io.ballerina.projects.util.ProjectUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.SCHEMA_FILE_NAME;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.BAL_EXTENTION;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_MYSQL;
import static io.ballerina.persist.nodegenerator.TomlSyntaxGenerator.readPackageName;
import static io.ballerina.persist.utils.BalProjectUtils.validateBallerinaProject;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

/**
 * Class to implement "persist init" command for ballerina.
 *
 * @since 0.1.0
 */

@CommandLine.Command(
        name = "init",
        description = "Initialize the persistence layer in the Ballerina project.")

public class Init implements BLauncherCmd {

    private final PrintStream errStream = System.err;
    private static final String COMMAND_IDENTIFIER = "persist-init";
    private final String sourcePath;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--datastore"})
    private String datastore;

    @CommandLine.Option(names = {"--module"})
    private String module;

    public Init() {
        this("");
    }

    public Init(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }

        if (datastore == null) {
            datastore = KEYWORD_MYSQL;
        } else if (!datastore.equals(KEYWORD_MYSQL)) {
            errStream.printf("ERROR: the persist layer supports only " +
                    "'mysql' datastore. but found '%s' datasource.%n", datastore);
            return;
        }

        Path projectPath = Paths.get(sourcePath);
        try {
            validateBallerinaProject(projectPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

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
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(BAL_EXTENTION))
                    .map(file -> file.toString().replace(BAL_EXTENTION, ""))
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
        String packageName;
        try {
            packageName = readPackageName(this.sourcePath);
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
        try {
            String moduleName = packageName;
            if (!module.equals(packageName)) {
                moduleName = String.format("%s.%s", packageName.replaceAll("\"", ""), module);
            }
            updateBallerinaToml(moduleName, datastore);
        } catch (BalException e) {
            errStream.println("ERROR: failed to add persist configurations in the toml file. " + e.getMessage());
            return;
        }
        if (schemaFiles.size() == 0) {
            try {
                generateSchemaBalFile(persistDirPath, SCHEMA_FILE_NAME);
            } catch (BalException e) {
                errStream.println("ERROR: failed to create the model definition file in persist directory. "
                        + e.getMessage());
                return;
            }
        }

        errStream.println("Initialized persistence in your Ballerina project.");
        errStream.println(System.lineSeparator() + "Your Persist schema is at persist/model.bal.");
        errStream.println("You can now update it with entity definitions.");
        errStream.println(System.lineSeparator() + "Next steps:");

        errStream.println("Run bal persist generate to generate the Ballerina Client, Types," +
                " and Scripts. You can then start querying your database.");

    }

    private void generateSchemaBalFile(Path persistPath, String schemaFile) throws BalException {
        try {
            String configTree = BalSyntaxGenerator.generateSchemaSyntaxTree();
            writeOutputString(configTree, persistPath.resolve(schemaFile + BAL_EXTENTION)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException(e.getMessage());
        }
    }

    private void updateBallerinaToml(String module, String datastore) throws BalException {
        try {
            String syntaxTree = TomlSyntaxGenerator.updateBallerinaToml(
                    Paths.get(this.sourcePath, BALLERINA_TOML), module, datastore);
            writeOutputString(syntaxTree,
                    Paths.get(this.sourcePath, BALLERINA_TOML).toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("could not update the Ballerina.toml with persist configurations. " +
                    e.getMessage());
        }
    }

    private void writeOutputString(String content, String outPath) throws Exception {
        Path pathToFile = Paths.get(outPath);
        Path parentDirectory = pathToFile.getParent();
        if (Objects.nonNull(parentDirectory)) {
            if (!Files.exists(parentDirectory)) {
                try {
                    Files.createDirectories(parentDirectory);
                } catch (IOException e) {
                    throw new BalException(
                            String.format("could not create the parent directories of output path %s. %s",
                                    parentDirectory, e.getMessage()));
                }
            }
            try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
                writer.println(content);
            }
        }
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }
    @Override
    public String getName() {
        return COMPONENT_IDENTIFIER;
    }
    
    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Generate database configurations file inside the Ballerina project").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + COMPONENT_IDENTIFIER +
                " init").append(System.lineSeparator());
    }
}
