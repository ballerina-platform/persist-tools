/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * persist tool in `bal build` command tests.
 */
public class BuildCodeGeneratorTest {
    public static final Path TARGET_DIR = Paths.get(System.getProperty("user.dir"), "build");
    public static final Path TEST_DISTRIBUTION_PATH = TARGET_DIR.resolve("ballerina-distribution");
    private String persistSqlVersion;

    @BeforeClass
    public void findLatestPersistVersion() {
        Path versionPropertiesFile = Paths.get("../", "persist-cli", "src", "main", "resources",
                "version.properties").toAbsolutePath();
        try (InputStream inputStream = Files.newInputStream(versionPropertiesFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            persistSqlVersion = properties.get("persistSqlVersion").toString();
        } catch (IOException e) {
            // ignore
        }
    }

    @Test(enabled = false)
    public void testBuildWithMysql() throws IOException, InterruptedException {
        updateOutputBallerinaToml("tool_test_build_1");
        String log = "Persist client and entity types generated successfully in the persist_build_1 directory.";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_1");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidTargetModule() throws IOException, InterruptedException {
        String log = "error: build tool execution contains errors";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_2");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidCharachtersInTargetModule() throws IOException, InterruptedException {
        String log = "error: build tool execution contains errors";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_3");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidLengthOfTargetModule() throws IOException, InterruptedException {
        String log = "error: build tool execution contains errors";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_4");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidDataSource() throws IOException, InterruptedException {
        String log = "error: build tool execution contains errors";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_5");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithoutEntities() throws IOException, InterruptedException {
        String log = "error: build tool execution contains errors";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_6");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithExistingDependency() throws IOException, InterruptedException {
        String log = "error: build tool execution contains errors";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_7");
        assertContainLogs(log, project);
    }

    @Test(enabled = true, description = "Test build with H2 data store")
    public void testBuildWithH2DataStore() throws IOException, InterruptedException {
        updateOutputBallerinaToml("tool_test_build_8");
        String log = "Persist client and entity types generated successfully in the persist_build_8 directory.";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_8");
        assertContainLogs(log, project);
    }

    private void updateOutputBallerinaToml(String fileName) {
        String tomlFileName = "Ballerina.toml";
        Path filePath = Paths.get("src", "test", "resources", "test-src", "input", fileName, tomlFileName);
        if (filePath.endsWith(tomlFileName)) {
            try {
                String content = Files.readString(filePath);
                String dataStore = "persist.sql";
                String version = persistSqlVersion;
                content = content.replaceAll(
                        "artifactId\\s=\\s\"" + dataStore + "-native\"\nversion\\s=\\s\\\"\\d+(\\.\\d+)+" +
                                "(-SNAPSHOT)?\\\"",  "artifactId = \"" + dataStore +
                                "-native\"\nversion = \"" + version + "\"");
                Files.writeString(filePath, content);
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private String collectLogOutput(Path project) throws IOException, InterruptedException {
        List<String> buildArgs = new LinkedList<>();
        Process process = executeBuild(TEST_DISTRIBUTION_PATH.toString(), project, buildArgs);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                StandardCharsets.UTF_8))) {
            Stream<String> logLines = br.lines();
            String generatedLog = logLines.collect(Collectors.joining(System.lineSeparator()));
            logLines.close();
            return generatedLog;
        }
    }

    private void assertContainLogs(String log, Path project) throws IOException, InterruptedException {
        String generatedLog = collectLogOutput(project);
        Assert.assertTrue(generatedLog.contains(log));
    }

    public static Process executeBuild(String distributionName, Path sourceDirectory,
                                       List<String> args) throws IOException, InterruptedException {
        args.add(0, "build");
        Process process = getProcessBuilderResults(distributionName, sourceDirectory, args);
        process.waitFor();
        return process;
    }

    public static Process getProcessBuilderResults(String distributionName, Path sourceDirectory, List<String> args)
            throws IOException {
        String balFile = "bal";

        if (System.getProperty("os.name").startsWith("Windows")) {
            balFile = "bal.bat";
        }
        args.add(0, TEST_DISTRIBUTION_PATH.resolve(distributionName).resolve("bin").resolve(balFile).toString());
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(sourceDirectory.toFile());
        return pb.start();
    }
}
