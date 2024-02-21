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
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.util.ProjectUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

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
        Path generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
        String datastore;
        Module entityModule;
        Path schemaFilePath;
        String packageName;
        String moduleNameWithPackageName;
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        Path projectPath = Paths.get(sourcePath);
        try {
            BalProjectUtils.validateBallerinaProject(projectPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            packageName = TomlSyntaxUtils.readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            HashMap<String, String> ballerinaTomlConfig = TomlSyntaxUtils.readBallerinaTomlConfig(
                    Paths.get(this.sourcePath, "Ballerina.toml"));
            moduleNameWithPackageName = ballerinaTomlConfig.get("module").trim();
            if (!moduleNameWithPackageName.equals(packageName)) {
                if (!moduleNameWithPackageName.startsWith(packageName + ".")) {
                    errStream.println("ERROR: invalid module name : '" + ballerinaTomlConfig.get("module") + "' :\n" +
                            "module name should follow the template <package_name>.<module_name>");
                    return;
                }
                String moduleName = moduleNameWithPackageName.replace(packageName + ".", "");
                if (!ProjectUtils.validateModuleName(moduleName)) {
                    errStream.println("ERROR: invalid module name : '" + moduleName + "' :\n" +
                            "module name can only contain alphanumerics, underscores and periods");
                    return;
                } else if (!ProjectUtils.validateNameLength(moduleName)) {
                    errStream.println("ERROR: invalid module name : '" + moduleName + "' :\n" +
                            "maximum length of module name is 256 characters");
                    return;
                }
                generatedSourceDirPath = generatedSourceDirPath.resolve(moduleName);
            }
            datastore = ballerinaTomlConfig.get("datastore").trim();
            if (!PersistToolsConstants.SUPPORTED_DB_PROVIDERS.contains(datastore)) {
                errStream.printf("ERROR: the persist layer supports one of data stores: %s" +
                                ". but found '%s' datasource.%n",
                        Arrays.toString(PersistToolsConstants.SUPPORTED_DB_PROVIDERS.toArray()), datastore);
                return;
            }
            if (Files.isDirectory(Paths.get(sourcePath, PersistToolsConstants.PERSIST_DIRECTORY,
                    PersistToolsConstants.MIGRATIONS)) &&
                    !datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
                errStream.println("ERROR: regenerating the client with a different datastore after executing " +
                        "the migrate command is not permitted. please remove the migrations directory within the " +
                        "persist directory and try executing the command again.");
                return;
            }
        } catch (BalException e) {
            errStream.printf("ERROR: failed to generate types and client for the definition file(%s). %s%n",
                    "Ballerina.toml", e.getMessage());
            return;
        }

        if (datastore.equals(PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS)) {
            errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for Google Sheets data store " +
                    "is currently an experimental feature, and its behavior may be subject to change in future " +
                    "releases." + System.lineSeparator());
        }

        if (datastore.equals(PersistToolsConstants.SupportedDataSources.REDIS)) {
            errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for Redis data store " +
                    "is currently an experimental feature, and its behavior may be subject to change in future " +
                    "releases." + System.lineSeparator());
        }

        try {
            schemaFilePath =  BalProjectUtils.getSchemaFilePath(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            BalProjectUtils.validateSchemaFile(schemaFilePath);
            Module module = BalProjectUtils.getEntities(schemaFilePath);
            if (module.getEntityMap().isEmpty()) {
                errStream.printf("ERROR: the model definition file(%s) does not contain any entity definition.%n",
                        schemaFilePath.getFileName());
                return;
            }
            entityModule = module;
        } catch (BalException e) {
            errStream.printf("ERROR: failed to generate types and client for the definition file(%s). %s%n",
                    schemaFilePath.getFileName(), e.getMessage());
            return;
        }

        if (!Files.exists(generatedSourceDirPath)) {
            try {
                Files.createDirectories(generatedSourceDirPath.toAbsolutePath());
            } catch (IOException e) {
                errStream.println("ERROR: failed to create the generated directory. " + e.getMessage());
                return;
            }
        }
        SourceGenerator sourceCreator = new SourceGenerator(sourcePath, generatedSourceDirPath,
                moduleNameWithPackageName, entityModule);
        try {
            switch (datastore) {
                case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                    sourceCreator.createDbSources(datastore);
                    break;
                case PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS:
                    sourceCreator.createGSheetSources();
                    break;
                case PersistToolsConstants.SupportedDataSources.REDIS:
                    sourceCreator.createRedisSources();
                    break;
                default:
                    sourceCreator.createInMemorySources();
                    break;
            }
        } catch (BalException e) {
            errStream.printf(String.format(BalSyntaxConstants.ERROR_MSG,
                    datastore, e.getMessage()));
            return;
        }
        errStream.println("Persist client and entity types generated successfully in the ./generated directory.");
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
