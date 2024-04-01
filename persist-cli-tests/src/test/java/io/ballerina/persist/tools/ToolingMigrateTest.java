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

    @Test(enabled = true)
    @Description("Test first time migration with no existing migrations")
    public void testMigrateFirstTime() {
        executeCommand("tool_test_migrate_1", "firstMigration");
        assertMigrateGeneratedSources("tool_test_migrate_1");
    }

    @Test(enabled = true)
    @Description("Test third time migration with two existing migrations")
    public void testMigrateThirdTime() {
        executeCommand("tool_test_migrate_2", "thirdMigration");
        assertMigrateGeneratedSources("tool_test_migrate_2");
    }

    @Test(enabled = true)
    @Description("Create a new table and migrate")
    public void testMigrateWithNewTable() {
        executeCommand("tool_test_migrate_3", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_3");
    }

    @Test(enabled = true)
    @Description("Remove a table and migrate")
    public void testMigrateRemoveTable() {
        executeCommand("tool_test_migrate_4", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_4");
    }

    @Test(enabled = true)
    @Description("Rename a table and migrate")
    public void testMigrateRenameTable() {
        executeCommand("tool_test_migrate_5", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_5");
    }

    @Test(enabled = true)
    @Description("Add a field to an existing table and migrate")
    public void testMigrateAddField() {
        executeCommand("tool_test_migrate_6", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_6");
    }

    @Test(enabled = true)
    @Description("Remove a field from an existing table and migrate")
    public void testMigrateRemoveField() {
        executeCommand("tool_test_migrate_7", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_7");
    }

    @Test(enabled = true)
    @Description("Change ballerina type of a column and migrate")
    public void testMigrateChangeBallerinaTypeOfAColumn() {
        executeCommand("tool_test_migrate_8", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_8");
    }

    @Test(enabled = true)
    @Description("Change the simple name of a column and migrate")
    public void testMigrateChangeSimpleNameOfAColumn() {
        executeCommand("tool_test_migrate_9", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_9");
    }

    @Test(enabled = true)
    @Description("Rename a primary key and migrate")
    public void testMigrateRenamePrimaryKey() {
        executeCommand("tool_test_migrate_10", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_10");
    }

    @Test(enabled = true)
    @Description("Make an existing required column optional and migrate")
    public void testMigrateMakeRequiredColumnOptional() {
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
