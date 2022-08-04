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

import io.ballerina.persist.cmd.PersistCmd;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.environment.Environment;
import io.ballerina.projects.environment.EnvironmentBuilder;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * persist tool test Utils.
 */
public class ToolingTestUtils {

    public static final String SAMPLES_DIRECTORY = "samples/";
    public static final String BAL_FILE_DIRECTORY = "generated-sources/";
    public static final String GENERATED_SOURCES_DIRECTORY = "build/generated-sources/";
    public static final String BALLERINA_TOML_FILE = "Ballerina.toml";

    public static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources", "test-src")
            .toAbsolutePath();
    private static final Path DISTRIBUTION_PATH = Paths.get("../", "target", "ballerina-runtime")
            .toAbsolutePath();
    private static final Path BALLERINA_TOML_PATH = Paths.get(RESOURCE_DIRECTORY.toString(), BALLERINA_TOML_FILE);
    
    public static void assertGeneratedSources() {

        generateSourceCode();
    }

    public static void generateSourceCode() {
        Class<?> persistCmdClass;
        try {
            persistCmdClass = Class.forName("io.ballerina.persist.cmd.PersistCmd");
            PersistCmd persistCmd = (PersistCmd) persistCmdClass.getDeclaredConstructor().newInstance();
            persistCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
