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
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.BalSyntaxGenerator;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.utils.BalProjectUtils.getBuildProject;
import static io.ballerina.persist.utils.BalProjectUtils.getEntityModule;


/**
 * This Class implements the `persist generate` command in Ballerina persist-tool.
 *
 * @since 0.1.0
 */
@CommandLine.Command(
        name = "generate",
        description = "Generate Ballerina client for the entity."
)

public class Generate implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-generate";

    public Generate() {
        this("");
    }

    public Generate(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        Path projectPath = Paths.get(sourcePath);
        try {
            ProjectLoader.loadProject(projectPath);
        } catch (ProjectException e) {
            errStream.println("Not a Ballerina project (or any parent up to mount point)\n" +
                    "You should run this command inside a Ballerina project. ");
            return;
        }

        Path persistTomlPath = Paths.get(this.sourcePath, PERSIST_DIRECTORY, PERSIST_TOML_FILE);
        if (!Files.exists(persistTomlPath)) {
            errStream.println("Persist project is not initiated. Please run `bal persist init` " +
                    "to initiate the project before generation");
            return;
        }

        Module entityModule;
        Path generatedSourceDirPath;
        try {
            BuildProject buildProject = getBuildProject(projectPath);
            io.ballerina.projects.Module module = getEntityModule(buildProject);
            if (module.moduleName().moduleNamePart() == null) {
                generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
            } else {
                generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY,
                        module.moduleName().moduleNamePart());
            }
            entityModule = BalProjectUtils.getEntities(module);
        } catch (BalException e) {
            errStream.println("Error while reading entities in the Ballerina project. " + e.getMessage());
            return;
        }

        generatePersistClients(entityModule, generatedSourceDirPath);
    }

    public static void generatePersistClients(Module entityModule, Path outputPath) {
        try {
            Collection<Entity> entityArray = entityModule.getEntityMap().values();
            if (entityArray.size() != 0) {
                for (Entity entity : entityArray) {
                    generateClientBalFile(entity, outputPath);
                    errStream.printf("Generated Ballerina client file for entity %s, " +
                            "inside clients sub module.%n", entity.getEntityName());
                }
            }
        } catch (BalException e) {
            errStream.println("Error while generating clients for entities. " + e.getMessage());
        }
    }

    private static void generateClientBalFile(Entity entity, Path outputPath) throws BalException {
        ArrayList<ImportDeclarationNode> imports = new ArrayList<>();
        SyntaxTree balTree = BalSyntaxGenerator.generateClientSyntaxTree(entity, imports);
        String clientPath = outputPath.resolve(
                entity.getEntityName().toLowerCase(Locale.getDefault()) + "_client.bal").
                toAbsolutePath().toString();
        try {
            writeOutputFile(balTree, clientPath);
        } catch (IOException | FormatterException e) {
            throw new BalException(String.format("Error while generating the client for the" +
                    " %s entity. ", entity.getEntityName()) + e.getMessage());
        }
    }

    private static void writeOutputFile(SyntaxTree syntaxTree, String outPath) throws IOException, FormatterException {
        String content;
        content = Formatter.format(syntaxTree.toSourceCode());
        try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
            writer.println(content);
        }
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
