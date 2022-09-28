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
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.tools.diagnostics.Diagnostic;
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
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.utils.BalProjectUtils.hasSemanticDiagnostics;
import static io.ballerina.persist.utils.BalProjectUtils.hasSyntacticDiagnostics;


/**
 * This Class implements the `persist generate` command in Ballerina persist-tool.
 *
 * @since 0.1.0
 */
@CommandLine.Command(
        name = "generate",
        description = "Generate Ballerina client object for the entity."
        )

public class Generate implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private String sourcePath = "";
    public ProjectEnvironmentBuilder projectEnvironmentBuilder;

    private static final String COMMAND_IDENTIFIER = "persist-generate";

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
                    entity.setPackageName(balProject.currentPackage().descriptor().org().value() + "/"
                            + balProject.currentPackage().descriptor().name().value());
                    generateClientBalFile(entity);
                }
                generateConfigurationBalFile();
            }
        } catch (Exception e) {
            errStream.println(e.getMessage());
        }
    }

    private ArrayList<Entity> readBalFiles() throws BalException {
        ArrayList<Entity> returnMetaData = new ArrayList<>();
        Path dirPath = Paths.get(this.sourcePath);
        List<Path> fileList;

        try (Stream<Path> walk = Files.walk(dirPath)) {
            if (walk != null) {
                fileList = walk.filter(Files::isRegularFile).collect(Collectors.toList());
                DiagnosticResult diagnosticResult = hasSemanticDiagnostics(
                        Paths.get(this.sourcePath), this.projectEnvironmentBuilder);
                ArrayList<String> syntaxDiagnostics = hasSyntacticDiagnostics(Paths.get(this.sourcePath));
                if (!syntaxDiagnostics.isEmpty()) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Error occurred when validating the project." +
                            " The project contains syntax errors. ");
                    for (String d : syntaxDiagnostics) {
                        errorMessage.append(System.lineSeparator());
                        errorMessage.append(d.toString());
                    }
                    throw new BalException(errorMessage.toString());
                }
                if (diagnosticResult.hasErrors()) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Error occurred when validating the project." +
                            " The project contains semantic errors. ");
                    for (Diagnostic d : diagnosticResult.errors()) {
                        errorMessage.append(System.lineSeparator());
                        errorMessage.append(d.toString());
                    }
                    throw new BalException(errorMessage.toString());
                }
                for (Path filePath : fileList) {
                    if (filePath.toString().endsWith(".bal")) {
                        String[] pathElements = filePath.toString().strip().split(Pattern.quote(File.separator));
                        Optional<String> module = Optional.empty();
                        String[] dirElements = this.sourcePath.split(Pattern.quote(File.separator));
                        if (pathElements.length > 2 && !Arrays.asList(dirElements).contains(pathElements[
                                pathElements.length - 2])) {
                            module = Optional.of(pathElements[pathElements.length - 2]);
                        }
                        ArrayList<Entity> retData = BalSyntaxTreeGenerator.getEntityRecord(filePath, module);
                        if (retData.size() != 0) {
                            returnMetaData.addAll(retData);
                        }
                    }
                }
                return returnMetaData;
            }

        } catch (IOException e) {
            throw new BalException("Error occurred while reading bal files!");
        }
        return new ArrayList<>();
    }

    private void generateClientBalFile(Entity entity) throws BalException {
        SyntaxTree balTree = BalSyntaxTreeGenerator.generateClientSyntaxTree(entity);
        String clientPath;
        if (entity.getModule().isEmpty()) {
            clientPath = Paths.get(this.sourcePath, "modules", "generated_clients",
                    entity.getEntityName().toLowerCase() + "_client.bal").toAbsolutePath().toString();
        } else {
            clientPath = Paths.get(this.sourcePath, "modules", "generated_clients",
                            entity.getModule().get() + "_" + entity.getEntityName().toLowerCase() + "_client.bal")
                    .toAbsolutePath().toString();
        }
        try {
            writeOutputFile(balTree, clientPath);
        } catch (IOException e) {
            throw new BalException("Error occurred in writing ballerina client files!");
        } catch (FormatterException e) {
            throw new BalException("Error occurred while formatting ballerina client files!");
        }

    }
    private void generateConfigurationBalFile() throws BalException {
        SyntaxTree configTree = BalSyntaxTreeGenerator.generateConfigSyntaxTree();
        try {
            writeOutputFile(configTree, Paths.get(this.sourcePath, "modules",
                            "generated_clients", "database_configuration.bal")
                    .toAbsolutePath().toString());
        } catch (IOException e) {
            throw new BalException("Error occurred while writing database configuration file!");
        } catch (FormatterException e) {
            throw new BalException("Error occurred while formatting database configuration file!");
        }
    }

    private static void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws IOException, FormatterException {
        String content;
        content = Formatter.format(syntaxTree.toSourceCode());
        Path pathToFile = Paths.get(outPath);
        Files.createDirectories(pathToFile.getParent());
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    public void setEnvironmentBuilder(ProjectEnvironmentBuilder projectEnvironmentBuilder) {
        this.projectEnvironmentBuilder = projectEnvironmentBuilder;
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {

    }
    @Override
    public String getName() {
        return PersistToolsConstants.COMPONENT_IDENTIFIER;
    }
    
    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Generate Client objects corresponding to persist entities").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + PersistToolsConstants.COMPONENT_IDENTIFIER + " generate").
                append(System.lineSeparator());
    }
}
