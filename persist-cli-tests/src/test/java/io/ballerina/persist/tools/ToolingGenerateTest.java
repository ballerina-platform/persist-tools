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

    @Test(enabled = true)
    @Description("There is multiple entities with associations in the Ballerina project")
    public void testGenerateAssociatedEntities() {
        executeGenerateCommand("tool_test_generate_1", "mysql", "persist_generate_1");
        assertGeneratedSources("tool_test_generate_1");
    }

    @Test(enabled = true)
    @Description("There are multiple entities in the Ballerina project")
    public void testGenerateMultipleEntities() {
        executeGenerateCommand("tool_test_generate_2", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_2");
    }

    @Test(enabled = true)
    @Description("There are no entities nor already generated client objects in the Ballerina project")
    public void testGenerateWithoutEntitiesWithoutClients() {
        executeGenerateCommand("tool_test_generate_3", "mysql", "entities");
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
        executeGenerateCommand("tool_test_generate_5", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_5");
    }

    @Test(enabled = true)
    @Description("A persist entity with all the supported fields data types")
    public void testGenerateAllEntityFieldTypes() {
        executeGenerateCommand("tool_test_generate_6", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_6");
    }

    @Test(enabled = true)
    @Description("Use case where unsupported datatypes are used")
    public void testGenerateClientWithUnsupportedDataTypes() {
        assertGeneratedSourcesNegative("tool_test_generate_7", GENERATE, new String[]{}, "mysql",
                "entities");
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project where key is a string")
    public void testGenerateSingleEntityWithStringKey() {
        executeGenerateCommand("tool_test_generate_8", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_8");
    }

    @Test(enabled = true) //removed until support for multiple keys are provided
    @Description("There is only a single entity in the Ballerina project with two keys")
    public void testGenerateSingleEntityWithMultipleKeysAndAutoInc() {
        executeGenerateCommand("tool_test_generate_9", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_9");
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the Ballerina project and there are errors in the project")
    public void testGenerateSingleEntityWithErrors() {
        assertGeneratedSourcesNegative("tool_test_generate_11", GENERATE, new String[]{}, "mysql",
                "entities");
    }

    @Test(enabled = true)
    @Description("There is only a single entity in the schema with wrong import")
    public void testGenerateSingleEntityWithWrongImport() {
        assertGeneratedSourcesNegative("tool_test_generate_12", GENERATE, new String[]{}, "mysql",
                "entities");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other")
    public void testGenerateThreeEntitiesWith1To1Associations() {
        executeGenerateCommand("tool_test_generate_13", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_13");
    }

    @Test(enabled = true)
    @Description("There are three entities in two schema files")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithChildEntityInSubModule() {
        assertGeneratedSourcesNegative("tool_test_generate_15", GENERATE, new String[]{}, "mysql",
                "entities");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other")
    public void testGenerateClientsWith1ToManyAssociations() {
        executeGenerateCommand("tool_test_generate_16", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_16");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other")
    public void testGenerateThreeClientsWith1ToManyAssociations() {
        executeGenerateCommand("tool_test_generate_17", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_17");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other without nullable fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithOutAnnotationValue() {
        assertGeneratedSourcesNegative("tool_test_generate_19", GENERATE, new String[]{}, "mysql",
                "entities");
    }

    @Test(enabled = true)
    @Description("There are two special entities with special characters in field names")
    public void testGenerateRelatedClientsWithSpecialCharactersInName() {
        executeGenerateCommand("tool_test_generate_22", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_22");
    }

    @Test(enabled = true)
    @Description("Negative test case where init command was not run before generate command")
    public void testGenerateWithoutInit() {
        assertGeneratedSourcesNegative("tool_test_generate_23", GENERATE, new String[]{}, "mysql",
                "entities");
    }

    @Test(enabled = true)
    @Description("Test the generate command with entities containing byte[] fields")
    public void testGenerateWithByteArrays() {
        executeGenerateCommand("tool_test_generate_24", "mysql", "foo");
        assertGeneratedSources("tool_test_generate_24");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to many associations between each other without relation annotation")
    public void testGenerateOneToManyAssociationsWithoutRelationAnnotation() {
        executeGenerateCommand("tool_test_generate_25", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_25");
    }

    @Test(enabled = true)
    @Description("There are two entities with one to one associations between each other without relation annotation")
    public void testGenerateOneToOneAssociationsWithoutRelationAnnotation() {
        executeGenerateCommand("tool_test_generate_26", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_26");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to one associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1To1AssociationsWithComments() {
        executeGenerateCommand("tool_test_generate_27", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_27");
    }

    @Test(enabled = true)
    @Description("There are three entities with one to many associations between each other with comments " +
            "in entity fields")
    public void testGenerateThreeEntitiesWith1ToManyAssociationsWithComments() {
        executeGenerateCommand("tool_test_generate_28", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_28");
    }

    @Test(enabled = true)
    @Description("There is a entity inside the project with comments inside entity")
    public void testGenerateWithComments() {
        executeGenerateCommand("tool_test_generate_29", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_29");
    }

    @Test(enabled = true)
    @Description("Test the generate command with out defining any schema files inside persist directory")
    public void testGenerateWithoutSchemaFile() {
        executeGenerateCommand("tool_test_generate_30", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_30");
    }

    @Test(enabled = true)
    @Description("Test the generate command with empty schema file inside persist directory")
    public void testGenerateWithEmptySchemaFile() {
        executeGenerateCommand("tool_test_generate_31", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_31");
    }

    @Test(enabled = true)
    @Description("Test the generate command without persist import in schema file")
    public void testGenerateWithoutPersistImport() {
        executeGenerateCommand("tool_test_generate_33", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_33");
    }

    @Test(enabled = true)
    @Description("Test the generate command with optional type in schema file")
    public void testGenerateWithOptionalType() {
        executeGenerateCommand("tool_test_generate_34", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_34");
    }

    @Test(enabled = true)
    @Description("Test the generate command in default Ballerina package")
    public void testGenerateInDefaultPackage() {
        executeGenerateCommand("tool_test_generate_35", "mysql");
        assertGeneratedSources("tool_test_generate_35");
    }

    @Test(enabled = true)
    @Description("Test the generate command with escape character in entity fields, and names")
    public void testGenerateRelationsWithSingleQuote() {
        executeGenerateCommand("tool_test_generate_36", "mysql", "rainier");
        assertGeneratedSources("tool_test_generate_36");
    }

    @Test(enabled = true)
    @Description("Test the created sql script content for relations and byte[] type")
    public void testSqlGen() {
        executeGenerateCommand("tool_test_generate_37", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_37");
    }

    @Test(enabled = true)
    @Description("Test the created sql script content with out defining any schema files inside persist directory")
    public void testSqlGenWithoutSchemaFile() {
        executeGenerateCommand("tool_test_generate_38", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_38");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with one to many relation entity")
    public void testSqlGenWithOneToManyRelationship() {
        executeGenerateCommand("tool_test_generate_39", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_39");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with optional type fields")
    public void testSqlGenWithOptionalTypeFields() {
        executeGenerateCommand("tool_test_generate_40", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_40");
    }

    @Test(enabled = true)
    @Description("Test the created sql script with composite reference keys")
    public void testSqlGenWithCompositeReferenceKeys() {
        executeGenerateCommand("tool_test_generate_41", "mysql", "entities");
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
        executeGenerateCommand("tool_test_generate_42", "my", "entities");
        assertGeneratedSources("tool_test_generate_42");
    }

    @Test(enabled = true)
    public void testInvalidModuleName() {
        String invalidModuleName = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt" +
                "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt" +
                "esttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestt" +
                "esttesttesttesttesttesttesttest";
        executeGenerateCommand("tool_test_generate_43", "mysql", invalidModuleName);
        assertGeneratedSources("tool_test_generate_43");
    }

    @Test(enabled = true)
    public void testInvalidModuleName1() {
        executeGenerateCommand("tool_test_generate_44", "mysql", ".<test");
        assertGeneratedSources("tool_test_generate_44");
    }

    @Test(enabled = true)
    public void testInvalidModuleName2() {
        executeGenerateCommand("tool_test_generate_45", "mysql", "^^^");
        assertGeneratedSources("tool_test_generate_45");
    }

    @Test(enabled = true)
    @Description("Test the generated types for multiple association between same entities")
    public void testGenerateMultipleAssociationBetweenSameEntities() {
        executeGenerateCommand("tool_test_generate_46", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_46");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryEntity() {
        executeGenerateCommand("tool_test_generate_47_in_memory", "inmemory", "entities");
        assertGeneratedSources("tool_test_generate_47_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryWithAssociatedEntity() {
        executeGenerateCommand("tool_test_generate_48_in_memory", "inmemory", "entities");
        assertGeneratedSources("tool_test_generate_48_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with in memory data source")
    public void testInMemoryWithCompositeKeys() {
        executeGenerateCommand("tool_test_generate_49_in_memory", "inmemory", "entities");
        assertGeneratedSources("tool_test_generate_49_in_memory");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and no imports")
    public void testGenerateWithEnums() {
        executeGenerateCommand("tool_test_generate_50", "mysql");
        assertGeneratedSources("tool_test_generate_50");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports")
    public void testGenerateWithEnumsWithImports() {
        executeGenerateCommand("tool_test_generate_51", "mysql");
        assertGeneratedSources("tool_test_generate_51");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with in memory data source")
    public void testGenerateWithEnumsInMemory() {
        executeGenerateCommand("tool_test_generate_52_in_memory", "inmemory", "entities");
        assertGeneratedSources("tool_test_generate_52_in_memory");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsInMemory() {
        executeGenerateCommand("tool_test_generate_53_in_memory", "inmemory", "entities");
        assertGeneratedSources("tool_test_generate_53_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with google sheets data source")
    public void testGoogleSheet() {
        executeGenerateCommand("tool_test_generate_54_gsheet", "googlesheets", "entities");
        assertGeneratedSources("tool_test_generate_54_gsheet");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeInMemory() {
        executeGenerateCommand("tool_test_generate_55_in_memory", "inmemory", "entities");
        assertGeneratedSources("tool_test_generate_55_in_memory");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeGoogleSheet() {
        executeGenerateCommand("tool_test_generate_56_gsheets", "googlesheets", "entities");
        assertGeneratedSources("tool_test_generate_56_gsheets");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with google sheets data source and ENUM as field type.")
    public void testGoogleSheetWithEnum() {
        executeGenerateCommand("tool_test_generate_57_gsheets", "googlesheets", "entities");
        assertGeneratedSources("tool_test_generate_57_gsheets");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with mssql data source")
    public void testMssqlEntity() {
        executeGenerateCommand("tool_test_generate_58_mssql", "mssql", "entities");
        assertGeneratedSources("tool_test_generate_58_mssql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with postgresql data source")
    public void testPostgresqlEntity() {
        executeGenerateCommand("tool_test_generate_66_postgresql", "postgresql", "entities");
        assertGeneratedSources("tool_test_generate_66_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with mssql data source")
    public void testMSSQLWithAssociatedEntity() {
        executeGenerateCommand("tool_test_generate_59_mssql", "mssql", "entities");
        assertGeneratedSources("tool_test_generate_59_mssql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with postgresql data source")
    public void testPostgreSQLWithAssociatedEntity() {
        executeGenerateCommand("tool_test_generate_67_postgresql", "postgresql", "entities");
        assertGeneratedSources("tool_test_generate_67_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with mssql data source")
    public void testMSSQLWithCompositeKeys() {
        executeGenerateCommand("tool_test_generate_60_mssql", "mssql", "entities");
        assertGeneratedSources("tool_test_generate_60_mssql");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with postgresql data source")
    public void testPostgreSQLWithCompositeKeys() {
        executeGenerateCommand("tool_test_generate_68_postgresql", "postgresql", "entities");
        assertGeneratedSources("tool_test_generate_68_postgresql");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with mssql data source")
    public void testGenerateWithEnumsMSSQL() {
        executeGenerateCommand("tool_test_generate_61_mssql", "mssql", "entities");
        assertGeneratedSources("tool_test_generate_61_mssql");
    }

    @Test(enabled = true)
    @Description("There is multiple entities with multiple enums and imports with postgresql data source")
    public void testGenerateWithEnumsPostgresSQL() {
        executeGenerateCommand("tool_test_generate_69_postgresql", "postgresql", "entities");
        assertGeneratedSources("tool_test_generate_69_postgresql");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsMSSQL() {
        executeGenerateCommand("tool_test_generate_62_mssql", "mssql", "entities");
        assertGeneratedSources("tool_test_generate_62_mssql");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsPostgresql() {
        executeGenerateCommand("tool_test_generate_70_postgresql", "postgresql", "entities");
        assertGeneratedSources("tool_test_generate_70_postgresql");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeMSSQL() {
        executeGenerateCommand("tool_test_generate_63_mssql", "mssql", "entities");
        assertGeneratedSources("tool_test_generate_63_mssql");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypePostgreSQL() {
        executeGenerateCommand("tool_test_generate_71_postgresql", "postgresql", "entities");
        assertGeneratedSources("tool_test_generate_71_postgresql");
    }

    @Test(enabled = true)
    @Description("module name is in the shape x.y.z")
    public void testGenerateEntityDotSeperatedModuleNames() {
        executeGenerateCommand("tool_test_generate_64", "mysql", "x.y.z");
        assertGeneratedSources("tool_test_generate_64");
    }

    @Test(enabled = true)
    @Description("The model has multiple relations of various types")
    public void testGenerateEntitiesWithMultipleRelations() {
        executeGenerateCommand("tool_test_generate_65", "mysql");
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
        executeGenerateCommand("tool_test_generate_72", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_72");
    }

    @Test(enabled = true)
    @Description("The model has Mapping annotations on entities")
    public void testGenerateEntitiesWithMappingAnnotations() {
        executeGenerateCommand("tool_test_generate_73", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_73");
    }

    @Test(enabled = true)
    @Description("The model has Mapping annotations on foreign keys.")
    public void testGenerateEntitiesWithMappingAnnotationOnForeignKeys() {
        executeGenerateCommand("tool_test_generate_74", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_74");
    }

    @Test(enabled = true)
    @Description("The model has type mapping annotations Char, VarChar and Decimal")
    public void testGenerateEntitiesWithTypeMappingAnnotations() {
        executeGenerateCommand("tool_test_generate_75", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_75");
    }

    @Test(enabled = true)
    @Description("The model has a unique index on a field")
    public void testGenerateEntitiesWithUniqueIndexesOnOneField() {
        executeGenerateCommand("tool_test_generate_76", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_76");
    }

    @Test(enabled = true)
    @Description("The model has a single unique index on two fields")
    public void testGenerateEntitiesSameUniqueIndexOnTwoFields() {
        executeGenerateCommand("tool_test_generate_77", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_77");
    }

    @Test(enabled = true)
    @Description("The model has a single Index on two fields")
    public void testGenerateEntitiesSameIndexOnTwoFields() {
        executeGenerateCommand("tool_test_generate_78", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_78");
    }

    @Test(enabled = true)
    @Description("The model has an entity whose id field is renamed")
    public void testGenerateEntitiesWithRenamedIdField() {
        executeGenerateCommand("tool_test_generate_79", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_79");
    }

    @Test(enabled = true)
    @Description("The model has a renamed Id field and a renamed foreign key")
    public void testGenerateEntitiesWithRenamedIdFieldAndForeignKey() {
        executeGenerateCommand("tool_test_generate_80", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_80");
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key")
    public void testGenerateEntitiesCompositeForeignKey() {
        executeGenerateCommand("tool_test_generate_81", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_81");
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with one key renamed")
    public void testGenerateEntitiesRenamedCompositeForeignKeyPartial() {
        executeGenerateCommand("tool_test_generate_82", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_82");
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with both keys renamed")
    public void testGenerateEntitiesRenamedCompositeForeignKey() {
        executeGenerateCommand("tool_test_generate_83", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_83");
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with both keys renamed along with part " +
            "of primary key")
    public void testGenerateEntitiesCompositeForeignKeyWithRenamedKeysPartial() {
        executeGenerateCommand("tool_test_generate_84", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_84");
    }

    @Test(enabled = true)
    @Description("The model has a relation with a composite foreign key with both keys renamed along with primary keys")
    public void testGenerateEntitiesCompositeForeignKeyWithRenamedKeys() {
        executeGenerateCommand("tool_test_generate_85", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_85");
    }

    @Test(enabled = true)
    @Description("The model has an Entity with auto generated index on a field")
    public void testGenerateEntitiesWithAutoGeneratedIndex() {
        executeGenerateCommand("tool_test_generate_86", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_86");
    }

    @Test(enabled = true)
    @Description("The model has an Entity with auto generated index on a field")
    public void testGenerateEntitiesWithAutoGeneratedUniqueIndex() {
        executeGenerateCommand("tool_test_generate_87", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_87");
    }

    @Test(enabled = true)
    @Description("The model has Entities with both index types on same field")
    public void testGenerateEntitiesWithBothIndexTypesOnSameField() {
        executeGenerateCommand("tool_test_generate_88", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_88");
    }

    @Test(enabled = true)
    @Description("The model has Entities with both index types on same field and one with a name")
    public void testGenerateEntitiesWithBothIndexTypesAndOneWithAName() {
        executeGenerateCommand("tool_test_generate_89", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_89");
    }

    @Test(enabled = true)
    @Description("The model has Entity with auto generated ID field")
    public void testGenerateEntityWithAutoGeneratedId() {
        executeGenerateCommand("tool_test_generate_90", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_90");
    }

    @Test(enabled = true)
    @Description("The model has Entity with auto generated ID field with a relation")
    public void testGenerateEntityWithAutoGeneratedIdWithRelation() {
        executeGenerateCommand("tool_test_generate_91", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_91");
    }

    @Test(enabled = true)
    @Description("The model has Entity with auto generated ID field with a renamed relation")
    public void testGenerateEntityWithAutoGeneratedIdWithRenamedRelation() {
        executeGenerateCommand("tool_test_generate_92", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_92");
    }

    @Test(enabled = true)
    @Description("The model has multiple relations with relation annotations")
    public void testGenerateEntitiesWithMultipleRelationsOnSame() {
        executeGenerateCommand("tool_test_generate_93", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_93");
    }

    @Test(enabled = true)
    @Description("The model has multiple relations with relation annotations and renamed foreign keys")
    public void testGenerateEntitiesWithMultipleRenamedRelationsOnSame() {
        executeGenerateCommand("tool_test_generate_94", "mysql", "entities");
        assertGeneratedSources("tool_test_generate_94");
    }

    @Test(enabled = true)
    @Description("The model has all possible data types inlcuding optional fields")
    public void testRedisEntity() {
        executeGenerateCommand("tool_test_generate_95_redis", "redis", "entities");
        assertGeneratedSources("tool_test_generate_95_redis");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with redis data source")
    public void testRedisWithAssociatedEntity() {
        executeGenerateCommand("tool_test_generate_96_redis", "redis", "entities");
        assertGeneratedSources("tool_test_generate_96_redis");
    }

    @Test(enabled = true)
    @Description("There is a generated client object with redis data source")
    public void testRedisWithCompositeKeys() {
        executeGenerateCommand("tool_test_generate_97_redis", "redis", "entities");
        assertGeneratedSources("tool_test_generate_97_redis");
    }

    @Test(enabled = true)
    @Description("There are multiple entities with multiple enums and imports with redis data source")
    public void testGenerateWithEnumsRedis() {
        executeGenerateCommand("tool_test_generate_98_redis", "redis", "entities");
        assertGeneratedSources("tool_test_generate_98_redis");
    }

    @Test(enabled = true)
    @Description("There is an entity which is associated with multiple relations")
    public void testGenerateWithSameEntityMultipleRelationsRedis() {
        executeGenerateCommand("tool_test_generate_99_redis", "redis", "entities");
        assertGeneratedSources("tool_test_generate_99_redis");
    }

    @Test(enabled = true)
    @Description("There is a model with an entity consisting of multiple relations of the same type")
    public void testGenerateEntityWithMultipleRelationsSameTypeRedis() {
        executeGenerateCommand("tool_test_generate_100_redis", "redis", "entities");
        assertGeneratedSources("tool_test_generate_100_redis");
    }

}
