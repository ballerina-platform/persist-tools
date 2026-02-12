/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.tools;

import io.ballerina.persist.BalException;
import io.ballerina.persist.cmd.Pull;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils;
import jdk.jfr.Description;
import org.junit.jupiter.api.condition.OS;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.DatabaseTestUtils.createFromDatabaseScript;
import static io.ballerina.persist.tools.utils.DatabaseTestUtils.resetMsSqlDatabase;
import static io.ballerina.persist.tools.utils.DatabaseTestUtils.resetPostgreSqlDatabase;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

public class ToolingDbPullTest {

    private static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources").toString();
    private static final DatabaseConfiguration mysqlDbConfig;
    private static final DatabaseConfiguration postgresDbConfig;
    private static final DatabaseConfiguration mssqlDbConfig;

    static {
        try {
            mysqlDbConfig = new DatabaseConfiguration("localhost", "root", "Test123#", "3307", "persist");
            postgresDbConfig = new DatabaseConfiguration("localhost", "postgres", "postgres", "5432", "persist");
            mssqlDbConfig = new DatabaseConfiguration("localhost", "sa", "Test123#", "1434", "persist");
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
        Path path = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        Pull persistCmd1 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd1).parseArgs("--datastore", "");
        new CommandLine(persistCmd1).parseArgs("--host", "localhost");
        new CommandLine(persistCmd1).parseArgs("--port", "3307");
        new CommandLine(persistCmd1).parseArgs("--user", "root");
        new CommandLine(persistCmd1).parseArgs("--database", "persist");
        persistCmd1.execute();
        assertGeneratedSources(subDir);

        // host is empty
        Pull persistCmd2 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd2).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd2).parseArgs("--host", "");
        new CommandLine(persistCmd2).parseArgs("--port", "3307");
        new CommandLine(persistCmd2).parseArgs("--user", "root");
        new CommandLine(persistCmd2).parseArgs("--database", "persist");
        persistCmd2.execute();
        assertGeneratedSources(subDir);

        // port is empty
        Pull persistCmd3 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd3).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd3).parseArgs("--host", "localhost");
        new CommandLine(persistCmd3).parseArgs("--port", "");
        new CommandLine(persistCmd3).parseArgs("--user", "root");
        new CommandLine(persistCmd3).parseArgs("--database", "persist");
        persistCmd3.execute();
        assertGeneratedSources(subDir);

        // user is empty
        Pull persistCmd4 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd4).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd4).parseArgs("--host", "localhost");
        new CommandLine(persistCmd4).parseArgs("--port", "3307");
        new CommandLine(persistCmd4).parseArgs("--user", "");
        new CommandLine(persistCmd4).parseArgs("--database", "persist");
        persistCmd4.execute();
        assertGeneratedSources(subDir);

        // database is empty
        Pull persistCmd5 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd5).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd5).parseArgs("--host", "localhost");
        new CommandLine(persistCmd5).parseArgs("--port", "3307");
        new CommandLine(persistCmd5).parseArgs("--user", "root");
        new CommandLine(persistCmd5).parseArgs("--database", "");
        persistCmd5.execute();
        assertGeneratedSources(subDir);

        // host is not provided
        Pull persistCmd6 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd6).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd6).parseArgs("--port", "3307");
        new CommandLine(persistCmd6).parseArgs("--user", "root");
        new CommandLine(persistCmd6).parseArgs("--database", "persist");
        persistCmd6.execute();
        assertGeneratedSources(subDir);

        // user is not provided
        Pull persistCmd7 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd7).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd7).parseArgs("--host", "localhost");
        new CommandLine(persistCmd7).parseArgs("--port", "3307");
        new CommandLine(persistCmd7).parseArgs("--database", "persist");
        persistCmd7.execute();
        assertGeneratedSources(subDir);

        // database is not provided
        Pull persistCmd8 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd8).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd8).parseArgs("--host", "localhost");
        new CommandLine(persistCmd8).parseArgs("--port", "3307");
        new CommandLine(persistCmd8).parseArgs("--user", "root");
        persistCmd8.execute();
        assertGeneratedSources(subDir);

        // invalid port number (not a number)
        Pull persistCmd9 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd9).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd9).parseArgs("--host", "localhost");
        new CommandLine(persistCmd9).parseArgs("--port", "lkj");
        new CommandLine(persistCmd9).parseArgs("--user", "root");
        new CommandLine(persistCmd9).parseArgs("--database", "persist");
        persistCmd9.execute();
        assertGeneratedSources(subDir);

        // invalid port number (number, but invalid)
        Pull persistCmd10 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd10).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd10).parseArgs("--host", "localhost");
        new CommandLine(persistCmd10).parseArgs("--port", "69000");
        new CommandLine(persistCmd10).parseArgs("--user", "root");
        new CommandLine(persistCmd10).parseArgs("--database", "persist");
        persistCmd10.execute();
        assertGeneratedSources(subDir);

        // invalid database name
        Pull persistCmd11 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        new CommandLine(persistCmd11).parseArgs("--datastore", "mysql");
        new CommandLine(persistCmd11).parseArgs("--host", "localhost");
        new CommandLine(persistCmd11).parseArgs("--port", "3307");
        new CommandLine(persistCmd11).parseArgs("--user", "root");
        new CommandLine(persistCmd11).parseArgs("--database", "8persist");
        persistCmd11.execute();
        assertGeneratedSources(subDir);

        // no args given
        Pull persistCmd12 = (Pull) persistClass.getDeclaredConstructor(String.class).newInstance(path.toAbsolutePath()
                .toString());
        persistCmd12.execute();
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test the --tables option to introspect only selected tables.")
    public void pullTestMysqlWithTablesOption() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandWithTables(subDir, "User,Department", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @DataProvider(name = "interactiveModeInputs")
    public Object[][] provideInteractiveModeInputs() {
        return new Object[][] {
                { "all\n", "tool_test_pull_1_mysql" },
                { "1,3\n", "tool_test_pull_tables_mysql" },
                { "User,Department\n", "tool_test_pull_tables_mysql" }
        };
    }

    @Test(enabled = true, dataProvider = "interactiveModeInputs")
    @Description("Test interactive mode with different table selection inputs.")
    public void pullTestMysqlInteractiveMode(String input, String subDir) throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandInteractive(subDir, input, mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test automatic inclusion of referenced tables via foreign keys when selecting tables.")
    public void pullTestMysqlForeignKeyAutoInclude() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_fk_auto_include_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Selecting only album_ratings table should automatically include albums table
        executePullCommandWithTables(subDir, "album_ratings", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode aborting with 'quit' command.")
    public void pullTestMysqlInteractiveModeQuit() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandInteractive(subDir, "quit\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode with empty input selecting all tables.")
    public void pullTestMysqlInteractiveModeEmpty() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_1_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandInteractive(subDir, "\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test --tables option with single table selection.")
    public void pullTestMysqlSingleTableSelection() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_single_table_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandWithTables(subDir, "User", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test --tables option with partial invalid table names (should continue with valid ones).")
    public void pullTestMysqlPartialInvalidTables() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // User and Department exist, InvalidTable does not
        executePullCommandWithTables(subDir, "User,InvalidTable,Department", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode with partial invalid indices (should continue with valid ones).")
    public void pullTestMysqlInteractiveModePartialInvalidIndices() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Indices 1,3 are valid, 99 is out of range
        executePullCommandInteractive(subDir, "1,99,3\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test --tables option with all invalid table names (should fail).")
    public void pullTestMysqlAllInvalidTables() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_invalid_tables_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandWithTables(subDir, "InvalidTable1,InvalidTable2", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode with all invalid indices (should fail).")
    public void pullTestMysqlInteractiveModeAllInvalidIndices() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_invalid_indices_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // All indices out of range
        executePullCommandInteractive(subDir, "99,100,101\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode with all invalid table names (should fail).")
    public void pullTestMysqlInteractiveModeAllInvalidNames() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_invalid_names_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandInteractive(subDir, "InvalidTable1,InvalidTable2\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode with mixed invalid formats (should skip invalid and fail if all invalid).")
    public void pullTestMysqlInteractiveModeMixedInvalidFormats() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_mixed_invalid_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Mix of invalid indices and non-numeric (treated as table names, but invalid)
        executePullCommandInteractive(subDir, "abc,999\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test --tables option with whitespace in table list.")
    public void pullTestMysqlTablesWithWhitespace() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Test whitespace handling
        executePullCommandWithTables(subDir, " User , Department ", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test interactive mode with 'q' to abort.")
    public void pullTestMysqlInteractiveModeAbortWithQ() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_abort_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executePullCommandInteractive(subDir, "q\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test foreign key dependency with multi-level chain (Address->City->Country).")
    public void pullTestMysqlForeignKeyChain() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_fk_chain_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Selecting the deepest table should auto-include all parent tables
        executePullCommandWithTables(subDir, "Address", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Test cyclic foreign key dependencies (User <-> Department circular reference).")
    public void pullTestMysqlCyclicForeignKeys() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_cyclic_fk_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Selecting User should auto-include Department, which references back to User
        executePullCommandWithTables(subDir, "User", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
        // Even though we generate the model file, the persist generate tool may not support cyclic FKs fully
    }

    @Test(enabled = true)
    @Description("Test self-referential foreign key (Employee table with manager_id FK to itself).")
    public void pullTestMysqlSelfReferentialForeignKey() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_self_ref_fk_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Selecting Employee should handle self-referential FK without infinite loop
        executePullCommandWithTables(subDir, "Employee", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
        // Even though we generate the model file, the persist generate tool may not support self-referential FKs fully
    }

    @Test(enabled = true)
    @Description("Test --tables with whitespace and proper case matching.")
    public void pullTestMysqlTablesCaseSensitivity() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_case_sensitive_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        // Test with correct case - MySQL table names are case-sensitive on Linux/Mac
        executePullCommandWithTables(subDir, "User,Department", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of one Entity with no annotations and not null fields.")
    public void pullTestMysqlBasic() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_1_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of one Entity with no annotations and one nullable field.")
    public void pullTestMysqlNullableField() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_2_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to many relation.")
    public void pullTestMysqlOneToMany() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_3_mysql");
    }

    @Test(enabled = true)
    @Description("Introspection when a model.bal file already exists and continue.")
    public void pullTestMysqlModelFileExistsContinue() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_4_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executeDefaultPullCommand(subDir, "y\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Introspection when a model.bal file already exists and abort.")
    public void pullTestMysqlModelFileExistsAbort() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_5_mysql";
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executeDefaultPullCommand(subDir, "n\n", mysqlDbConfig, "mysql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to one relation " +
            "by making foreign key unique.")
    public void pullTestMysqlOneToOneUniqueKey() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_6_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to one relation by " +
            "making foreign key primary.")
    public void pullTestMysqlOneToOnePrimaryKey() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_7_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two Entities with a one to many relation by " +
            "making foreign key a partial key.")
    public void pullTestMysqlPartialPrimaryKey() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_8_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file three Entities with two one to many relations.")
    public void pullTestMysqlMultipleRelations() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_9_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of name mapping annotations.")
    public void pullTestMysqlNameMappingAnnotation() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_10_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of type mapping annotations Char, VarChar and Decimal.")
    public void pullTestMysqlTypeMappingAnnotation() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_11_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of unique index annotation.")
    public void pullTestMysqlUniqueIndexAnnotation() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_12_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of index annotation.")
    public void pullTestMysqlIndexAnnotation() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_13_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple relations between same entities.")
    public void pullTestMysqlMultipleRelationsBetweenSameEntities() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_14_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple unique indexes on same column.")
    public void pullTestMysqlMultipleUniqueIndexes() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_15_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of multiple indexes on same column.")
    public void pullTestMysqlMultipleIndexes() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_16_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of a one to many relation with composite foreign key.")
    public void pullTestMysqlCompositeForeignKeys() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_17_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of an entity with self-referenced relation.")
    public void pullTestMysqlSelfReferencedRelation() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_18_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of two relations which cross-reference each other.")
    public void pullTestMysqlCrossReferencedRelations() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_19_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of keyword field names which are to be escaped.")
    public void pullTestMysqlEscapedFieldNames() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_20_mysql");
    }

    @Test(enabled = true)
    @Description("When the database does not contain any tables.")
    public void pullTestMysqlNoTablesInDatabase() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_21_mysql");
    }

    @Test(enabled = true)
    @Description("When the database does not exist.")
    public void pullTestMysqlDatabaseDoesNotExist() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_22_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of a generated primary key field.")
    public void pullTestMysqlWithGeneratedId() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_24_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of a blob type field.")
    public void pullTestMysqlWithBlobTypeField() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_25_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of new and unsupported types.")
    public void pullTestMysqlWithNewAndUnsupportedTypes() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_26_mysql");
    }

    @Test(enabled = true)
    @Description("Create a model.bal file consisting of an unsupported foreign key referencing a unique key")
    public void pullTestMysqlWithForeignKeyOnUniqueKey() throws BalException {
        runIntrospectionTestMySql("tool_test_pull_27_mysql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of one Entity with no annotations and not null " +
            "fields.")
    public void pullTestPostgreSqlBasic() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_28_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of one Entity with no annotations and one nullable " +
            "field.")
    public void pullTestPostgreSqlNullableField() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_29_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of two Entities with a one to many relation.")
    public void pullTestPostgreSqlOneToMany() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_30_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of two Entities with a one to one relation by " +
            "making foreign key unique.")
    public void pullTestPostgreSqlOneToOneUniqueKey() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_31_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of two Entities with a one to one relation by " +
            "making foreign key primary.")
    public void pullTestPostgreSqlOneToOnePrimaryKey() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_32_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of two Entities with a one to many relation by " +
            "making foreign key a partial key.")
    public void pullTestPostgreSqlPartialPrimaryKey() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_33_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file three Entities with two one to many relations.")
    public void pullTestPostgreSqlMultipleRelations() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_34_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of name mapping annotations.")
    public void pullTestPostgreSqlNameMappingAnnotation() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_35_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of type mapping annotations Char, VarChar and " +
            "Decimal.")
    public void pullTestPostgreSqlTypeMappingAnnotation() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_36_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of unique index annotation.")
    public void pullTestPostgreSqlUniqueIndexAnnotation() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_37_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of index annotation.")
    public void pullTestPostgreSqlIndexAnnotation() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_38_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of multiple relations between same entities.")
    public void pullTestPostgreSqlMultipleRelationsBetweenSameEntities() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_39_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of multiple unique indexes on same column.")
    public void pullTestPostgreSqlMultipleUniqueIndexes() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_40_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of multiple indexes on same column.")
    public void pullTestPostgreSqlMultipleIndexes() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_41_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of a one to many relation with composite foreign " +
            "key.")
    public void pullTestPostgreSqlCompositeForeignKeys() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_42_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of an entity with self-referenced relation.")
    public void pullTestPostgreSqlSelfReferencedRelation() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_43_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of two relations which cross-reference each other.")
    public void pullTestPostgreSqlCrossReferencedRelations() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_44_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of keyword field names which are to be escaped.")
    public void pullTestPostgreSqlEscapedFieldNames() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_45_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] When the database does not contain any tables.")
    public void pullTestPostgreSqlNoTablesInDatabase() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_46_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] When the database does not exist.")
    public void pullTestPostgreSqlDatabaseDoesNotExist() throws BalException {
        String subDir = "tool_test_pull_47_postgresql";
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetPostgreSqlDatabase(postgresDbConfig, false);
        createFromDatabaseScript(subDir, "postgresql", postgresDbConfig);
        executeDefaultPullCommand(subDir, "postgresql", postgresDbConfig);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of a generated primary key field.")
    public void pullTestPostgreSqlWithGeneratedId() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_48_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of a blob type field.")
    public void pullTestPostgreSqlWithBlobTypeField() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_49_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of new and unsupported types.")
    public void pullTestPostgreSqlWithNewAndUnsupportedTypes() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_50_postgresql");
    }

    @Test(enabled = true)
    @Description("[PosgreSql] Create a model.bal file consisting of an unsupported foreign key referencing a unique " +
            "key")
    public void pullTestPostgreSqlWithForeignKeyOnUniqueKey() throws BalException {
        runIntrospectionTestPostgreSql("tool_test_pull_51_postgresql");
    }

    @Test(enabled = true)
    @Description("[PostgreSQL] Test the --tables option to introspect only selected tables.")
    public void pullTestPostgreSqlWithTablesOption() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_postgresql";
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript(subDir, "postgresql", postgresDbConfig);
        executePullCommandWithTables(subDir, "User,Department", postgresDbConfig, "postgresql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("[PostgreSQL] Test automatic inclusion of referenced tables via foreign keys when selecting tables.")
    public void pullTestPostgreSqlForeignKeyAutoInclude() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_fk_auto_include_postgresql";
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript(subDir, "postgresql", postgresDbConfig);
        executePullCommandWithTables(subDir, "album_ratings", postgresDbConfig, "postgresql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of one Entity with no annotations and not null fields.")
    public void pullTestMsSqlBasic() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_52_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of one Entity with no annotations and one nullable field.")
    public void pullTestMsSqlNullableField() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_53_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of two Entities with a one to many relation.")
    public void pullTestMsSqlOneToMany() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_54_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of two Entities with a one to one relation by making " +
            "foreign key unique.")
    public void pullTestMsSqlOneToOneUniqueKey() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_55_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of two Entities with a one to one relation by making " +
            "foreign key primary.")
    public void pullTestMsSqlOneToOnePrimaryKey() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_56_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of two Entities with a one to many relation by making " +
            "foreign key a partial key.")
    public void pullTestMsSqlPartialPrimaryKey() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_57_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file three Entities with two one to many relations.")
    public void pullTestMsSqlMultipleRelations() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_58_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of name mapping annotations.")
    public void pullTestMsSqlNameMappingAnnotation() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_59_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of type mapping annotations Char, VarChar and Decimal.")
    public void pullTestMsSqlTypeMappingAnnotation() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_60_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of unique index annotation.")
    public void pullTestMsSqlUniqueIndexAnnotation() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_61_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of index annotation.")
    public void pullTestMsSqlIndexAnnotation() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_62_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of multiple relations between same entities.")
    public void pullTestMsSqlMultipleRelationsBetweenSameEntities() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_63_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of multiple unique indexes on same column.")
    public void pullTestMsSqlMultipleUniqueIndexes() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_64_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of multiple indexes on same column.")
    public void pullTestMsSqlMultipleIndexes() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_65_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of a one to many relation with composite foreign key.")
    public void pullTestMsSqlCompositeForeignKeys() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_66_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of an entity with self-referenced relation.")
    public void pullTestMsSqlSelfReferencedRelation() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_67_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of two relations which cross-reference each other.")
    public void pullTestMsSqlCrossReferencedRelations() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_68_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of keyword field names which are to be escaped.")
    public void pullTestMsSqlEscapedFieldNames() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_69_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] When the database does not contain any tables.")
    public void pullTestMsSqlNoTablesInDatabase() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_70_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] When the database does not exist.")
    public void pullTestMsSqlDatabaseDoesNotExist() throws BalException {
        String subDir = "tool_test_pull_71_mssql";
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetMsSqlDatabase(mssqlDbConfig, false);
        createFromDatabaseScript(subDir, "mssql", mssqlDbConfig);
        executeDefaultPullCommand(subDir, "mssql", mssqlDbConfig);
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of a generated primary key field.")
    public void pullTestMsSqlWithGeneratedId() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_72_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of a blob type field.")
    public void pullTestMsSqlWithBlobTypeField() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_73_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of new and unsupported types.")
    public void pullTestMsSqlWithNewAndUnsupportedTypes() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_74_mssql");
    }

    @Test(enabled = true)
    @Description("[MsSql] Create a model.bal file consisting of an unsupported foreign key referencing a unique " +
            "key")
    public void pullTestMsSqlWithForeignKeyOnUniqueKey() throws BalException {
        runIntrospectionTestMsSql("tool_test_pull_75_mssql");
    }

    @Test(enabled = true)
    @Description("[MSSQL] Test the --tables option to introspect only selected tables.")
    public void pullTestMsSqlWithTablesOption() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_tables_mssql";
        resetMsSqlDatabase(mssqlDbConfig, true);
        createFromDatabaseScript(subDir, "mssql", mssqlDbConfig);
        executePullCommandWithTables(subDir, "User,Department", mssqlDbConfig, "mssql");
        assertGeneratedSources(subDir);
    }

    @Test(enabled = true)
    @Description("[MSSQL] Test automatic inclusion of referenced tables via foreign keys when selecting tables.")
    public void pullTestMsSqlForeignKeyAutoInclude() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        String subDir = "tool_test_pull_fk_auto_include_mssql";
        resetMsSqlDatabase(mssqlDbConfig, true);
        createFromDatabaseScript(subDir, "mssql", mssqlDbConfig);
        executePullCommandWithTables(subDir, "album_ratings", mssqlDbConfig, "mssql");
        assertGeneratedSources(subDir);
    }

    private static void runIntrospectionTestMySql(String subDir) throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        createFromDatabaseScript(subDir, "mysql", mysqlDbConfig);
        executeDefaultPullCommand(subDir, "mysql", mysqlDbConfig);
        assertGeneratedSources(subDir);
    }

    private static void runIntrospectionTestPostgreSql(String subDir) throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript(subDir, "postgresql", postgresDbConfig);
        executeDefaultPullCommand(subDir, "postgresql", postgresDbConfig);
        assertGeneratedSources(subDir);
    }

    private static void runIntrospectionTestMsSql(String subDir) throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetMsSqlDatabase(mssqlDbConfig, true);
        createFromDatabaseScript(subDir, "mssql", mssqlDbConfig);
        executeDefaultPullCommand(subDir, "mssql", mssqlDbConfig);
        assertGeneratedSources(subDir);
    }

    private static void executeDefaultPullCommand(String subDir, String datastore, DatabaseConfiguration dbConfig)
            throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", datastore);
            new CommandLine(persistCmd).parseArgs("--host", dbConfig.getHost());
            new CommandLine(persistCmd).parseArgs("--port", String.valueOf(dbConfig.getPort()));
            new CommandLine(persistCmd).parseArgs("--user", dbConfig.getUsername());
            new CommandLine(persistCmd).parseArgs("--database", dbConfig.getDatabase());
            String password = dbConfig.getPassword() + "\n";
            InputStream originalSystemIn = System.in;
            try (InputStream inputStream = new ByteArrayInputStream(password.getBytes(StandardCharsets.UTF_8))) {
                System.setIn(inputStream);
                persistCmd.execute();
            } finally {
                System.setIn(originalSystemIn);
            }
            System.setIn(originalSystemIn);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command: " + e.getMessage());
        }
    }

    private static void executeDefaultPullCommand(String subDir, String simulatedInput, DatabaseConfiguration dbConfig,
            String datastore)
            throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", datastore);
            new CommandLine(persistCmd).parseArgs("--host", dbConfig.getHost());
            new CommandLine(persistCmd).parseArgs("--port", String.valueOf(dbConfig.getPort()));
            new CommandLine(persistCmd).parseArgs("--user", dbConfig.getUsername());
            new CommandLine(persistCmd).parseArgs("--database", dbConfig.getDatabase());
            InputStream originalSystemIn = System.in;
            String passwordAndSimulatedInput = dbConfig.getPassword() + "\n" + simulatedInput;
            try (InputStream inputStream = new ByteArrayInputStream(passwordAndSimulatedInput
                    .getBytes(StandardCharsets.UTF_8))) {
                System.setIn(inputStream);
                persistCmd.execute();
            } finally {
                System.setIn(originalSystemIn);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command: " + e.getMessage());
        }
    }

    private static void executePullCommandWithTables(String subDir, String tables, DatabaseConfiguration dbConfig,
            String datastore)
            throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", datastore,
                    "--host", dbConfig.getHost(),
                    "--port", String.valueOf(dbConfig.getPort()),
                    "--user", dbConfig.getUsername(),
                    "--database", dbConfig.getDatabase(),
                    "--tables", tables);
            // Include "y\n" for potential model.bal overwrite confirmation
            String input = dbConfig.getPassword() + "\ny\n";
            InputStream originalSystemIn = System.in;
            try (InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
                System.setIn(inputStream);
                persistCmd.execute();
            } finally {
                System.setIn(originalSystemIn);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command with tables: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command with tables: " + e.getMessage());
        }
    }

    private static void executePullCommandInteractive(String subDir, String tableSelection,
            DatabaseConfiguration dbConfig, String datastore)
            throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", datastore,
                    "--host", dbConfig.getHost(),
                    "--port", String.valueOf(dbConfig.getPort()),
                    "--user", dbConfig.getUsername(),
                    "--database", dbConfig.getDatabase(),
                    "--tables");
            // Note: --tables with no value triggers interactive mode
            String input = dbConfig.getPassword() + "\n" + tableSelection;
            InputStream originalSystemIn = System.in;
            try (InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
                System.setIn(inputStream);
                persistCmd.execute();
            } finally {
                System.setIn(originalSystemIn);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command in interactive mode: " +
                    e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command in interactive mode: " +
                    e.getMessage());
        }
    }

    // Multi-model support tests

    @Test
    @Description("Test pull command with --model option to create subdirectory model")
    public void testPullWithModelOption() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript("tool_test_pull_multimodel_1", "postgresql", postgresDbConfig);
        executePullCommandWithModel("tool_test_pull_multimodel_1", "users", postgresDbConfig, "postgresql");
        assertGeneratedSources("tool_test_pull_multimodel_1");
    }

    @Test
    @Description("Test pull command with multiple models from different databases")
    public void testPullMultipleModels() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript("tool_test_pull_multimodel_2", "postgresql", postgresDbConfig);
        
        // Pull first model
        executePullCommandWithModel("tool_test_pull_multimodel_2", "users", postgresDbConfig, "postgresql");
        // Pull second model
        executePullCommandWithModel("tool_test_pull_multimodel_2", "orders", postgresDbConfig, "postgresql");
        
        assertGeneratedSources("tool_test_pull_multimodel_2");
    }

    @Test
    @Description("Test pull command backward compatibility - creates root model by default")
    public void testPullDefaultModelPath() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript("tool_test_pull_backward_compat", "postgresql", postgresDbConfig);
        executePullCommandWithoutModel("tool_test_pull_backward_compat", postgresDbConfig, "postgresql");
        assertGeneratedSources("tool_test_pull_backward_compat");
    }

    @Test
    @Description("Test pull command with reserved model name should fail")
    public void testPullReservedModelName() throws BalException {
        if (OS.WINDOWS.isCurrentOs()) {
            return;
        }
        resetPostgreSqlDatabase(postgresDbConfig, true);
        createFromDatabaseScript("tool_test_pull_multimodel_3", "postgresql", postgresDbConfig);
        assertGeneratedSourcesNegative("tool_test_pull_multimodel_3", GeneratedSourcesTestUtils.Command.PULL,
                new String[]{"persist/migrations"}, "--model", "migrations", "--datastore", "postgresql",
                "--host", postgresDbConfig.getHost(),
                "--port", String.valueOf(postgresDbConfig.getPort()),
                "--user", postgresDbConfig.getUsername(),
                "--database", postgresDbConfig.getDatabase());
    }

    private static void executePullCommandWithModel(String subDir, String modelName, DatabaseConfiguration dbConfig,
                                                    String datastore) throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", datastore,
                    "--host", dbConfig.getHost(),
                    "--port", String.valueOf(dbConfig.getPort()),
                    "--user", dbConfig.getUsername(),
                    "--database", dbConfig.getDatabase(),
                    "--model", modelName);
            String input = dbConfig.getPassword() + "\ny\n";
            InputStream originalSystemIn = System.in;
            try (InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
                System.setIn(inputStream);
                persistCmd.execute();
            } finally {
                System.setIn(originalSystemIn);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command with model: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command with model: " + e.getMessage());
        }
    }

    private static void executePullCommandWithoutModel(String subDir, DatabaseConfiguration dbConfig, 
            String datastore) throws BalException {
        try {
            Class<?> persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
            Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir).toAbsolutePath().toString());
            new CommandLine(persistCmd).parseArgs("--datastore", datastore,
                    "--host", dbConfig.getHost(),
                    "--port", String.valueOf(dbConfig.getPort()),
                    "--user", dbConfig.getUsername(),
                    "--database", dbConfig.getDatabase());
            String input = dbConfig.getPassword() + "\ny\n";
            InputStream originalSystemIn = System.in;
            try (InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
                System.setIn(inputStream);
                persistCmd.execute();
            } finally {
                System.setIn(originalSystemIn);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error occurred while executing pull command: " + e.getMessage());
        } catch (Exception e) {
            throw new BalException("Error occurred while executing pull command: " + e.getMessage());
        }
    }
}
