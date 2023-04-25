/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.persist.components.syntax;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;
import io.ballerina.persist.utils.SqlScriptGenerationUtils;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is used to generate the all files to data source type.
 *
 * @since 0.3.1
 */
public class SourceGenerator {

    private static final String persistTypesBal = "persist_types.bal";
    private static final String persistClientBal = "persist_client.bal";
    public SourceGenerator() {}

    public void createDbSources(String sourcePath, String subModuleName, String moduleName, Module entityModule) 
            throws BalException {
        DbSyntaxTree dbSyntaxTree = new DbSyntaxTree();
        Path generatedSourceDirPath = Paths.get(sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
        if (!subModuleName.isEmpty()) {
            generatedSourceDirPath = generatedSourceDirPath.resolve(subModuleName);
        }
        try {
            createGeneratedDirectory(generatedSourceDirPath); 
            addDbConfigFile(generatedSourceDirPath, dbSyntaxTree.getDataStoreConfigSyntax());
            addOrUpdateConfigFile(sourcePath, dbSyntaxTree, moduleName);
            addDataTypesFile(dbSyntaxTree.getDataTypesSyntax(entityModule),
                    generatedSourceDirPath.resolve(persistTypesBal).toAbsolutePath(), moduleName);
            addClientFile(dbSyntaxTree.getClientSyntax(entityModule),
                    generatedSourceDirPath.resolve(persistClientBal).toAbsolutePath(), moduleName);
            ArrayList<Entity> entityArray = new ArrayList<>(entityModule.getEntityMap().values());
            SqlScriptGenerationUtils.writeScriptFile(entityModule.getModuleName(),
                    SqlScriptGenerationUtils.generateSqlScript(entityArray), generatedSourceDirPath);
        } catch (BalException e) {
            throw new BalException(e.getMessage());
        }
    }

    public void createInMemorySources(String sourcePath, String subModuleName, String moduleName, Module entityModule)
            throws BalException {
        InMemorySyntaxTree inMemorySyntaxTree = new InMemorySyntaxTree();
        Path generatedSourceDirPath = Paths.get(sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
        if (!subModuleName.isEmpty()) {
            generatedSourceDirPath = generatedSourceDirPath.resolve(subModuleName);
        }
        try {
            createGeneratedDirectory(generatedSourceDirPath);
            addDataTypesFile(inMemorySyntaxTree.getDataTypesSyntax(entityModule),
                    generatedSourceDirPath.resolve(persistTypesBal).toAbsolutePath(), moduleName);
            addClientFile(inMemorySyntaxTree.getClientSyntax(entityModule),
                    generatedSourceDirPath.resolve(persistClientBal).toAbsolutePath(), moduleName);
        } catch (BalException e) {
            throw new BalException(e.getMessage());
        }
    }
    
    private void addDbConfigFile(Path generatedSourceDirPath, SyntaxTree syntaxTree) 
            throws BalException {
        Path databaseConfigPath = generatedSourceDirPath.resolve(BalSyntaxConstants.PATH_CONFIGURATION_BAL_FILE);
        if (!Files.exists(databaseConfigPath)) {
            try {
                writeOutputFile(syntaxTree.toSourceCode(), databaseConfigPath.toAbsolutePath());
            } catch (Exception e) {
                throw new BalException("ERROR: failed to generate the database_configurations.bal file. "
                        + e.getMessage());
            }
        }
    }

    private void addOrUpdateConfigFile(String sourcePath, DbSyntaxTree dbSyntaxTree, String moduleName)
            throws BalException {
        Path configPath = Paths.get(sourcePath, PersistToolsConstants.CONFIG_SCRIPT_FILE).toAbsolutePath();
        try {
            if (!Files.exists(configPath.toAbsolutePath())) {
                writeOutputFile(dbSyntaxTree.getConfigTomlSyntax(moduleName).toSourceCode(), configPath);
            } else {
                writeOutputFile(dbSyntaxTree.getUpdateConfigTomlSyntax(configPath, moduleName).toSourceCode(),
                        configPath);
            }
        } catch (IOException e) {
            throw new BalException("could not update Config.toml file inside the Ballerina project. " + e.getMessage());
        }
    }

    private void addDataTypesFile(SyntaxTree syntaxTree, Path path, String moduleName) throws BalException {
        try {
            writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), path);
        } catch (FormatterException | IOException e) {
            throw new BalException(String.format("could not write the client code for the `%s` data model " +
                    "to the generated_types.bal file.", moduleName) + e.getMessage());
        }
    }

    private void addClientFile(SyntaxTree syntaxTree, Path path, String moduleName) throws BalException {
        try {
            writeOutputFile(Formatter.format(syntaxTree.toSourceCode()), path);
        } catch (FormatterException | IOException e) {
            throw new BalException(String.format("could not write the client code for the `%s` data model " +
                    "to the generated_types.bal file.", moduleName) + e.getMessage());
        }
    }
    
    private void createGeneratedDirectory(Path path) throws BalException {
        if (Objects.nonNull(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new BalException(String.format("could not create the parent directories of output path %s. %s",
                                path, e.getMessage()));
            }
        }
    }

    private void writeOutputFile(String syntaxTree, Path outPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(outPath.toString(), StandardCharsets.UTF_8)) {
            writer.println(syntaxTree);
        }
    }
}
