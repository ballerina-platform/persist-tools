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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Singularizer {
    private Singularizer() {}

    public static String singularize(String word) {
        for (String rule : Rules.UNCOUNTABLE_RULES) {
            if (Pattern.matches(rule, word)) {
                return word;
            }
        }
        for (String[] irregularRule: Rules.IRREGULAR_RULES) {
            if (irregularRule[0].equals(word)) {
                return word;
            }
            if (irregularRule[1].equals(word)) {
                return irregularRule[0];
            }
        }
        for (String[] singularizationRule: Rules.SINGULARIZATION_RULES) {
            Matcher matcher = Pattern.compile(singularizationRule[0]).matcher(word);
            if (matcher.find()) {
                return matcher.replaceFirst(singularizationRule[1]);
            }
        }
        return word;
    }
}
