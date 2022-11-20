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
import io.ballerina.persist.utils.ReadBalFiles;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static io.ballerina.persist.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.PersistToolsConstants.SUBMODULE_PERSIST;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_CLIENTS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_MODULES;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PATH_ENTITIES_FILE;


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

    public Generate() {}

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        Path persistTomlPath = Paths.get(this.sourcePath, SUBMODULE_PERSIST, PERSIST_TOML_FILE);
        File persistToml = new File(persistTomlPath.toString());
        if (!persistToml.exists()) {
            errStream.println("Persist project is not initiated. Please run `bal persist init` " +
                    "to initiate the project before generation");
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
            EntityMetaData retEntityMetaData = ReadBalFiles.readBalFiles(this.sourcePath);
            ArrayList<Entity> entityArray = retEntityMetaData.entityArray;
            ArrayList<ModuleMemberDeclarationNode> returnModuleMembers = retEntityMetaData.moduleMembersArray;
            ArrayList<ImportDeclarationNode> imports = new ArrayList<>();
            if (entityArray.size() != 0) {
                for (Entity entity : entityArray) {
                    entity.setPackageName(balProject.currentPackage().descriptor().org().value() + "/"
                            + balProject.currentPackage().descriptor().name().value());
                    generateClientBalFile(entity, imports);
                    outStream.printf("Generated Ballerina client file for entity %s, " +
                            "inside clients sub module.%n", entity.getEntityName());
                }
                copyEntities(returnModuleMembers, imports);
                outStream.println("Created entities.bal");
            }
        } catch (Exception e) {
            errStream.println(e.getMessage());
        }
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

    private void copyEntities(ArrayList<ModuleMemberDeclarationNode> moduleMembers,
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

    private static void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws IOException, FormatterException {
        String content;
        content = Formatter.format(syntaxTree.toSourceCode());
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
