package io.ballerina.persist.tools;


import io.ballerina.persist.BalException;
import io.ballerina.persist.cmd.Pull;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import picocli.CommandLine;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.DatabaseTestUtils.createFromDatabaseScript;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;

public class ToolingDbPullTest {
    private static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources")
            .toString();

    private static DatabaseConfiguration databaseConfig;

    static {
        try {
            databaseConfig = new DatabaseConfiguration("localhost", "root",
                    "Test123#", "3307", "persist");
        } catch (BalException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(enabled = true)
    @Description("Test the command line args for pull command.")
    public void pullTestArgs() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        String subDir = "tool_test_pull_23";
        Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");

        // datastore is empty
        Pull persistCmd1 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd1).parseArgs("--datastore", "");
        new CommandLine(persistCmd1).parseArgs("--host", "localhost");
        new CommandLine(persistCmd1).parseArgs("--port", "3307");
        new CommandLine(persistCmd1).parseArgs("--user", "root");
        new CommandLine(persistCmd1).parseArgs("--database", "persist");
        persistCmd1.execute();
        assertGeneratedSources(subDir);

        // host is empty
        Pull persistCmd2 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd2).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd2).parseArgs("--host", "");
        new CommandLine(persistCmd2).parseArgs("--port", "3307");
        new CommandLine(persistCmd2).parseArgs("--user", "root");
        new CommandLine(persistCmd2).parseArgs("--database", "persist");
        persistCmd2.execute();
        assertGeneratedSources(subDir);

        // port is empty
        Pull persistCmd3 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd3).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd3).parseArgs("--host", "localhost");
        new CommandLine(persistCmd3).parseArgs("--port", "");
        new CommandLine(persistCmd3).parseArgs("--user", "root");
        new CommandLine(persistCmd3).parseArgs("--database", "persist");
        persistCmd3.execute();
        assertGeneratedSources(subDir);

        // user is empty
        Pull persistCmd4 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd4).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd4).parseArgs("--host", "localhost");
        new CommandLine(persistCmd4).parseArgs("--port", "3307");
        new CommandLine(persistCmd4).parseArgs("--user", "");
        new CommandLine(persistCmd4).parseArgs("--database", "persist");
        persistCmd4.execute();
        assertGeneratedSources(subDir);

        // database is empty
        Pull persistCmd5 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd5).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd5).parseArgs("--host", "localhost");
        new CommandLine(persistCmd5).parseArgs("--port", "3307");
        new CommandLine(persistCmd5).parseArgs("--user", "root");
        new CommandLine(persistCmd5).parseArgs("--database", "");
        persistCmd5.execute();
        assertGeneratedSources(subDir);

        // host is not provided
        Pull persistCmd6 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd6).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd6).parseArgs("--port", "3307");
        new CommandLine(persistCmd6).parseArgs("--user", "root");
        new CommandLine(persistCmd6).parseArgs("--database", "persist");
        persistCmd6.execute();
        assertGeneratedSources(subDir);

        // user is not provided
        Pull persistCmd7 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd7).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd7).parseArgs("--host", "localhost");
        new CommandLine(persistCmd7).parseArgs("--port", "3307");
        new CommandLine(persistCmd7).parseArgs("--database", "persist");
        persistCmd7.execute();
        assertGeneratedSources(subDir);

        // database is not provided
        Pull persistCmd8 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd8).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd8).parseArgs("--host", "localhost");
        new CommandLine(persistCmd8).parseArgs("--port", "3307");
        new CommandLine(persistCmd8).parseArgs("--user", "root");
        persistCmd8.execute();
        assertGeneratedSources(subDir);

        // invalid port number (not a number)
        Pull persistCmd9 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd9).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd9).parseArgs("--host", "localhost");
        new CommandLine(persistCmd9).parseArgs("--port", "lkj");
        new CommandLine(persistCmd9).parseArgs("--user", "root");
        new CommandLine(persistCmd9).parseArgs("--database", "persist");
        persistCmd9.execute();
        assertGeneratedSources(subDir);

        // invalid port number (number, but invalid)
        Pull persistCmd10 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd10).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd10).parseArgs("--host", "localhost");
        new CommandLine(persistCmd10).parseArgs("--port", "69000");
        new CommandLine(persistCmd10).parseArgs("--user", "root");
        new CommandLine(persistCmd10).parseArgs("--database", "persist");
        persistCmd10.execute();
        assertGeneratedSources(subDir);

        // invalid database name
        Pull persistCmd11 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        new CommandLine(persistCmd11).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd11).parseArgs("--host", "localhost");
        new CommandLine(persistCmd11).parseArgs("--port", "3307");
        new CommandLine(persistCmd11).parseArgs("--user", "root");
        new CommandLine(persistCmd11).parseArgs("--database", "8persist");
        persistCmd11.execute();
        assertGeneratedSources(subDir);

        // no args given
        Pull persistCmd12 = (Pull) persistClass.getDeclaredConstructor(String.class).
                newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                        toString());
        persistCmd12.execute();
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of one Entity with no annotations and not null fields.")
    public void pullTestMysqlBasic() throws BalException {
        String subDir = "tool_test_pull_1_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of one Entity with no annotations and one nullable field.")
    public void pullTestMysqlNullableField() throws BalException {
        String subDir = "tool_test_pull_2_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to many relation.")
    public void pullTestMysqlOneToMany() throws BalException {
        String subDir = "tool_test_pull_3_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Introspection when a model.bal file already exists and continue.")
    public void pullTestMysqlModelFileExistsContinue() throws BalException {
        String subDir = "tool_test_pull_4_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir, "y\n");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Introspection when a model.bal file already exists and abort.")
    public void pullTestMysqlModelFileExistsAbort() throws BalException {
        String subDir = "tool_test_pull_5_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir, "n\n");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to one relation " +
            "by making foreign key unique.")
    public void pullTestMysqlOneToOneUniqueKey() throws BalException {
        String subDir = "tool_test_pull_6_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to one relation by " +
            "making foreign key primary.")
    public void pullTestMysqlOneToOnePrimaryKey() throws BalException {
        String subDir = "tool_test_pull_7_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to many relation by " +
            "making foreign key a partial key.")
    public void pullTestMysqlPartialPrimaryKey() throws BalException {
        String subDir = "tool_test_pull_8_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file three Entities with two one to many relations.")
    public void pullTestMysqlMultipleRelations() throws BalException {
        String subDir = "tool_test_pull_9_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of name mapping annotations.")
    public void pullTestMysqlNameMappingAnnotation() throws BalException {
        String subDir = "tool_test_pull_10_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of type mapping annotations Char, VarChar and Decimal.")
    public void pullTestMysqlTypeMappingAnnotation() throws BalException {
        String subDir = "tool_test_pull_11_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of unique index annotation.")
    public void pullTestMysqlUniqueIndexAnnotation() throws BalException {
        String subDir = "tool_test_pull_12_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of index annotation.")
    public void pullTestMysqlIndexAnnotation() throws BalException {
        String subDir = "tool_test_pull_13_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple relations between same entities.")
    public void pullTestMysqlMultipleRelationsBetweenSameEntities() throws BalException {
        String subDir = "tool_test_pull_14_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple unique indexes on same column.")
    public void pullTestMysqlMultipleUniqueIndexes() throws BalException {
        String subDir = "tool_test_pull_15_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple indexes on same column.")
    public void pullTestMysqlMultipleIndexes() throws BalException {
        String subDir = "tool_test_pull_16_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of a one to many relation with composite foreign key.")
    public void pullTestCompositeForeignKeys() throws BalException {
        String subDir = "tool_test_pull_17_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of an entity with self-referenced relation.")
    public void pullTestSelfReferencedRelation() throws BalException {
        String subDir = "tool_test_pull_18_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two relations which cross-reference each other.")
    public void pullTestCrossReferencedRelations() throws BalException {
        String subDir = "tool_test_pull_19_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting keyword field names which are to be escaped.")
    public void pullTestEscapedFieldNames() throws BalException {
        String subDir = "tool_test_pull_20_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("When the database does not contain any tables.")
    public void pullTestNoTablesInDatabase() throws BalException {
        String subDir = "tool_test_pull_21_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("When the database does not exist.")
    public void pullTestDatabaseDoesNotExist() throws BalException {
        String subDir = "tool_test_pull_22_mysql";
        createFromDatabaseScript(subDir, "mysql", databaseConfig);
        executeDefaultPullCommand(subDir);
        assertGeneratedSources(subDir);
    }

    private static void executeDefaultPullCommand(String subDir) throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class).
                    newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                            toString());
            new CommandLine(persistCmd).parseArgs("--datastore", "mysql");
            new CommandLine(persistCmd).parseArgs("--host", "localhost");
            new CommandLine(persistCmd).parseArgs("--port", "3307");
            new CommandLine(persistCmd).parseArgs("--user", "root");
            new CommandLine(persistCmd).parseArgs("--database", "persist");
            String password = "Test123#\n";
            InputStream originalSystemIn = System.in;
            InputStream inputStream = new ByteArrayInputStream(password.getBytes(StandardCharsets.UTF_8));
            System.setIn(inputStream);
            persistCmd.execute();
            System.setIn(originalSystemIn);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command: " + e.getMessage());
        }
    }

    private static void executeDefaultPullCommand(String subDir, String simulatedInput) throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class).
                    newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().
                            toString());
            new CommandLine(persistCmd).parseArgs("--datastore", "mysql");
            new CommandLine(persistCmd).parseArgs("--host", databaseConfig.getHost());
            new CommandLine(persistCmd).parseArgs("--port", String.valueOf(databaseConfig.getPort()));
            new CommandLine(persistCmd).parseArgs("--user", databaseConfig.getUsername());
            new CommandLine(persistCmd).parseArgs("--database", databaseConfig.getDatabase());
            InputStream originalSystemIn = System.in;
            String passwordAndSimulatedInput = databaseConfig.getPassword() + "\n" + simulatedInput;
            InputStream inputStream = new ByteArrayInputStream
                    (passwordAndSimulatedInput.getBytes(StandardCharsets.UTF_8));
            System.setIn(inputStream);
            persistCmd.execute();
            System.setIn(originalSystemIn);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command: " + e.getMessage());
        }
    }
}

