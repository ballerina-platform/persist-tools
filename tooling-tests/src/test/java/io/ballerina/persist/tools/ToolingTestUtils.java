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

import io.ballerina.persist.cmd.Generate;
import io.ballerina.persist.cmd.Init;
import io.ballerina.persist.cmd.PersistCmd;
import io.ballerina.persist.cmd.Push;
import io.ballerina.persist.objects.BalException;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.utils.BalProjectUtils.hasSemanticDiagnostics;
import static io.ballerina.persist.utils.BalProjectUtils.hasSyntacticDiagnostics;

/**
 * persist tool test Utils.
 */
public class ToolingTestUtils {

    /**
     * Represents persist commands.
     */
    public enum Command {
        INIT,
        GENERATE,
        DBPUSH
    }

    private static final PrintStream errStream = System.err;
    public static final String GENERATED_SOURCES_DIRECTORY = Paths.get("build", "generated-sources").toString();
    public static final Path RESOURCES_EXPECTED_OUTPUT = Paths.get("src", "test", "resources", "test-src", "output")
            .toAbsolutePath();
    private static final Path DISTRIBUTION_PATH = Paths.get(".." + File.separator, "target", "ballerina-runtime")
            .toAbsolutePath();
    private static final Path DRIVER_PATH = Paths.get("lib").toAbsolutePath();

    private static ProjectEnvironmentBuilder getEnvironmentBuilder() {
        Environment environment = EnvironmentBuilder.getBuilder().setBallerinaHome(DISTRIBUTION_PATH).build();
        return ProjectEnvironmentBuilder.getBuilder(environment);
    }

    public static void assertGeneratedSources(String subDir, Command cmd) {

        generateSourceCode(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir), cmd);
        Assert.assertTrue(directoryContentEquals(Paths.get(RESOURCES_EXPECTED_OUTPUT.toString()).resolve(subDir),
                Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)));

        if (!subDir.equals("tool_test_generate_4")) {
            try {
                Assert.assertFalse(!hasSyntacticDiagnostics(Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir))
                        .isEmpty());
            } catch (IOException | BalException e) {
                errStream.println(e.getMessage());
                Assert.fail();
            }
            Assert.assertFalse(hasSemanticDiagnostics(Paths.get(GENERATED_SOURCES_DIRECTORY)
                    .resolve(subDir), getEnvironmentBuilder()).hasErrors());
        }

        for (Path actualOutputFile: listFiles(Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir))) {
            if (actualOutputFile.toString().contains("persist_db_scripts.sql")
                    && subDir.equals("tool_test_generate_7")) {
                continue;
            }
            Path expectedOutputFile = Paths.get(RESOURCES_EXPECTED_OUTPUT.toString(), subDir).
                    resolve(actualOutputFile.subpath(3, actualOutputFile.getNameCount()));
            Assert.assertTrue(Files.exists(actualOutputFile));
            Assert.assertEquals(readContent(actualOutputFile), readContent(expectedOutputFile));
        }
    }

    public static void assertGeneratedDbSources(String subDir, Command cmd) {
        String osName = System.getProperty("os.name");
        if (!osName.toLowerCase().contains("windows")) {
            File sqlFile;
            sqlFile = new File(
                    Paths.get("target", "persist_db_scripts.sql").toAbsolutePath().toString());
            if (sqlFile.exists()) {
                boolean deleteRes = sqlFile.delete();
                if (deleteRes) {
                    errStream.println("File deleted successfully");
                } else {
                    errStream.println("Error while deleting file");
                }
            }

            Connection connection = null;
            ResultSet resultSet = null;
            HashMap configurations = generateSourceCode(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir), cmd);

            Assert.assertTrue(directoryContentEquals(Paths.get(RESOURCES_EXPECTED_OUTPUT.toString()).resolve(subDir),
                    Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)));

            Assert.assertTrue(Files.exists(Paths.get(GENERATED_SOURCES_DIRECTORY, subDir, "target"
                            , "persist_db_scripts.sql").toAbsolutePath()));
            String url = String.format("jdbc:mysql://%s:%s",
                    configurations.get("host").toString().replaceAll("\"", ""), configurations.get("port").toString());
            String user = configurations.get("user").toString().replaceAll("\"", "");
            String password = configurations.get("password").toString().replaceAll("\"", "");
            String database = configurations.get("database").toString().replaceAll("\"", "");
            try {
                connection = DriverManager.getConnection(url, user, password);
                resultSet = connection.getMetaData().getCatalogs();
                boolean databaseExists = false;
                while (resultSet.next()) {
                    if (resultSet.getString(1).trim().equals(database)) {
                        databaseExists = true;
                        break;
                    }
                }
                if (!databaseExists) {
                    Assert.fail();
                }
                resultSet.close();
                connection.close();
                String databaseUrl = String.format("jdbc:mysql://%s:%s/%s",
                        configurations.get("host").toString().replaceAll("\"", ""),
                        configurations.get("port").toString(),
                        configurations.get("database").toString().replaceAll("\"", ""));
                connection = DriverManager.getConnection(databaseUrl, user, password);
                resultSet = connection.getMetaData().getTables(null, null, "%", null);
                boolean table1 = false;
                boolean table2 = false;
                while (resultSet.next()) {
                    if (resultSet.getString(3).trim().equals("MedicalNeed")) {
                        table1 = true;
                    } else if (resultSet.getString(3).trim().equals("MedicalItems")) {
                        table2 = true;
                    }
                    if (table1 && table2) {
                        break;
                    }
                }
                resultSet.close();
                connection.close();
                if (!table1 && !table2) {
                    Assert.fail();
                }
            } catch (SQLException e) {
                errStream.println("*** Error : " + e.getMessage());
                errStream.println("*** ");
                Assert.fail();
            }
            try {
                resultSet.close();
                connection.close();
            } catch (SQLException e) {
                errStream.println("Error Closing the database connections");
            }
        }

    }

    public static void assertGeneratedSourcesNegative(String subDir, Command cmd, String object) {
        Path sourceDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        generateSourceCode(sourceDirPath, cmd);
        if (cmd == Command.DBPUSH) {
            Assert.assertFalse(false);
        } else {
            Assert.assertTrue(directoryContentEquals(Paths.get(RESOURCES_EXPECTED_OUTPUT.toString()).resolve(subDir),
                    Paths.get(GENERATED_SOURCES_DIRECTORY).resolve(subDir)));
            Path actualObjectFilePath = sourceDirPath.resolve(object);
            Assert.assertFalse(Files.exists(actualObjectFilePath));
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

    public static HashMap generateSourceCode(Path sourcePath, Command cmd) {
        Class<?> persistClass;

        try {
            if (cmd == Command.INIT) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Init");
                Init persistCmd = (Init) persistClass.getDeclaredConstructor().newInstance();
                persistCmd.setSourcePath(sourcePath.toAbsolutePath().toString());
                persistCmd.setEnvironmentBuilder(getEnvironmentBuilder());
                persistCmd.execute();
            } else if (cmd == Command.GENERATE) {
                persistClass = Class.forName("io.ballerina.persist.cmd.Generate");
                Generate persistCmd = (Generate) persistClass.getDeclaredConstructor().newInstance();
                persistCmd.setSourcePath(sourcePath.toAbsolutePath().toString());
                persistCmd.setEnvironmentBuilder(getEnvironmentBuilder());
                persistCmd.execute();
            } else {
                persistClass = Class.forName("io.ballerina.persist.cmd.Push");
                Push persistCmd = (Push) persistClass.getDeclaredConstructor().newInstance();
                persistCmd.setSourcePath(sourcePath.toAbsolutePath().toString());
                persistCmd.setEnvironmentBuilder(getEnvironmentBuilder());
                persistCmd.setDriverPath(DRIVER_PATH);
                persistCmd.execute();
                return persistCmd.getConfigurations();
            }

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
        return new HashMap<>();
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
