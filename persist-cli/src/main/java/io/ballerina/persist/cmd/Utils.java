/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

package io.ballerina.persist.cmd;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.BalException;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.SCHEMA_FILE_NAME;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.BAL_EXTENSION;

public class Utils {
    public static void generateSchemaBalFile(Path persistPath) throws BalException {
        try {
            String configTree = generateSchemaSyntaxTree();
            writeOutputString(configTree, persistPath.resolve(SCHEMA_FILE_NAME + BAL_EXTENSION)
                    .toAbsolutePath().toString());
        } catch (Exception e) {
            throw new BalException(e.getMessage());
        }
    }

    public static void writeOutputString(String content, String outPath) throws BalException, IOException {
        Path pathToFile = Paths.get(outPath);
        Path parentDirectory = pathToFile.getParent();
        if (Objects.nonNull(parentDirectory)) {
            if (!Files.exists(parentDirectory)) {
                try {
                    Files.createDirectories(parentDirectory);
                } catch (IOException e) {
                    throw new BalException(
                            String.format("could not create the parent directories of output path %s. %s",
                                    parentDirectory, e.getMessage()));
                }
            }
            try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8)) {
                writer.println(content);
            }
        }
    }

    public static void writeToTargetFile(String content, String outPath) throws BalException, IOException {
        Path pathToFile = Paths.get(outPath);
        Path parentDirectory = pathToFile.getParent();
        if (Objects.nonNull(parentDirectory)) {
            if (!Files.exists(parentDirectory)) {
                try {
                    Files.createDirectories(parentDirectory);
                } catch (IOException e) {
                    throw new BalException(
                            String.format("could not create the parent directories of output path %s. %s",
                                    parentDirectory, e.getMessage()));
                }
            }
            File file = new File(pathToFile.toString());
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    throw new BalException(
                            String.format("Could not create the file in the output path %s.", outPath));
                }
            }
            try (PrintWriter writer = new PrintWriter(outPath, StandardCharsets.UTF_8)) {
                writer.println(content);
            }
        }
    }

    public static String generateSchemaSyntaxTree() throws FormatterException {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        imports = imports.add(NodeParser.parseImportDeclaration("import ballerina/persist as _;"));
        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalSyntaxConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalSyntaxConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);
        return Formatter.format(balTree.modifyWith(modulePartNode).toSourceCode());
    }
}
