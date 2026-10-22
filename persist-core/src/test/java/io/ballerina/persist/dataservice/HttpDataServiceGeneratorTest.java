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
    public void testGeneratedService(String model, String datastore, String basePath, boolean userOwned,
                                     String expected) throws BalException, IOException {
        Path modelPath = RESOURCES.resolve(model).resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> entities = DataServiceModel.resolve(module, ExposeAnnotations.read(modelPath),
                datastore);
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(entities,
                new DataServiceOptions("http", DataServiceOptions.validateBasePath(basePath)),
                module.getModuleName(), userOwned);
        assertSource(generator.generateService(), RESOURCES.resolve(model).resolve(expected));
    }

    @DataProvider
    public Object[][] cases() {
        return new Object[][]{
                {"employees", "inmemory", "/api", false, "persist_dataservice.bal"},
                {"employees", "inmemory", "/", false, "root_base_path.bal"},
                {"employees", "inmemory", "/hr/v1", false, "custom_base_path.bal"},
                {"employees", "inmemory", "/hr-data/v1.0", false, "quoted_base_path.bal"},
                {"employees", "inmemory", "/api", true, "user_owned.bal"},
                {"composite", "inmemory", "/api", false, "persist_dataservice.bal"},
                {"wire", "inmemory", "/api", false, "persist_dataservice.bal"},
                {"combinations", "inmemory", "/api", false, "persist_dataservice.bal"},
                {"generated_key", "h2", "/api", false, "persist_dataservice.bal"},
        };
    }

    @Test
    public void testGeneratedConfig() throws BalException, IOException {
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(List.of(),
                DataServiceOptions.defaults("http"), "model", false);
        assertSource(generator.generateConfig(), RESOURCES.resolve("persist_dataservice_config.bal"));
        HttpDataServiceGenerator userOwned = new HttpDataServiceGenerator(List.of(),
                DataServiceOptions.defaults("http"), "model", true);
        assertSource(userOwned.generateConfig(), RESOURCES.resolve("persist_dataservice_config_user_owned.bal"));
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
