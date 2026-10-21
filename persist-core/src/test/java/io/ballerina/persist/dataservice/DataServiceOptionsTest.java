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
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Tests the validation of the {@code options.dataservice} group.
 */
public class DataServiceOptionsTest {

    @Test
    public void testDefaults() throws BalException {
        DataServiceOptions options = DataServiceOptions.from(Map.of("protocol", "http"));
        Assert.assertEquals(options.protocol(), DataServiceOptions.Protocol.HTTP);
        Assert.assertEquals(options.basePath(), "/api");
    }

    @Test
    public void testBasePath() throws BalException {
        DataServiceOptions options = DataServiceOptions.from(Map.of("protocol", "http", "basePath", "/hr-api/v1/"));
        Assert.assertEquals(options.basePath(), "/hr-api/v1");
    }

    @Test
    public void testRootBasePath() throws BalException {
        Assert.assertEquals(DataServiceOptions.from(Map.of("protocol", "http", "basePath", "/")).basePath(), "");
    }

    @Test
    public void testMissingProtocol() {
        assertError(Map.of("basePath", "/api"),
                "'options.dataservice.protocol' is required. Supported values: http.");
    }

    @Test
    public void testFutureProtocol() {
        assertError(Map.of("protocol", "graphql"),
                "the 'graphql' data service protocol is not supported yet. Supported values: http.");
    }

    @Test
    public void testUnsupportedProtocol() {
        assertError(Map.of("protocol", "odata"),
                "unsupported data service protocol 'odata'. Supported values: http.");
    }

    @Test
    public void testRelativeBasePath() {
        assertError(Map.of("protocol", "http", "basePath", "api"),
                "invalid 'options.dataservice.basePath' 'api': the base path must start with '/'.");
    }

    @Test
    public void testInvalidBasePathSegment() {
        assertError(Map.of("protocol", "http", "basePath", "/api//v1"),
                "invalid 'options.dataservice.basePath' '/api//v1': each path segment must be non-empty and " +
                        "contain only letters, digits, '_', '-' and '.'.");
    }

    @Test
    public void testNotATable() {
        assertError("http", "'options.dataservice' must be a table of data service options.");
    }

    private static void assertError(Object value, String message) {
        BalException error = Assert.expectThrows(BalException.class, () -> DataServiceOptions.from(value));
        Assert.assertEquals(error.getMessage(), message);
    }
}
