/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.dataservice;

import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.utils.BalProjectUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Expected-output tests of the generated HTTP data service.
 */
public class HttpDataServiceGeneratorTest {

    private static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice").toAbsolutePath();
    private static final Path ACTUAL = Paths.get("build", "dataservice-actual").toAbsolutePath();

    @Test(dataProvider = "cases")
    public void testGeneratedService(String model, String basePath, boolean auth, boolean introspection,
                                     boolean userOwned, String expected, String expectedTypes)
            throws BalException, IOException {
        Path modelPath = RESOURCES.resolve(model).resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> entities = DataServiceModel.resolve(modelPath, module).entities();
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(entities,
                new DataServiceOptions("http", DataServiceOptions.validateBasePath(basePath), auth, introspection),
                module.getModuleName(), "h2", userOwned);
        assertSource(generator.generateService(), RESOURCES.resolve(model).resolve(expected));
        if (expectedTypes != null) {
            assertSource(generator.generateTypes().orElseThrow(), RESOURCES.resolve(model).resolve(expectedTypes));
        }
    }

    @DataProvider
    public Object[][] cases() {
        return new Object[][]{
                {"employees", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"employees", "/api", false, false, false, "no_introspection.bal", null},
                {"employees", "/", false, true, false, "root_base_path.bal", null},
                {"employees", "/hr/v1", false, true, false, "custom_base_path.bal", null},
                {"employees", "/hr-data/v1.0", false, true, false, "quoted_base_path.bal", null},
                {"employees", "/api", false, true, true, "user_owned.bal", "user_owned_types.bal"},
                {"composite", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"wire", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"combinations", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"generated_key", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"rules", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"auth", "/api", true, false, false, "dataservice.bal", "dataservice_types.bal"},
        };
    }

    @Test
    public void testNoTypesWithoutReadOrCreate() throws BalException {
        Path modelPath = RESOURCES.resolve("combinations").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> updateOnly = DataServiceModel.resolve(modelPath, module).entities().stream()
                .filter(entity -> entity.name().equals("UpdateDeleteItem")).toList();
        Assert.assertTrue(new HttpDataServiceGenerator(updateOnly, DataServiceOptions.defaults("http"), "model",
                "h2", false).generateTypes().isEmpty());
    }

    @Test
    public void testGeneratedConfig() throws BalException, IOException {
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(List.of(),
                DataServiceOptions.defaults("http"), "model", "h2", false);
        assertSource(generator.generateConfig(), RESOURCES.resolve("dataservice_config.bal"));
        HttpDataServiceGenerator userOwned = new HttpDataServiceGenerator(List.of(),
                DataServiceOptions.defaults("http"), "model", "h2", true);
        assertSource(userOwned.generateConfig(), RESOURCES.resolve("dataservice_config_user_owned.bal"));
        HttpDataServiceGenerator withAuth = new HttpDataServiceGenerator(List.of(),
                new DataServiceOptions("http", "/api", true, false), "model", "h2", false);
        assertSource(withAuth.generateConfig(), RESOURCES.resolve("dataservice_config_auth.bal"));
    }

    // The actual output is also written under build/ so that a changed expectation can be reviewed with a diff.
    private static void assertSource(String actual, Path expected) throws IOException {
        Path actualPath = ACTUAL.resolve(RESOURCES.relativize(expected));
        Files.createDirectories(actualPath.getParent());
        Files.writeString(actualPath, actual, StandardCharsets.UTF_8);
        Assert.assertTrue(Files.exists(expected), "missing expected output " + expected);
        Assert.assertEquals(actual, Files.readString(expected, StandardCharsets.UTF_8), expected.toString());
    }
}
