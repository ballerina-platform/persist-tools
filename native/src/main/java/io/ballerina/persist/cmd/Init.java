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
import io.ballerina.persist.nodegenerator.CreateSyntaxTree;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to implement "persist init" command for ballerina.
 */

@CommandLine.Command(
        name = "init",
        description = "generate database configurations.")

public class Init implements BLauncherCmd {

    private final PrintStream errStream = System.err;
    private String sourcePath = "";
    private ProjectEnvironmentBuilder projectEnvironmentBuilder;
    private String configPath = PersistToolsConstants.CONFIG_PATH;

    private String generationPath = "";

    private CommandLine parentCmdParser;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        try  {
            if (projectEnvironmentBuilder == null) {
                ProjectLoader.loadProject(Paths.get(""));
            } else {
                ProjectLoader.loadProject(Paths.get(this.sourcePath), projectEnvironmentBuilder);
            }
        } catch (ProjectException e) {
            errStream.println("Current Directory is not a Ballerina Project!");
            return;
        }
        if (!Files.exists(Paths.get(sourcePath, configPath))) {
            try {
                createConfigToml();
            } catch (Exception e) {
                errStream.println("Failure when creating the Config.toml file: ");
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
        SyntaxTree toml = CreateSyntaxTree.createToml();
        writeOutputFile(toml, Paths.get(this.generationPath).toAbsolutePath().toString() + "/Config.toml");
    }

    private void updateConfigToml() throws Exception {
        SyntaxTree toml = CreateSyntaxTree.updateToml(Paths.get(this.sourcePath, this.configPath));
        writeOutputFile(toml, Paths.get(this.generationPath).toAbsolutePath().toString() + "/Config.toml");
    }

    private void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws Exception {
        String content = "";
        Path pathToFile = Paths.get(outPath);
        Files.createDirectories(pathToFile.getParent());
        try {
            content = Formatter.format(syntaxTree.toSourceCode());
        } catch (FormatterException e) {
            throw e;
        }
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        } catch (IOException e) {
            throw e;
        }
    }

    public void setSourcePath(String sourceDir) {
        this.sourcePath = sourceDir;
    }

    public void setEnviorenmentBuilder(ProjectEnvironmentBuilder projectEnvironmentBuilder) {
        this.projectEnvironmentBuilder = projectEnvironmentBuilder;
    }

    public void setGenerationPath(String generationDir) {
        this.generationPath = generationDir;
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
        this.parentCmdParser = parentCmdParser;
    }
    @Override
    public String getName() {
        return PersistToolsConstants.COMPONENT_IDENTIFIER;
    }
    
    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Generate database configurations file inside the Ballerina project").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + PersistToolsConstants.COMPONENT_IDENTIFIER + " init\n");
    }
}
