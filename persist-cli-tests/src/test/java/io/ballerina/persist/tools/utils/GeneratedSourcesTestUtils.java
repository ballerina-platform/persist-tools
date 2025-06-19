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

package io.ballerina.persist.tools.utils;

import io.ballerina.persist.cmd.Add;
import io.ballerina.persist.cmd.Generate;
import io.ballerina.persist.cmd.Init;
import io.ballerina.persist.cmd.Migrate;
import io.ballerina.persist.cmd.PersistCmd;
import io.ballerina.persist.cmd.Pull;
import io.ballerina.persist.cmd.Push;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import org.testng.Assert;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * persist tool test Utils.
 */
public class GeneratedSourcesTestUtils {

    /**
     * Represents persist commands.
     */
    public enum Command {
        INIT,
        ADD,
        GENERATE,
        DB_PUSH,
        MIGRATE,
        PULL
    }

    private static final PrintStream errStream = System.err;
    public static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources")
            .toString();

    public static final String INPUT_RESOURCES_DIRECTORY =
            Paths.get("src", "test", "resources", "test-src", "input").toString();
    public static final Path RESOURCES_EXPECTED_OUTPUT =
            Paths.get("src", "test", "resources", "test-src", "output")
            .toAbsolutePath();
    public static final String PERSIST_MIGRATIONS_DIR = Paths.get("persist", "migrations").toString();

    public static void assertGeneratedSources(String subDir) {
        Assert.assertTrue(directoryContentEquals(Paths.get(RESOURCES_EXPECTED_OUTPUT.toString()).resolve(subDir),
                Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)));
        for (Path actualOutputFile: listFiles(Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir))) {
            errStream.println(actualOutputFile);
            if ((actualOutputFile.toString().contains("persist_db_scripts.sql") ||
                    actualOutputFile.toString().contains("model.bal")) &&
                    (subDir.equals("tool_test_generate_7") || subDir.equals("tool_test_generate_14") ||
                            subDir.equals("tool_test_generate_15"))) {
                continue;
            }
            Path expectedOutputFile = Paths.get(RESOURCES_EXPECTED_OUTPUT.toString(), subDir).
                    resolve(actualOutputFile.subpath(3, actualOutputFile.getNameCount()));
            Assert.assertTrue(Files.exists(actualOutputFile));
            if (!actualOutputFile.toString().contains("tool_test_migrate")) {
                Assert.assertEquals(readContent(actualOutputFile), readContent(expectedOutputFile));
            }
        }
        if (!(subDir.equals("tool_test_generate_4") || subDir.equals("tool_test_generate_26") ||
                subDir.equals("tool_test_generate_18"))) {

            BuildProject buildProject = BuildProject.load(Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)
                    .toAbsolutePath());
            Package currentPackage = buildProject.currentPackage();
            try {
                PackageCompilation compilation = currentPackage.getCompilation();
                boolean hasError = compilation.diagnosticResult().hasErrors();
                if (hasError) {
                    errStream.println("Compilation errors found in generated sources");
                    errStream.println(Arrays.toString(compilation.diagnosticResult().errors().toArray()));
                }
                Assert.assertFalse(hasError);
            } catch (Exception e) {
                errStream.println("Error occurred while executing the generated packages: " + e.getMessage());
            }
        }
    }

    public static void assertMigrateGeneratedSources(String subDir) {
        Path generatedSources = Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir);
        Path expectedSources = RESOURCES_EXPECTED_OUTPUT.resolve(subDir);

        // check if directory content equals except the migrations directory
        Assert.assertTrue(directoryContentEquals(expectedSources, generatedSources));

        for (Path actualOutputFile: listFiles(generatedSources)) {
            Path generatedMigrations = generatedSources.resolve(PERSIST_MIGRATIONS_DIR);
            Path expectedMigrations = expectedSources.resolve(PERSIST_MIGRATIONS_DIR);
            Assert.assertEquals(getNumOfSubDirectories(generatedMigrations),
                    getNumOfSubDirectories(expectedMigrations));
            try (Stream<Path> generatedFileList = Files.list(generatedMigrations)) {
                generatedFileList.forEach(generatedDirectory -> {
                    String generatedDirectoryNameWithoutTimeStamp =
                            getDirectoryNameWithoutTimeStamp(generatedDirectory);
                    assert(generatedDirectoryNameWithoutTimeStamp != null);
                    try (Stream<Path> expectedFileList = Files.list(expectedMigrations)) {
                        for (Path expectedDirectory : expectedFileList.toList()) {
                            String expectedDirectoryNameWithoutTimeStamp =
                                    getDirectoryNameWithoutTimeStamp(expectedDirectory);
                            if (generatedDirectoryNameWithoutTimeStamp.equals(expectedDirectoryNameWithoutTimeStamp)) {
                                Assert.assertTrue(directoryContentEquals(expectedDirectory, generatedDirectory));
                                for (Path expectedFile : listFiles(expectedDirectory)) {
                                    Path generatedFile = generatedDirectory.resolve(expectedFile.getFileName());
                                    Assert.assertTrue(Files.exists(generatedFile));
                                    Assert.assertEquals(readContent(generatedFile), readContent(expectedFile));
                                }
                                break;
                            }
                        }
                    } catch (IOException e) {
                        Assert.fail("IO Exception: " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                Assert.fail("IO Exception: " + e.getMessage());
            }
            errStream.println(actualOutputFile);
        }
    }

    private static String getDirectoryNameWithoutTimeStamp(Path generatedDirectory) {
        Path directory = generatedDirectory.getFileName();
        if (Objects.isNull(directory)) {
            Assert.fail("Generated migration directory doesn't exist");
        }
        String[] directoryName = directory.toString().split("_");
        if (directoryName.length == 1) {
            Assert.fail("Invalid directory name format. Expected format: <timestamp>_<migrationLabel>");
        }
        return directoryName[1];
    }

    private static long getNumOfSubDirectories(Path path) {
        try (Stream<Path> fileList = Files.list(path)) {
            return fileList.filter(Files::isDirectory).count();
        } catch (IOException e) {
            Assert.fail("IO Exception: " + e.getMessage());
        }
        return 0;
    }

    public static void assertGeneratedSourcesNegative(String subDir, Command cmd, String[] relativeFilepaths,
                                                      String... args) {
        Path sourceDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        if (cmd == Command.GENERATE) {
            executeGenerateCommand(subDir, args);
        } else {
            executeCommand(subDir, cmd);
        }
        if (cmd == Command.DB_PUSH) {
            Assert.assertFalse(false);
        } else {
            Assert.assertTrue(directoryContentEquals(Paths.get(RESOURCES_EXPECTED_OUTPUT.toString()).resolve(subDir),
                    Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)));
            for (String filePath : relativeFilepaths) {
                Path absoluteFilepath = sourceDirPath.resolve(filePath);
                Assert.assertFalse(Files.exists(absoluteFilepath));
            }
        }
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

            StringBuilder initLongDesc = new StringBuilder();
            StringBuilder cmdLongDesc = new StringBuilder();

            persistCmdInit.printLongDesc(initLongDesc);
            persistCmdInit.printUsage(initLongDesc);
            String initLongDescription = initLongDesc.toString();

            persistCmd.printLongDesc(cmdLongDesc);
            persistCmd.printUsage(cmdLongDesc);
            String cmdLongDescription = cmdLongDesc.toString();
            Assert.assertEquals(initLongDescription.trim().replaceAll(System.lineSeparator(), ""),
                    "Generate database configurations file inside the Ballerina project  ballerina persist " +
                            "init");
            Assert.assertEquals(cmdLongDescription.trim().replaceAll(System.lineSeparator(), ""),
                    "Perform operations on Ballerina Persistent Layer  ballerina persist");
            persistCmdInit.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }

    public static HashMap executeCommand(String subDir, Command cmd) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            if (cmd == Command.ADD) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Add");
                Add persistCmd = (Add) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                persistCmd.execute();
            } else if (cmd == Command.INIT) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Init");
                Init persistCmd = (Init) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                persistCmd.execute();
            } else if (cmd == Command.GENERATE) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Generate");
                Generate persistCmd = (Generate) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                persistCmd.execute();
            } else if (cmd == Command.DB_PUSH) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Push");
                Push persistCmd = (Push) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                persistCmd.execute();
            } else if (cmd == Command.PULL) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Pull");
                Pull persistCmd = (Pull) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                persistCmd.execute();
            } else {
                persistClass = Class.forName("io.ballerina.persist.cmd.Migrate");
                Migrate persistCmd = (Migrate) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                persistCmd.execute();
            }

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
        return new HashMap<>();
    }

    public static void executeGenerateCommand(String subDir, String... args) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Generate");
            errStream.println("Class found: " + persistClass != null);
            errStream.println("Source path: " + sourcePath.toAbsolutePath());
            Generate persistCmd = (Generate) persistClass.getDeclaredConstructor(String.class)
                    .newInstance(sourcePath.toAbsolutePath().toString());

            new CommandLine(persistCmd).parseArgs(args);
            persistCmd.execute();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }

    private static List<Path> listFiles(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk != null ? walk.filter(Files::isRegularFile).collect(Collectors.toList()) : new ArrayList<>();
        } catch (IOException e) {
            errStream.println(e.getMessage());
        }
        return new ArrayList<>();
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
                errStream.println("files in directories doesn't match");
                errStream.println(dir1Paths);
                errStream.println(dir2Paths);
                return false;
            }
            for (Map.Entry<Path, Path> pathEntry : dir1Paths.entrySet()) {
                Path relativePath = pathEntry.getKey();
                if (relativePath.toString().contains(File.separator + "migrations" + File.separator)) {
                    continue;
                }
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
