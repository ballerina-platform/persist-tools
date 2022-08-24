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

import io.ballerina.persist.cmd.Init;
import io.ballerina.persist.cmd.PersistCmd;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.environment.Environment;
import io.ballerina.projects.environment.EnvironmentBuilder;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * persist tool test Utils.
 */
public class ToolingTestUtils {

    /**
     * Represents persist commands.
     */
    public enum Command {
        INIT,
        GENERATE
    }

    private static final PrintStream errStream = System.err;

    public static final String CONFIG_FILE = "Config.toml";
    public static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources").toString();
    public static final Path RESOURCES_EXPECTED_OUTPUT = Paths.get("src", "test", "resources", "test-src", "output")
            .toAbsolutePath();
    private static final Path DISTRIBUTION_PATH = Paths.get(".." + File.separator, "target", "ballerina-runtime")
            .toAbsolutePath();

    private static ProjectEnvironmentBuilder getEnvironmentBuilder() {
        Environment environment = EnvironmentBuilder.getBuilder().setBallerinaHome(DISTRIBUTION_PATH).build();
        return ProjectEnvironmentBuilder.getBuilder(environment);
    }

    public static void assertGeneratedSources(String subDir, Command cmd) {
        generateSourceCode(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir), cmd);
        Assert.assertTrue(directoryContentEquals(Paths.get(RESOURCES_EXPECTED_OUTPUT.toString()).resolve(subDir),
                Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)));

        for (Path actualOutputFile: listFiles(Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir))) {
            Path expectedOutputFile = Paths.get(RESOURCES_EXPECTED_OUTPUT.toString(), subDir).
                    resolve(actualOutputFile.subpath(3, actualOutputFile.getNameCount()));

            Assert.assertTrue(Files.exists(actualOutputFile));
            Assert.assertEquals(readContent(actualOutputFile), readContent(expectedOutputFile));
        }
    }

    public static void assertGeneratedSourcesNegative(String subDir, Command cmd) {
        Path sourceDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        Path actualConfigFilePath = sourceDirPath.resolve(CONFIG_FILE);
        generateSourceCode(sourceDirPath, cmd);
        Assert.assertFalse(Files.exists(actualConfigFilePath));
    }

    public static void assertAuxiliaryFunctions() {
        Class<?> persistInitClass;
        Class<?> persistCmdClass;
        try {
            persistCmdClass = Class.forName("io.ballerina.persist.cmd.PersistCmd");
            PersistCmd  persistCmd = (PersistCmd) persistCmdClass.getDeclaredConstructor().newInstance();
            persistInitClass = Class.forName("io.ballerina.persist.cmd.Init");
            Init persistCmdInit = (Init) persistInitClass.getDeclaredConstructor().newInstance();
            Assert.assertEquals(persistCmdInit.getName(), "persist");
            Assert.assertEquals(persistCmd.getName(), "persist");
            persistCmdInit.setEnvironmentBuilder(getEnvironmentBuilder());

            StringBuilder initLongDesc = new StringBuilder();
            StringBuilder cmdLongDesc = new StringBuilder();

            persistCmdInit.printLongDesc(initLongDesc);
            persistCmdInit.printUsage(initLongDesc);
            String initLongDescription = initLongDesc.toString();

            persistCmd.printLongDesc(cmdLongDesc);
            persistCmd.printUsage(cmdLongDesc);
            String cmdLongDescription = cmdLongDesc.toString();
            Assert.assertEquals(initLongDescription.trim().replaceAll(System.lineSeparator(), ""),
                    "Generate database configurations file inside the Ballerina project  ballerina persist init");
            Assert.assertEquals(cmdLongDescription.trim().replaceAll(System.lineSeparator(), ""),
                    "Perform operations on Ballerina Persistent Layer  ballerina persist");
            persistCmdInit.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }

    private static void generateSourceCode(Path sourcePath, Command cmd) {
        Class<?> persistClass;
        PersistCmd persistCmd;
        try {
            if (cmd == Command.INIT) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Init");
            } else {
                persistClass = Class.forName("io.ballerina.persist.cmd.Generate");
            }
            persistCmd = (Init) persistClass.getDeclaredConstructor().newInstance();
            persistCmd.setSourcePath(sourcePath.toAbsolutePath().toString());
            persistCmd.setEnvironmentBuilder(getEnvironmentBuilder());
            persistCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }

    private static List<Path> listFiles(Path path) {
        Stream<Path> walk = null;
        try {
            walk = Files.walk(path);
        } catch (IOException e) {
            errStream.println(e.getMessage());
        }
        return walk != null ? walk.filter(Files::isRegularFile).collect(Collectors.toList()) : new ArrayList<>();
    }

    private static boolean directoryContentEquals(Path dir1, Path dir2) {
        boolean dir1Exists = Files.exists(dir1) && Files.isDirectory(dir1);
        boolean dir2Exists = Files.exists(dir2) && Files.isDirectory(dir2);

        if (dir1Exists && dir2Exists) {
            HashMap<Path, Path> dir1Paths = new HashMap<>();
            HashMap<Path, Path> dir2Paths = new HashMap<>();

            for (Path p : listFiles(dir1)) {
                dir1Paths.put(dir1.relativize(p), p);
            }

            for (Path p : listFiles(dir2)) {
                dir2Paths.put(dir2.relativize(p), p);
            }

            if (dir1Paths.size() != dir2Paths.size()) {
                return false;
            }

            for (Map.Entry<Path, Path> pathEntry : dir1Paths.entrySet()) {
                Path relativePath = pathEntry.getKey();
                if (!dir2Paths.containsKey(relativePath)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static String readContent(Path filePath) {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            errStream.println(e.getMessage());
            return "";
        }
        return content.replaceAll(System.lineSeparator(), "");
    }
}
