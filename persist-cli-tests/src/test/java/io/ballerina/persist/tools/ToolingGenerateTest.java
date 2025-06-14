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

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.GENERATE;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.INIT;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.executeCommand;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.executeGenerateCommand;

/**
 * persist tool generate command tests.
 */
public class ToolingGenerateTest {
    private String persistSqlVersion;
    private String persistInMemoryVersion;
    private String persistGoogleSheetsVersion;
    private String persistRedisVersion;

    @BeforeClass
    public void findLatestPersistVersion() {
        Path versionPropertiesFile = Paths.get("../", "persist-cli", "src", "main", "resources",
                "version.properties").toAbsolutePath();
        try (InputStream inputStream = Files.newInputStream(versionPropertiesFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            persistSqlVersion = properties.get("persistSqlVersion").toString();
            persistInMemoryVersion = properties.get("persistInMemoryVersion").toString();
            persistGoogleSheetsVersion = properties.get("persistGoogleSheetsVersion").toString();
            persistRedisVersion = properties.get("persistRedisVersion").toString();
        } catch (IOException e) {
            // ignore
        }
    }

    @Test(enabled = true)
    @Description("There is multiple entities with associations in the Ballerina project")
    public void testGenerateAssociatedEntities() {
        updateOutputBallerinaToml("tool_test_generate_1");
        executeGenerateCommand("tool_test_generate_1",
                "--datastore", "mysql", "--module", "persist_generate_1");
        assertGeneratedSources("tool_test_generate_1");
    }

    @Test(enabled = true)
    @Description("There are multiple entities in the Ballerina project")
    public void testGenerateMultipleEntities() {
        updateOutputBallerinaToml("tool_test_generate_2");
        executeGenerateCommand("tool_test_generate_2",
                "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_2");
    }

    @Test(enabled = true)
    @Description("There are no entities nor already generated client objects in the Ballerina project")
    public void testGenerateWithoutEntitiesWithoutClients() {
        updateOutputBallerinaToml("tool_test_generate_3");
        executeGenerateCommand("tool_test_generate_3",
                "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_3");
    }

    @Test(enabled = true)
    @Description("When the generate command is executed outside a Ballerina project")
    public void testGenerateOutsideBalProject() {
        updateOutputBallerinaToml("tool_test_generate_4");
        executeCommand("tool_test_generate_4", GENERATE);
        assertGeneratedSources("tool_test_generate_4");
    }

    @Test(enabled = true)
    @Description("There is a generated client object and the corresponding entity is updated")
    public void testGenerateUpdateEntity() {
        updateOutputBallerinaToml("tool_test_generate_5");
        executeGenerateCommand("tool_test_generate_5",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_5");
    }

    @Test(enabled = true)
    @Description("A persist entity with all the supported fields data types")
    public void testGenerateAllEntityFieldTypes() {
        updateOutputBallerinaToml("tool_test_generate_6");
        executeGenerateCommand("tool_test_generate_6",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "inmemory");
        assertGeneratedSources("tool_test_generate_6");
    }

    @Test(enabled = true)
    @Description("Use case where unsupported datatypes are used")
    public void testGenerateClientWithUnsupportedDataTypes() {
        updateOutputBallerinaToml("tool_test_generate_7");
        assertGeneratedSourcesNegative("tool_test_generate_7", GENERATE, new String[]{},
                "--datastore", "mysql", "--module", "entities");
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project where key is a string")
    public void testGenerateSingleEntityWithStringKey() {
        updateOutputBallerinaToml("tool_test_generate_8");
        executeGenerateCommand("tool_test_generate_8", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_8");
    }

    @Test(enabled = true) //removed until support for multiple keys are provided
    @Description("There is only a single entity in the Ballerina project with two keys")
    public void testGenerateSingleEntityWithMultipleKeysAndAutoInc() {
        updateOutputBallerinaToml("tool_test_generate_9");
        executeGenerateCommand("tool_test_generate_9", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_9");
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project and there are errors in the project")
    public void testGenerateSingleEntityWithErrors() {
        updateOutputBallerinaToml("tool_test_generate_11");
        assertGeneratedSourcesNegative("tool_test_generate_11", GENERATE, new String[]{},
                "--datastore", "mysql", "--module", "entities");
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the schema with wrong import")
    public void testGenerateSingleEntityWithWrongImport() {
        updateOutputBallerinaToml("tool_test_generate_12");
        assertGeneratedSourcesNegative("tool_test_generate_12", GENERATE, new String[]{},
                "--datastore", "mysql", "--module", "entities");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other")
    public void testGenerateThreeEntitiesWith1To1Associations() {
        updateOutputBallerinaToml("tool_test_generate_13");
        executeGenerateCommand("tool_test_generate_13",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_13");
    }

    @Test(enabled = true)
    @Description("There are three entities in two schema files")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithChildEntityInSubModule() {
        updateOutputBallerinaToml("tool_test_generate_15");
        assertGeneratedSourcesNegative("tool_test_generate_15", GENERATE, new String[]{},
                "--datastore", "mysql", "--module", "entities");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other")
    public void testGenerateClientsWith1ToManyAssociations() {
        updateOutputBallerinaToml("tool_test_generate_16");
        executeGenerateCommand("tool_test_generate_16",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_16");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other")
    public void testGenerateThreeClientsWith1ToManyAssociations() {
        updateOutputBallerinaToml("tool_test_generate_17");
        executeGenerateCommand("tool_test_generate_17", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_17");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other without nullable fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithOutAnnotationValue() {
        updateOutputBallerinaToml("tool_test_generate_19");
        assertGeneratedSourcesNegative("tool_test_generate_19", GENERATE, new String[]{},
                "--datastore", "mysql", "--module", "entities");
    }

    @Test(enabled = true)
    @Description("There are two special entities with special characters in field names")
    public void testGenerateRelatedClientsWithSpecialCharactersInName() {
        updateOutputBallerinaToml("tool_test_generate_22");
        executeGenerateCommand("tool_test_generate_22",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_22");
    }

    @Test(enabled = true)
    @Description("Negative test case where init command was not run before generate command")
    public void testGenerateWithoutInit() {
        updateOutputBallerinaToml("tool_test_generate_23");
        assertGeneratedSourcesNegative("tool_test_generate_23", GENERATE, new String[]{},
                "--datastore", "mysql", "--module", "entities");
    }

    @Test(enabled = true)
    @Description("Test the generate command with entities containing byte[] fields")
    public void testGenerateWithByteArrays() {
        updateOutputBallerinaToml("tool_test_generate_24");
        executeGenerateCommand("tool_test_generate_24", "--datastore", "mysql", "--module", "foo");
        assertGeneratedSources("tool_test_generate_24");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other without relation annotation")
    public void testGenerateOneToManyAssociationsWithoutRelationAnnotation() {
        updateOutputBallerinaToml("tool_test_generate_25");
        executeGenerateCommand("tool_test_generate_25", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_25");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to one associations between each other without relation annotation")
    public void testGenerateOneToOneAssociationsWithoutRelationAnnotation() {
        updateOutputBallerinaToml("tool_test_generate_26");
        executeGenerateCommand("tool_test_generate_26", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_26");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithComments() {
        updateOutputBallerinaToml("tool_test_generate_27");
        executeGenerateCommand("tool_test_generate_27", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_27");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1ToManyAssociationsWithComments() {
        updateOutputBallerinaToml("tool_test_generate_28");
        executeGenerateCommand("tool_test_generate_28", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_28");
    }

    @Test(enabled = true)
    @Description("There is a entity inside the project with comments inside entity")
    public void testGenerateWithComments() {
        updateOutputBallerinaToml("tool_test_generate_29");
        executeGenerateCommand("tool_test_generate_29",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_29");
    }

    @Test(enabled = true)
    @Description("Test the generate command with out defining any schema files inside persist directory")
    public void testGenerateWithoutSchemaFile() {
        updateOutputBallerinaToml("tool_test_generate_30");
        executeGenerateCommand("tool_test_generate_30",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_30");
    }

    @Test(enabled = true)
    @Description("Test the generate command with empty schema file inside persist directory")
    public void testGenerateWithEmptySchemaFile() {
        updateOutputBallerinaToml("tool_test_generate_31");
        executeGenerateCommand("tool_test_generate_31",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_31");
    }

    @Test(enabled = true)
    @Description("Test the generate command without persist import in schema file")
    public void testGenerateWithoutPersistImport() {
        updateOutputBallerinaToml("tool_test_generate_33");
        executeGenerateCommand("tool_test_generate_33",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_33");
    }

    @Test(enabled = true)
    @Description("Test the generate command with optional type in schema file")
    public void testGenerateWithOptionalType() {
        updateOutputBallerinaToml("tool_test_generate_34");
        executeGenerateCommand("tool_test_generate_34",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_34");
    }

    @Test(enabled = true)
    @Description("Test the generate command in default Ballerina package")
    public void testGenerateInDefaultPackage() {
        updateOutputBallerinaToml("tool_test_generate_35");
        executeGenerateCommand("tool_test_generate_35", "--datastore", "mysql", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_35");
    }

    @Test(enabled = true)
    @Description("Test the generate command with escape character in entity fields, and names")
    public void testGenerateRelationsWithSingleQuote() {
        updateOutputBallerinaToml("tool_test_generate_36");
        executeGenerateCommand("tool_test_generate_36", "--datastore", "mysql", "--module", "rainier",
                "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_36");
    }

    @Test(enabled = true)
    @Description("Test the created sql script content for relations and byte[] type")
    public void testSqlGen() {
        updateOutputBallerinaToml("tool_test_generate_37");
        executeGenerateCommand("tool_test_generate_37",
                "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_37");
    }

    @Test(enabled = true)
    @Description("Test the created sql script content with out defining any schema files inside persist directory")
    public void testSqlGenWithoutSchemaFile() {
        updateOutputBallerinaToml("tool_test_generate_38");
        executeGenerateCommand("tool_test_generate_38", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_38");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with one to many relation entity")
    public void testSqlGenWithOneToManyRelationship() {
        updateOutputBallerinaToml("tool_test_generate_39");
        executeGenerateCommand("tool_test_generate_39", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_39");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with optional type fields")
    public void testSqlGenWithOptionalTypeFields() {
        updateOutputBallerinaToml("tool_test_generate_40");
        executeGenerateCommand("tool_test_generate_40", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_40");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with composite reference keys")
    public void testSqlGenWithCompositeReferenceKeys() {
        updateOutputBallerinaToml("tool_test_generate_41");
        executeGenerateCommand("tool_test_generate_41", "--datastore", "mysql", "--module", "entities");
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
        executeGenerateCommand("tool_test_generate_42", "--datastore", "my", "--module", "entities");
        assertGeneratedSources("tool_test_generate_42");
    }

    @Test(enabled = true)
    public void testInvalidModuleName() {
        String invalidModuleName = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt" +
                "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt" +
                "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt" +
                "esttesttesttesttesttesttesttest";
        executeGenerateCommand("tool_test_generate_43",
                "--datastore", "my", "--module", invalidModuleName);
        assertGeneratedSources("tool_test_generate_43");
    }

    @Test(enabled = true)
    public void testInvalidModuleName1() {
        executeGenerateCommand("tool_test_generate_44", "--datastore", "mysql", "--module", ".<test");
        assertGeneratedSources("tool_test_generate_44");
    }

    @Test(enabled = true)
    public void testInvalidModuleName2() {
        executeGenerateCommand("tool_test_generate_45", "--datastore", "mysql", "--module", "^^^");
        assertGeneratedSources("tool_test_generate_45");
    }

    @Test(enabled = true)
    @Description("Test the generated types for multiple association between same entities")
    public void testGenerateMultipleAssociationBetweenSameEntities() {
        updateOutputBallerinaToml("tool_test_generate_46");
        executeGenerateCommand("tool_test_generate_46", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_46");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryEntity() {
        updateOutputBallerinaToml("tool_test_generate_47_in_memory");
        executeGenerateCommand("tool_test_generate_47_in_memory", "--datastore",
                "inmemory", "--module", "entities");
        assertGeneratedSources("tool_test_generate_47_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryWithAssociatedEntity() {
        updateOutputBallerinaToml("tool_test_generate_48_in_memory");
        executeGenerateCommand("tool_test_generate_48_in_memory", "--datastore",
                "inmemory", "--module", "entities");
        assertGeneratedSources("tool_test_generate_48_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryWithCompositeKeys() {
        updateOutputBallerinaToml("tool_test_generate_49_in_memory");
        executeGenerateCommand("tool_test_generate_49_in_memory", "--datastore",
                "inmemory", "--module", "entities");
        assertGeneratedSources("tool_test_generate_49_in_memory");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and no imports")
    public void testGenerateWithEnums() {
        updateOutputBallerinaToml("tool_test_generate_50");
        executeGenerateCommand("tool_test_generate_50", "--datastore", "mysql");
        assertGeneratedSources("tool_test_generate_50");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports")
    public void testGenerateWithEnumsWithImports() {
        updateOutputBallerinaToml("tool_test_generate_51");
        executeGenerateCommand("tool_test_generate_51", "--datastore", "mysql");
        assertGeneratedSources("tool_test_generate_51");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with in memory data source")
    public void testGenerateWithEnumsInMemory() {
        updateOutputBallerinaToml("tool_test_generate_52_in_memory");
        executeGenerateCommand("tool_test_generate_52_in_memory",
                "--datastore", "inmemory",  "--module", "entities");
        assertGeneratedSources("tool_test_generate_52_in_memory");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsInMemory() {
        updateOutputBallerinaToml("tool_test_generate_53_in_memory");
        executeGenerateCommand("tool_test_generate_53_in_memory",
                "--datastore", "inmemory",  "--module", "entities");
        assertGeneratedSources("tool_test_generate_53_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with google sheets data source")
    public void testGoogleSheet() {
        updateOutputBallerinaToml("tool_test_generate_54_gsheet");
        executeGenerateCommand("tool_test_generate_54_gsheet",
                "--datastore", "googlesheets",  "--module", "entities", "--test-datastore", "inmemory");
        assertGeneratedSources("tool_test_generate_54_gsheet");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeInMemory() {
        updateOutputBallerinaToml("tool_test_generate_55_in_memory");
        executeGenerateCommand("tool_test_generate_55_in_memory",
                "--datastore", "inmemory",  "--module", "entities");
        assertGeneratedSources("tool_test_generate_55_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeGoogleSheet() {
        updateOutputBallerinaToml("tool_test_generate_56_gsheets");
        executeGenerateCommand("tool_test_generate_56_gsheets",
                "--datastore", "googlesheets",  "--module", "entities");
        assertGeneratedSources("tool_test_generate_56_gsheets");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with google sheets data source and ENUM as field type.")
    public void testGoogleSheetWithEnum() {
        updateOutputBallerinaToml("tool_test_generate_57_gsheets");
        executeGenerateCommand("tool_test_generate_57_gsheets",
                "--datastore", "googlesheets",  "--module", "entities");
        assertGeneratedSources("tool_test_generate_57_gsheets");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with mssql data source")
    public void testMssqlEntity() {
        updateOutputBallerinaToml("tool_test_generate_58_mssql");
        executeGenerateCommand("tool_test_generate_58_mssql",
                "--datastore", "mssql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_58_mssql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with postgresql data source")
    public void testPostgresqlEntity() {
        updateOutputBallerinaToml("tool_test_generate_66_postgresql");
        executeGenerateCommand("tool_test_generate_66_postgresql",
                "--datastore", "postgresql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_66_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with h2 data source")
    public void testH2Entity() {
        updateOutputBallerinaToml("tool_test_generate_107_h2");
        executeGenerateCommand("tool_test_generate_107_h2",
                "--datastore", "h2", "--module", "entities");
        assertGeneratedSources("tool_test_generate_107_h2");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with mssql data source")
    public void testMSSQLWithAssociatedEntity() {
        updateOutputBallerinaToml("tool_test_generate_59_mssql");
        executeGenerateCommand("tool_test_generate_59_mssql",
                "--datastore", "mssql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_59_mssql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with postgresql data source")
    public void testPostgreSQLWithAssociatedEntity() {
        updateOutputBallerinaToml("tool_test_generate_67_postgresql");
        executeGenerateCommand("tool_test_generate_67_postgresql",
                "--datastore", "postgresql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_67_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with h2 data source")
    public void testH2WithAssociatedEntity() {
        updateOutputBallerinaToml("tool_test_generate_108_h2");
        executeGenerateCommand("tool_test_generate_108_h2",
                "--datastore", "h2", "--module", "entities");
        assertGeneratedSources("tool_test_generate_108_h2");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with mssql data source")
    public void testMSSQLWithCompositeKeys() {
        updateOutputBallerinaToml("tool_test_generate_60_mssql");
        executeGenerateCommand("tool_test_generate_60_mssql",
                "--datastore", "mssql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_60_mssql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with postgresql data source")
    public void testPostgreSQLWithCompositeKeys() {
        updateOutputBallerinaToml("tool_test_generate_68_postgresql");
        executeGenerateCommand("tool_test_generate_68_postgresql",
                "--datastore", "postgresql", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_68_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with h2 data source")
    public void testH2WithCompositeKeys() {
        updateOutputBallerinaToml("tool_test_generate_109_h2");
        executeGenerateCommand("tool_test_generate_109_h2",
                "--datastore", "h2", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_109_h2");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with mssql data source")
    public void testGenerateWithEnumsMSSQL() {
        updateOutputBallerinaToml("tool_test_generate_61_mssql");
        executeGenerateCommand("tool_test_generate_61_mssql",
                "--datastore", "mssql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_61_mssql");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with postgresql data source")
    public void testGenerateWithEnumsPostgresSQL() {
        updateOutputBallerinaToml("tool_test_generate_69_postgresql");
        executeGenerateCommand("tool_test_generate_69_postgresql",
                "--datastore", "postgresql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_69_postgresql");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with h2 data source")
    public void testGenerateWithEnumsH2() {
        updateOutputBallerinaToml("tool_test_generate_110_h2");
        executeGenerateCommand("tool_test_generate_110_h2", "--datastore", "h2", "--module", "entities");
        assertGeneratedSources("tool_test_generate_110_h2");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsMSSQL() {
        updateOutputBallerinaToml("tool_test_generate_62_mssql");
        executeGenerateCommand("tool_test_generate_62_mssql",
                "--datastore", "mssql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_62_mssql");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsPostgresql() {
        updateOutputBallerinaToml("tool_test_generate_70_postgresql");
        executeGenerateCommand("tool_test_generate_70_postgresql",
                "--datastore", "postgresql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_70_postgresql");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsH2() {
        updateOutputBallerinaToml("tool_test_generate_111_h2");
        executeGenerateCommand("tool_test_generate_111_h2", "--datastore", "h2", "--module", "entities");
        assertGeneratedSources("tool_test_generate_111_h2");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeMSSQL() {
        updateOutputBallerinaToml("tool_test_generate_63_mssql");
        executeGenerateCommand("tool_test_generate_63_mssql",
                "--datastore", "mssql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_63_mssql");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypePostgreSQL() {
        updateOutputBallerinaToml("tool_test_generate_71_postgresql");
        executeGenerateCommand("tool_test_generate_71_postgresql",
                "--datastore", "postgresql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_71_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeH2() {
        updateOutputBallerinaToml("tool_test_generate_112_h2");
        executeGenerateCommand("tool_test_generate_112_h2", "--datastore", "h2", "--module", "entities");
        assertGeneratedSources("tool_test_generate_112_h2");
    }

    @Test(enabled = true)
    @Description("module name is in the shape x.y.z")
    public void testGenerateEntityDotSeperatedModuleNames() {
        updateOutputBallerinaToml("tool_test_generate_64");
        executeGenerateCommand("tool_test_generate_64", "--datastore", "mysql", "--module", "x.y.z");
        assertGeneratedSources("tool_test_generate_64");
    }

    @Test(enabled = true)
    @Description("The model has multiple relations of various types")
    public void testGenerateEntitiesWithMultipleRelations() {
        updateOutputBallerinaToml("tool_test_generate_65");
        executeGenerateCommand("tool_test_generate_65", "--datastore", "mysql");
        assertGeneratedSources("tool_test_generate_65");
    }

    @Test(enabled = true)
    @Description("The model has multiple relations of various types")
    public void testInit() {
        executeCommand("tool_test_init_1", INIT);
        assertGeneratedSources("tool_test_init_1");
    }

    @Test
    @Description("The model has a relation with relation annotation")
    public void testGenerateEntitiesWithRelationAnnotations() {
        String subDir = "tool_test_generate_72";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Mapping annotations on entities")
    public void testGenerateEntitiesWithMappingAnnotations() {
        String subDir = "tool_test_generate_73";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Mapping annotations on foreign keys.")
    public void testGenerateEntitiesWithMappingAnnotationOnForeignKeys() {
        String subDir = "tool_test_generate_74";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has type mapping annotations Char, VarChar and Decimal")
    public void testGenerateEntitiesWithTypeMappingAnnotations() {
        String subDir = "tool_test_generate_75";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a unique index on a field")
    public void testGenerateEntitiesWithUniqueIndexesOnOneField() {
        String subDir = "tool_test_generate_76";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a single unique index on two fields")
    public void testGenerateEntitiesSameUniqueIndexOnTwoFields() {
        String subDir = "tool_test_generate_77";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a single Index on two fields")
    public void testGenerateEntitiesSameIndexOnTwoFields() {
        String subDir = "tool_test_generate_78";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has an entity whose id field is renamed")
    public void testGenerateEntitiesWithRenamedIdField() {
        String subDir = "tool_test_generate_79";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a renamed Id field and a renamed foreign key")
    public void testGenerateEntitiesWithRenamedIdFieldAndForeignKey() {
        String subDir = "tool_test_generate_80";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key")
    public void testGenerateEntitiesCompositeForeignKey() {
        String subDir = "tool_test_generate_81";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with one key renamed")
    public void testGenerateEntitiesRenamedCompositeForeignKeyPartial() {
        String subDir = "tool_test_generate_82";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with both keys renamed")
    public void testGenerateEntitiesRenamedCompositeForeignKey() {
        String subDir = "tool_test_generate_83";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with both keys renamed along with part " +
            "of primary key")
    public void testGenerateEntitiesCompositeForeignKeyWithRenamedKeysPartial() {
        String subDir = "tool_test_generate_84";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with both keys renamed along with primary keys")
    public void testGenerateEntitiesCompositeForeignKeyWithRenamedKeys() {
        String subDir = "tool_test_generate_85";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has an Entity with auto generated index on a field")
    public void testGenerateEntitiesWithAutoGeneratedIndex() {
        String subDir = "tool_test_generate_86";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has an Entity with auto generated unique index on a field")
    public void testGenerateEntitiesWithAutoGeneratedUniqueIndex() {
        String subDir = "tool_test_generate_87";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Entities with both index types on same field")
    public void testGenerateEntitiesWithBothIndexTypesOnSameField() {
        String subDir = "tool_test_generate_88";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Entities with both index types on same field and one with a name")
    public void testGenerateEntitiesWithBothIndexTypesAndOneWithAName() {
        String subDir = "tool_test_generate_89";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Entity with auto generated ID field")
    public void testGenerateEntityWithAutoGeneratedId() {
        String subDir = "tool_test_generate_90";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Entity with auto generated ID field with a relation")
    public void testGenerateEntityWithAutoGeneratedIdWithRelation() {
        String subDir = "tool_test_generate_91";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has Entity with auto generated ID field with a renamed relation")
    public void testGenerateEntityWithAutoGeneratedIdWithRenamedRelation() {
        String subDir = "tool_test_generate_92";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has multiple relations with relation annotations")
    public void testGenerateEntitiesWithMultipleRelationsOnSame() {
        String subDir = "tool_test_generate_93";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has multiple relations with relation annotations and renamed foreign keys")
    public void testGenerateEntitiesWithMultipleRenamedRelationsOnSame() {
        String subDir = "tool_test_generate_94";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has all possible data types inlcuding optional fields")
    public void testRedisEntity() {
        updateOutputBallerinaToml("tool_test_generate_95_redis");
        executeGenerateCommand("tool_test_generate_95_redis",
                "--datastore", "redis", "--module", "entities", "--test-datastore", "inmemory");
        assertGeneratedSources("tool_test_generate_95_redis");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with redis data source")
    public void testRedisWithAssociatedEntity() {
        updateOutputBallerinaToml("tool_test_generate_96_redis");
        executeGenerateCommand("tool_test_generate_96_redis",
                "--datastore", "redis", "--module", "entities", "--test-datastore", "h2");
        assertGeneratedSources("tool_test_generate_96_redis");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with redis data source")
    public void testRedisWithCompositeKeys() {
        updateOutputBallerinaToml("tool_test_generate_97_redis");
        executeGenerateCommand("tool_test_generate_97_redis",
                "--datastore", "redis", "--module", "entities");
        assertGeneratedSources("tool_test_generate_97_redis");
    }

    @Test(enabled = true)
    @Description("There are multiple entities with multiple enums and imports with redis data source")
    public void testGenerateWithEnumsRedis() {
        updateOutputBallerinaToml("tool_test_generate_98_redis");
        executeGenerateCommand("tool_test_generate_98_redis",
                "--datastore", "redis", "--module", "entities");
        assertGeneratedSources("tool_test_generate_98_redis");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsRedis() {
        updateOutputBallerinaToml("tool_test_generate_99_redis");
        executeGenerateCommand("tool_test_generate_99_redis",
                "--datastore", "redis", "--module", "entities");
        assertGeneratedSources("tool_test_generate_99_redis");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeRedis() {
        updateOutputBallerinaToml("tool_test_generate_100_redis");
        executeGenerateCommand("tool_test_generate_100_redis",
                "--datastore", "redis", "--module", "entities");
        assertGeneratedSources("tool_test_generate_100_redis");
    }

    @Test(enabled = true)
    @Description("The model has entities with unsupported fields")
    public void testGenerateEntitiesWithUnsupportedFields() {
        String subDir = "tool_test_generate_101";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities", "--test-datastore", "h2");
        executeGenerateCommand(subDir,
                "--datastore", "mssql", "--module", "mssql_entities", "--test-datastore", "h2");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities", "--test-datastore", "h2");
        executeGenerateCommand(subDir,
                "--datastore", "postgresql", "--module", "postgresql_entities", "--test-datastore", "h2");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("There are entities with 'sql' annotations")
    public void testGenerateIgnoreSQLAnnotationsForInmemory() {
        updateOutputBallerinaToml("tool_test_generate_102_inmemory");
        executeGenerateCommand("tool_test_generate_102_inmemory",
                "--datastore", "inmemory", "--module", "entities");
        assertGeneratedSources("tool_test_generate_102_inmemory");
    }

    @Test(enabled = true)
    @Description("There are entities with 'sql' annotations")
    public void testGenerateIgnoreSQLAnnotationsForGoogleSheets() {
        updateOutputBallerinaToml("tool_test_generate_103_googlesheets");
        executeGenerateCommand("tool_test_generate_103_googlesheets",
                "--datastore", "googlesheets", "--module", "entities");
        assertGeneratedSources("tool_test_generate_103_googlesheets");
    }

    @Test(enabled = true)
    @Description("There are entities with 'sql' annotations")
    public void testGenerateIgnoreSQLAnnotationsForRedis() {
        updateOutputBallerinaToml("tool_test_generate_104_redis");
        executeGenerateCommand("tool_test_generate_104_redis",
                "--datastore", "redis", "--module", "entities");
        assertGeneratedSources("tool_test_generate_104_redis");
    }

    @Test(enabled = true)
    @Description("There are entities with 'sql' annotations")
    public void testGenerateWithAllUnsupportedEntities() {
        updateOutputBallerinaToml("tool_test_generate_105");
        executeGenerateCommand("tool_test_generate_105", "--datastore", "mysql", "--module", "entities");
        assertGeneratedSources("tool_test_generate_105");
    }

    @Test(enabled = true)
    @Description("The model has a one-to-one relation where the unique index name is managed")
    public void testGenerateEntitiesWithUniqueIndexAnnotatedForeignKeys() {
        String subDir = "tool_test_generate_106";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has multiple db schema with schema annotation in model file")
    public void testGenerateEntitiesWithMultipleDBSchemas() {
        String subDir = "tool_test_generate_113_schema";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("The model has multiple tables with different relations and test table creation order")
    public void testGenerateSQLScriptWithRelations() {
        String subDir = "tool_test_generate_114";
        updateOutputBallerinaToml(subDir);
        executeGenerateCommand(subDir, "--datastore", "mysql", "--module", "entities");
        executeGenerateCommand(subDir, "--datastore", "mssql", "--module", "mssql_entities");
        executeGenerateCommand(subDir, "--datastore", "h2", "--module", "h2_entities");
        executeGenerateCommand(subDir, "--datastore", "postgresql", "--module", "postgresql_entities");
        assertGeneratedSources(subDir);
    }

    private void updateOutputBallerinaToml(String fileName) {
        String tomlFileName = "Ballerina.toml";
        Path filePath = Paths.get("src", "test", "resources", "test-src", "output", fileName, tomlFileName);
        if (filePath.endsWith(tomlFileName)) {
            try {
                String content = Files.readString(filePath);
                String dataStore = "persist.inmemory";
                String version = persistInMemoryVersion;
                if (content.contains("artifactId = \"persist.sql-native\"")) {
                    dataStore = "persist.sql";
                    version = persistSqlVersion;
                } else if (content.contains("artifactId = \"persist.googlesheets-native\"")) {
                    dataStore = "persist.googlesheets";
                    version = persistGoogleSheetsVersion;
                } else if (content.contains("artifactId = \"persist.redis-native\"")) {
                    dataStore = "persist.redis";
                    version = persistRedisVersion;
                }
                content = content.replaceAll(
                        "artifactId\\s=\\s\"" + dataStore + "-native\"" + System.lineSeparator() +
                                "version\\s=\\s\\\"\\d+(\\.\\d+)+" +
                                "(-SNAPSHOT)?\\\"",  "artifactId = \"" + dataStore +
                                "-native\"" + System.lineSeparator() + "version = \"" + version + "\"");
                // Update inmemory test datastore dependency if exists
                // For H2 database, related persist.sql version is resolved earlier
                String testDatastore = "persist.inmemory";
                String testVersion = persistInMemoryVersion;
                content = content.replaceAll("artifactId\\s=\\s\"" + testDatastore + "-native\""
                        + System.lineSeparator() + "version\\s=\\s\\\"\\d+(\\.\\d+)+" +
                        "(-SNAPSHOT)?\\\"",  "artifactId = \"" + testDatastore +
                        "-native\"" + System.lineSeparator() + "version = \"" + testVersion + "\"");
                Files.writeString(filePath, content);
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
