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
import org.testng.annotations.Test;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.INIT;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertAuxiliaryFunctions;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

/**
 * persist tool init command tests.
 */
public class ToolingInitTest {

    @Test()
    @Description("When there isn't a Config.toml file inside the project root directory")
    public void testInitCreateConfig() {
        assertGeneratedSources("tool_test_init_1", INIT);
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory but there are no database " +
            "configurations")
    public void testInitUpdateConfigWithNewDbConfigurations() {
        assertGeneratedSources("tool_test_init_2", INIT);
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory and there are database " +
            "configurations")
    public void testsInitUpdateConfigWithUpdatedDbConfigurations() {
        assertGeneratedSources("tool_test_init_3", INIT);
    }

    @Test()
    @Description("When the init command is executed outside a Ballerina project")
    public void testsInitOutsideBalProject() {
        assertGeneratedSourcesNegative("tool_test_init_4", INIT, "Config.toml");
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory and there are database " +
            "configurations mixed with other configurations")
    public void testsInitUpdateConfigWithUpdatedDbConfigurationsMixed() {
        assertGeneratedSources("tool_test_init_5", INIT);
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory and there are database " +
            "configurations mixed with other configurations")
    public void testsInitUpdateConfigWithUpdatedDbConfigurationsMixed2() {
        assertGeneratedSources("tool_test_init_6", INIT);
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory but there are no database " +
            "configurations but there is a table")
    public void testInitUpdateConfigWithNewDbConfigurationsWithTable() {
        assertGeneratedSources("tool_test_init_7", INIT);
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory but there are no database " +
            "configurations but there is a table")
    public void testInitUpdateConfigWithNewDbConfigurationsWithTableArray() {
        assertGeneratedSources("tool_test_init_8", INIT);
    }

    @Test()
    @Description("When there is a Config.toml file inside the project root directory and there are database " +
            "configurations and a table")
    public void testInitUpdateConfigWithUpdateDbConfigurationsWithTableArray() {
        assertGeneratedSources("tool_test_init_9", INIT);
    }

    @Test()
    @Description("Test the auxiliary functions of the class")
    public void testAuxiliaryFunctions() {
        assertAuxiliaryFunctions();
    }
}
