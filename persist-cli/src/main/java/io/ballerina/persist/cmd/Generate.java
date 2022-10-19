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
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Project;
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

import static io.ballerina.persist.nodegenerator.BalFileConstants.EXTENSION_BAL;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_CLIENTS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_MODULES;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PATH_CONFIGURATION_BAL_FILE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PATH_ENTITIES_FILE;
import static io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator.formatModuleMembers;
import static io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator.generateRelations;
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
    private static final PrintStream outStream = System.out;

    private String sourcePath = "";

    private static final String COMMAND_IDENTIFIER = "persist-generate";

    Project balProject;

    String name;

    boolean isTime = false;

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
            balProject = ProjectLoader.loadProject(Paths.get(this.sourcePath));
            name = balProject.currentPackage().descriptor().org().value() + "." + balProject.currentPackage()
                    .descriptor().name().value();
        } catch (ProjectException e) {
            errStream.println("Not a Ballerina project (or any parent up to mount point)\n" +
                    "You should run this command inside a Ballerina project. ");
            return;
        }
        try {
            EntityMetaData retEntityMetaData = readBalFiles();
            ArrayList<Entity> entityArray = retEntityMetaData.entityArray;
            ArrayList<ModuleMemberDeclarationNode> returnModuleMembers = retEntityMetaData.moduleMembersArray;
            ArrayList<ImportDeclarationNode> imports = new ArrayList<>();
            if (entityArray.size() == 0) {
                errStream.println("No entity found inside the Ballerina project");
            } else {
                for (Entity entity : entityArray) {
                    entity.setPackageName(balProject.currentPackage().descriptor().org().value() + "/"
                            + balProject.currentPackage().descriptor().name().value());
                    generateClientBalFile(entity, imports);
                    outStream.println(String.format("Generated Ballerina client file for entity %s, " +
                            "inside clients sub module.", entity.getEntityName()));
                }
                generateConfigurationBalFile();
                outStream.println("Created database_configurations.bal");
                copyEntities(entityArray, returnModuleMembers, imports);
                outStream.println("Created entities.bal");
            }
        } catch (Exception e) {
            errStream.println(e.getMessage());
        }
    }

    private EntityMetaData readBalFiles() throws BalException {
        ArrayList<Entity> returnMetaData = new ArrayList<>();
        ArrayList<ModuleMemberDeclarationNode> returnModuleMembers = new ArrayList<>();
        Path dirPath = Paths.get(this.sourcePath);
        List<Path> fileList;

        try (Stream<Path> walk = Files.walk(dirPath)) {
            boolean skipValidation = false;
            if (walk != null) {
                fileList = walk.filter(Files::isRegularFile).collect(Collectors.toList());
                Path clientEntitiesPath = Paths.get(this.sourcePath, KEYWORD_MODULES, KEYWORD_CLIENTS,
                        PATH_ENTITIES_FILE).toAbsolutePath();
                for (Path path : fileList) {
                    if (path.toAbsolutePath().toString().equals(clientEntitiesPath.toString())) {
                        skipValidation = true;
                        break;
                    }
                }
                if (!skipValidation) {
                    DiagnosticResult diagnosticResult = hasSemanticDiagnostics(Paths.get(this.sourcePath));
                    ArrayList<String> syntaxDiagnostics = hasSyntacticDiagnostics(Paths.get(this.sourcePath));
                    if (!syntaxDiagnostics.isEmpty()) {
                        StringBuilder errorMessage = new StringBuilder();
                        errorMessage.append("Error occurred when validating the project." +
                                " The project contains syntax errors. ");
                        for (String d : syntaxDiagnostics) {
                            errorMessage.append(System.lineSeparator());
                            errorMessage.append(d);
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
                }
                for (Path filePath : fileList) {
                    if (filePath.toString().endsWith(EXTENSION_BAL) &&
                            !filePath.toAbsolutePath().equals(clientEntitiesPath)) {
                        String[] pathElements = filePath.toString().strip().split(Pattern.quote(File.separator));
                        Optional<String> module = Optional.empty();
                        String[] dirElements = this.sourcePath.split(Pattern.quote(File.separator));
                        if (pathElements.length > 2 && !Arrays.asList(dirElements).contains(pathElements[
                                pathElements.length - 2])) {
                            module = Optional.of(pathElements[pathElements.length - 2]);
                        }
                        EntityMetaData retEntityMetaData = BalSyntaxTreeGenerator.getEntityRecord(filePath, module);
                        ArrayList<Entity> retData = retEntityMetaData.entityArray;
                        ArrayList<ModuleMemberDeclarationNode> retMembers = retEntityMetaData.moduleMembersArray;
                        if (retData.size() != 0) {
                            returnMetaData.addAll(retData);
                            returnModuleMembers.addAll(retMembers);
                        }
                    }
                }
                generateRelations(returnMetaData);
                formatModuleMembers(returnModuleMembers, returnMetaData);
                return new EntityMetaData(returnMetaData, returnModuleMembers);
            }

        } catch (IOException e) {
            throw new BalException("Error while reading entities in the Ballerina project. " + e.getMessage());
        }
        return new EntityMetaData(new ArrayList<>(), new ArrayList<>());
    }

    private void generateClientBalFile(Entity entity, ArrayList<ImportDeclarationNode> imports) throws BalException {
        SyntaxTree balTree = BalSyntaxTreeGenerator.generateClientSyntaxTree(entity, imports);
        String clientPath = Paths.get(this.sourcePath, KEYWORD_MODULES, KEYWORD_CLIENTS,
                    entity.getEntityName().toLowerCase() + "_client.bal").toAbsolutePath().toString();
        try {
            writeOutputFile(balTree, clientPath);
        } catch (IOException | FormatterException e) {
            throw new BalException(String.format("Error while generating the client for the" +
                    " %s entity. ", entity.getEntityName()) + e.getMessage());
        }
    }
    private void generateConfigurationBalFile() throws BalException {
        SyntaxTree configTree = BalSyntaxTreeGenerator.generateConfigSyntaxTree();
        try {
            writeOutputFile(configTree, Paths.get(this.sourcePath, KEYWORD_MODULES,
                            KEYWORD_CLIENTS, PATH_CONFIGURATION_BAL_FILE)
                    .toAbsolutePath().toString());
        } catch (IOException e) {
            throw new BalException("Error occurred while writing database configuration file!");
        } catch (FormatterException e) {
            throw new BalException("Error occurred while formatting database configuration file!");
        }
    }

    private void copyEntities(ArrayList<Entity> entityArray, ArrayList<ModuleMemberDeclarationNode> moduleMembers,
                              ArrayList<ImportDeclarationNode> importArray)
            throws BalException {
        SyntaxTree copiedEntitiesTree = BalSyntaxTreeGenerator.copyEntities(moduleMembers, importArray);
        try {
            writeOutputFile(copiedEntitiesTree, Paths.get(this.sourcePath, KEYWORD_MODULES,
                            KEYWORD_CLIENTS, PATH_ENTITIES_FILE)
                    .toAbsolutePath().toString());
        } catch (IOException e) {
            throw new BalException("Error occurred while writing database configuration file!");
        } catch (FormatterException e) {
            throw new BalException("Error occurred while formatting database configuration file!");
        }
    }

    private static void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws IOException, FormatterException,
            BalException {
        String content;
        content = Formatter.format(syntaxTree.toSourceCode());
        Path pathToFile = Paths.get(outPath);
        if (!Files.exists(pathToFile.getParent())) {
            try {
                Files.createDirectories(pathToFile.getParent());
                outStream.println("Added new Ballerina module at modules/clients");
            } catch (IOException e) {
                throw new BalException("Error while adding new Ballerina module at modules/clients. " +
                        e.getMessage());
            }
        }
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
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
