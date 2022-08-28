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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class to implement "persist" command for ballerina.
 */
@CommandLine.Command(
        name = "generate",
        description = "generate database configurations."
        )

public class Generate extends CmdCommon implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private String sourcePath = "";

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
            if (entityArray.size() == 0) {
                errStream.println("Current directory doesn't contain any persist entities");
            } else {
                for (Entity entity : entityArray) {
                    generateScripts(entity);
                }
            }
        } catch (Exception e) {
            errStream.println(e.getMessage());
        }
    }

    private ArrayList<Entity> readBalFiles() throws IOException {
        ArrayList<Entity> returnMetaData = new ArrayList<>();
        Path dirPath = Paths.get(this.sourcePath);
        List<Path> fileList = listFiles(dirPath);

        for (Path i : fileList) {
            if (i.toString().endsWith(".bal")) {
                String[] pathElements = i.toString().strip().split(File.separator, -1);
                String module = "";
                String[] dirElements = this.sourcePath.split(File.separator, -1);
                if (!Arrays.asList(dirElements).contains(pathElements[pathElements.length - 2])) {
                    module = pathElements[pathElements.length - 2];
                }
                Path filePath = i;
                ArrayList<Entity> retData = BalSyntaxTreeGenerator.readBalFiles(filePath, module);
                if (retData.size() != 0) {
                    returnMetaData.addAll(retData);
                }
            }
        }
        return returnMetaData;
    }

    private void generateScripts(Entity entity) throws Exception {
        SyntaxTree balTree = BalSyntaxTreeGenerator.generateBalFile(entity);
        if (entity.module.equals("")) {
            writeOutputFile(balTree, Paths.get(this.sourcePath, "modules", "generated_clients",
                    entity.entityName.toLowerCase() + "_client.bal").toAbsolutePath().toString());
        } else {
            writeOutputFile(balTree, Paths.get(this.sourcePath, "modules", "generated_clients", entity.module,
                    entity.entityName.toLowerCase() + "_client.bal").toAbsolutePath().toString());
        }

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

    private static List<Path> listFiles(Path path) {
        Stream<Path> walk = null;
        try {
            walk = Files.walk(path);
        } catch (IOException e) {
            errStream.println(e.getMessage());
        }
        return walk != null ? walk.filter(Files::isRegularFile).collect(Collectors.toList()) : new ArrayList<>();
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
