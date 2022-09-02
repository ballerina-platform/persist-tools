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
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */

public class BalProjectUtils {
    private BalProjectUtils() {

    }

    public static boolean hasSyntacticDiagnostics(Path filePath) {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            return false;
        }
        TextDocument textDocument = TextDocuments.from(content);
        return SyntaxTree.from(textDocument).hasDiagnostics();
    }

    public static boolean hasSemanticDiagnostics(Path projectPath,
                                                 ProjectEnvironmentBuilder projectEnvironmentBuilder) {
        Package currentPackage;
        BuildProject buildProject;
        if (projectEnvironmentBuilder == null) {
            buildProject = BuildProject.load(projectPath.toAbsolutePath());
        } else {
            buildProject = BuildProject.load(projectEnvironmentBuilder, projectPath);
        }
        currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        return diagnosticResult.hasErrors();
    }



}
