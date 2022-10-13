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
import io.ballerina.persist.objects.BalException;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */

public class BalProjectUtils {

    private BalProjectUtils() {

    }

    public static ArrayList<String> hasSyntacticDiagnostics(Path filePath) throws IOException, BalException {
        ArrayList<String> diagnostics = new ArrayList<>();
        List<Path> pathList = listFiles(filePath);
        for (Path path : pathList) {
            if (path.toString().endsWith(".bal")) {
                TextDocument textDocument = TextDocuments.from(Files.readString(path));
                for (Diagnostic diagnostic : SyntaxTree.from(textDocument).diagnostics()) {
                    diagnostics.add(fotmatError(diagnostic.toString(), path));
                }
            }
        }
        return diagnostics;
    }

    public static DiagnosticResult hasSemanticDiagnostics(Path projectPath) {
        Package currentPackage;
        BuildProject buildProject;
        buildProject = BuildProject.load(projectPath.toAbsolutePath());
        currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        return compilation.diagnosticResult();
    }

    private static List<Path> listFiles(Path path) throws BalException {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk != null ? walk.filter(Files::isRegularFile).collect(Collectors.toList()) : new ArrayList<>();
        } catch (IOException e) {
            throw new BalException("Error occurred while reading bal : " + e.getMessage());
        }
    }

    private static String fotmatError(String errorMessage, Path path) {
        if (errorMessage.contains("[null:")) {
            return String.format("ERROR [%s:" + errorMessage.split(":", 2)[1], path.toFile().getName());
        }
        return errorMessage;
    }
}
