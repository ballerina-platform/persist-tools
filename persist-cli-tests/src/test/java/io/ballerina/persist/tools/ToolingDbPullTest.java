package io.ballerina.persist.tools;


import io.ballerina.persist.BalException;
import jdk.jfr.Description;
import org.testng.annotations.Test;


import static io.ballerina.persist.tools.utils.DatabaseTestUtils.createFromDatabaseScript;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.PULL;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.executeCommand;

public class ToolingDbPullTest {
    @Test(enabled = true)
    @Description("Create a model.bal file with one Entity with no annotations and not null fields.")
    public void pullTestMysql1() throws BalException {
        String subDir = "tool_test_pull_1_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file with one Entity with no annotations and one nullable field.")
    public void pullTestMysql2() throws BalException {
        String subDir = "tool_test_pull_2_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file with two Entities with a one to many relation.")
    public void pullTestMysql3() throws BalException {
        String subDir = "tool_test_pull_3_mysql";
        createFromDatabaseScript(subDir, "mysql");
        executeCommand(subDir, PULL);
        assertGeneratedSources(subDir);
    }
}

