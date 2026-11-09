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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
        List<ExposedEntity> entities = DataServiceModel.resolve(modelPath, module, false).entities();
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(entities,
                new DataServiceOptions("http", DataServiceOptions.validateBasePath(basePath), auth, introspection),
                "hr", module.getModuleName(), "h2", userOwned);
        // The types are generated first, so that a state left by one call would show in the next.
        String types = generator.generateTypes().orElseThrow();
        String service = generator.generateService();
        writeActual(service, RESOURCES.resolve(model).resolve(expected));
        if (expectedTypes != null) {
            assertSource(types, RESOURCES.resolve(model).resolve(expectedTypes));
        }
        assertSource(service, RESOURCES.resolve(model).resolve(expected));
    }

    @DataProvider
    public Object[][] cases() {
        return new Object[][]{
                {"employees", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"composite", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"wire", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"combinations", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"generated_key", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"rules", "/api", false, true, false, "dataservice.bal", "dataservice_types.bal"},
                {"auth", "/api", true, false, false, "dataservice.bal", "dataservice_types.bal"},
                {"auth", "/api", true, true, false, "introspection.bal", null},
        };
    }

    // Each variant is `employees/dataservice.bal` with only the lines its option changes.
    @Test(dataProvider = "variants")
    public void testServiceVariant(String basePath, boolean introspection, boolean userOwned, List<String[]> service,
                                   List<String[]> types) throws BalException, IOException {
        Path modelPath = RESOURCES.resolve("employees").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(
                DataServiceModel.resolve(modelPath, module, false).entities(),
                new DataServiceOptions("http", DataServiceOptions.validateBasePath(basePath), false, introspection),
                "hr", module.getModuleName(), "h2", userOwned);
        Assert.assertEquals(generator.generateService(), edit("dataservice.bal", service));
        Assert.assertEquals(generator.generateTypes().orElseThrow(), edit("dataservice_types.bal", types));
    }

    @DataProvider
    public Object[][] variants() {
        List<String[]> header = List.of(
                new String[]{"// AUTO-GENERATED FILE. DO NOT MODIFY.\n", "// AUTO-GENERATED FILE.\n"},
                new String[]{"// It should not be modified by hand.\n", ""});
        return new Object[][]{
                {"/api", false, false, List.of(new String[]{"import ballerina/openapi;\n", ""},
                        new String[]{"@openapi:ServiceInfo {embed: true, title: \"hr\"}\n", ""}), List.of()},
                {"/", true, false, basePath("/", ""), List.of()},
                {"/hr/v1", true, false, basePath("/hr/v1", "/hr/v1"), List.of()},
                {"/hr-data/v1.0", true, false, basePath("\"/hr-data/v1.0\"", "/hr-data/v1.0"), List.of()},
                {"/api", true, true, header, header},
        };
    }

    private static List<String[]> basePath(String servicePath, String locationPrefix) {
        return List.of(new String[]{"InterceptableService /api on", "InterceptableService " + servicePath + " on"},
                new String[]{"toLocation(\"/api/employees\"", "toLocation(\"" + locationPrefix + "/employees\""});
    }

    private static String edit(String golden, List<String[]> edits) throws IOException {
        String expected = Files.readString(RESOURCES.resolve("employees").resolve(golden), StandardCharsets.UTF_8);
        for (String[] edit : edits) {
            Assert.assertTrue(expected.contains(edit[0]), "missing '" + edit[0] + "' in " + golden);
            expected = expected.replace(edit[0], edit[1]);
        }
        return expected;
    }

    // A key field becomes a path parameter of the resources, so every name they declare must be reserved.
    @Test
    public void testResourceVariablesAreReserved() throws IOException {
        List<Pattern> declarations = List.of(Pattern.compile("^ {8,}\\w[\\w:<>|\\[\\]?, &]* (\\w+) (?:=|;)"),
                Pattern.compile("\\b(?:from|foreach) \\S+ (\\w+) in\\b"),
                Pattern.compile("\\bon fail error (\\w+)"));
        try (Stream<Path> files = Files.walk(RESOURCES)) {
            for (Path file : files.filter(path -> path.getFileName().toString().equals("dataservice.bal")
                    || path.getFileName().toString().equals("introspection.bal")).toList()) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    if (!line.startsWith("        ")) {
                        continue;
                    }
                    for (Pattern declaration : declarations) {
                        Matcher matcher = declaration.matcher(line);
                        while (matcher.find()) {
                            Assert.assertTrue(DataServiceModel.RESERVED_KEY_NAMES.contains(matcher.group(1)),
                                    matcher.group(1) + " in " + file + ": " + line);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testNoTypesWithoutReadOrCreate() throws BalException {
        Path modelPath = RESOURCES.resolve("combinations").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> updateOnly = DataServiceModel.resolve(modelPath, module, false).entities().stream()
                .filter(entity -> entity.name().equals("UpdateDeleteItem")).toList();
        Assert.assertTrue(new HttpDataServiceGenerator(updateOnly, DataServiceOptions.defaults("http"), "hr", "model",
                "h2", false).generateTypes().isEmpty());
    }

    @Test
    public void testWriteDeletesStaleTypes() throws BalException, IOException {
        Path modelPath = RESOURCES.resolve("combinations").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> updateOnly = DataServiceModel.resolve(modelPath, module, false).entities().stream()
                .filter(entity -> entity.name().equals("UpdateDeleteItem")).toList();
        Path dir = Files.createTempDirectory("dataservice-write");
        Files.writeString(dir.resolve(HttpDataServiceGenerator.TYPES_FILE), "// stale");
        new HttpDataServiceGenerator(updateOnly, DataServiceOptions.defaults("http"), "hr", "model", "h2", false)
                .write(dir);
        Assert.assertTrue(Files.exists(dir.resolve(HttpDataServiceGenerator.SERVICE_FILE)));
        Assert.assertTrue(Files.exists(dir.resolve(HttpDataServiceGenerator.CONFIG_FILE)));
        Assert.assertFalse(Files.exists(dir.resolve(HttpDataServiceGenerator.TYPES_FILE)));
    }

    @Test
    public void testGeneratedConfig() throws BalException, IOException {
        HttpDataServiceGenerator generator = new HttpDataServiceGenerator(List.of(),
                DataServiceOptions.defaults("http"), "hr", "model", "h2", false);
        assertSource(generator.generateConfig(), RESOURCES.resolve("dataservice_config.bal"));
        HttpDataServiceGenerator userOwned = new HttpDataServiceGenerator(List.of(),
                DataServiceOptions.defaults("http"), "hr", "model", "h2", true);
        assertSource(userOwned.generateConfig(), RESOURCES.resolve("dataservice_config_user_owned.bal"));
        HttpDataServiceGenerator withAuth = new HttpDataServiceGenerator(List.of(),
                new DataServiceOptions("http", "/api", true, false), "hr", "model", "h2", false);
        assertSource(withAuth.generateConfig(), RESOURCES.resolve("dataservice_config_auth.bal"));
    }

    @Test
    public void testFormatErrorNamesTheLine() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> HttpDataServiceGenerator.format("function f() {\n    int x = ;\n}\n"));
        Assert.assertTrue(error.getMessage().contains("at line 2: int x = ;"), error.getMessage());
    }

    // The actual output is also written under build/ so that a changed expectation can be reviewed with a diff.
    private static void assertSource(String actual, Path expected) throws IOException {
        writeActual(actual, expected);
        Assert.assertTrue(Files.exists(expected), "missing expected output " + expected);
        Assert.assertEquals(actual, Files.readString(expected, StandardCharsets.UTF_8), expected.toString());
    }

    private static void writeActual(String actual, Path expected) throws IOException {
        Path actualPath = ACTUAL.resolve(RESOURCES.relativize(expected));
        Files.createDirectories(actualPath.getParent());
        Files.writeString(actualPath, actual, StandardCharsets.UTF_8);
    }
}
