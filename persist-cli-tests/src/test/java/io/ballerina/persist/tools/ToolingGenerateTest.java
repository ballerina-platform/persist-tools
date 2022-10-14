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

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.GENERATE;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

/**
 * persist tool generate command tests.
 */
public class ToolingGenerateTest {

    @Test()
    @Description("There is only a single entity in the Ballerina project")
    public void testGenerateSingleEntity() {

        assertGeneratedSources("tool_test_generate_1", GENERATE);
    }

    @Test()
    @Description("There are multiple entities in the Ballerina project")
    public void testGenerateMultipleEntities() {
        assertGeneratedSources("tool_test_generate_2", GENERATE);
    }

    @Test()
    @Description("There are no entities nor already generated client objects in the Ballerina project")
    public void testGenerateWithoutEntitiesWithoutClients() {
        assertGeneratedSources("tool_test_generate_3", GENERATE);
    }

    @Test()
    @Description("When the generate command is executed outside a Ballerina project")
    public void testGenerateOutsideBalProject() {
        assertGeneratedSources("tool_test_generate_4", GENERATE);
    }

    @Test()
    @Description("There is a generated client object and the corresponding entity is updated")
    public void testGenerateUpdateEntity() {
        assertGeneratedSources("tool_test_generate_5", GENERATE);
    }

    @Test()
    @Description("A persist entity with all the supported fields data types")
    public void testGenerateAllEntityFieldTypes() {
        assertGeneratedSources("tool_test_generate_6", GENERATE);
    }

    @Test()
    @Description("Use case where a entity is located inside a module")
    public void testGenerateClientWithEntityInModule() {
        assertGeneratedSources("tool_test_generate_7", GENERATE);
    }

    @Test()
    @Description("There is only a single entity in the Ballerina project where key is a string")
    public void testGenerateSingleEntityWithStringKey() {
        assertGeneratedSources("tool_test_generate_8", GENERATE);
    }

    @Test()
    @Description("There is only a single entity in the Ballerina project with two keys one autoincrement")
    public void testGenerateSingleEntityWithMultipleKeysAndAutoInc() {
        assertGeneratedSources("tool_test_generate_9", GENERATE);
    }

    @Test()
    @Description("There is only a single entity in the Ballerina project with two keys without autoincrement")
    public void testGenerateSingleEntityWithMultipleKeys() {
        assertGeneratedSources("tool_test_generate_10", GENERATE);
    }

    @Test()
    @Description("There is only a single entity in the Ballerina project and there are errors in the project")
    public void testGenerateSingleEntityWithErrors() {
        assertGeneratedSourcesNegative("tool_test_generate_11", GENERATE, "modules");
    }

    @Test()
    @Description("There is only a single entity in the Ballerina project and there are errors in Entity annotation")
    public void testGenerateSingleEntityWithAnnotationErrors() {
        assertGeneratedSourcesNegative("tool_test_generate_12", GENERATE, "modules");
    }
    @Test()
    @Description("There are three entities with one to one associations between each other")
    public void testGenerateThreeEntitiesWith1To1Associations() {
        assertGeneratedSources("tool_test_generate_13", GENERATE);
    }

    @Test(enabled = false)
    @Description("There are three entities with one to one associations between each other with one parent entity " +
            "in sub module")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithEntityInSubModule() {
        assertGeneratedSources("tool_test_generate_14", GENERATE);
    }

    @Test(enabled = false)
    @Description("There are three entities with one to one associations between each other with one child entity " +
            "in sub module")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithChildEntityInSubModule() {
        assertGeneratedSources("tool_test_generate_15", GENERATE);
    }
}
