/*
 *  Copyright (c) 2025 WSO2 LLC. (http://www.wso2.com).
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

package io.ballerina.persist.utils;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for StubUtils.
 */
public class StubUtilsTest {

    @DataProvider(name = "reservedWords")
    public Object[][] reservedWords() {
        return new Object[][] {
                // Original reserved words
                {"import"}, {"public"}, {"private"}, {"function"}, {"record"},
                {"from"}, {"order"}, {"type"}, {"int"}, {"string"}, {"error"},
                {"table"}, {"return"}, {"if"}, {"while"}, {"foreach"},
                // Swan Lake reserved words
                {"class"}, {"distinct"}, {"never"}, {"isolated"}, {"readonly"}, {"configurable"},
        };
    }

    @Test(dataProvider = "reservedWords")
    public void testIsLiteralNameForReservedWords(String word) {
        Assert.assertTrue(StubUtils.isLiteralName(word),
                "Expected '" + word + "' to be recognized as a reserved word");
    }

    @DataProvider(name = "nonReservedWords")
    public Object[][] nonReservedWords() {
        return new Object[][] {
                {"student"}, {"enrollment"}, {"customerId"}, {"classId"},
                {"firstName"}, {"lastName"}, {"email"}, {"Company"},
        };
    }

    @Test(dataProvider = "nonReservedWords")
    public void testIsLiteralNameForNonReservedWords(String word) {
        Assert.assertFalse(StubUtils.isLiteralName(word),
                "Expected '" + word + "' to NOT be recognized as a reserved word");
    }
}
