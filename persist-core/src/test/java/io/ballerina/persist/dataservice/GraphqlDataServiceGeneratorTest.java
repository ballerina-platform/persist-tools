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
 * Expected-output tests of the generated GraphQL data service, and of its checks.
 */
public class GraphqlDataServiceGeneratorTest {

    private static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice", "graphql")
            .toAbsolutePath();
    private static final Path ACTUAL = Paths.get("build", "dataservice-actual", "graphql").toAbsolutePath();

    @Test(dataProvider = "cases")
    public void testGeneratedService(String model, String basePath, boolean introspection, String expected,
                                     String expectedTypes) throws BalException, IOException {
        GraphqlDataServiceGenerator generator = generator(model, basePath, introspection, false);
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
                {"demo", "/graphql", true, "dataservice.bal", "dataservice_types.bal"},
                {"relations", "/graphql", true, "dataservice.bal", "dataservice_types.bal"},
                {"writes", "/graphql", true, "dataservice.bal", "dataservice_types.bal"},
                {"types", "/graphql", true, "dataservice.bal", "dataservice_types.bal"},
        };
    }

    // Each variant is `demo/dataservice.bal` with only the lines its option changes.
    @Test(dataProvider = "variants")
    public void testServiceVariant(String basePath, boolean introspection, boolean userOwned, List<String[]> service)
            throws BalException, IOException {
        GraphqlDataServiceGenerator generator = generator("demo", basePath, introspection, userOwned);
        Assert.assertEquals(generator.generateService(), edit("dataservice.bal", service));
    }

    @DataProvider
    public Object[][] variants() {
        return new Object[][]{
                {"/graphql", false, false,
                        List.<String[]>of(new String[]{"graphiql: {enabled: true}", "introspection: false"})},
                {"/", true, false, List.<String[]>of(new String[]{"Service /graphql on", "Service / on"})},
                {"/hr/v1", true, false,
                        List.<String[]>of(new String[]{"Service /graphql on", "Service /hr/v1 on"})},
                {"/hr-data/v1.0", true, false,
                        List.<String[]>of(new String[]{"Service /graphql on", "Service \"/hr-data/v1.0\" on"})},
                {"/graphql", true, true, List.of(
                        new String[]{"// AUTO-GENERATED FILE. DO NOT MODIFY.\n", "// AUTO-GENERATED FILE.\n"},
                        new String[]{"// It should not be modified by hand.\n", ""})},
        };
    }

    // A key field becomes a parameter of the resolvers, so every name they declare must be reserved.
    @Test
    public void testResolverVariablesAreReserved() throws IOException {
        List<Pattern> declarations = List.of(Pattern.compile("^ {8,}\\w[\\w:<>|\\[\\]?, &]* (\\w+) (?:=|;)"),
                Pattern.compile("\\b(?:from|foreach) \\S+ (\\w+) in\\b"),
                Pattern.compile("\\bon fail error (\\w+)"));
        try (Stream<Path> files = Files.walk(RESOURCES)) {
            for (Path file : files.filter(path -> path.getFileName().toString().equals("dataservice.bal")).toList()) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
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
    public void testGeneratedConfig() throws BalException, IOException {
        assertSource(generator("demo", "/graphql", true, false).generateConfig(),
                RESOURCES.resolve("dataservice_config.bal"));
    }

    @Test
    public void testDispatchByProtocol() {
        Assert.assertTrue(DataServiceGenerator.of(List.of(), DataServiceOptions.defaults("graphql"), "hr", "model",
                "h2", false, false) instanceof GraphqlDataServiceGenerator);
        Assert.assertTrue(DataServiceGenerator.of(List.of(), DataServiceOptions.defaults("http"), "hr", "model",
                "h2", false, false) instanceof HttpDataServiceGenerator);
    }

    @Test(dataProvider = "checks")
    public void testCheck(String model, String message) throws IOException, BalException {
        Path modelPath = Files.createTempDirectory("graphql-check").resolve("model.bal");
        Files.writeString(modelPath, model, StandardCharsets.UTF_8);
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> entities = DataServiceModel.resolve(modelPath, module, false).entities();
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceGenerator.validate("graphql", entities, module));
        Assert.assertEquals(error.getMessage(), message);
        // The HTTP service has none of these rules.
        DataServiceGenerator.validate("http", entities, module);
    }

    @DataProvider
    public Object[][] checks() {
        String header = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n";
        return new Object[][]{
                {header + "@dataservice:Expose {operations: [dataservice:CREATE]}\n" +
                        "type Item record {|\n    readonly int id;\n|};\n",
                        DataServiceMessages.GRAPHQL_NO_READ},
                {header + "@dataservice:Expose\ntype Equipment record {|\n    readonly int id;\n|};\n\n" +
                        "@dataservice:Expose\ntype EquipmentList record {|\n    readonly int id;\n|};\n",
                        "the entities 'Equipment' and 'EquipmentList' map to the same GraphQL field " +
                                "'equipmentList'. Rename one of them, or remove its @dataservice:Expose annotation." +
                                System.lineSeparator() + "the type 'EquipmentList' clashes with a type of the " +
                                "GraphQL data service. Rename it."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    string _note;\n|};\n",
                        "the field '_note' of the entity 'Item' starts with '_', which the GraphQL data service " +
                                "reserves. Rename the field, or mark it @dataservice:Internal."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n" +
                        "    string 'n\\-ame;\n|};\n",
                        "the name 'n-ame' of the entity 'Item' is not a valid GraphQL name. Rename it, or mark the " +
                                "field @dataservice:Internal."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int input;\n|};\n",
                        "the key field 'input' of the entity 'Item' uses a name reserved by the data service. Rename " +
                                "the field, or remove the entity's @dataservice:Expose annotation."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n|};\n\n" +
                        "type IntFilter record {|\n    readonly int id;\n|};\n",
                        "the type 'IntFilter' clashes with a type of the GraphQL data service. Rename it."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n|};\n\n" +
                        "type ItemObject record {|\n    readonly int id;\n|};\n",
                        "the type 'ItemObject' clashes with a type of the GraphQL data service. Rename it."},
        };
    }

    @Test
    public void testHiddenFieldsAreNotChecked() throws IOException, BalException {
        Path modelPath = Files.createTempDirectory("graphql-check").resolve("model.bal");
        Files.writeString(modelPath, "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n" +
                "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    @dataservice:Internal\n" +
                "    string _note;\n|};\n", StandardCharsets.UTF_8);
        Module module = BalProjectUtils.getEntities(modelPath);
        DataServiceGenerator.validate("graphql", DataServiceModel.resolve(modelPath, module, false).entities(),
                module);
    }

    private static GraphqlDataServiceGenerator generator(String model, String basePath, boolean introspection,
                                                         boolean userOwned) throws BalException {
        Path modelPath = RESOURCES.resolve(model).resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> entities = DataServiceModel.resolve(modelPath, module, false).entities();
        return new GraphqlDataServiceGenerator(entities, new DataServiceOptions("graphql",
                DataServiceOptions.validateBasePath(basePath), false, introspection), module.getModuleName(), "h2",
                userOwned);
    }

    private static String edit(String golden, List<String[]> edits) throws IOException {
        String expected = Files.readString(RESOURCES.resolve("demo").resolve(golden), StandardCharsets.UTF_8);
        for (String[] edit : edits) {
            Assert.assertTrue(expected.contains(edit[0]), "missing '" + edit[0] + "' in " + golden);
            expected = expected.replace(edit[0], edit[1]);
        }
        return expected;
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
