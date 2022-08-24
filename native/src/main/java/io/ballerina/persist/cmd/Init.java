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
import io.ballerina.persist.nodegenerator.SyntaxTreeGenerator;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import picocli.CommandLine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;

/**
 * Class to implement "persist init" command for ballerina.
 */

@CommandLine.Command(
        name = "init",
        description = "generate database configurations.")

public class Init extends PersistCmd implements BLauncherCmd {

    private final PrintStream errStream = System.err;
    private final String configPath = PersistToolsConstants.CONFIG_PATH;

    private CommandLine parentCmdParser;
    private String name = "";
    private static final String COMMAND_IDENTIFIER = "persist-init";

    Project balProject;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        try  {
            if (projectEnvironmentBuilder == null) {
                balProject = ProjectLoader.loadProject(Paths.get(""));

            } else {
                balProject = ProjectLoader.loadProject(Paths.get(sourcePath), projectEnvironmentBuilder);
            }
            name = balProject.currentPackage().descriptor().org().value() + "." + balProject.currentPackage()
                    .descriptor().name().value();
        } catch (ProjectException e) {
            errStream.println("The current directory is not a Ballerina project!");
            return;
        }
        if (!Files.exists(Paths.get(sourcePath, configPath))) {
            try {
                createConfigToml();
            } catch (Exception e) {
                errStream.println("Failure when creating the Config.toml file: " + e.getMessage());
            }
        } else {
            try {
                updateConfigToml();
            } catch (Exception e) {
                errStream.println("Failure when updating the Config.toml file: " + e.getMessage());
            }
        }
    }
    private void createConfigToml() throws Exception {
        SyntaxTree syntaxTree = SyntaxTreeGenerator.createToml(this.name);
        writeOutputFile(syntaxTree, Paths.get(this.sourcePath, "Config.toml").toAbsolutePath().toString());
    }

    private void updateConfigToml() throws Exception {
        SyntaxTree syntaxTree = SyntaxTreeGenerator.updateToml(Paths.get(this.sourcePath, this.configPath), this.name);
        writeOutputFile(syntaxTree, Paths.get(this.sourcePath, "Config.toml").toAbsolutePath().toString());
    }

    private void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws Exception {
        String content;
        Path pathToFile = Paths.get(outPath);
        Files.createDirectories(pathToFile.getParent());
        content = syntaxTree.toSourceCode();
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
    }

    public void setHelpFlag() {
        this.helpFlag = true;
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
        this.parentCmdParser = parentCmdParser;
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
