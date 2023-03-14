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

import jdk.jfr.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.INIT;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertAuxiliaryFunctions;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

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
        assertGeneratedSources("tool_test_init_1", INIT);
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
        assertGeneratedSources("tool_test_init_3", INIT);
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
        assertGeneratedSources("tool_test_init_5", INIT);
    }

    @Test(enabled = true)
    @Description("Test the auxiliary functions of the class")
    public void testAuxiliaryFunctions() {
        assertAuxiliaryFunctions();
    }

    @Test(enabled = true)
    @Description("Running init on a already initialized project")
    public void testInitAlreadyInitializedProject() {
        updateOutputBallerinaToml("tool_test_init_6");
        assertGeneratedSources("tool_test_init_6", INIT);
    }

    @Test(enabled = true)
    @Description("Running init on a already initialized project with database configurations missing")
    public void testInitAlreadyInitializedProjectWithOutDatabaseConfiguration() {
        updateOutputBallerinaToml("tool_test_init_7");
        assertGeneratedSources("tool_test_init_7", INIT);
    }

    @Test(enabled = false)
    @Description("Running init on a project with syntax errors.")
    public void testInitWithSyntaxErrors() {
        updateOutputBallerinaToml("tool_test_init_8");
        assertGeneratedSourcesNegative("tool_test_init_8", INIT, new String[]{});
    }

    @Test(enabled = true)
    @Description("Running init on a project with manually created definition file")
    public void testInitWithManuallyCreatedDefinitionFile() {
        updateOutputBallerinaToml("tool_test_init_9");
        assertGeneratedSources("tool_test_init_9", INIT);
    }

    @Test(enabled = false) //disables as the schema file would have a defined name
    @Description("Running init on a project with invalid definition filename")
    public void testInitWithInvalidDefinitionFileName() {
        updateOutputBallerinaToml("tool_test_init_10");
        assertGeneratedSources("tool_test_init_10", INIT);
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
