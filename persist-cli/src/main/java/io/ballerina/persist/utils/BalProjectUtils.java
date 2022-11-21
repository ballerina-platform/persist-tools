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

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.nodegenerator.BalFileConstants.EXTENSION_BAL;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_CLIENTS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_MODULES;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PATH_ENTITIES_FILE;
import static io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator.formatModuleMembers;
import static io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator.generateRelations;

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

    public static EntityMetaData readBalFiles(String sourcePath) throws BalException {
        ArrayList<Entity> returnMetaData = new ArrayList<>();
        ArrayList<ModuleMemberDeclarationNode> returnModuleMembers = new ArrayList<>();
        Path dirPath = Paths.get(sourcePath);
        List<Path> fileList;

        try (Stream<Path> walk = Files.walk(dirPath)) {
            boolean skipValidation = false;
            if (walk != null) {
                fileList = walk.filter(Files::isRegularFile).collect(Collectors.toList());
                Path clientEntitiesPath = Paths.get(sourcePath, KEYWORD_MODULES, KEYWORD_CLIENTS,
                        PATH_ENTITIES_FILE).toAbsolutePath();
                for (Path path : fileList) {
                    if (path.toAbsolutePath().toString().equals(clientEntitiesPath.toString())) {
                        skipValidation = true;
                        break;
                    }
                }
                if (!skipValidation) {
                    DiagnosticResult diagnosticResult = hasSemanticDiagnostics(dirPath);
                    ArrayList<String> syntaxDiagnostics = hasSyntacticDiagnostics(dirPath);
                    if (!syntaxDiagnostics.isEmpty()) {
                        StringBuilder errorMessage = new StringBuilder();
                        errorMessage.append("Error occurred when validating the project." +
                                " The project contains syntax errors. ");
                        for (String d : syntaxDiagnostics) {
                            errorMessage.append(System.lineSeparator());
                            errorMessage.append(d);
                        }
                        throw new BalException(errorMessage.toString());
                    }
                    if (diagnosticResult.hasErrors()) {
                        StringBuilder errorMessage = new StringBuilder();
                        errorMessage.append("Error occurred when validating the project." +
                                " The project contains semantic errors. ");
                        for (Diagnostic d : diagnosticResult.errors()) {
                            errorMessage.append(System.lineSeparator());
                            errorMessage.append(d.toString());
                        }
                        throw new BalException(errorMessage.toString());
                    }
                }
                for (Path filePath : fileList) {
                    if (filePath.toString().endsWith(EXTENSION_BAL) &&
                            !filePath.toAbsolutePath().equals(clientEntitiesPath)) {
                        EntityMetaData retEntityMetaData = BalSyntaxTreeGenerator.getEntityRecord(filePath);
                        ArrayList<Entity> retData = retEntityMetaData.entityArray;
                        ArrayList<ModuleMemberDeclarationNode> retMembers = retEntityMetaData.moduleMembersArray;
                        if (retData.size() != 0) {
                            returnMetaData.addAll(retData);
                            returnModuleMembers.addAll(retMembers);
                        }
                    }
                }
                generateRelations(returnMetaData);
                returnModuleMembers = formatModuleMembers(returnModuleMembers, returnMetaData);
                return new EntityMetaData(returnMetaData, returnModuleMembers);
            }

        } catch (IOException e) {
            throw new BalException("Error while reading entities in the Ballerina project. " + e.getMessage());
        }
        return new EntityMetaData(new ArrayList<>(), new ArrayList<>());
    }
}
