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
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.BalSyntaxGenerator;
import io.ballerina.persist.nodegenerator.TomlSyntaxGenerator;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.CONFIG_SCRIPT_FILE;
import static io.ballerina.persist.PersistToolsConstants.DATABASE_MYSQL;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.PATH_CONFIGURATION_BAL_FILE;
import static io.ballerina.persist.utils.BalProjectUtils.getEntityModule;
import static io.ballerina.persist.utils.BalProjectUtils.validateSchemaFile;

/**
 * Client to implement "persist init" command for ballerina.
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

        Path persistTomlPath = Paths.get(this.sourcePath, PERSIST_DIRECTORY, PERSIST_TOML_FILE);
        if (Files.exists(persistTomlPath)) {
            errStream.println("`bal persist init` command can only be used once to initialize the project");
            return;
        }

        io.ballerina.projects.Module module;
        Module entityModule;
        try {
            BuildProject buildProject = validateSchemaFile(projectPath, true);
            module = getEntityModule(buildProject);
            entityModule = BalProjectUtils.getEntities(module);
        } catch (BalException e) {
            errStream.println("Error while fetching entity module in the Ballerina project. " + e.getMessage());
            return;
        }

        Path generatedSourceDirPath;
        if (module.moduleName().moduleNamePart() == null) {
            generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
        } else {
            generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY,
                    module.moduleName().moduleNamePart());
        }
        try {

            generateConfigurationBalFile(generatedSourceDirPath);
            errStream.println("Created database_configuration.bal file with default configurations " +
                    "in the generated directory.");

            createPersistToml(persistTomlPath);
            errStream.println("Created Persist.toml file with configurations. ");
            if (!Files.exists(Paths.get(sourcePath, CONFIG_SCRIPT_FILE))) {
                createConfigToml(module.moduleName().toString());
                errStream.println("Created Config.toml file inside the Ballerina project. ");
            } else {
                updateConfigToml(module.moduleName().toString());
                errStream.println("Updated Config.toml file with default database configurations. ");
            }
//            Generate.generatePersistClients(entityModule, generatedSourceDirPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
        }
    }

    private void createConfigToml(String configName) throws BalException {
        try {
            SyntaxTree syntaxTree = TomlSyntaxGenerator.createConfigToml(configName);
            writeOutputSyntaxTree(syntaxTree, Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while adding Config.toml file inside the Ballerina project. " +
                    e.getMessage());
        }
    }

    private void createPersistToml(Path persistTomlPath) throws BalException {
        try {
            SyntaxTree syntaxTree = TomlSyntaxGenerator.createConfigToml(DATABASE_MYSQL);
            writeOutputSyntaxTree(syntaxTree, persistTomlPath.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while adding Persist.toml to the project. " +
                    e.getMessage());
        }
    }

    private void generateConfigurationBalFile(Path generatedSourcePath) throws BalException {
        try {
            String configTree = BalSyntaxGenerator.generateDatabaseConfigSyntaxTree();
            writeOutputString(configTree, generatedSourcePath.resolve(PATH_CONFIGURATION_BAL_FILE)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while adding database_configuration.bal file inside the client sub module. " +
                    e.getMessage());
        }
    }

    private void updateConfigToml(String configName) throws BalException {
        try {
            SyntaxTree syntaxTree = TomlSyntaxGenerator.updateConfigToml(
                    Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE), configName);
            writeOutputSyntaxTree(syntaxTree,
                    Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while updating Config.toml file to default database configurations . " +
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
                throw new BalException("Error while creating a new file. " +
                        e.getMessage());
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
                    throw new BalException("Error while creating a new file. " +
                            e.getMessage());
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
