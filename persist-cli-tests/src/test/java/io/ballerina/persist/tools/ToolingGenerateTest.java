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

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project")
    public void testGenerateSingleEntity() {

        assertGeneratedSources("tool_test_generate_1", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are multiple entities in the Ballerina project")
    public void testGenerateMultipleEntities() {
        assertGeneratedSources("tool_test_generate_2", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are no entities nor already generated client objects in the Ballerina project")
    public void testGenerateWithoutEntitiesWithoutClients() {
        assertGeneratedSources("tool_test_generate_3", GENERATE);
    }

    @Test(enabled = true)
    @Description("When the generate command is executed outside a Ballerina project")
    public void testGenerateOutsideBalProject() {
        assertGeneratedSources("tool_test_generate_4", GENERATE);
    }

    @Test(enabled = true)
    @Description("There is a generated client object and the corresponding entity is updated")

    public void testGenerateUpdateEntity() {
        assertGeneratedSources("tool_test_generate_5", GENERATE);
    }

    @Test(enabled = true)
    @Description("A persist entity with all the supported fields data types")
    public void testGenerateAllEntityFieldTypes() {
        assertGeneratedSources("tool_test_generate_6", GENERATE);
    }

    @Test(enabled = true)
    @Description("Use case where unsupported datatypes are used")
    public void testGenerateClientWithUnsupportedDataTypes() {
        assertGeneratedSourcesNegative("tool_test_generate_7", GENERATE, new String[]{});
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project where key is a string")
    public void testGenerateSingleEntityWithStringKey() {
        assertGeneratedSources("tool_test_generate_8", GENERATE);
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project with two keys one autoincrement")
    public void testGenerateSingleEntityWithMultipleKeysAndAutoInc() {
        assertGeneratedSources("tool_test_generate_9", GENERATE);
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project with two keys without autoincrement")
    public void testGenerateSingleEntityWithMultipleKeys() {
        assertGeneratedSources("tool_test_generate_10", GENERATE);
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project and there are errors in the project")
    public void testGenerateSingleEntityWithErrors() {
        assertGeneratedSourcesNegative("tool_test_generate_11", GENERATE,  new String[]{});
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the schema with wrong import")
    public void testGenerateSingleEntityWithWrongImport() {
        assertGeneratedSourcesNegative("tool_test_generate_12", GENERATE, new String[]{});
    }
    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other")
    public void testGenerateThreeEntitiesWith1To1Associations() {
        assertGeneratedSources("tool_test_generate_13", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other with one parent entity " +
            "in sub module")
    public void testGenerateWithDifferentEntities() {
        assertGeneratedSources("tool_test_generate_14", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are three entities in two schema files")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithChildEntityInSubModule() {
        assertGeneratedSourcesNegative("tool_test_generate_15", GENERATE, new String[]{});
    }
    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other")
    public void testGenerateClientsWith1ToManyAssociations() {
        assertGeneratedSources("tool_test_generate_16", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other")
    public void testGenerateThreeClientsWith1ToManyAssociations() {
        assertGeneratedSources("tool_test_generate_17", GENERATE);
    }
    @Test(enabled = true)
    @Description("There are two entities with one to one associations between each " +
            "other with no annotation values in any Relation")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithNoAnnotationValue() {
        assertGeneratedSources("tool_test_generate_18", GENERATE);
    }
    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other with only " +
            "one annotation values in any Relation")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithOneAnnotationValue() {
        assertGeneratedSources("tool_test_generate_19", GENERATE);
    }
    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other with zero to " +
            "one annotations")
    public void testGenerateThreeEntitiesWith1ToManyAssociationsWithOneToNoAnnotationValue() {
        assertGeneratedSources("tool_test_generate_20", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are two entities and time modeule is imported through a relation")
    public void testGenerateClientsWithAdditionsImportsTroughRelations() {
        assertGeneratedSources("tool_test_generate_21", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are two special entities with special characters in field names")
    public void testGenerateRelatedClientsWithSpecialCharactersInName() {
        assertGeneratedSources("tool_test_generate_22", GENERATE);
    }
    @Test(enabled = true)
    @Description("Negative test case where init command was not run before generate command")
    public void testGenerateWithoutInit() {
        assertGeneratedSourcesNegative ("tool_test_generate_23", GENERATE, new String[]{});
    }

    @Test(enabled = false) // Disabled due to windows build failure
    @Description("Generate is executed with clients already initailized in main.bal")
    public void testGenerateUpdateClientsWithAlreadyInitializedClients() {
        assertGeneratedSources("tool_test_generate_24", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other without relation annotation")
    public void testGenerateOneToManyAssociationsWithoutRelationAnnotation() {
        assertGeneratedSources("tool_test_generate_25", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are two entities with one to one associations between each other without relation annotation")
    public void testGenerateOneToOneAssociationsWithoutRelationAnnotation() {
        assertGeneratedSources("tool_test_generate_26", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithComments() {
        assertGeneratedSources("tool_test_generate_27", GENERATE);
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1ToManyAssociationsWithComments() {
        assertGeneratedSources("tool_test_generate_28", GENERATE);
    }

    @Test(enabled = true)
    @Description("There is a entity inside the project with comments inside entity")
    public void testGenerateWithComments() {
        assertGeneratedSources("tool_test_generate_29", GENERATE);
    }
}
