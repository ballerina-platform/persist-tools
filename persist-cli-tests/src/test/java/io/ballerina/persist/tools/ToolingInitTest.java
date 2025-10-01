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
import io.ballerina.persist.utils.BalProjectUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;

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
