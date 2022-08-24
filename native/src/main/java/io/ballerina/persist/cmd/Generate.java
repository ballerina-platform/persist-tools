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
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator;
import io.ballerina.persist.objects.Entity;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.ProjectLoader;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * Class to implement "persist" command for ballerina.
 */
@CommandLine.Command(
        name = "generate",
        description = "generate database configurations.",
        subcommands = {Init.class}
        )

public class Generate implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private String sourcePath = "";
    private ProjectEnvironmentBuilder projectEnvironmentBuilder;

    private static final String COMMAND_IDENTIFIER = "persist-generate";

    private CommandLine parentCmdParser;

    Project balProject;

    String name;

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
                balProject = ProjectLoader.loadProject(Paths.get(this.sourcePath), projectEnvironmentBuilder);
            }
            name = balProject.currentPackage().descriptor().org().value() + "." + balProject.currentPackage()
                    .descriptor().name().value();
        } catch (ProjectException e) {
            errStream.println("The current directory is not a Ballerina project!");
            return;
        }
        try {
            ArrayList<Entity> entityArray = readBalFiles();
            errStream.println(entityArray.get(0).fields.get(0));
            for (Entity entity : entityArray) {
                generateScripts(entity);
            }
        } catch (Exception e) {
            errStream.println(e.getMessage());
        }
    }

    private ArrayList<Entity> readBalFiles() throws IOException {
        ArrayList<Entity> returnMetaData = new ArrayList<>();
        Path dirPath = Paths.get(this.sourcePath);
        File folder = new File(dirPath.toAbsolutePath().toString());
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".bal")) {
                errStream.println(file.getName());
                Path filePath = Paths.get(this.sourcePath, file.getName());
                ArrayList<Entity> retData = BalSyntaxTreeGenerator.readBalFiles(filePath);
                if (retData.size() != 0) {
                    returnMetaData.addAll(retData);
                }
            }
        }
        return returnMetaData;
    }

    private void generateScripts(Entity entity) throws Exception {
        SyntaxTree balTree = BalSyntaxTreeGenerator.generateBalFile(entity);

        writeOutputFile(balTree, Paths.get(this.sourcePath, "module", "generated_clients", entity.tableName
                + "_client.bal").toAbsolutePath().toString());
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    private static void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws Exception {
        String content;
        try {
            content = Formatter.format(syntaxTree.toSourceCode());
        } catch (FormatterException e) {
            throw e;
        }
        Path pathToFile = Paths.get(outPath);
        Files.createDirectories(pathToFile.getParent());
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
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
        out.append("Generated the Client object").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + PersistToolsConstants.COMPONENT_IDENTIFIER + " generate").
                append(System.lineSeparator());
    }
}
