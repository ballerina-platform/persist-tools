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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.nodegenerator.BalFileConstants;
import io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator;
import io.ballerina.persist.nodegenerator.SyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.CONFIG_SCRIPT_FILE;
import static io.ballerina.persist.PersistToolsConstants.DATABASE_CONFIGURATION_BAL;
import static io.ballerina.persist.PersistToolsConstants.KEYWORD_CLIENTS;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.PersistToolsConstants.SUBMODULE_FOLDER;
import static io.ballerina.persist.PersistToolsConstants.SUBMODULE_PERSIST;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PATH_CONFIGURATION_BAL_FILE;

/**
 * Class to implement "persist init" command for ballerina.
 *
 * @since 0.1.0
 */

@CommandLine.Command(
        name = "init",
        description = "generate database configurations.")

public class Init implements BLauncherCmd {

    private final PrintStream errStream = System.err;
    private final PrintStream outStream = System.out;
    private final String configPath = PersistToolsConstants.CONFIG_SCRIPT_FILE;

    private String configName = "";
    private String projectName = "";
    public String sourcePath = "";
    private static final String COMMAND_IDENTIFIER = "persist-init";

    Project balProject;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    public Init() {}

    @Override
    public void execute() {
        Path persistTomlPath = Paths.get(this.sourcePath, SUBMODULE_PERSIST, PERSIST_TOML_FILE);
        Path databaseConfigPath = Paths.get(this.sourcePath, SUBMODULE_FOLDER, KEYWORD_CLIENTS,
                DATABASE_CONFIGURATION_BAL);
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        try  {
            balProject = ProjectLoader.loadProject(Paths.get(sourcePath));
            configName = balProject.currentPackage().descriptor().name().value() + "." + KEYWORD_CLIENTS;
            projectName = balProject.currentPackage().descriptor().name().value();
        } catch (ProjectException e) {
            errStream.println("Not a Ballerina project (or any parent up to mount point)\n" +
                    "You should run this command inside a Ballerina project. ");
            return;
        }
        File persistToml = new File(persistTomlPath.toString());
        File databaseConfig = new File(databaseConfigPath.toString());
        if (persistToml.exists()) {
            errStream.println("`bal persist init` command can only be used once to initialize the project");
            return;
        }
        Generate generateCMD = new Generate();
        generateCMD.setSourcePath(Paths.get(sourcePath).toAbsolutePath().toString());
        try {
            if (!databaseConfig.exists()) {
                generateConfigurationBalFile();
                outStream.println("Added new sub module 'clients' used for generated clients");
                outStream.println("Created database_configuration.bal file with configurations.");
            } else {
                generateConfigurationBalFile();
                outStream.println("Replaced database_configuration.bal file with default configurations.");
            }
            createPersistToml(persistTomlPath);
            outStream.println("Created Persist.toml file with configurations. ");
            if (!Files.exists(Paths.get(sourcePath, configPath))) {
                createConfigToml();
                outStream.println("Created Config.toml file inside the Ballerina project. ");
            } else {
                updateConfigToml();
                outStream.println("Updated Config.toml file with default database configurations. ");
            }
            generateCMD.execute();
        } catch (BalException e) {
            errStream.println(e.getMessage());
        }

    }
    private void createConfigToml() throws BalException {
        try {
            SyntaxTree syntaxTree = SyntaxTreeGenerator.createConfigToml(this.configName);
            writeOutputSyntaxTree(syntaxTree, Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while adding Config.toml file inside the Ballerina project. " +
                    e.getMessage());
        }
    }

    private void createPersistToml(Path persistTomlPath) throws BalException {
        try {
            SyntaxTree syntaxTree = SyntaxTreeGenerator.createPesistToml(projectName);
            writeOutputSyntaxTree(syntaxTree, persistTomlPath.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while adding Persist.toml to the project. " +
                    e.getMessage());
        }
    }

    private void generateConfigurationBalFile() throws BalException {
        try {
            String configTree = BalSyntaxTreeGenerator.generateDatabaseConfigSyntaxTree();
            writeOutputString(configTree, Paths.get(this.sourcePath, BalFileConstants.KEYWORD_MODULES,
                            BalFileConstants.KEYWORD_CLIENTS, PATH_CONFIGURATION_BAL_FILE)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while adding database_configuration.bal file inside the client sub module. " +
                    e.getMessage());
        }
    }

    private void updateConfigToml() throws BalException {
        try {
            SyntaxTree syntaxTree = SyntaxTreeGenerator.updateConfigToml(
                    Paths.get(this.sourcePath, this.configPath), this.configName);
            writeOutputSyntaxTree(syntaxTree, Paths.get(this.sourcePath, this.configPath).toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException("Error while updating Config.toml file to default database configurations . " +
                    e.getMessage());
        }
    }
    private void writeOutputSyntaxTree(SyntaxTree syntaxTree, String outPath) throws Exception {
        String content;
        Path pathToFile = Paths.get(outPath);
        Files.createDirectories(pathToFile.getParent());
        content = syntaxTree.toSourceCode();
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
    }
    private void writeOutputString(String content, String outPath) throws Exception {
        Path pathToFile = Paths.get(outPath);
        if (!Files.exists(pathToFile.getParent())) {
            try {
                Files.createDirectories(pathToFile.getParent());
            } catch (IOException e) {
                throw new BalException("Error while creating a new file. " +
                        e.getMessage());
            }
        }
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
    }

    public void setSourcePath(String sourceDir) {
        this.sourcePath = sourceDir;
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
