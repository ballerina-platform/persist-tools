package io.ballerina.persist.tools;


import io.ballerina.persist.BalException;
import jdk.jfr.Description;
import org.testng.annotations.Test;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.ballerina.persist.tools.utils.DatabaseTestUtils.createFromDatabaseScript;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.PULL;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.executeCommand;

public class ToolingDbPullTest {
    @Test(enabled = true)
    @Description("Create a model.bal file consisting of one Entity with no annotations and not null fields.")
    public void pullTestMysqlBasic() throws BalException {
        String subDir = "tool_test_pull_1_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of one Entity with no annotations and one nullable field.")
    public void pullTestMysqlNullableField() throws BalException {
        String subDir = "tool_test_pull_2_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to many relation.")
    public void pullTestMysqlOneToMany() throws BalException {
        String subDir = "tool_test_pull_3_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Introspection when a model.bal file already exists and continue.")
    public void pullTestMysqlModelFileExistsContinue() throws BalException {
        String subDir = "tool_test_pull_4_mysql";
        createFromDatabaseScript(subDir, "mysql");
        String simulatedInput = "y\n";
        InputStream originalSystemIn = System.in;
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(inputStream);
        executeCommand(subDir, PULL);
        System.setIn(originalSystemIn);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Introspection when a model.bal file already exists and abort.")
    public void pullTestMysqlModelFileExistsAbort() throws BalException {
        String subDir = "tool_test_pull_5_mysql";
        createFromDatabaseScript(subDir, "mysql");
        String simulatedInput = "n\n";
        InputStream originalSystemIn = System.in;
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(inputStream);
        executeCommand(subDir, PULL);
        System.setIn(originalSystemIn);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to one relation " +
            "by making foreign key unique.")
    public void pullTestMysqlOneToOneUniqueKey() throws BalException {
        String subDir = "tool_test_pull_6_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to one relation by " +
            "making foreign key primary.")
    public void pullTestMysqlOneToOnePrimaryKey() throws BalException {
        String subDir = "tool_test_pull_7_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to many relation by " +
            "making foreign key a partial key.")
    public void pullTestMysqlPartialPrimaryKey() throws BalException {
        String subDir = "tool_test_pull_8_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file three Entities with two one to many relations.")
    public void pullTestMysqlMultipleRelations() throws BalException {
        String subDir = "tool_test_pull_9_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of name mapping annotations.")
    public void pullTestMysqlNameMappingAnnotation() throws BalException {
        String subDir = "tool_test_pull_10_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of type mapping annotations Char, VarChar and Decimal.")
    public void pullTestMysqlTypeMappingAnnotation() throws BalException {
        String subDir = "tool_test_pull_11_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of unique index annotation.")
    public void pullTestMysqlUniqueIndexAnnotation() throws BalException {
        String subDir = "tool_test_pull_12_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of index annotation.")
    public void pullTestMysqlIndexAnnotation() throws BalException {
        String subDir = "tool_test_pull_13_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple relations between same entities.")
    public void pullTestMysqlMultipleRelationsBetweenSameEntities() throws BalException {
        String subDir = "tool_test_pull_14_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple unique indexes on same column.")
    public void pullTestMysqlMultipleUniqueIndexes() throws BalException {
        String subDir = "tool_test_pull_15_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple indexes on same column.")
    public void pullTestMysqlMultipleIndexes() throws BalException {
        String subDir = "tool_test_pull_16_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of a one to many relation with composite foreign key.")
    public void pullTestCompositeForeignKeys() throws BalException {
        String subDir = "tool_test_pull_17_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of an entity with self-referenced relation.")
    public void pullTestSelfReferencedRelation() throws BalException {
        String subDir = "tool_test_pull_18_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two relations which cross-reference each other.")
    public void pullTestCrossReferencedRelations() throws BalException {
        String subDir = "tool_test_pull_19_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }
}

