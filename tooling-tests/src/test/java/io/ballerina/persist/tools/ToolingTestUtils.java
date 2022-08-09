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
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.environment.Environment;
import io.ballerina.projects.environment.EnvironmentBuilder;
import org.testng.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * persist tool test Utils.
 */
public class ToolingTestUtils {

    public static final String SAMPLES_DIRECTORY = "samples/";

    public static final String REFERENCE_DIRECTORY = "reference/";
    public static final String GENERATED_SOURCES_DIRECTORY = "build/generated-sources/";
    public static final Path RESOURCE_PATH = Paths.get("src", "test", "resources", "test-src")
            .toAbsolutePath();
    private static final Path DISTRIBUTION_PATH = Paths.get("../", "target", "ballerina-runtime")
            .toAbsolutePath();
    private static ProjectEnvironmentBuilder getEnvironmentBuilder() {
        Environment environment = EnvironmentBuilder.getBuilder().setBallerinaHome(DISTRIBUTION_PATH).build();
        return ProjectEnvironmentBuilder.getBuilder(environment);
    }

    public static void assertGeneratedSources(String subDir, String configFile) {
        Path sourceDirPath = Paths.get(RESOURCE_PATH.toString(), SAMPLES_DIRECTORY, subDir);
        Path outPath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        Path referenceFilePath = Paths.get(RESOURCE_PATH.toString(), REFERENCE_DIRECTORY, subDir);
        Path actualConfigFilePath = outPath.resolve(configFile);
        Path referenceConfigFilePath = referenceFilePath.resolve(configFile);
        generateSourceCode(sourceDirPath.toAbsolutePath(), outPath.toAbsolutePath());
        Assert.assertTrue(Files.exists(actualConfigFilePath));
        Assert.assertEquals(readContent(actualConfigFilePath), readContent(referenceConfigFilePath));
        try {
            Files.deleteIfExists(actualConfigFilePath);
        } catch (IOException e) {
            Assert.fail("Failed to delete Config.toml file", e);
        }
    }

    public static void assertGeneratedSourcesNegative(String subDir, String configFile) {
        Path sourceDirPath = Paths.get(RESOURCE_PATH.toString(), SAMPLES_DIRECTORY, subDir);
        Path outPath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        Path actualConfigFilePath = outPath.resolve(configFile);
        generateSourceCode(sourceDirPath, outPath);
        Assert.assertFalse(Files.exists(actualConfigFilePath));
    }

    public static void generateSourceCode(Path sourcePath, Path generatePath) {
        Class<?> persistInitClass;
        try {
            persistInitClass = Class.forName("io.ballerina.persist.cmd.Init");
            Init persistCmdInit = (Init) persistInitClass.getDeclaredConstructor().newInstance();
            persistCmdInit.setSourcePath(sourcePath.toAbsolutePath().toString());
            persistCmdInit.setGenerationPath(generatePath.toAbsolutePath().toString());
            persistCmdInit.setEnviorenmentBuilder(getEnvironmentBuilder());
            persistCmdInit.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public static String readContent(Path filePath) {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            return "";
        }
        return content.replaceAll("\n", "");
    }
}
