/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.persist.tools;

import io.ballerina.persist.cmd.Init;
import io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

public class ToolingInitTest {
    public static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources")
            .toString();
    private static final PrintStream errStream = System.err;

    @Test(enabled = true)
    public void testInitWithDatastore() {
        executeInitCommand("tool_test_init_1", "mysql");
        assertGeneratedSources("tool_test_init_1");
    }

    @Test(enabled = true)
    public void testInitWithoutArguments() {
        executeInitCommand("tool_test_init_1");
        assertGeneratedSources("tool_test_init_1");
    }

    @Test(enabled = true)
    public void testInitWithBothDatastoreAndModule() {
        executeInitCommand("tool_test_init_1", "mysql", "test_module");
        assertGeneratedSources("tool_test_init_1");
    }

    @Test
    public void testValidateBallerinaProjectWithoutBallerinaToml() {
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_workspace", "package2");
        try {
            BalProjectUtils.validateBallerinaProject(sourcePath);
            Assert.fail("Expected exception was not thrown");
        } catch (Exception e) {
            String exceptionMessage = e.getMessage();
            Assert.assertTrue(exceptionMessage.contains("ERROR: invalid Ballerina package directory:"));
            Assert.assertTrue(exceptionMessage.contains("cannot find 'Ballerina.toml' file"));
        }
    }

    @Test
    public void testValidateBallerinaProjectInsideWorkspace() {
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_workspace");
        try {
            BalProjectUtils.validateBallerinaProject(sourcePath);
            Assert.fail("Expected exception was not thrown");
        } catch (Exception e) {
            String exceptionMessage = e.getMessage();
            Assert.assertTrue(exceptionMessage.contains("ERROR: invalid Ballerina package directory:"));
            Assert.assertTrue(exceptionMessage.contains("the persist tool does not support Ballerina workspaces"));
        }
    }

    // Multi-model support tests

    @Test
    public void testInitWithModelOption() {
        executeInitCommandWithModel("tool_test_init_multimodel_1", "users");
        assertGeneratedSources("tool_test_init_multimodel_1");
    }

    @Test
    public void testInitMultipleModels() {
        // Initialize first model
        executeInitCommandWithModel("tool_test_init_multimodel_2", "users");
        // Initialize second model
        executeInitCommandWithModel("tool_test_init_multimodel_2", "orders");
        assertGeneratedSources("tool_test_init_multimodel_2");
    }

    @Test
    public void testInitReservedModelName() {
        // Attempt to create model with reserved name "migrations"
        assertGeneratedSourcesNegative("tool_test_init_multimodel_3", GeneratedSourcesTestUtils.Command.INIT,
                new String[]{"persist/migrations"}, "--model", "migrations");
    }

    @Test
    public void testInitBackwardCompatibility() {
        // Test init without --model creates traditional structure
        executeInitCommand("tool_test_init_backward_compat");
        assertGeneratedSources("tool_test_init_backward_compat");
    }

    @Test
    public void testInitHybridStructure() {
        // Create root model first
        executeInitCommand("tool_test_init_hybrid");
        // Then add subdirectory model
        executeInitCommandWithModel("tool_test_init_hybrid", "analytics");
        assertGeneratedSources("tool_test_init_hybrid");
    }

    public static void executeInitCommandWithModel(String subDir, String modelName) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Init");
            Init persistCmd = (Init) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(sourcePath.toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--model", modelName);
            persistCmd.execute();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }

    public static void executeInitCommand(String subDir, String... args) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Init");
            Init persistCmd = (Init) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(sourcePath.toAbsolutePath().toString());
            if (args.length > 1) {
                // ballerina persist init --datastore <datastore> --module <module>
                new CommandLine(persistCmd).parseArgs("--datastore", args[0], "--module", args[1]);
            } else if (args.length == 1) {
                // ballerina persist init --datastore <datastore>
                new CommandLine(persistCmd).parseArgs("--datastore", args[0]);
            } else {
                // ballerina persist init
                new CommandLine(persistCmd).parseArgs();
            }
            persistCmd.execute();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }
}
