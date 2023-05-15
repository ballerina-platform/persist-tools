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
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;

/**
 * persist tool migrate command tests.
 */
public class ToolingMigrateTest {

    private static final PrintStream errStream = System.err;

    @Test(enabled = false)
    @Description("There has been 1 previous migration")
    public void testFirstMigrateTest() {

        executeCommand("tool_test_migrate_1");
        assertGeneratedSources("tool_test_migrate_1");
    }

    @Test(enabled = false)
    @Description("There has been no previous migrations")
    public void testSecondMigrateTest() {

        executeCommand("tool_test_migrate_2");
        assertGeneratedSources("tool_test_migrate_2");
    }

    private void executeCommand(String subDir) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Migrate");
                Migrate persistCmd = (Migrate) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                new CommandLine(persistCmd).parseArgs("migrationLabel");
                persistCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }
}
