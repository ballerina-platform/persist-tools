/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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

import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.buildtools.CodeGeneratorTool;
import io.ballerina.projects.buildtools.ToolContext;
import io.ballerina.projects.util.ProjectUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import static io.ballerina.persist.PersistToolsConstants.TARGET_MODULE;

public class PersistCodeGeneratorTool implements CodeGeneratorTool {

    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    public PersistCodeGeneratorTool() {
        this("");
    }

    public PersistCodeGeneratorTool(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public void execute(ToolContext toolContext) {
        String datastore;
        Module entityModule;
        Path schemaFilePath;
        String packageName;
        String targetModule;
        Path generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);

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
            schemaFilePath =  BalProjectUtils.getSchemaFilePath(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            HashMap<String, String> ballerinaTomlConfig = TomlSyntaxUtils.readBallerinaTomlConfig(
                    Paths.get(this.sourcePath, "Ballerina.toml"));
            targetModule = ballerinaTomlConfig.get(TARGET_MODULE).trim();
            if (!targetModule.equals(packageName)) {
                if (!targetModule.startsWith(packageName + ".")) {
                    errStream.println("ERROR: invalid module name : '" + ballerinaTomlConfig.get(TARGET_MODULE)
                            + "' :\n" + "module name should follow the template <package_name>.<module_name>");
                    return;
                }
                String moduleName = targetModule.replace(packageName + ".", "");
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
            datastore = ballerinaTomlConfig.get("options.datastore").trim();
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
            entityModule = BalProjectUtils.getEntities(schemaFilePath);
            if (entityModule.getEntityMap().isEmpty()) {
                errStream.printf("ERROR: the model definition file(%s) does not contain any entity definition.%n",
                        schemaFilePath.getFileName());
                return;
            }
        } catch (BalException e) {
            errStream.printf("ERROR: failed to generate types and client for the definition file(%s). %s%n",
                    "Ballerina.toml", e.getMessage());
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
                targetModule, entityModule);
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
                default:
                    sourceCreator.createInMemorySources();
                    break;
            }
        } catch (BalException e) {
            errStream.printf(BalSyntaxConstants.ERROR_MSG, datastore, e.getMessage());
            return;
        }
        errStream.println("Persist client and entity types generated successfully in the " + targetModule +
                " directory.");
    }

    @Override
    public String toolName() {
        return "persist";
    }
}
