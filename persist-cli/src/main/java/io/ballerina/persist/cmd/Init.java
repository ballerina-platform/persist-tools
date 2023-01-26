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
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.toml.syntax.tree.SyntaxTree;
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
import static io.ballerina.persist.PersistToolsConstants.CONFIG_SCRIPT_FILE;
import static io.ballerina.persist.PersistToolsConstants.GENERATED_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.BAL_EXTENTION;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.PATH_CONFIGURATION_BAL_FILE;
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
        Path projectPath = Paths.get(sourcePath);
        try  {
            ProjectLoader.loadProject(projectPath);
        } catch (ProjectException e) {
            errStream.println("Not a Ballerina project (or any parent up to mount point)\n" +
                    "You should run this command inside a Ballerina project. ");
            return;
        }
        BuildProject buildProject = BuildProject.load(projectPath.toAbsolutePath());
        String packageName = buildProject.currentPackage().packageName().value();
        Path persistDirPath = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
        if (!Files.exists(persistDirPath)) {
            try {
                Files.createDirectory(persistDirPath.toAbsolutePath());
                errStream.println("Created persist directory in the Ballerina project.");
            } catch (IOException e) {
                errStream.println("Error while creating the persist directory. " + e.getMessage());
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
            errStream.println("Error while listing the model definition files in persist directory. " + e.getMessage());
            return;
        }

        if (schemaFiles.size() == 0) {
            schemaFiles.add(packageName);
            try {
                generateSchemaBalFile(persistDirPath, packageName);
                errStream.printf("Created model definition file(%s) in persist directory.%n",
                        packageName + BAL_EXTENTION);
            } catch (BalException e) {
                errStream.println("Error while creating the model definition file in persist directory. "
                        + e.getMessage());
                return;
            }
        }
        Path generatedSourceDirPath = Paths.get(this.sourcePath, GENERATED_DIRECTORY);
        if (!Files.exists(generatedSourceDirPath)) {
            try {
                Files.createDirectory(generatedSourceDirPath.toAbsolutePath());
            } catch (IOException e) {
                errStream.println("Error while creating the generated directory. " + e.getMessage());
                return;
            }
        }
        for (String file : schemaFiles) {
            if (!ProjectUtils.validateModuleName(file)) {
                errStream.println("Invalid definition file name : '" + file + "' :\n" +
                        "File name can only contain alphanumerics, underscores and periods");
                return;
            } else if (!ProjectUtils.validateNameLength(file)) {
                errStream.println("Invalid definition file name : '" + file + "' :\n" +
                        "Maximum length of file name is 256 characters");
                return;
            }
            Path schemaDirPath;
            if (file.equals(packageName)) {
                schemaDirPath = generatedSourceDirPath;
            } else {
                schemaDirPath = generatedSourceDirPath.resolve(file);
            }
            Path databaseConfigPath = schemaDirPath.resolve(PATH_CONFIGURATION_BAL_FILE);
            if (!Files.exists(databaseConfigPath)) {
                try {
                    generateConfigurationBalFile(schemaDirPath);
                    errStream.printf(
                            "Created database_configurations.bal file inside `%s` module in generated directory.%n",
                            file.equals(packageName) ? "default" : file);
                } catch (BalException e) {
                    errStream.println("Error while generating the database_configurations.bal file. " + e.getMessage());
                    return;
                }
            }
        }

        try {
            updateBallerinaToml(schemaFiles);
            if (!Files.exists(Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath())) {
                createConfigTomlFile(schemaFiles, packageName);
            } else {
                updateConfigTomlFile(schemaFiles, packageName);
            }
        } catch (BalException e) {
            errStream.println("Error while adding database configurations. " + e.getMessage());
        }
    }

    private void generateConfigurationBalFile(Path generatedSourcePath) throws BalException {
        try {
            String configTree = BalSyntaxGenerator.generateDatabaseConfigSyntaxTree();
            writeOutputString(configTree, generatedSourcePath.resolve(PATH_CONFIGURATION_BAL_FILE)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException(e.getMessage());
        }
    }

    private void generateSchemaBalFile(Path persistPath, String packageName) throws BalException {
        try {
            String configTree = BalSyntaxGenerator.generateSchemaSyntaxTree();
            writeOutputString(configTree, persistPath.resolve(packageName + BAL_EXTENTION)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException(e.getMessage());
        }
    }

    private void updateBallerinaToml(List<String> schemas) throws BalException {
        try {
            SyntaxTree syntaxTree = TomlSyntaxGenerator.updateBallerinaToml(
                    Paths.get(this.sourcePath, BALLERINA_TOML), schemas);
            writeOutputSyntaxTree(syntaxTree,
                    Paths.get(this.sourcePath, BALLERINA_TOML).toAbsolutePath().toString());
            errStream.println("Updated Ballerina.toml with database configurations.");
        } catch (Exception e) {
            throw new BalException("Error while updating Ballerina.toml with database configurations . " +
                    e.getMessage());
        }
    }

    private void createConfigTomlFile(List<String> schemas, String packageName) throws BalException {
        try {
            Path configPath = Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath();
            SyntaxTree syntaxTree = TomlSyntaxGenerator.createConfigToml(schemas, packageName);
            writeOutputSyntaxTree(syntaxTree, configPath.toString());
            errStream.println("Created Config.toml file inside the Ballerina project.");
        } catch (Exception e) {
            throw new BalException("Error while adding Config.toml file inside the Ballerina project. " +
                    e.getMessage());
        }
    }

    private void updateConfigTomlFile(List<String> schemas, String packageName) throws BalException {
        try {
            Path configPath = Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath();
            SyntaxTree syntaxTree = TomlSyntaxGenerator.updateConfigToml(configPath, schemas, packageName);
            writeOutputSyntaxTree(syntaxTree, configPath.toString());
            errStream.println("Updated Config.toml file inside the Ballerina project.");
        } catch (Exception e) {
            throw new BalException("Error while updating Config.toml file inside the Ballerina project. " +
                    e.getMessage());
        }
    }
    private void writeOutputSyntaxTree(SyntaxTree syntaxTree, String outPath) throws Exception {
        String content;
        Path pathToFile = Paths.get(outPath);
        Path parentDirectory = pathToFile.getParent();
        if (Objects.nonNull(parentDirectory)) {
            try {
                Files.createDirectories(parentDirectory);
            } catch (IOException e) {
                throw new BalException(
                        String.format("Failed to create the parent directories of output path %s. %s",
                                parentDirectory, e.getMessage()));
            }
            content = syntaxTree.toSourceCode();
            try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
                writer.println(content);
            }
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
                            String.format("Failed to create the parent directories of output path %s. %s",
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
