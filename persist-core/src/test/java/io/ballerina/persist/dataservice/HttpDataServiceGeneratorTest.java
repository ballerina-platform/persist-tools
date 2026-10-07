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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Tests the sources generated for an HTTP data service.
 */
public class HttpDataServiceGeneratorTest {

    private static final Path ACTUAL_OUTPUT = Paths.get("build", "dataservice-actual").toAbsolutePath();

    @Test
    public void testAllActions() throws BalException, IOException {
        assertGenerated("hr", Map.of("protocol", "http"));
    }

    @Test
    public void testNarrowedActions() throws BalException, IOException {
        assertGenerated("annotated", Map.of("protocol", "http", "operations", List.of("read", "update", "delete")));
    }

    @Test
    public void testEscapedBasePath() throws BalException, IOException {
        assertGenerated("basepath", Map.of("protocol", "http", "basePath", "/hr-api/v1.0",
                "entities", List.of("Department")));
    }

    @Test
    public void testConfig() throws BalException, IOException {
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(
                DataServiceModelTest.resolve("hr", Map.of("protocol", "http")));
        assertSource("hr", HttpDataServiceGenerator.CONFIG_FILE, generator.generateConfig());
    }

    private static void assertGenerated(String model, Map<String, Object> options) throws BalException, IOException {
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(DataServiceModelTest.resolve(model,
                options));
        assertSource(model, HttpDataServiceGenerator.SERVICE_FILE, generator.generateService());
    }

    // The actual source is written under build/ so that a changed expectation can be reviewed and copied.
    private static void assertSource(String model, String fileName, String actual) throws IOException {
        Path actualPath = ACTUAL_OUTPUT.resolve(model).resolve(fileName);
        Files.createDirectories(actualPath.getParent());
        Files.writeString(actualPath, actual, StandardCharsets.UTF_8);
        Path expectedPath = DataServiceModelTest.RESOURCES.resolve(model).resolve("expected").resolve(fileName);
        String expected = Files.exists(expectedPath) ? Files.readString(expectedPath, StandardCharsets.UTF_8) : "";
        Assert.assertEquals(actual, expected, "see " + actualPath);
    }
}
