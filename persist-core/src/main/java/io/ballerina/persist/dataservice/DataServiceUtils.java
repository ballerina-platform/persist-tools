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

import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

import java.util.regex.Pattern;

/**
 * Helpers shared by the data service model readers and the generator.
 */
final class DataServiceUtils {

    // Splits a camel-case name into its words, keeping an acronym such as `HTTP` as one word.
    static final Pattern WORD_BOUNDARY = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

    private DataServiceUtils() {
    }

    static String unescape(String name) {
        return BalSyntaxUtils.stripEscapeCharacter(name.startsWith("'") ? name.substring(1) : name);
    }

    static String unquote(String text) {
        return text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"") ?
                text.substring(1, text.length() - 1) : text;
    }

    static boolean isInternal(FieldRules.Applied rule) {
        return rule != null && rule.rule() == FieldRule.INTERNAL;
    }
}
