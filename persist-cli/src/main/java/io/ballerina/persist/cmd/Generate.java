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
import io.ballerina.persist.nodegenerator.TomlSyntaxGenerator;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.persist.utils.SqlScriptGenerationUtils;
import io.ballerina.projects.util.ProjectUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.CONFIG_SCRIPT_FILE;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.PATH_CONFIGURATION_BAL_FILE;
import static io.ballerina.persist.nodegenerator.BalSyntaxGenerator.generateClientSyntaxTree;
import static io.ballerina.persist.nodegenerator.TomlSyntaxGenerator.readPackageName;
import static io.ballerina.persist.nodegenerator.TomlSyntaxGenerator.readPersistConfigurations;
import static io.ballerina.persist.utils.BalProjectUtils.validateBallerinaProject;
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
            validateBallerinaProject(projectPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        Path persistDir = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
        if (!Files.isDirectory(persistDir, NOFOLLOW_LINKS)) {
            errStream.println("ERROR: the persist directory inside the Ballerina project does not exist. " +
                    "run `bal persist init` to initiate the project before generation");
            return;
        }

        List<Path> schemaFilePaths;
        try (Stream<Path> stream = Files.list(persistDir)) {
            schemaFilePaths = stream.filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(".bal"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            errStream.println("ERROR: failed to list the model definition files in the persist directory. "
                    + e.getMessage());
            return;
        }

        if (schemaFilePaths.isEmpty()) {
            errStream.println("ERROR: the persist directory does not contain any model definition file. " +
                    "run `bal persist init` to initiate the project before generation.");
            return;
        } else if (schemaFilePaths.size() > 1) {
            errStream.println("ERROR: persist directory contains multiple schema files. ");
            return;
        }

        String packageName;
        try {
            packageName = readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }
        schemaFilePaths.forEach(file -> {
            Module entityModule;
            Path generatedSourceDirPath;

            try {
                BalProjectUtils.validateSchemaFile(file);
                entityModule = BalProjectUtils.getEntities(file);
                String fileName = entityModule.getModuleName();
                if (!ProjectUtils.validateModuleName(fileName)) {
                    errStream.println("ERROR: invalid definition file name : '" + fileName + "' :\n" +
                            "file name can only contain alphanumerics, underscores and periods");
                    return;
                } else if (!ProjectUtils.validateNameLength(fileName)) {
                    errStream.println("ERROR: invalid definition file name : '" + fileName + "' :\n" +
                            "maximum length of file name is 256 characters");
                    return;
                }
                generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
                HashMap<String, String> persistConfig = readPersistConfigurations(
                        Paths.get(this.sourcePath, "Ballerina.toml"));
                if (!persistConfig.get("module").equals(packageName)) {
                    String submodule = persistConfig.get("module").split("\\.")[1];
                    generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY,
                            submodule);
                }
                if (!Files.exists(generatedSourceDirPath)) {
                    try {
                        if (!persistConfig.get("module").equals(packageName) && !Files.exists(
                                Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY))) {
                            Files.createDirectory(Paths.get(this.sourcePath,
                                    BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY).toAbsolutePath());

                        }
                        Files.createDirectory(generatedSourceDirPath.toAbsolutePath());
                    } catch (IOException e) {
                        errStream.println("ERROR: failed to create the generated directory. " + e.getMessage());
                        return;
                    }
                }

                Path databaseConfigPath = generatedSourceDirPath.resolve(PATH_CONFIGURATION_BAL_FILE);
                if (!Files.exists(databaseConfigPath)) {
                    try {
                        generateConfigurationBalFile(generatedSourceDirPath);
                        errStream.printf(
                                "Created database_configurations.bal file inside `%s` module in generated directory.%n",
                                fileName.equals(packageName) ? "default" : fileName);
                    } catch (BalException e) {
                        errStream.println("ERROR: failed to generate the database_configurations.bal file. "
                                + e.getMessage());
                        return;
                    }
                }

                if (!Files.exists(Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath())) {
                    createConfigTomlFile(schemaFilePaths, packageName);
                } else {
                    updateConfigTomlFile(schemaFilePaths, packageName);
                }

                if (entityModule.getEntityMap().isEmpty()) {
                    errStream.printf("ERROR: the model definition file(%s) does not contain any entity definition.%n",
                            file.getFileName());
                    return;
                }
                if (!Files.exists(generatedSourceDirPath)) {
                    errStream.printf("ERROR: the generated source directory: %s does not exist. " +
                                    "run `bal persist init` to initiate the project before generation.%n",
                            generatedSourceDirPath.toAbsolutePath());
                    return;
                }
                generateDataTypes(entityModule, generatedSourceDirPath);
                generateClientBalFile(entityModule, generatedSourceDirPath);
            } catch (BalException e) {
                errStream.printf("ERROR: failed to generate types and client for the definition file(%s). %s%n",
                        file.getFileName(), e.getMessage());
                return;
            }

            try {
                ArrayList<Entity> entityArray = new ArrayList<>(entityModule.getEntityMap().values());
                String[] sqlScripts = SqlScriptGenerationUtils.generateSqlScript(entityArray);
                SqlScriptGenerationUtils.writeScriptFile(entityModule.getModuleName(), sqlScripts,
                        generatedSourceDirPath);
            } catch (BalException e) {
                errStream.printf("ERROR: failed to generate SQL schema for the definition file(%s). %s%n",
                        file.getFileName(), e.getMessage());
            }
        });

    }

    private void generateConfigurationBalFile(Path generatedSourcePath) throws BalException {
        try {
            SyntaxTree configTree = BalSyntaxGenerator.generateDatabaseConfigSyntaxTree();
            writeOutputSyntaxTree(configTree, generatedSourcePath.resolve(PATH_CONFIGURATION_BAL_FILE)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException(e.getMessage());
        }
    }

    private static void generateClientBalFile(Module entityModule, Path outputPath) throws BalException {
        String clientPath = outputPath.resolve("generated_client.bal").toAbsolutePath().toString();

        SyntaxTree balTree = generateClientSyntaxTree(entityModule);
        try {
            writeOutputSyntaxTree(balTree, clientPath);
            errStream.printf("Generated Ballerina client object for the `%s` data model" +
                    " inside the generated directory.%n", entityModule.getModuleName());
        } catch (IOException | FormatterException e) {
            throw new BalException(String.format("could not write the client code for the `%s` data model " +
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
            writeOutputSyntaxTree(generatedTypes, generatedTypesPath);
        } catch (IOException | FormatterException e) {
            throw new BalException(String.format(
                    "could not write the types for the %s data model to the generated_types.bal file. ",
                    entityModule.getModuleName()) + e.getMessage());
        }
    }

    private void createConfigTomlFile(List<Path> schemas, String packageName) throws BalException {
        try {
            Path configPath = Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath();
            String syntaxTree = TomlSyntaxGenerator.createConfigToml(schemas, packageName);
            writeOutputFile(syntaxTree, configPath.toString());
            errStream.println("Created Config.toml file inside the Ballerina project.");
        } catch (Exception e) {
            throw new BalException("could not add Config.toml file inside the Ballerina project. " +
                    e.getMessage());
        }
    }

    private void writeOutputFile(String syntaxTree, String outPath) throws Exception {
        String content;
        Path pathToFile = Paths.get(outPath);
        Path parentDirectory = pathToFile.getParent();
        if (Objects.nonNull(parentDirectory)) {
            try {
                Files.createDirectories(parentDirectory);
            } catch (IOException e) {
                throw new BalException(
                        String.format("could not create the parent directories of output path %s. %s",
                                parentDirectory, e.getMessage()));
            }
            content = syntaxTree;
            try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8.name())) {
                writer.println(content);
            }
        }
    }

    private void updateConfigTomlFile(List<Path> schemas, String packageName) throws BalException {
        try {
            Path configPath = Paths.get(this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath();
            String syntaxTree = TomlSyntaxGenerator.updateConfigToml(configPath, schemas, packageName);
            writeOutputFile(syntaxTree, configPath.toString());
            errStream.println("Updated Config.toml file inside the Ballerina project.");
        } catch (Exception e) {
            throw new BalException("could not update Config.toml file inside the Ballerina project. " +
                    e.getMessage());
        }
    }

    private static void writeOutputSyntaxTree(SyntaxTree syntaxTree, String outPath) throws IOException,
            FormatterException {
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
