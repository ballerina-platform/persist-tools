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
import io.ballerina.projects.Document;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.Module;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.tools.diagnostics.Diagnostic;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static io.ballerina.persist.nodegenerator.BalFileConstants.CONFIG_MODULE_ENDING;
import static io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator.formatModuleMembers;
import static io.ballerina.persist.nodegenerator.BalSyntaxTreeGenerator.generateRelations;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */

public class BalProjectUtils {

    private BalProjectUtils() {}

    public static EntityMetaData getEntitiesInBalFiles(String sourcePath) throws BalException {
        ArrayList<Entity> entityMetaData = new ArrayList<>();
        ArrayList<ModuleMemberDeclarationNode> entityModuleMembers = new ArrayList<>();
        Path dirPath = Paths.get(sourcePath);
        try {
            BuildProject buildProject = BuildProject.load(dirPath.toAbsolutePath());;
            Package currentPackage = buildProject.currentPackage();
            PackageCompilation compilation = currentPackage.getCompilation();
            DiagnosticResult diagnosticResult = compilation.diagnosticResult();
            if (diagnosticResult.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("Error occurred when validating the project. ");
                for (Diagnostic d : diagnosticResult.errors()) {
                    errorMessage.append(System.lineSeparator());
                    errorMessage.append(d.toString());
                }
                throw new BalException(errorMessage.toString());
            }
            for (Module module : buildProject.currentPackage().modules()) {
                for (DocumentId documentId : module.documentIds()) {
                    if (documentId.moduleId().moduleName().trim().endsWith(CONFIG_MODULE_ENDING)) {
                        continue;
                    }
                    Document document = module.document(documentId);
                    EntityMetaData retEntityMetaData = BalSyntaxTreeGenerator
                            .getEntityMetadata(document.syntaxTree());
                    ArrayList<Entity> entityMetadataList = retEntityMetaData.entityArray;
                    ArrayList<ModuleMemberDeclarationNode> entityModuleMembersList =
                            retEntityMetaData.moduleMembersArray;
                    for (Entity entityMetadata : entityMetadataList) {
                        entityMetaData.add(entityMetadata);
                        entityModuleMembers.add(entityModuleMembersList.get(entityMetadataList
                                .indexOf(entityMetadata)));
                    }
                }
            }
            generateRelations(entityMetaData);
            entityModuleMembers = formatModuleMembers(entityModuleMembers, entityMetaData);
            return new EntityMetaData(entityMetaData, entityModuleMembers);

        } catch (IOException e) {
            throw new BalException("Error while reading entities in the Ballerina project. " + e.getMessage());
        }
    }
}

