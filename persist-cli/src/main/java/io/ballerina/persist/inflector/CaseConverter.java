/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
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
package io.ballerina.persist.inflector;

import java.util.Locale;
import java.util.regex.Pattern;

public class CaseConverter {

    private static final String CAMEL_CASE_PATTERN = "^[a-z]+(?:[A-Z][a-z]+)*$";
    private static final String PASCAL_CASE_PATTERN = "^[A-Z][a-z]+(?:[A-Z][a-z]+)*$";
    private static final String DELIMITER_PATTERN = "[^a-zA-Z0-9]";
    private CaseConverter() {}
    public static String toPascalCase(String word) {
        //check if camelCase
        if (Pattern.matches(CAMEL_CASE_PATTERN, word)) {
            StringBuilder result = new StringBuilder();
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1));
            return result.toString();
        }
        //check if already PascalCase
        if (Pattern.matches(PASCAL_CASE_PATTERN, word)) {
            return word;
        }
        word = word.toLowerCase(Locale.ENGLISH);
        String[] words = word.split(DELIMITER_PATTERN);
        StringBuilder result = new StringBuilder();
        for (String  item: words) {
            if (!item.isEmpty()) {
                // Capitalize the first letter of each word
                result.append(Character.toUpperCase(item.charAt(0)));
                result.append(item.substring(1).toLowerCase(Locale.ENGLISH));
            }
        }
        return result.toString();
    }
    public static String toCamelCase(String word) {
        //Check if already camelCase
        if (Pattern.matches(CAMEL_CASE_PATTERN, word)) {
            return word;
        }
        word = word.toLowerCase(Locale.ENGLISH);
        String[] words = word.split(DELIMITER_PATTERN);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String item = words[i];
            if (!item.isEmpty()) {
                // Capitalize the first letter of the first word, and lowercase the first letter of subsequent words
                if (i == 0) {
                    result.append(item.toLowerCase(Locale.ENGLISH));
                } else {
                    result.append(Character.toUpperCase(item.charAt(0)))
                            .append(item.substring(1).toLowerCase(Locale.ENGLISH));
                }
            }
        }
        return result.toString();
    }

    public static String toSingularPascalCase(String word) {
        //check if camelCase
        if (Pattern.matches(CAMEL_CASE_PATTERN, word)) {
            StringBuilder result = new StringBuilder();
            word = Singularizer.singularize(word);
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1));
            return result.toString();
        }
        //check if already PascalCase
        if (Pattern.matches(PASCAL_CASE_PATTERN, word)) {
            return word;
        }
        word = word.toLowerCase(Locale.ENGLISH);
        String[] words = word.split(DELIMITER_PATTERN);
        StringBuilder result = new StringBuilder();
        for (String  item: words) {
            if (!item.isEmpty()) {
                // Capitalize the first letter of each word
                item = Singularizer.singularize(item);
                result.append(Character.toUpperCase(item.charAt(0)));
                result.append(item.substring(1).toLowerCase(Locale.ENGLISH));
            }
        }
        return result.toString();
    }
}
