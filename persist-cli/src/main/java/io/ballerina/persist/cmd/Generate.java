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
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.BalSyntaxGenerator;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.ProjectException;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.BalSyntaxGenerator.generateClientSyntaxTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;


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

        Path persistDir = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
        if (!Files.isDirectory(persistDir, NOFOLLOW_LINKS)) {
            errStream.println("The persist directory inside the Ballerina project doesn't exist. " +
                    "Please run `bal persist init` to initiate the project before generation");
            return;
        }

        List<Path> schemaFilePaths;
        try (Stream<Path> stream = Files.list(persistDir)) {
            schemaFilePaths = stream.filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(".bal"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            errStream.println("Error while listing the persist schema files in persist directory. " + e.getMessage());
            return;
        }

        if (schemaFilePaths.isEmpty()) {
            errStream.println("The persist directory doesn't contain any schema file. " +
                    "Please run `bal persist init` to initiate the project before generation");
            return;
        }

        schemaFilePaths.forEach(file -> {
            Module entityModule;
            Path generatedSourceDirPath;
            try {
                BalProjectUtils.validateSchemaFile(file);
                entityModule = BalProjectUtils.getEntities(file);
                generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY,
                        entityModule.getModuleName());
                generateDataTypes(entityModule, generatedSourceDirPath);
                generateClientBalFile(entityModule, generatedSourceDirPath);
            } catch (BalException e) {
                errStream.println("Error while generating types and client for the schema in "
                        + file + " file . " + e.getMessage());
            }
        });
    }

    private static void generateClientBalFile(Module entityModule, Path outputPath) throws BalException {
        String clientPath = outputPath.resolve("generated_client.bal").toAbsolutePath().toString();

        SyntaxTree balTree = generateClientSyntaxTree(entityModule);
        try {
            writeOutputFile(balTree, clientPath);
        } catch (IOException | FormatterException e) {
            throw new BalException(String.format("Failed to write the client code for the %s data model " +
                    "to the generated_types.bal file.", entityModule.getModuleName()) + e.getMessage());
        }
    }

    public static void generateDataTypes(Module entityModule, Path outputPath) throws BalException {
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() != 0) {

            generateTypeBalFile(entityModule, outputPath);
            errStream.printf("Generated Ballerina types for the `%s` data model" +
                    " inside the generated directory.%n", entityModule.getModuleName());
        }
    }

    private static void generateTypeBalFile(Module entityModule, Path outputPath) throws BalException {
        SyntaxTree generatedTypes =  BalSyntaxGenerator.generateTypeSyntaxTree(entityModule);
        String generatedTypesPath = outputPath.resolve("generated_types.bal").toAbsolutePath().toString();
        try {
            writeOutputFile(generatedTypes, generatedTypesPath);
        } catch (IOException | FormatterException e) {
            throw new BalException(String.format(
                    "Failed to write the types for the %s data model to the generated_types.bal file. ",
                    entityModule.getModuleName()) + e.getMessage());
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
