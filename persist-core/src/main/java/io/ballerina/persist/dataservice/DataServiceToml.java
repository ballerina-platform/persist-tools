/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.dataservice;

import io.ballerina.persist.BalException;
import io.ballerina.toml.syntax.tree.AbstractNodeFactory;
import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.syntax.tree.NodeFactory;
import io.ballerina.toml.syntax.tree.NodeList;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import io.ballerina.toml.syntax.tree.TableArrayNode;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reads the data service command-line flags and writes them to a {@code [[tool.persist]]} entry.
 */
public final class DataServiceToml {

    private static final String TOOL_PERSIST = "tool.persist";
    private static final String FILE_PATH = "filePath";
    private static final String KEY_PREFIX = "options." + DataServiceOptions.OPTION_DATASERVICE + ".";
    private static final String NEW_LINE = System.lineSeparator();

    private DataServiceToml() {
    }

    /**
     * Validates the {@code --dataservice} and {@code --entities} flags.
     *
     * @param protocol the value of {@code --dataservice}, or {@code null}
     * @param entities the value of {@code --entities}, or {@code null}
     * @return the entity names, or an empty list when {@code --entities} is not given
     * @throws BalException if the flags are invalid
     */
    public static List<String> validateFlags(String protocol, String entities) throws BalException {
        if (protocol == null) {
            if (entities != null) {
                throw new BalException("the '--entities' option requires the '--dataservice' option.");
            }
            return List.of();
        }
        DataServiceOptions.from(Map.of(DataServiceOptions.PROTOCOL, protocol));
        if (entities == null) {
            return List.of();
        }
        List<String> names = Arrays.stream(entities.split(",")).map(String::trim).toList();
        if (names.stream().anyMatch(String::isEmpty)) {
            throw new BalException("the '--entities' option must be a comma-separated list of entity names.");
        }
        return names;
    }

    /**
     * Creates the {@code options.dataservice} key-value pairs for a {@code [[tool.persist]]} entry.
     *
     * @param protocol the protocol of the data service
     * @param entities the entities to expose, or an empty list to expose every entity
     * @return the key-value nodes
     */
    public static List<KeyValueNode> keyValues(String protocol, List<String> entities) {
        StringBuilder source = new StringBuilder()
                .append(KEY_PREFIX).append(DataServiceOptions.PROTOCOL).append(" = \"").append(protocol)
                .append('"').append(NEW_LINE);
        if (!entities.isEmpty()) {
            source.append(KEY_PREFIX).append(DataServiceOptions.ENTITIES).append(" = ")
                    .append(entities.stream().map(name -> '"' + name + '"').collect(Collectors.joining(", ", "[", "]")))
                    .append(NEW_LINE);
        }
        DocumentNode root = SyntaxTree.from(TextDocuments.from(source.toString())).rootNode();
        List<KeyValueNode> nodes = new ArrayList<>();
        for (DocumentMemberDeclarationNode member : root.members()) {
            nodes.add((KeyValueNode) member);
        }
        return nodes;
    }

    /**
     * Records the data service options in the {@code [[tool.persist]]} entry of a model, replacing any data service
     * options it has. When no entry exists for the model, one is created.
     *
     * @param tomlPath     the path of {@code Ballerina.toml}
     * @param newEntry     the keys of the entry to create when none exists for the model
     * @param modelPath    the {@code filePath} of the model, relative to the package
     * @param protocol     the protocol of the data service
     * @param entities     the entities to expose, or an empty list to expose every entity
     * @return the updated content of {@code Ballerina.toml}
     * @throws IOException if {@code Ballerina.toml} cannot be read
     */
    public static String recordInEntry(Path tomlPath, List<KeyValueNode> newEntry, String modelPath,
                                       String protocol, List<String> entities) throws IOException {
        DocumentNode root = SyntaxTree.from(TextDocuments.from(Files.readString(tomlPath, StandardCharsets.UTF_8)))
                .rootNode();
        NodeList<DocumentMemberDeclarationNode> members = AbstractNodeFactory.createEmptyNodeList();
        boolean recorded = false;
        for (DocumentMemberDeclarationNode member : root.members()) {
            if (!recorded && member instanceof TableArrayNode entry && isEntryOf(entry, modelPath)) {
                NodeList<KeyValueNode> fields = AbstractNodeFactory.createEmptyNodeList();
                for (KeyValueNode field : entry.fields()) {
                    if (!field.identifier().toSourceCode().trim().startsWith(KEY_PREFIX)) {
                        fields = fields.add(field);
                    }
                }
                for (KeyValueNode field : keyValues(protocol, entities)) {
                    fields = fields.add(field);
                }
                member = entry.modify().withFields(fields).apply();
                recorded = true;
            }
            members = members.add(member);
        }
        String content = NodeFactory.createDocumentNode(members, root.eofToken()).toSourceCode();
        if (recorded) {
            return content;
        }
        StringBuilder entry = new StringBuilder(content.stripTrailing()).append(NEW_LINE).append(NEW_LINE)
                .append("[[").append(TOOL_PERSIST).append("]]").append(NEW_LINE);
        for (KeyValueNode field : newEntry) {
            entry.append(field.toSourceCode().strip()).append(NEW_LINE);
        }
        for (KeyValueNode field : keyValues(protocol, entities)) {
            entry.append(field.toSourceCode().strip()).append(NEW_LINE);
        }
        return entry.toString();
    }

    // An entry without a filePath uses the default model, persist/model.bal.
    private static boolean isEntryOf(TableArrayNode entry, String modelPath) {
        if (!TOOL_PERSIST.equals(entry.identifier().toSourceCode().trim())) {
            return false;
        }
        for (KeyValueNode field : entry.fields()) {
            if (FILE_PATH.equals(field.identifier().toSourceCode().trim())) {
                String value = field.value().toSourceCode().trim();
                return value.length() >= 2 && modelPath.equals(value.substring(1, value.length() - 1));
            }
        }
        return "persist/model.bal".equals(modelPath);
    }
}
