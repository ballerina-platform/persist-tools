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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the validation of `options.dataservice`.
 */
public class DataServiceOptionsTest {

    @Test
    public void testDefaults() throws BalException {
        Assert.assertEquals(DataServiceOptions.from(Map.of("protocol", "http")),
                new DataServiceOptions("http", "/api"));
    }

    @Test
    public void testProtocolRequired() {
        assertError(Map.of("basePath", "/api"),
                "'options.dataservice.protocol' is required. Supported values: http.");
    }

    @Test(dataProvider = "reservedProtocols")
    public void testReservedProtocol(String protocol) {
        assertError(Map.of("protocol", protocol),
                "the protocol '" + protocol + "' is not supported yet. Supported values: http.");
    }

    @DataProvider
    public Object[][] reservedProtocols() {
        return new Object[][]{{"graphql"}, {"mcp"}, {"odata"}, {"soap"}};
    }

    @Test(dataProvider = "invalidProtocols")
    public void testInvalidProtocol(String protocol) {
        assertError(Map.of("protocol", protocol),
                "invalid 'options.dataservice.protocol' '" + protocol + "'. Supported values: http.");
    }

    @DataProvider
    public Object[][] invalidProtocols() {
        return new Object[][]{{"rest"}, {"HTTP"}, {""}};
    }

    @Test(dataProvider = "validBasePaths")
    public void testValidBasePath(String basePath, String expected) throws BalException {
        Assert.assertEquals(DataServiceOptions.from(group(basePath)).basePath(), expected);
    }

    @DataProvider
    public Object[][] validBasePaths() {
        return new Object[][]{{"/api", "/api"}, {"/api/", "/api"}, {"/", "/"},
                {"/v1.0/hr-data/x_y", "/v1.0/hr-data/x_y"}, {"/2024", "/2024"}};
    }

    @Test
    public void testBasePathWithoutLeadingSlash() {
        assertError(group("api"),
                "invalid 'options.dataservice.basePath' 'api': the base path must start with '/'.");
    }

    @Test(dataProvider = "invalidSegments")
    public void testBasePathWithInvalidSegment(String basePath) {
        assertError(group(basePath), "invalid 'options.dataservice.basePath' '" + basePath + "': each path " +
                "segment must be non-empty and contain only letters, digits, '_', '-' and '.'.");
    }

    @DataProvider
    public Object[][] invalidSegments() {
        return new Object[][]{{"//"}, {"/api//v1"}, {"/api v1"}, {"/api/{id}"}, {"/a$b"}};
    }

    @Test
    public void testCliProtocolValidation() throws BalException {
        Assert.assertEquals(DataServiceOptions.validateProtocol("http"), "http");
        Assert.assertThrows(BalException.class, () -> DataServiceOptions.validateProtocol("grpc"));
    }

    private static Map<String, Object> group(String basePath) {
        Map<String, Object> group = new HashMap<>();
        group.put("protocol", "http");
        group.put("basePath", basePath);
        return group;
    }

    private static void assertError(Map<String, ?> group, String message) {
        BalException error = Assert.expectThrows(BalException.class, () -> DataServiceOptions.from(group));
        Assert.assertEquals(error.getMessage(), message);
    }
}
