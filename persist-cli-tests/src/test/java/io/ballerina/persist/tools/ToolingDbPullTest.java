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
    @Description("Create a basic model.bal file with one Entity with no annotations and not null fields.")
    public void test1() throws BalException {
        createFromDatabaseScript("tool_test_pull_1_mysql", "mysql");
        executeCommand("tool_test_pull_1_mysql", PULL);
        assertGeneratedSources("tool_test_pull_1_mysql");
    }
}

