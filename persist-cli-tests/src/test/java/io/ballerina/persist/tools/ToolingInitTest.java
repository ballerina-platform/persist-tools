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

import io.ballerina.persist.cmd.Init;
import jdk.jfr.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.INIT;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.executeCommand;

/**
 * persist tool init command tests.
 */
public class ToolingInitTest {

    private String version;

    @BeforeClass
    public void findLatestPersistVersion() {
        Path versionPropertiesFile = Paths.get("../", "persist-cli", "src", "main", "resources",
                "version.properties").toAbsolutePath();
        try (InputStream inputStream = Files.newInputStream(versionPropertiesFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            version = properties.get("persistVersion").toString();
        } catch (IOException e) {
            // ignore
        }
    }

    @Test(enabled = true)
    @Description("When the project is not initiated")
    public void testInit() {
        updateOutputBallerinaToml("tool_test_init_1");
        executeCommand("tool_test_init_1", INIT);
        assertGeneratedSources("tool_test_init_1");
    }

    @Test(enabled = true)
    @Description("When there is an already initiated configs and there is an uninitiated schema")
    public void testInitUpdateConfigWithNewDbConfigurations() {
        assertGeneratedSourcesNegative("tool_test_init_2", INIT, new String[]{});
    }

    @Test(enabled = true)
    @Description("When there is a database config files inside the directories and there are missing database " +
            "configurations")
    public void testsInitUpdateConfigWithPartialyInitiatedFiles() {
        updateOutputBallerinaToml("tool_test_init_3");
        executeCommand("tool_test_init_3", INIT);
        assertGeneratedSources("tool_test_init_3");
    }

    @Test(enabled = true)
    @Description("When the init command is executed outside a Ballerina project")
    public void testsInitOutsideBalProject() {
        assertGeneratedSourcesNegative("tool_test_init_4", INIT, new String[]{"Config.toml"});
    }

    @Test(enabled = true)
    @Description("When the configs are already updated")
    public void testsInitUpdateConfigWithUpdatedDbConfigurations() {
        updateOutputBallerinaToml("tool_test_init_5");
        executeCommand("tool_test_init_5", INIT);
        assertGeneratedSources("tool_test_init_5");
    }

    @Test(enabled = true)
    @Description("Running init on a already initialized project")
    public void testInitAlreadyInitializedProject() {
        executeCommand("tool_test_init_6", INIT);
        assertGeneratedSources("tool_test_init_6");
    }

    @Test(enabled = true)
    @Description("Running init on a already initialized project with database configurations missing")
    public void testInitAlreadyInitializedProjectWithOutPersistConfiguration() {
        updateOutputBallerinaToml("tool_test_init_7");
        executeCommand("tool_test_init_7", INIT);
        assertGeneratedSources("tool_test_init_7");
    }

    @Test(enabled = true)
    @Description("Running init on a project with manually created definition file")
    public void testInitWithManuallyCreatedDefinitionFile() {
        updateOutputBallerinaToml("tool_test_init_9");
        executeCommand("tool_test_init_9", INIT);
        assertGeneratedSources("tool_test_init_9");
    }

    @Test(enabled = true)
    public void testInitArgs() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Init");
        Init persistCmd = (Init) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--help");
        persistCmd.execute();
        assertGeneratedSources("tool_test_init_11");

        Init persistCmd1 = (Init) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd1).parseArgs("--datastore", "");
        persistCmd1.execute();
        assertGeneratedSources("tool_test_init_11");

        Init persistCmd2 = (Init) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd2).parseArgs("--module", "^db");
        persistCmd2.execute();
        assertGeneratedSources("tool_test_init_11");

        Init persistCmd3 = (Init) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_11").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd3).parseArgs("--module",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        persistCmd3.execute();
        assertGeneratedSources("tool_test_init_11");
    }

    @Test(enabled = true)
    public void testInitWithModuleArg() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        updateOutputBallerinaToml("tool_test_init_12");
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Init");
        Init persistCmd = (Init) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_init_12").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--module", "test");
        persistCmd.execute();
        assertGeneratedSources("tool_test_init_12");
    }

    private void updateOutputBallerinaToml(String fileName) {
        String tomlFileName = "Ballerina.toml";
        Path filePath = Paths.get("src", "test", "resources", "test-src", "output", fileName, tomlFileName);
        if (filePath.endsWith(tomlFileName)) {
           try {
               String content = Files.readString(filePath);
               content = content.replaceAll(
                        "artifactId\\s=\\s\"persist-native\"\nversion\\s=\\s\\\"\\d+(\\.\\d+)+(-SNAPSHOT)?\\\"",
                        "artifactId = \"persist-native\"\nversion = \"" + version + "\"");
               Files.writeString(filePath, content);
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
