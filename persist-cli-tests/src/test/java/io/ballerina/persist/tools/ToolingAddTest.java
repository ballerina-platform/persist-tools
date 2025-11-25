/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
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

import io.ballerina.persist.cmd.Add;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.ADD;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

/**
 * persist tool add command tests.
 */
public class ToolingAddTest {

    private static final PrintStream errStream = System.err;
    public static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources").toString();

    @Test(enabled = true)
    @Description("When the project is not initiated")
    public void testAdd() {
        executeCommand("tool_test_add_1");
        assertGeneratedSources("tool_test_add_1");
    }

    @Test(enabled = true)
    @Description("When there is an already initiated configs and there is an uninitiated schema")
    public void testAddUpdateConfigWithNewDbConfigurations() {
        assertGeneratedSourcesNegative("tool_test_add_2", ADD, new String[]{});
    }

    @Test(enabled = true)
    @Description("When there is a database config files inside the directories and there are missing database " +
            "configurations")
    public void testsInitUpdateConfigWithPartialyInitiatedFiles() {
        executeCommand("tool_test_add_3");
        assertGeneratedSources("tool_test_add_3");
    }

    @Test(enabled = true)
    @Description("When the init command is executed outside a Ballerina project")
    public void testsInitOutsideBalProject() {
        assertGeneratedSourcesNegative("tool_test_add_4", ADD, new String[]{"Config.toml"});
    }

    @Test(enabled = true)
    @Description("When the configs are already updated")
    public void testsInitUpdateConfigWithUpdatedDbConfigurations() {
        executeCommand("tool_test_add_5");
        assertGeneratedSources("tool_test_add_5");
    }

    @Test(enabled = true)
    @Description("Running init on a already initialized project")
    public void testInitAlreadyInitializedProject() {
        executeCommand("tool_test_add_6");
        assertGeneratedSources("tool_test_add_6");
    }

    @Test(enabled = true)
    @Description("Running init on a already initialized project with database configurations missing")
    public void testInitAlreadyInitializedProjectWithOutPersistConfiguration() {
        executeCommand("tool_test_add_7");
        assertGeneratedSources("tool_test_add_7");
    }

    @Test(enabled = true)
    @Description("Running init on a project with manually created definition file")
    public void testInitWithManuallyCreatedDefinitionFile() {
        executeCommand("tool_test_add_9");
        assertGeneratedSources("tool_test_add_9");
    }

    @Test(enabled = true)
    public void testAddArgs() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--help");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_11");

        Add persistCmd1 = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd1).parseArgs("--datastore", "");
        persistCmd1.execute();
        assertGeneratedSources("tool_test_add_11");

        Add persistCmd2 = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd2).parseArgs("--module", "^db");
        persistCmd2.execute();
        assertGeneratedSources("tool_test_add_11");

        Add persistCmd3 = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd3).parseArgs("--module",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        persistCmd3.execute();
        assertGeneratedSources("tool_test_add_11");
    }

    @Test
    public void testInitWithModuleArg() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_12").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--module", "test");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_12");
    }

    @Test(enabled = true)
    public void testInitWithMssql() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_13").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "mssql");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_13");
    }

    @Test
    public void testInitWithPostgresql() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_14").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "postgresql");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_14");
    }

    @Test
    public void testInitWithH2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_15").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "h2");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_15");
    }

    @Test
    public void testInitWithValidateDatastore() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_16").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--test-datastore", "h2");
        new CommandLine(persistCmd).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd).parseArgs("--module", "test");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_16");
    }

    @Test
    public void testInitWithInvalidateDatastore() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_17").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--test-datastore", "sqllite");
        new CommandLine(persistCmd).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd).parseArgs("--module", "test");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_17");
    }

    @Test
    @Description("Test add command with eager-loading flag for MySQL datastore")
    public void testAddWithEagerLoadingMySQL() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_18").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "mysql", "--module", "entities", "--eager-loading");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_18");
    }

    @Test
    @Description("Test add command with eager-loading flag for PostgreSQL datastore")
    public void testAddWithEagerLoadingPostgreSQL() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_19").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "postgresql", "--module", "entities", "--eager-loading");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_19");
    }

    @Test
    @Description("Test add command with eager-loading flag for MSSQL datastore")
    public void testAddWithEagerLoadingMSSQL() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_20").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "mssql", "--module", "entities", "--eager-loading");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_20");
    }

    @Test
    @Description("Test add command with eager-loading flag for unsupported datastore (inmemory) - should show warning")
    public void testAddWithEagerLoadingUnsupportedDatastore() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Add");
        Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_add_21").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--datastore", "inmemory", "--module", "entities", "--eager-loading");
        persistCmd.execute();
        assertGeneratedSources("tool_test_add_21");
    }

    private void executeCommand(String subDir) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Add");
            Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(sourcePath.toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", "mysql");
            persistCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }
}
