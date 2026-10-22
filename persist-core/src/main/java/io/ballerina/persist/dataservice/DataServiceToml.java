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
import io.ballerina.tools.text.TextDocuments;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reads and updates the `[[tool.persist]]` entry of the default model in `Ballerina.toml`, for `bal persist pull`.
 */
public final class DataServiceToml {

    public static final String DEFAULT_MODEL_PATH = "persist/model.bal";

    private static final String TOOL_PERSIST = "tool.persist";
    private static final String FILE_PATH = "filePath";
    private static final String TARGET_MODULE = "targetModule";
    private static final String WITH_INIT_PARAMS = "options.withInitParams";
    private static final String PROTOCOL = "options.dataservice.protocol";

    private DataServiceToml() {
    }

    /**
     * The `[[tool.persist]]` entry of the default model.
     *
     * @param values      the entry's keys and their values as written, with strings unquoted
     * @param endOffset   the offset in the file just after the entry's last key
     * @param protocolKey the key-value node of `options.dataservice.protocol`, if the entry has one
     */
    public record Entry(Map<String, String> values, int endOffset, Optional<KeyValueNode> protocolKey) {

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
    }

    /**
     * Finds the entry whose `filePath` is `persist/model.bal`, or that has no `filePath`.
     *
     * @param toml the content of `Ballerina.toml`
     * @return the entry, if there is one
     */
    public static Optional<Entry> defaultModelEntry(String toml) {
        DocumentNode document = SyntaxTree.from(TextDocuments.from(toml)).rootNode();
        for (DocumentMemberDeclarationNode member : document.members()) {
            if (!(member instanceof TableArrayNode table)
                    || !TOOL_PERSIST.equals(table.identifier().toSourceCode().trim())) {
                continue;
            }
            Map<String, String> values = new LinkedHashMap<>();
            Optional<KeyValueNode> protocol = Optional.empty();
            int endOffset = table.textRange().endOffset();
            for (KeyValueNode field : table.fields()) {
                String key = field.identifier().toSourceCode().trim();
                values.put(key, unquote(field.value().toSourceCode().trim()));
                if (PROTOCOL.equals(key)) {
                    protocol = Optional.of(field);
                }
                endOffset = Math.max(endOffset, field.textRange().endOffset());
            }
            if (DEFAULT_MODEL_PATH.equals(values.getOrDefault(FILE_PATH, DEFAULT_MODEL_PATH))) {
                return Optional.of(new Entry(values, endOffset, protocol));
            }
        }
        return Optional.empty();
    }

    /**
     * Sets `options.dataservice.protocol` in an entry, keeping every other key, including `basePath`.
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
        return toml.substring(0, entry.endOffset()) + System.lineSeparator() + PROTOCOL + " = " + value +
                toml.substring(entry.endOffset());
    }

    private static String unquote(String value) {
        return value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"") ?
                value.substring(1, value.length() - 1) : value;
    }
}
