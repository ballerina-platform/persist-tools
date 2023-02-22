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

package io.ballerina.persist.utils;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxGenerator;
import io.ballerina.projects.BuildOptions;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static io.ballerina.persist.nodegenerator.BalSyntaxGenerator.inferRelationDetails;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */

public class BalProjectUtils {

    private BalProjectUtils() {}

    public static Module getEntities(Path schemaFile) throws BalException {
        Path schemaFilename = schemaFile.getFileName();
        String moduleName;
        if (schemaFilename != null) {
            moduleName = schemaFilename.toString().substring(0, schemaFilename.toString().lastIndexOf('.'));
        } else {
            throw new BalException("the model definition file name is invalid.");
        }
        Module.Builder moduleBuilder = Module.newBuilder(moduleName);

        try {
            SyntaxTree balSyntaxTree = SyntaxTree.from(TextDocuments.from(Files.readString(schemaFile)));
            BalSyntaxGenerator.populateEntities(moduleBuilder, balSyntaxTree);
            Module entityModule = moduleBuilder.build();
            inferRelationDetails(entityModule);
            return entityModule;
        } catch (IOException | BalException | RuntimeException e) {
            throw new BalException(e.getMessage());
        }
    }

    public static void validateSchemaFile(Path schemaPath) throws BalException {
        BuildOptions.BuildOptionsBuilder buildOptionsBuilder = BuildOptions.builder();
        buildOptionsBuilder.setOffline(true);
        SingleFileProject buildProject = SingleFileProject.load(schemaPath.toAbsolutePath(),
                buildOptionsBuilder.build());
        Package currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        if (diagnosticResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append(String.format("the model definition file(%s) has errors.", schemaPath.getFileName()));
            int validErrors = 0;
            for (Diagnostic diagnostic : diagnosticResult.errors()) {
                errorMessage.append(System.lineSeparator());
                errorMessage.append(diagnostic);
                validErrors += 1;
            }
            if (validErrors > 0) {
                throw new BalException(errorMessage.toString());
            }
        }
    }

    public static void validateBallerinaProject(Path projectPath) throws BalException {
        Optional<Path> ballerinaToml;
        try (Stream<Path> stream = Files.list(projectPath)) {
            ballerinaToml = stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .filter(Objects::nonNull)
                    .filter(file -> BALLERINA_TOML.equals(file.toString()))
                    .findFirst();
        } catch (IOException e) {
            throw new BalException(String.format("ERROR: invalid Ballerina package directory: %s, " +
                    "%s.%n", projectPath.toAbsolutePath(), e.getMessage()));
        }
        if (ballerinaToml.isEmpty()) {
            throw new BalException(String.format("ERROR: invalid Ballerina package directory: %s, " +
                    "cannot find 'Ballerina.toml' file.%n", projectPath.toAbsolutePath()));
        }
    }
}
