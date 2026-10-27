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

import io.ballerina.toml.syntax.tree.DocumentMemberDeclarationNode;
import io.ballerina.toml.syntax.tree.DocumentNode;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.syntax.tree.SyntaxTree;
import io.ballerina.toml.syntax.tree.TableArrayNode;
import io.ballerina.toml.syntax.tree.TableNode;
import io.ballerina.tools.text.TextDocuments;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reads and updates the `[[tool.persist]]` entry of the default model in `Ballerina.toml`, for `bal persist pull`.
 */
public final class DataServiceToml {

    public static final String DEFAULT_MODEL_PATH = "persist/model.bal";

    private static final String TOOL_PERSIST = "tool.persist";
    private static final String DATASERVICE_TABLE = "tool.persist.options.dataservice";
    private static final String FILE_PATH = "filePath";
    private static final String TARGET_MODULE = "targetModule";
    private static final String WITH_INIT_PARAMS = "options.withInitParams";
    private static final String PROTOCOL_KEY = "protocol";
    private static final String PROTOCOL = "options.dataservice." + PROTOCOL_KEY;
    private static final String DATASERVICE_PREFIX = "options.dataservice.";

    private DataServiceToml() {
    }

    /**
     * The `[[tool.persist]]` entry of the default model.
     *
     * @param values           the entry's keys, including those of its sub-tables, as dotted names, and their
     *                         values as written, with strings unquoted
     * @param endOffset        the offset in the file just after the entry's last key
     * @param protocolKey      the key-value node of `options.dataservice.protocol`, if the entry has one
     * @param dataServiceTable the entry's `[tool.persist.options.dataservice]` table, if it has one
     */
    public record Entry(Map<String, String> values, int endOffset, Optional<KeyValueNode> protocolKey,
                        Optional<TableNode> dataServiceTable) {

        /**
         * Returns the entry's target module.
         *
         * @param packageName the package name, which persist uses when `targetModule` is absent
         * @return the target module
         */
        public String targetModule(String packageName) {
            return values.getOrDefault(TARGET_MODULE, packageName);
        }

        /**
         * Tells whether the entry sets `options.withInitParams = true`.
         *
         * @return whether init parameters are used
         */
        public boolean withInitParams() {
            return "true".equals(values.get(WITH_INIT_PARAMS));
        }

        /**
         * Returns the entry's `options.dataservice` group, if it has one.
         *
         * @return the group's keys, without the `options.dataservice.` prefix, and their values
         */
        public Optional<Map<String, String>> dataService() {
            Map<String, String> group = new LinkedHashMap<>();
            values.forEach((key, value) -> {
                if (key.startsWith(DATASERVICE_PREFIX)) {
                    group.put(key.substring(DATASERVICE_PREFIX.length()), value);
                }
            });
            return group.isEmpty() ? Optional.empty() : Optional.of(group);
        }
    }

    /**
     * Finds the entry whose `filePath` is `persist/model.bal`, or that has no `filePath`.
     *
     * @param toml the content of `Ballerina.toml`
     * @return the entry, if there is one
     */
    public static Optional<Entry> defaultModelEntry(String toml) {
        DocumentNode document = SyntaxTree.from(TextDocuments.from(toml)).rootNode();
        List<Entry> entries = new ArrayList<>();
        for (DocumentMemberDeclarationNode member : document.members()) {
            if (member instanceof TableArrayNode table) {
                if (TOOL_PERSIST.equals(name(table.identifier().toSourceCode()))) {
                    entries.add(withFields(new Entry(new LinkedHashMap<>(), table.textRange().endOffset(),
                            Optional.empty(), Optional.empty()), "", table.fields(), Optional.empty()));
                }
            } else if (member instanceof TableNode table && !entries.isEmpty()) {
                // A [tool.persist.<key>] table is a sub-table of the last entry.
                String tableName = name(table.identifier().toSourceCode());
                if (tableName.startsWith(TOOL_PERSIST + ".")) {
                    Entry last = entries.removeLast();
                    entries.add(withFields(last, tableName.substring(TOOL_PERSIST.length() + 1) + ".",
                            table.fields(), DATASERVICE_TABLE.equals(tableName) ? Optional.of(table) :
                                    Optional.empty()));
                }
            }
        }
        return entries.stream().filter(entry -> DEFAULT_MODEL_PATH.equals(
                entry.values().getOrDefault(FILE_PATH, DEFAULT_MODEL_PATH))).findFirst();
    }

    /**
     * Sets `options.dataservice.protocol` in an entry, keeping every other key, including `basePath`. A new group is
     * written as a `[tool.persist.options.dataservice]` table after the entry.
     *
     * @param toml     the content of `Ballerina.toml`
     * @param entry    the entry, as `defaultModelEntry` found it in the same content
     * @param protocol the protocol
     * @return the updated content
     */
    public static String setProtocol(String toml, Entry entry, String protocol) {
        String value = "\"" + protocol + "\"";
        if (entry.protocolKey().isPresent()) {
            KeyValueNode key = entry.protocolKey().get();
            return toml.substring(0, key.value().textRange().startOffset()) + value +
                    toml.substring(key.value().textRange().endOffset());
        }
        String separator = System.lineSeparator();
        if (entry.dataServiceTable().isPresent()) {
            TableNode table = entry.dataServiceTable().get();
            int offset = table.fields().isEmpty() ? table.closeBracket().textRange().endOffset() :
                    table.fields().get(table.fields().size() - 1).textRange().endOffset();
            return toml.substring(0, offset) + separator + PROTOCOL_KEY + " = " + value + toml.substring(offset);
        }
        if (entry.dataService().isPresent()) {
            // A group started with dotted keys cannot be continued as a table.
            return toml.substring(0, entry.endOffset()) + separator + PROTOCOL + " = " + value +
                    toml.substring(entry.endOffset());
        }
        return toml.substring(0, entry.endOffset()) + separator + separator + "[" + DATASERVICE_TABLE + "]" +
                separator + PROTOCOL_KEY + " = " + value + toml.substring(entry.endOffset());
    }

    private static Entry withFields(Entry entry, String prefix, Iterable<KeyValueNode> fields,
                                    Optional<TableNode> dataServiceTable) {
        Optional<KeyValueNode> protocol = entry.protocolKey();
        int endOffset = entry.endOffset();
        for (KeyValueNode field : fields) {
            String key = prefix + name(field.identifier().toSourceCode());
            entry.values().put(key, unquote(field.value().toSourceCode().trim()));
            if (PROTOCOL.equals(key)) {
                protocol = Optional.of(field);
            }
            endOffset = Math.max(endOffset, field.textRange().endOffset());
        }
        return new Entry(entry.values(), endOffset, protocol,
                dataServiceTable.isPresent() ? dataServiceTable : entry.dataServiceTable());
    }

    private static String name(String identifier) {
        return identifier.trim().replace(" ", "");
    }

    private static String unquote(String value) {
        return value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"") ?
                value.substring(1, value.length() - 1) : value;
    }
}
