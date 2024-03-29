/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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

import io.ballerina.persist.cmd.Migrate;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertMigrateGeneratedSources;

/**
 * persist tool migrate command tests.
 */
public class ToolingMigrateTest {

    private static final PrintStream errStream = System.err;

    @Test(enabled = false)
    @Description("There has been 1 previous migration")
    public void testExistingMigrateTest() {
        executeCommand("tool_test_migrate_1", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_1");
    }

    @Test(enabled = false)
    @Description("There has been no previous migrations")
    public void testNewMigrateTest() {
        executeCommand("tool_test_migrate_2", "firstMigration");
        assertMigrateGeneratedSources("tool_test_migrate_2");
    }

    @Test(enabled = false)
    @Description("Create a new table and migrate")
    public void testCreateTableMigrateTest() {
        executeCommand("tool_test_migrate_3", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_3");
    }

    @Test(enabled = false)
    @Description("Remove a table and migrate")
    public void testRemoveTableMigrateTest() {
        executeCommand("tool_test_migrate_4", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_4");
    }

    @Test(enabled = false)
    @Description("Add a new field to a table and migrate")
    public void testAddFieldMigrateTest() {
        executeCommand("tool_test_migrate_5", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_5");
    }

    @Test(enabled = false)
    @Description("Remove a field from a table and migrate")
    public void testRemoveFieldMigrateTest() {
        executeCommand("tool_test_migrate_6", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_6");
    }

    @Test(enabled = false)
    @Description("Change data type of a field and migrate")
    public void testChangeTypeMigrateTest() {
        executeCommand("tool_test_migrate_7", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_7");
    }

    @Test(enabled = false)
    @Description("Add foreign key to a table and migrate")
    public void testAddFKMigrateTest() {
        executeCommand("tool_test_migrate_8", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_8");
    }

    @Test(enabled = false)
    @Description("Remove foreign key from a table and migrate")
    public void testRemoveFKMigrateTest() {
        executeCommand("tool_test_migrate_9", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_9");
    }

    @Test(enabled = false)
    @Description("Add and Remove a primary key from a table and migrate")
    public void testPrimaryKeyMigrateTest() {
        executeCommand("tool_test_migrate_10", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_10");
    }

    @Test(enabled = false)
    @Description("Test execute command with no difference")
    public void testNoDifferenceMigrateTest() {
        executeCommand("tool_test_migrate_11", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_11");
    }

    private void executeCommand(String subDir, String migrationLabel) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Migrate");
                Migrate persistCmd = (Migrate) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                new CommandLine(persistCmd).parseArgs(migrationLabel);
                persistCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }
}
