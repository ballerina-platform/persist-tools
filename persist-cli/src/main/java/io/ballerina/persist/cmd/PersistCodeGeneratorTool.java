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
import io.ballerina.projects.buildtools.ToolConfig;
import io.ballerina.projects.buildtools.ToolContext;
import io.ballerina.projects.util.ProjectUtils;
import io.ballerina.toml.semantic.diagnostics.TomlNodeLocation;
import io.ballerina.tools.diagnostics.DiagnosticFactory;
import io.ballerina.tools.diagnostics.DiagnosticInfo;
import io.ballerina.tools.diagnostics.Location;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static io.ballerina.persist.PersistToolsConstants.CACHE_FILE;
import static io.ballerina.persist.PersistToolsConstants.OPTION_DATASTORE;
import static io.ballerina.persist.PersistToolsConstants.OPTION_TEST_DATASTORE;
import static io.ballerina.persist.PersistToolsConstants.TARGET_MODULE;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.getConfigDeclaration;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.getDependencyConfig;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.populateNativeDependencyConfig;
import static io.ballerina.persist.utils.BalProjectUtils.validateDatastore;
import static io.ballerina.persist.utils.BalProjectUtils.validateTestDatastore;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

@ToolConfig(name = "persist")
public class PersistCodeGeneratorTool implements CodeGeneratorTool {

    private static final PrintStream errStream = System.err;

    @Override
    public void execute(ToolContext toolContext) {
        String datastore;
        Module entityModule;
        Path schemaFilePath;
        String packageName;
        String targetModule;
        String testDatastore;

        TomlNodeLocation location = toolContext.currentPackage().ballerinaToml().get().tomlAstNode().location();
        Path projectPath = toolContext.currentPackage().project().sourceRoot();
        Path generatedSourceDirPath = Paths.get(projectPath.toString(), BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
        try {
            BalProjectUtils.validateBallerinaProject(projectPath);
            packageName = TomlSyntaxUtils.readPackageName(projectPath.toString());
            schemaFilePath = BalProjectUtils.getSchemaFilePath(projectPath.toString());
            HashMap<String, String> ballerinaTomlConfig = TomlSyntaxUtils.readBallerinaTomlConfig(
                    Paths.get(projectPath.toString(), BALLERINA_TOML));
            targetModule = ballerinaTomlConfig.get(TARGET_MODULE).trim();
            datastore = ballerinaTomlConfig.get(OPTION_DATASTORE).trim();
            testDatastore = ballerinaTomlConfig.get(OPTION_TEST_DATASTORE) == null ? null :
                    ballerinaTomlConfig.get(OPTION_TEST_DATASTORE).trim();

            validateDatastore(datastore);
            validateTestDatastore(datastore, testDatastore);
            if (!targetModule.equals(packageName)) {
                if (!targetModule.startsWith(packageName + ".")) {
                    createDiagnostics(toolContext, PersistToolsConstants.DiagnosticMessages.INVALID_MODULE_NAME,
                            location, ballerinaTomlConfig.get(TARGET_MODULE));
                    return;
                }
                String moduleName = targetModule.replace(packageName + ".", "");
                validateModuleName(moduleName);
                generatedSourceDirPath = generatedSourceDirPath.resolve(moduleName);
            }
            validatePersistDirectory(datastore, projectPath);
            printExperimentalFeatureInfo(datastore);
            try {
                if (validateCache(toolContext, schemaFilePath) && Files.exists(generatedSourceDirPath)) {
                    return;
                }
            } catch (NoSuchAlgorithmException e) {
                errStream.println("INFO: unable to validate the cache. Generating sources for the schema file.");
            }
            BalProjectUtils.validateSchemaFile(schemaFilePath);
            entityModule = BalProjectUtils.getEntities(schemaFilePath);
            validateEntityModule(entityModule, schemaFilePath);
            String syntaxTree = updateBallerinaToml(
                    Paths.get(projectPath.toString(), BALLERINA_TOML), datastore, testDatastore);
            Utils.writeOutputString(syntaxTree,
                    Paths.get(projectPath.toString(), BALLERINA_TOML).toAbsolutePath().toString());
            createGeneratedSourceDirIfNotExists(generatedSourceDirPath);
            generateSources(datastore, entityModule, targetModule, projectPath, generatedSourceDirPath);
            generateTestSources(testDatastore, entityModule, targetModule, projectPath, generatedSourceDirPath);
            String modelHashVal = getHashValue(schemaFilePath);
            Path cachePath = toolContext.cachePath();
            updateCacheFile(cachePath, modelHashVal);
            errStream.println("Persist client and entity types generated successfully in the " + targetModule +
                    " directory.");
        } catch (BalException | IOException | NoSuchAlgorithmException e) {
            createDiagnostics(toolContext, PersistToolsConstants.DiagnosticMessages.ERROR_WHILE_GENERATING_CLIENT,
                    location, e.getMessage());
        }
    }

    /**
     * Method to update the Ballerina.toml with persist native dependency.
     */
    private String updateBallerinaToml(Path tomlPath, String datastore, String testDatastore)
            throws BalException, IOException {
        TomlSyntaxUtils.NativeDependency dependency = getDependencyConfig(datastore, testDatastore);
        TomlSyntaxUtils.ConfigDeclaration declaration = getConfigDeclaration(tomlPath, dependency);

        return populateNativeDependencyConfig(datastore, testDatastore, declaration, dependency);
    }

    private void validateModuleName(String moduleName) throws BalException {
        if (!ProjectUtils.validateModuleName(moduleName)) {
            throw new BalException("invalid module name : '" + moduleName + "' :" + System.lineSeparator() +
                    "module name can only contain alphanumerics, underscores and periods");
        } else if (!ProjectUtils.validateNameLength(moduleName)) {
            throw new BalException("invalid module name : '" + moduleName + "' :" + System.lineSeparator() +
                    "maximum length of module name is 256 characters");
        }
    }

    /**
     * This method is used to validate the cache.
     */
    private static boolean validateCache(ToolContext toolContext, Path schemaFilePath)
            throws IOException, NoSuchAlgorithmException {
        Path cachePath = toolContext.cachePath();
        String modelHashVal = getHashValue(schemaFilePath);
        if (!Files.isDirectory(cachePath)) {
            return false;
        }
        // read the cache file
        Path cacheFilePath = Paths.get(cachePath.toString(), CACHE_FILE);
        String cacheContent = Files.readString(Paths.get(cacheFilePath.toString()));
        return cacheContent.equals(modelHashVal);
    }

    private static void updateCacheFile(Path cachePath, String modelHashVal) {
        try {
            Path cacheFilePath = Paths.get(cachePath.toString(), CACHE_FILE);
            if (!Files.exists(cacheFilePath)) {
                Files.createDirectories(cachePath);
                Files.createFile(cacheFilePath);
            }
            Files.writeString(cacheFilePath, modelHashVal, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        } catch (IOException e) {
            errStream.println("ERROR: failed to update the cache file: " + e.getMessage());
        }
    }

    private static String getHashValue(Path schemaFilePath) throws IOException, NoSuchAlgorithmException {
        String schema = readFileToString(schemaFilePath);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = messageDigest.digest(schema.getBytes(StandardCharsets.UTF_8));
        return new String(hashBytes, StandardCharsets.UTF_8);
    }

    public static String readFileToString(Path filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(filePath);
        return new String(fileContent, StandardCharsets.UTF_8);
    }

    private void validatePersistDirectory(String datastore, Path projectPath) throws BalException {
        if (Files.isDirectory(Paths.get(projectPath.toString(), PersistToolsConstants.PERSIST_DIRECTORY,
                PersistToolsConstants.MIGRATIONS)) &&
                !datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
            throw new BalException("regenerating the client with a different datastore after executing " +
                    "the migrate command is not permitted. please remove the migrations directory within the " +
                    "persist directory and try executing the command again.");
        }
    }

    private void printExperimentalFeatureInfo(String datastore) {
        if (datastore.equals(PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS)) {
            errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for Google Sheets data store " +
                    "is currently an experimental feature, and its behavior may be subject to change in future " +
                    "releases." + System.lineSeparator());
        }
    }

    private void validateEntityModule(Module entityModule, Path schemaFilePath) throws BalException {
        if (entityModule.getEntityMap().isEmpty()) {
            throw new BalException(String.format("the model definition file(%s) does not contain any " +
                            "entity definition.", schemaFilePath.getFileName()));
        }
    }

    private void createGeneratedSourceDirIfNotExists(Path generatedSourceDirPath) throws IOException {
        if (!Files.exists(generatedSourceDirPath)) {
                Files.createDirectories(generatedSourceDirPath.toAbsolutePath());
        }
    }

    private void generateSources(String datastore, Module entityModule, String targetModule, Path projectPath,
                                 Path generatedSourceDirPath) throws BalException {
        SourceGenerator sourceCreator = new SourceGenerator(projectPath.toString(), generatedSourceDirPath,
                targetModule, entityModule);
        switch (datastore) {
            case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
            case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
            case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
            case PersistToolsConstants.SupportedDataSources.H2_DB:
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
    }

    private void generateTestSources(String testDatastore, Module entityModule, String targetModule,
                                     Path projectPath, Path generatedSourceDirPath) {
        if (testDatastore != null) {
            try {
                SourceGenerator sourceCreator = new SourceGenerator(projectPath.toString(), generatedSourceDirPath,
                        targetModule, entityModule);
                sourceCreator.createTestDataSources(testDatastore);
            } catch (BalException e) {
                errStream.printf("ERROR: the test data source creation failed. %s%n", e.getMessage());
            }
        }
    }

    private static void createDiagnostics(ToolContext toolContext, PersistToolsConstants.DiagnosticMessages error,
                                          Location location, String... args) {
        String message = String.format(error.getDescription(), (Object[]) args);
        DiagnosticInfo diagnosticInfo = new DiagnosticInfo(error.getCode(), message,
                error.getSeverity());
        toolContext.reportDiagnostic(DiagnosticFactory.createDiagnostic(diagnosticInfo, location));
    }
}
