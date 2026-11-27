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
    public void testGeneratedService(String model, boolean auth, String expected, String expectedTypes)
            throws BalException, IOException {
        GraphqlDataServiceGenerator generator = generator(model, "/graphql", auth, true, false);
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
                {"demo", false, "dataservice.bal", "dataservice_types.bal"},
                {"relations", false, "dataservice.bal", "dataservice_types.bal"},
                {"writes", false, "dataservice.bal", "dataservice_types.bal"},
                {"types", false, "dataservice.bal", "dataservice_types.bal"},
                {"auth", true, "dataservice.bal", "dataservice_types.bal"},
                {"constraints", false, "dataservice.bal", "dataservice_types.bal"},
        };
    }

    @Test(description = "An eagerly loading client returns arrays, which every read takes in place of a stream")
    public void testEagerLoading() throws BalException, IOException {
        Path modelPath = RESOURCES.resolve("demo").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        GraphqlDataServiceGenerator generator = new GraphqlDataServiceGenerator(
                DataServiceModel.resolve(modelPath, module, false).entities(),
                new DataServiceOptions("graphql", DataServiceOptions.validateBasePath("/graphql"), false, true),
                module.getModuleName(), "h2", false, true);
        assertSource(generator.generateService(), RESOURCES.resolve("demo").resolve("dataservice_eager.bal"));
        assertSource(generator.generateTypes().orElseThrow(),
                RESOURCES.resolve("demo").resolve("dataservice_types_eager.bal"));
    }

    // Each variant is `demo/dataservice.bal` with only the lines its option changes.
    @Test(dataProvider = "variants")
    public void testServiceVariant(String basePath, boolean introspection, boolean userOwned, List<String[]> service)
            throws BalException, IOException {
        GraphqlDataServiceGenerator generator = generator("demo", basePath, false, introspection, userOwned);
        Assert.assertEquals(lf(generator.generateService()), edit("dataservice.bal", service));
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
        assertSource(generator("demo", "/graphql", false, true, false).generateConfig(),
                RESOURCES.resolve("dataservice_config.bal"));
        assertSource(generator("demo", "/graphql", true, true, false).generateConfig(),
                RESOURCES.resolve("dataservice_config_auth.bal"));
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
        // The HTTP service has none of these rules, but generates an `<Entity>List` type too.
        if (!message.contains("EquipmentList")) {
            DataServiceGenerator.validate("http", entities, module);
        }
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
                                System.lineSeparator() + "the entity 'EquipmentList' has the name of a type that the " +
                                "data service generates for the entity 'Equipment'. Rename one of them, or remove " +
                                "the @dataservice:Expose annotation of 'Equipment'."},
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
                        "the entity 'ItemObject' has the name of a type that the data service generates for the " +
                                "entity 'Item'. Rename one of them, or remove the @dataservice:Expose annotation of " +
                                "'Item'."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n|};\n\n" +
                        "enum ItemSort {\n    A\n}\n",
                        "the enum 'ItemSort' has the name of a type that the data service generates for the entity " +
                                "'Item'. Rename one of them, or remove the @dataservice:Expose annotation of 'Item'."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    Status status;\n" +
                        "|};\n\nenum Status {\n    A\n}\n\ntype StatusFilter record {|\n    readonly int id;\n|};\n",
                        "the type 'StatusFilter' clashes with a type of the GraphQL data service. Rename it."},
                {header + "@dataservice:Expose\ntype Owner record {|\n    readonly int id;\n    Item[] items;\n" +
                        "|};\n\n" +
                        "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    Owner __owner;\n|};\n",
                        "the field '__ownerId' of the entity 'Item' starts with '_', which the GraphQL data " +
                                "service reserves. Rename the field, or mark it @dataservice:Internal." +
                                System.lineSeparator() + "the field '__owner' of the entity 'Item' starts with '_', " +
                                "which the GraphQL data service reserves. Rename the field, or mark it " +
                                "@dataservice:Internal."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    Status? status;\n" +
                        "|};\n\n@dataservice:Expose\ntype StatusNullable record {|\n    readonly int id;\n|};\n\n" +
                        "enum Status {\n    A\n}\n",
                        "the filter 'StatusNullableFilter' of the enum 'Status' has the name of a type that the data " +
                                "service generates for the entity 'StatusNullable'. Rename one of them."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    State? a;\n" +
                        "    StateNullable b;\n|};\n\nenum State {\n    A\n}\n\nenum StateNullable {\n    B\n}\n",
                        "the filter 'StateNullableFilter' is generated for both the enum 'State' and the enum " +
                                "'StateNullable'. Rename one of them."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n" +
                        "    StringNullable a;\n|};\n\nenum StringNullable {\n    A\n}\n",
                        "the filter 'StringNullableFilter' of the enum 'StringNullable' has the name of a type of " +
                                "ballerina/dataservice.graphql. Rename the enum."},
                {header + "@dataservice:Expose\ntype Int record {|\n    readonly int id;\n|};\n",
                        "the data service generates the type 'IntFilter' for the entity 'Int', and " +
                                "ballerina/dataservice.graphql has a type of that name. Rename the entity, or remove " +
                                "the @dataservice:Expose annotation of 'Int'."},
                {header + "@dataservice:Expose\ntype Item record {|\n    readonly int id;\n    string? note;\n" +
                        "    string unset;\n|};\n",
                        "the field 'unset' of the entity 'Item' clashes with the field of its update input that " +
                                "names the fields to set to null. Rename the field."},
        };
    }

    @Test(description = "Two to-one relations whose names concatenate to one loader get different loaders")
    public void testLoaderNamesAreUnique() throws BalException, IOException {
        String service = lf(fromModel("@dataservice:Expose\ntype Owner record {|\n    readonly int id;\n" +
                "    Sales[] sales;\n    SalesLead[] leads;\n|};\n\n@dataservice:Expose\ntype Sales record {|\n" +
                "    readonly int id;\n    Owner leadOwner;\n|};\n\n@dataservice:Expose\ntype SalesLead record {|\n" +
                "    readonly int id;\n    Owner owner;\n|};\n", false).generateService());
        Assert.assertTrue(service.contains("ctx.registerDataLoader(\"Sales.leadOwner\",\n" +
                "            new dataloader:DefaultDataLoader(dataserviceLoadSalesLeadOwner));"), service);
        Assert.assertTrue(service.contains("ctx.registerDataLoader(\"SalesLead.owner\",\n" +
                "            new dataloader:DefaultDataLoader(dataserviceLoadSalesLeadOwner2));"), service);
        Assert.assertTrue(service.contains("isolated function dataserviceLoadSalesLeadOwner2("), service);
    }

    @Test(description = "The local holding a converted field's string does not take the name of another field")
    public void testConversionLocalsAreUnique() throws BalException, IOException {
        String types = fromModel("import ballerina/time;\n\n@dataservice:Expose\ntype Item record {|\n" +
                "    readonly int id;\n    time:Date? startedOn;\n    time:Date startedOnText;\n|};\n", false)
                .generateTypes().orElseThrow();
        // Once in the insert converter and once in the update converter.
        Assert.assertEquals(types.split("string\\? _startedOnText = value\\.startedOn;", -1).length, 3, types);
    }

    @Test(description = "A scope reaches the generated code as declared")
    public void testScopesAreNotUnescaped() throws BalException, IOException {
        String service = fromModel("@dataservice:Expose {scopes: {read: [\"'admin\", \"a\\\"b\\\\c\"]}}\n" +
                "type Item record {|\n    readonly int id;\n|};\n", true).generateService();
        Assert.assertTrue(service.contains("[\"'admin\", \"a\\\"b\\\\c\"]"), service);
    }

    @Test(description = "An entity without a field that a patch can change has no patch")
    public void testKeyOnlyEntityHasNoPatch() throws BalException, IOException {
        GraphqlDataServiceGenerator generator = fromModel("@dataservice:Expose\ntype Item record {|\n" +
                "    readonly int id;\n|};\n", false);
        Assert.assertFalse(generator.generateService().contains("updateItem"));
        Assert.assertFalse(generator.generateTypes().orElseThrow().contains("ItemUpdate"));
    }

    @Test(description = "An entity without a field that a client gives has no create")
    public void testGeneratedKeyOnlyEntityHasNoCreate() throws BalException, IOException {
        GraphqlDataServiceGenerator generator = fromModel("import ballerinax/persist.sql;\n\n" +
                "@dataservice:Expose\ntype Item record {|\n    @sql:Generated\n    readonly int id;\n|};\n", false);
        Assert.assertFalse(generator.generateService().contains("createItem"));
        Assert.assertFalse(generator.generateTypes().orElseThrow().contains("ItemInsertInput"));
    }

    @Test(description = "Two to-one relations whose names differ only in case get different prefetch methods")
    public void testPrefetchNamesAreUnique() throws BalException, IOException {
        String types = fromModel("@dataservice:Expose\ntype Owner record {|\n    readonly int id;\n" +
                "    Item[] items;\n|};\n\n@dataservice:Expose\ntype Boss record {|\n    readonly int id;\n" +
                "    Item[] items;\n|};\n\n@dataservice:Expose\ntype Item record {|\n    readonly int id;\n" +
                "    Owner owner;\n    Boss Owner;\n|};\n", false).generateTypes().orElseThrow();
        Assert.assertTrue(types.contains("@graphql:ResourceConfig {prefetchMethodName: \"prefetchOwner\"}"), types);
        Assert.assertTrue(types.contains("@graphql:ResourceConfig {prefetchMethodName: \"prefetchOwner2\"}"), types);
    }

    private static GraphqlDataServiceGenerator fromModel(String model, boolean auth)
            throws IOException, BalException {
        Path modelPath = Files.createTempDirectory("graphql-model").resolve("model.bal");
        String imports = model.startsWith("import ") ? "" : "\n";
        Files.writeString(modelPath, "import ballerina/dataservice;\nimport ballerina/persist as _;\n" + imports +
                model, StandardCharsets.UTF_8);
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> entities = DataServiceModel.resolve(modelPath, module, auth).entities();
        DataServiceGenerator.validate("graphql", entities, module);
        return new GraphqlDataServiceGenerator(entities, new DataServiceOptions("graphql",
                DataServiceOptions.validateBasePath("/graphql"), auth, true), module.getModuleName(), "h2", false);
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

    private static GraphqlDataServiceGenerator generator(String model, String basePath, boolean auth,
                                                         boolean introspection, boolean userOwned)
            throws BalException {
        Path modelPath = RESOURCES.resolve(model).resolve("model.bal");
        Module module = BalProjectUtils.getEntities(modelPath);
        List<ExposedEntity> entities = DataServiceModel.resolve(modelPath, module, auth).entities();
        return new GraphqlDataServiceGenerator(entities, new DataServiceOptions("graphql",
                DataServiceOptions.validateBasePath(basePath), auth, introspection), module.getModuleName(), "h2",
                userOwned);
    }

    private static String lf(String source) {
        // The generator writes the platform's line separator, and Git may check the golden files out with CRLF.
        return source.replace("\r\n", "\n");
    }

    private static String edit(String golden, List<String[]> edits) throws IOException {
        String expected = lf(Files.readString(RESOURCES.resolve("demo").resolve(golden), StandardCharsets.UTF_8));
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
