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
import io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.tools.diagnostics.Diagnostic;

import java.io.File;
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

    private BalProjectUtils() {}

    public static DiagnosticResult hasSemanticDiagnostics(Path projectPath) {
        Package currentPackage;
        BuildProject buildProject;
        buildProject = BuildProject.load(projectPath.toAbsolutePath());
        currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        return compilation.diagnosticResult();
    }

    public static EntityMetaData readBalFiles(String sourcePath) throws BalException {
        ArrayList<Entity> returnMetaData = new ArrayList<>();
        ArrayList<ModuleMemberDeclarationNode> returnModuleMembers = new ArrayList<>();
        Path dirPath = Paths.get(sourcePath);
        List<Path> fileList;
        Path clientEntitiesPath = Paths.get(sourcePath, KEYWORD_MODULES, KEYWORD_CLIENTS,
                PATH_ENTITIES_FILE).toAbsolutePath();
        File entitiesBal = new File(clientEntitiesPath.toString());
        try {
            if (!entitiesBal.exists()) {
                DiagnosticResult diagnosticResult = hasSemanticDiagnostics(dirPath);
                if (diagnosticResult.hasErrors()) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Error occurred when validating the project. ");
                    for (Diagnostic d : diagnosticResult.errors()) {
                        errorMessage.append(System.lineSeparator());
                        errorMessage.append(d.toString());
                    }
                    throw new BalException(errorMessage.toString());
                }
            }
            try (Stream<Path> walk = Files.walk(dirPath)) {
                if (walk != null) {
                    fileList = walk.filter(Files::isRegularFile).collect(Collectors.toList());
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
            }

        } catch (IOException e) {
            throw new BalException("Error while reading entities in the Ballerina project. " + e.getMessage());
        }
        return new EntityMetaData(new ArrayList<>(), new ArrayList<>());
    }
}
