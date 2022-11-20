/*
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
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
package io.ballerina.persist.utils;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.tools.diagnostics.Diagnostic;

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
import static io.ballerina.persist.utils.BalProjectUtils.hasSemanticDiagnostics;
import static io.ballerina.persist.utils.BalProjectUtils.hasSyntacticDiagnostics;

/**
 *
 */
public class ReadBalFiles {

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
