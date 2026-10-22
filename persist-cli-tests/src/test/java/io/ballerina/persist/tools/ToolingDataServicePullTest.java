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

package io.ballerina.persist.tools;

import io.ballerina.persist.BalException;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.ballerina.persist.tools.utils.DatabaseTestUtils.createFromDatabaseScript;

/**
 * Tests `bal persist pull --dataservice` against MySQL. These tests need the Docker test databases.
 */
public class ToolingDataServicePullTest {

    private static final Path SOURCES = Paths.get("build", "generated-sources").toAbsolutePath();
    private static final Path DISTRIBUTION = Paths.get("build", "ballerina-distribution").toAbsolutePath();
    private static final DatabaseConfiguration MYSQL;

    static {
        try {
            MYSQL = new DatabaseConfiguration("localhost", "root", "Test123#", "3307", "persist");
        } catch (BalException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Test(description = "pull --dataservice annotates every supported entity and creates the entry")
    public void testPullAnnotatesEveryEntity() throws IOException {
        String subDir = "tool_test_pull_dataservice_mysql_1";
        createFromDatabaseScript(subDir, "mysql", MYSQL);
        String output = pull(subDir, "--dataservice", "http");
        String model = Files.readString(SOURCES.resolve(subDir).resolve("persist").resolve("model.bal"));
        Assert.assertTrue(model.startsWith("import ballerina/dataservice;"), model);
        Assert.assertEquals(model.split("@dataservice:Expose", -1).length - 1, 2, model);
        Assert.assertFalse(model.matches("(?s).*@dataservice:Expose\\s+(@[^\\n]*\\s+)*public type Gadget.*"), model);
        Assert.assertTrue(output.contains("WARNING: the table 'gadgets' has an unsupported data type, so its " +
                "entity 'Gadget' is not exposed."), output);
        Assert.assertTrue(output.contains("The table 'order_items' is exposed as the entity 'OrderItem'."), output);
        Assert.assertTrue(output.contains("The exposed entities allow every action. Set 'operations' in their " +
                "@dataservice:Expose annotations to restrict them."), output);
        Assert.assertTrue(output.contains("Enabled the HTTP data service in the [[tool.persist]] entry of " +
                "persist/model.bal in Ballerina.toml."), output);
        Assert.assertTrue(output.contains("Execute `bal build` to generate the persist client and the HTTP data " +
                "service."), output);
        String toml = Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml"));
        Assert.assertTrue(toml.contains("[[tool.persist]]"), toml);
        Assert.assertTrue(toml.contains("options.datastore = \"mysql\""), toml);
        Assert.assertTrue(toml.contains("filePath = \"persist/model.bal\""), toml);
        Assert.assertTrue(toml.contains("options.dataservice.protocol = \"http\""), toml);
    }

    @Test(description = "pull --expose-tables annotates the named tables and keeps the entry's base path")
    public void testPullExposeTables() throws IOException {
        String subDir = "tool_test_pull_dataservice_mysql_2";
        createFromDatabaseScript(subDir, "mysql", MYSQL);
        pull(subDir, "--dataservice", "http", "--expose-tables", "employees");
        String model = Files.readString(SOURCES.resolve(subDir).resolve("persist").resolve("model.bal"));
        Assert.assertEquals(model.split("@dataservice:Expose", -1).length - 1, 1, model);
        Assert.assertTrue(model.contains("@dataservice:Expose\npublic type Employee"), model);
        String toml = Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml"));
        Assert.assertTrue(toml.contains("options.dataservice.protocol = \"http\""), toml);
        Assert.assertTrue(toml.contains("options.dataservice.basePath = \"/hr\""), toml);
        Assert.assertFalse(toml.contains("graphql"), toml);
    }

    @Test(description = "pull --expose-tables with a table that cannot be exposed writes nothing")
    public void testPullExposeTablesErrors() throws IOException {
        String subDir = "tool_test_pull_dataservice_mysql_3";
        createFromDatabaseScript(subDir, "mysql", MYSQL);
        String toml = Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml"));
        String output = pull(subDir, "--dataservice", "http", "--expose-tables", "employees,orders,gadgets");
        Assert.assertTrue(output.contains("ERROR: the table 'orders' in '--expose-tables' is not a pulled table."),
                output);
        Assert.assertTrue(output.contains("ERROR: the table 'gadgets' in '--expose-tables' has an unsupported " +
                "data type, so its entity 'Gadget' cannot be exposed."), output);
        Assert.assertFalse(Files.exists(SOURCES.resolve(subDir).resolve("persist").resolve("model.bal")));
        Assert.assertEquals(Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml")), toml);
    }

    @Test(description = "pull without --dataservice into an entry that enables a data service annotates and " +
            "leaves the entry unchanged")
    public void testPullKeepsTheEntrysDataService() throws IOException {
        String subDir = "tool_test_pull_dataservice_mysql_4";
        createFromDatabaseScript(subDir, "mysql", MYSQL);
        String toml = Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml"));
        String output = pull(subDir, "--expose-tables", "employees,order_items");
        String model = Files.readString(SOURCES.resolve(subDir).resolve("persist").resolve("model.bal"));
        Assert.assertEquals(model.split("@dataservice:Expose", -1).length - 1, 2, model);
        Assert.assertEquals(Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml")), toml);
        Assert.assertFalse(output.contains("Enabled the"), output);
        Assert.assertTrue(output.contains("Execute `bal build` to generate the persist client and the HTTP data " +
                "service."), output);
    }

    @Test(description = "pull without --dataservice into an entry without a data service behaves as before")
    public void testPullWithoutDataService() throws IOException {
        String subDir = "tool_test_pull_dataservice_mysql_5";
        createFromDatabaseScript(subDir, "mysql", MYSQL);
        String toml = Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml"));
        String output = pull(subDir);
        String model = Files.readString(SOURCES.resolve(subDir).resolve("persist").resolve("model.bal"));
        Assert.assertFalse(model.contains("dataservice"), model);
        Assert.assertEquals(Files.readString(SOURCES.resolve(subDir).resolve("Ballerina.toml")), toml);
        Assert.assertFalse(output.contains("data service"), output);
    }

    // Runs the installed command, as a user would, so that its output can be read.
    private static String pull(String subDir, String... options) throws IOException {
        List<String> command = new ArrayList<>(List.of(DISTRIBUTION.resolve("bin").resolve("bal").toString(),
                "persist", "pull", "--datastore", "mysql", "--host", MYSQL.getHost(), "--port",
                String.valueOf(MYSQL.getPort()), "--user", MYSQL.getUsername(), "--database", MYSQL.getDatabase()));
        command.addAll(List.of(options));
        Process process = new ProcessBuilder(command).directory(SOURCES.resolve(subDir).toFile())
                .redirectErrorStream(true).start();
        try (OutputStream stdin = process.getOutputStream()) {
            stdin.write((MYSQL.getPassword() + "\n").getBytes(StandardCharsets.UTF_8));
        }
        try (InputStream stdout = process.getInputStream()) {
            String output = new String(stdout.readAllBytes(), StandardCharsets.UTF_8);
            process.waitFor();
            return output;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }
}
