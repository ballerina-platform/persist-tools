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

import java.util.Arrays;
import java.util.Locale;

public class CaseConverter {
    private CaseConverter() {}
    public static String toPascalCase(String word) {
        return arrayToPascalCase(split(word));
    }
    public static String toCamelCase(String word) {
        String pascalsCase = arrayToPascalCase(split(word));
        return pascalsCase.substring(0, 1).toLowerCase(Locale.ENGLISH) + pascalsCase.substring(1);
    }

    public static String toSingularPascalCase(String word) {
        String[] words = split(word);
        words = Arrays.stream(words).map(Singularizer::singularize).toArray(String[]::new);
        return arrayToPascalCase(words);
    }

    private static String arrayToPascalCase(String[] words) {
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
    private static String[] split(String value) {
        String splitUpperUpperRe = "([\\p{Ll}\\d])(\\p{Lu})";
        String splitLowerUpperRe = "([\\p{Ll}\\d])(\\p{Lu})";
        String splitReplaceValue = "$1\0$2";
        String result = value.replaceAll(splitLowerUpperRe, splitReplaceValue)
                .replaceAll(splitUpperUpperRe, splitReplaceValue);
        String defaultStripRegexp = "[^\\p{L}\\d]+";
        result = result.replaceAll(defaultStripRegexp, "\0");
        int start = 0;
        int end = result.length();
        while (result.charAt(start) == '\0') {
            start++;
        }
        while (result.charAt(end - 1) == '\0') {
            end--;
        }
        return Arrays.stream(result.substring(start, end)
                .split("\0"))
                .map(String::trim)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }
}
