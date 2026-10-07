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
import io.ballerina.toml.validator.SampleNodeGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests how the data service flags are validated and recorded in {@code Ballerina.toml}.
 */
public class DataServiceTomlTest {

    private static final String PACKAGE = """
            [package]
            org = "foo"
            name = "hr"
            version = "0.1.0"
            """;

    @Test
    public void testValidateFlags() throws BalException {
        Assert.assertEquals(DataServiceToml.validateFlags(null, null), List.of());
        Assert.assertEquals(DataServiceToml.validateFlags("http", null), List.of());
        Assert.assertEquals(DataServiceToml.validateFlags("http", " Employee, Department "),
                List.of("Employee", "Department"));
    }

    @Test
    public void testEntitiesWithoutDataService() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceToml.validateFlags(null, "Employee"));
        Assert.assertEquals(error.getMessage(), "the '--entities' option requires the '--dataservice' option.");
    }

    @Test
    public void testRecordInExistingEntry() throws IOException {
        String toml = PACKAGE + """

                [[tool.persist]]
                id = "generate-db-client"
                targetModule = "hr"
                options.datastore = "mysql"
                filePath = "persist/model.bal"
                options.dataservice.protocol = "http"
                options.dataservice.entities = ["Old"]

                [[tool.persist]]
                id = "generate-users-client"
                targetModule = "hr.users"
                options.datastore = "mysql"
                filePath = "persist/users/model.bal"
                """;
        Assert.assertEquals(record(toml, List.of("Employee")), PACKAGE + """

                [[tool.persist]]
                id = "generate-db-client"
                targetModule = "hr"
                options.datastore = "mysql"
                filePath = "persist/model.bal"
                options.dataservice.protocol = "http"
                options.dataservice.entities = ["Employee"]

                [[tool.persist]]
                id = "generate-users-client"
                targetModule = "hr.users"
                options.datastore = "mysql"
                filePath = "persist/users/model.bal"
                """);
    }

    @Test
    public void testRecordCreatesEntry() throws IOException {
        Assert.assertEquals(record(PACKAGE, List.of()), PACKAGE + """

                [[tool.persist]]
                id = "generate-db-client"
                targetModule = "hr"
                options.datastore = "mysql"
                filePath = "persist/model.bal"
                options.dataservice.protocol = "http"
                """);
    }

    private static String record(String toml, List<String> entities) throws IOException {
        Path tomlPath = Files.createTempFile("Ballerina", ".toml");
        try {
            Files.writeString(tomlPath, toml, StandardCharsets.UTF_8);
            return DataServiceToml.recordInEntry(tomlPath, List.of(
                    SampleNodeGenerator.createStringKV("id", "generate-db-client", null),
                    SampleNodeGenerator.createStringKV("targetModule", "hr", null),
                    SampleNodeGenerator.createStringKV("options.datastore", "mysql", null),
                    SampleNodeGenerator.createStringKV("filePath", "persist/model.bal", null)),
                    "persist/model.bal", "http", entities);
        } finally {
            Files.deleteIfExists(tomlPath);
        }
    }
}
