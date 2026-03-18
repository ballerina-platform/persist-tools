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

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.SyntaxKind;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities related to Stub files.
 *
 */
public class StubUtils {

    private StubUtils() {}

    /**
     * Set of Ballerina reserved keywords derived directly from the compiler's SyntaxKind enum.
     * This stays in sync with the Ballerina parser version used by the project.
     */
    private static final Set<String> BALLERINA_KEYWORDS = Arrays.stream(SyntaxKind.values())
            .filter(kind -> kind.name().endsWith("_KEYWORD"))
            .map(kind -> AbstractNodeFactory.createToken(kind).text())
            .collect(Collectors.toSet());

    public static boolean isLiteralName(String name) {
        return BALLERINA_KEYWORDS.contains(name);
    }
}
