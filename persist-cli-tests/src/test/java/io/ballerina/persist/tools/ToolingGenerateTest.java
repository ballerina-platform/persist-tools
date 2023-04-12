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

import io.ballerina.persist.cmd.Generate;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.GENERATE;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.executeCommand;

/**
 * persist tool generate command tests.
 */
public class ToolingGenerateTest {

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project")
    public void testGenerateSingleEntity() {

        executeCommand("tool_test_generate_1", GENERATE);
        assertGeneratedSources("tool_test_generate_1");
    }

    @Test(enabled = true)
    @Description("There are multiple entities in the Ballerina project")
    public void testGenerateMultipleEntities() {
        executeCommand("tool_test_generate_2", GENERATE);
        assertGeneratedSources("tool_test_generate_2");
    }

    @Test(enabled = true)
    @Description("There are no entities nor already generated client objects in the Ballerina project")
    public void testGenerateWithoutEntitiesWithoutClients() {
        executeCommand("tool_test_generate_3", GENERATE);
        assertGeneratedSources("tool_test_generate_3");
    }

    @Test(enabled = true)
    @Description("When the generate command is executed outside a Ballerina project")
    public void testGenerateOutsideBalProject() {
        executeCommand("tool_test_generate_4", GENERATE);
        assertGeneratedSources("tool_test_generate_4");
    }

    @Test(enabled = true)
    @Description("There is a generated client object and the corresponding entity is updated")
    public void testGenerateUpdateEntity() {
        executeCommand("tool_test_generate_5", GENERATE);
        assertGeneratedSources("tool_test_generate_5");
    }

    @Test(enabled = true)
    @Description("A persist entity with all the supported fields data types")
    public void testGenerateAllEntityFieldTypes() {
        executeCommand("tool_test_generate_6", GENERATE);
        assertGeneratedSources("tool_test_generate_6");
    }

    @Test(enabled = true)
    @Description("Use case where unsupported datatypes are used")
    public void testGenerateClientWithUnsupportedDataTypes() {
        assertGeneratedSourcesNegative("tool_test_generate_7", GENERATE, new String[]{});
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project where key is a string")
    public void testGenerateSingleEntityWithStringKey() {
        executeCommand("tool_test_generate_8", GENERATE);
        assertGeneratedSources("tool_test_generate_8");
    }

    @Test(enabled = true) //removed until support for multiple keys are provided
    @Description("There is only a single entity in the Ballerina project with two keys")
    public void testGenerateSingleEntityWithMultipleKeysAndAutoInc() {
        executeCommand("tool_test_generate_9", GENERATE);
        assertGeneratedSources("tool_test_generate_9");
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
        executeCommand("tool_test_generate_13", GENERATE);
        assertGeneratedSources("tool_test_generate_13");
    }

    @Test(enabled = true)
    @Description("There are three entities in two schema files")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithChildEntityInSubModule() {
        assertGeneratedSourcesNegative("tool_test_generate_15", GENERATE, new String[]{});
    }
    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other")
    public void testGenerateClientsWith1ToManyAssociations() {
        executeCommand("tool_test_generate_16", GENERATE);
        assertGeneratedSources("tool_test_generate_16");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other")
    public void testGenerateThreeClientsWith1ToManyAssociations() {
        executeCommand("tool_test_generate_17", GENERATE);
        assertGeneratedSources("tool_test_generate_17");
    }
    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other without nullable fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithOutAnnotationValue() {
        assertGeneratedSourcesNegative("tool_test_generate_19", GENERATE, new String[]{});
    }

    @Test(enabled = true)
    @Description("There are two special entities with special characters in field names")
    public void testGenerateRelatedClientsWithSpecialCharactersInName() {
        executeCommand("tool_test_generate_22", GENERATE);
        assertGeneratedSources("tool_test_generate_22");
    }
    @Test(enabled = true)
    @Description("Negative test case where init command was not run before generate command")
    public void testGenerateWithoutInit() {
        assertGeneratedSourcesNegative ("tool_test_generate_23", GENERATE, new String[]{});
    }

    @Test(enabled = true)
    @Description("Test the generate command with entities containing byte[] fields")
    public void testGenerateWithByteArrays() {
        executeCommand("tool_test_generate_24", GENERATE);
        assertGeneratedSources("tool_test_generate_24");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other without relation annotation")
    public void testGenerateOneToManyAssociationsWithoutRelationAnnotation() {
        executeCommand("tool_test_generate_25", GENERATE);
        assertGeneratedSources("tool_test_generate_25");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to one associations between each other without relation annotation")
    public void testGenerateOneToOneAssociationsWithoutRelationAnnotation() {
        executeCommand("tool_test_generate_26", GENERATE);
        assertGeneratedSources("tool_test_generate_26");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithComments() {
        executeCommand("tool_test_generate_27", GENERATE);
        assertGeneratedSources("tool_test_generate_27");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1ToManyAssociationsWithComments() {
        executeCommand("tool_test_generate_28", GENERATE);
        assertGeneratedSources("tool_test_generate_28");
    }

    @Test(enabled = true)
    @Description("There is a entity inside the project with comments inside entity")
    public void testGenerateWithComments() {
        executeCommand("tool_test_generate_29", GENERATE);
        assertGeneratedSources("tool_test_generate_29");
    }

    @Test(enabled = true)
    @Description("Test the generate command with out defining any schema files inside persist directory")
    public void testGenerateWithoutSchemaFile() {
        executeCommand("tool_test_generate_30", GENERATE);
        assertGeneratedSources("tool_test_generate_30");
    }

    @Test(enabled = true)
    @Description("Test the generate command with empty schema file inside persist directory")
    public void testGenerateWithEmptySchemaFile() {
        executeCommand("tool_test_generate_31", GENERATE);
        assertGeneratedSources("tool_test_generate_31");
    }

    @Test(enabled = true)
    @Description("Test the generate command without persist import in schema file")
    public void testGenerateWithoutPersistImport() {
        executeCommand("tool_test_generate_33", GENERATE);
        assertGeneratedSources("tool_test_generate_33");
    }

    @Test(enabled = true)
    @Description("Test the generate command with optional type in schema file")
    public void testGenerateWithOptionalType() {
        executeCommand("tool_test_generate_34", GENERATE);
        assertGeneratedSources("tool_test_generate_34");
    }

    @Test(enabled = true)
    @Description("Test the generate command in default Ballerina package")
    public void testGenerateInDefaultPackage() {
        executeCommand("tool_test_generate_35", GENERATE);
        assertGeneratedSources("tool_test_generate_35");
    }

    @Test(enabled = true)
    @Description("Test the generate command with escape character in entity fields, and names")
    public void testGenerateRelationsWithSingleQuote() {
        executeCommand("tool_test_generate_36", GENERATE);
        assertGeneratedSources("tool_test_generate_36");
    }

    @Test(enabled = true)
    @Description("Test the created sql script content for relations and byte[] type")
    public void testSqlGen() {
        executeCommand("tool_test_generate_37", GENERATE);
        assertGeneratedSources("tool_test_generate_37");
    }

    @Test(enabled = true)
    @Description("Test the created sql script content with out defining any schema files inside persist directory")
    public void testSqlGenWithoutSchemaFile() {
        executeCommand("tool_test_generate_38", GENERATE);
        assertGeneratedSources("tool_test_generate_38");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with one to many relation entity")
    public void testSqlGenWithOneToManyRelationship() {
        executeCommand("tool_test_generate_39", GENERATE);
        assertGeneratedSources("tool_test_generate_39");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with optional type fields")
    public void testSqlGenWithOptionalTypeFields() {
        executeCommand("tool_test_generate_40", GENERATE);
        assertGeneratedSources("tool_test_generate_40");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with composite reference keys")
    public void testSqlGenWithCompositeReferenceKeys() {
        executeCommand("tool_test_generate_41", GENERATE);
        assertGeneratedSources("tool_test_generate_41");
    }

    @Test(enabled = true)
    public void testGenerateArgs() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Generate");
        Generate persistCmd = (Generate) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_generate_42").toAbsolutePath().
                        toString());
        new CommandLine(persistCmd).parseArgs("--help");
        persistCmd.execute();
        assertGeneratedSources("tool_test_generate_42");
    }

    @Test(enabled = true)
    public void testInvalidDataStore() {
        executeCommand("tool_test_generate_42", GENERATE);
        assertGeneratedSources("tool_test_generate_42");
    }

    @Test(enabled = true)
    public void testInvalidModuleName() {
        executeCommand("tool_test_generate_43", GENERATE);
        assertGeneratedSources("tool_test_generate_43");
    }

    @Test(enabled = true)
    public void testInvalidModuleName1() {
        executeCommand("tool_test_generate_44", GENERATE);
        assertGeneratedSources("tool_test_generate_44");
    }

    @Test(enabled = true)
    public void testInvalidModuleName2() {
        executeCommand("tool_test_generate_45", GENERATE);
        assertGeneratedSources("tool_test_generate_45");
    }

    @Test(enabled = true)
    @Description("Test the generated types for multiple association between same entities")
    public void testGenerateMultipleAssociationBetweenSameEntities() {
        executeCommand("tool_test_generate_46", GENERATE);
        assertGeneratedSources("tool_test_generate_46");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryEntity() {
        executeCommand("tool_test_generate_47_in_memory", GENERATE);
        assertGeneratedSources("tool_test_generate_47_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryWithAssociatedEntity() {
        executeCommand("tool_test_generate_48_in_memory", GENERATE);
        assertGeneratedSources("tool_test_generate_48_in_memory");
    }
}
