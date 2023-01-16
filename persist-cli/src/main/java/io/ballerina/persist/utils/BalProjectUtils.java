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

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.BalSyntaxGenerator;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Document;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_ENTITY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.PERSIST_MODULE;
import static io.ballerina.persist.nodegenerator.BalSyntaxGenerator.inferRelationDetails;

/**
 * This Class implements the utility methods for persist tool.
 *
 * @since 0.1.0
 */

public class BalProjectUtils {

    private BalProjectUtils() {}
    // TODO: Remove this function once DB push command migrated.
    public static Module getEntities(io.ballerina.projects.Module module) throws BalException {
        Module.Builder moduleBuilder = Module.newBuilder(module.moduleName().moduleNamePart());
        try {
            for (DocumentId documentId : module.documentIds()) {
                Document document = module.document(documentId);
                BalSyntaxGenerator.populateEntities(moduleBuilder, document.syntaxTree());
            }
            Module entityModule = moduleBuilder.build();
            inferRelationDetails(entityModule);
            return entityModule;
        } catch (IOException | BalException | RuntimeException e) {
            throw new BalException("Error while reading entities in the Ballerina project. " + e.getMessage());
        }
    }

    public static Module getEntities(Path schemaFile) throws BalException {
        if (schemaFile == null || schemaFile.getFileName() == null) {
            throw new BalException("schema file is null or empty");
        }
        String schemaFilename = schemaFile.getFileName().toString();
        String moduleName = schemaFilename.substring(0, schemaFilename.lastIndexOf('.'));
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

    // TODO: Remove this function once DB push command migrated.
    public static BuildProject validateSchemaFile(Path projectPath, boolean skipGeneratedDir) throws BalException {
        BuildProject buildProject = BuildProject.load(projectPath.toAbsolutePath());
        Package currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        if (diagnosticResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Error occurred when validating the project. ");
            int validErrors = 0;
            for (Diagnostic diagnostic : diagnosticResult.errors()) {
                if (!skipGeneratedDir || !diagnostic.location().lineRange().filePath().startsWith(
                        GENERATED_SOURCE_DIRECTORY)) {
                    errorMessage.append(System.lineSeparator());
                    errorMessage.append(diagnostic);
                    validErrors += 1;
                }
            }
            if (validErrors > 0) {
                throw new BalException(errorMessage.toString());
            }
        }
        return buildProject;
    }

    public static void validateSchemaFile(Path schemaPath) throws BalException {
        SingleFileProject buildProject = SingleFileProject.load(schemaPath.toAbsolutePath());
        Package currentPackage = buildProject.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        if (diagnosticResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Error occurred when validating the project. ");
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
    
    public static io.ballerina.projects.Module getEntityModule(BuildProject project) throws BalException {
        io.ballerina.projects.Module entityModule = null;
        io.ballerina.projects.Module defaultModule = null;
        for (io.ballerina.projects.Module module : project.currentPackage().modules()) {
            boolean entityExists = false;
            if (module.moduleName().moduleNamePart() == null) {
                defaultModule = module;
            }
            for (DocumentId documentId : module.documentIds()) {
                Document document = module.document(documentId);
                ModulePartNode rootNote = document.syntaxTree().rootNode();
                NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
                for (ModuleMemberDeclarationNode moduleNode : nodeList) {
                    if (moduleNode.kind() != SyntaxKind.TYPE_DEFINITION || ((TypeDefinitionNode) moduleNode)
                            .metadata().isEmpty()) {
                        continue;
                    }

                    for (AnnotationNode annotation : ((TypeDefinitionNode) moduleNode).metadata().get().annotations()) {
                        Node annotReference = annotation.annotReference();
                        if (annotReference.kind() != SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                            continue;
                        }
                        QualifiedNameReferenceNode qualifiedNameRef = (QualifiedNameReferenceNode) annotReference;
                        if (qualifiedNameRef.identifier().text().equals(KEYWORD_ENTITY) && qualifiedNameRef
                                .modulePrefix().text().equals(PERSIST_MODULE) && annotation.annotValue()
                                .isPresent()) {
                            entityExists = true;
                        }
                    }
                }
            }
            if (entityExists) {
                if (entityModule == null) {
                    entityModule = module;
                } else {
                    throw new BalException("Entities are allowed to define in one module. " +
                            "but found in both " + entityModule.moduleName() + " and " +
                            module.moduleName());
                }
            }
        }
        return entityModule == null ? defaultModule : entityModule;
    }
}
