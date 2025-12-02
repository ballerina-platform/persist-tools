/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
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
package io.ballerina.persist.nodegenerator;

import io.ballerina.persist.BalException;
import io.ballerina.persist.nodegenerator.syntax.sources.DbModelGenSyntaxTree;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.Project;
import org.ballerinalang.formatter.core.Formatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DriverResolver {

    private final Path driverImportFile;
    private final String datastore;
    private final Path tempDirectory;

    public DriverResolver(String datastore) throws BalException {
        try {
            // Create a temporary directory along with some prefix
            this.tempDirectory = Files.createTempDirectory("persist-driver-test-");
            // Set the driver file path in the temp directory
            this.driverImportFile = this.tempDirectory.resolve("driver.bal");
            this.datastore = datastore;
        } catch (IOException e) {
            throw new BalException("failed to create temporary directory: " + e.getMessage());
        }
    }

    public Project resolveDriverDependencies() throws BalException {
        createDriverImportFile();
        return BalProjectUtils.buildDriverFile(driverImportFile);
    }

    private void createDriverImportFile() throws BalException {
        DbModelGenSyntaxTree dbModelGenSyntaxTree = new DbModelGenSyntaxTree();
        try {
            writeOutputFile(Formatter.format(
                    dbModelGenSyntaxTree.createInitialDriverImportFile(datastore).toSourceCode()));
        } catch (Exception e) {
            throw new BalException("failed to create driver import file: " + e.getMessage());
        }
    }

    private void writeOutputFile(String syntaxTree) throws IOException {
        try (PrintWriter writer = new PrintWriter(this.driverImportFile.toString(), StandardCharsets.UTF_8)) {
            writer.println(syntaxTree);
        }
    }

    public void deleteDriverFile() throws BalException {
        try {
            Files.deleteIfExists(driverImportFile);
            if (Files.exists(tempDirectory)) {
                Files.delete(tempDirectory);
            }
        } catch (IOException e) {
            throw new BalException("failed to delete driver import file and temp directory: " + e.getMessage());
        }
    }

}
