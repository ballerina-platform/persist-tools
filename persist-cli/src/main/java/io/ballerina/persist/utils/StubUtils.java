/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
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

/**
 * Utilities related to Stub files.
 *
 */
public class StubUtils {

    private StubUtils() {}

    private static final String[] RESERVED_LITERAL_NAMES = {
            "import", "as", "public", "private", "external", "final", "service", "resource", "function", "object",
            "record", "annotation", "parameter", "transformer", "worker", "listener", "remote", "xmlns", "returns",
            "version", "channel", "abstract", "client", "const", "typeof", "source", "from", "on", "group", "by",
            "having", "order", "where", "followed", "for", "window", "every", "within", "snapshot", "inner", "outer",
            "right", "left", "full", "unidirectional", "forever", "limit", "ascending", "descending", "int", "byte",
            "float", "decimal", "boolean", "string", "error", "map", "json", "xml", "table", "stream", "any",
            "typedesc", "type", "future", "anydata", "handle", "var", "new", "init", "if", "match", "else",
            "foreach", "while", "continue", "break", "fork", "join", "some", "all", "try", "catch", "finally", "throw",
            "panic", "trap", "return", "transaction", "abort", "retry", "onretry", "retries", "committed", "aborted",
            "with", "in", "lock", "untaint", "start", "but", "check", "checkpanic", "primarykey", "is", "flush",
            "wait", "default", "enum", "error"};

    public static boolean isLiteralName(String name) {
        for (String reservedLiteralName : RESERVED_LITERAL_NAMES) {
            if (reservedLiteralName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
