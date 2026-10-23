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

package io.ballerina.persist.tools;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * End-to-end tests of data service generation, run through the test distribution as `bal` would be run.
 */
public class ToolingDataServiceTest {

    private static final Path SOURCES = Paths.get("build", "generated-sources").toAbsolutePath();
    private static final Path DISTRIBUTION = Paths.get("build", "ballerina-distribution").toAbsolutePath();
    private static final String SERVICE = "dataservice.bal";
    private static final String TYPES = "dataservice_types.bal";
    private static final String CONFIG = "dataservice_config.bal";

    private static final String MODEL = """
            import ballerina/dataservice;
            import ballerina/persist as _;
            import ballerina/time;

            @dataservice:Expose
            type Employee record {|
                readonly int id;
                string name;
                time:Date hiredOn;
            |};

            @dataservice:Expose {operations: [dataservice:READ]}
            type Assignment record {|
                readonly string projectCode;
                readonly int seq;
                decimal hours;
            |};

            type AuditLog record {|
                readonly int id;
                string entry;
            |};
            """;
    private static final String PLAIN_MODEL = """
            import ballerina/persist as _;

            type Employee record {|
                readonly int id;
                string name;
            |};

            type Department record {|
                readonly int id;
                string name;
            |};
            """;

    private record Result(int exitCode, String output) {
    }

    // Behaviour

    @Test(description = "Every operation and error of the HTTP data service against the in-memory datastore")
    public void testBehaviourInMemory() throws IOException, InterruptedException {
        Result result = bal(SOURCES.resolve("tool_test_dataservice_inmemory"), Map.of(), "test", "--offline");
        assertTestsPassed(result, 28);
    }

    // H2 reads a time:Utc back from a TIMESTAMP column as local time, so the service runs in UTC.
    @Test(description = "Every operation and error of the HTTP data service against the H2 datastore")
    public void testBehaviourH2() throws IOException, InterruptedException {
        Path project = SOURCES.resolve("tool_test_dataservice_h2");
        assertSucceeded(bal(project, Map.of(), "build", "--offline"));
        Path init = project.resolve("db").resolve("init.sql");
        Files.createDirectories(init.getParent());
        // H2 runs the INIT script on every new connection, so the schema must not drop tables.
        String schema = Files.readAllLines(project.resolve("generated").resolve("script.sql")).stream()
                .filter(line -> !line.startsWith("DROP"))
                .map(line -> line.replace("CREATE TABLE ", "CREATE TABLE IF NOT EXISTS "))
                .collect(Collectors.joining(System.lineSeparator()));
        Files.writeString(init, schema);
        Files.writeString(project.resolve("tests").resolve("Config.toml"), String.format("""
                dataservicePort = 9591

                [dataservice_test.dataservice_h2]
                url = "jdbc:h2:mem:dstest;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM '%s'"
                user = "sa"
                password = ""
                """, init.toString().replace('\\', '/')));
        assertTestsPassed(bal(project, Map.of("TZ", "UTC"), "test", "--offline"), 32);
    }

    // Generation from bal build

    @Test(description = "The service is generated next to the client, with the default base path")
    public void testBuildGeneratesTheService() throws IOException, InterruptedException {
        Path project = project("ds_build", entry("inmemory", "options.dataservice.protocol = \"http\""), MODEL);
        assertSucceeded(build(project));
        String service = Files.readString(project.resolve("generated").resolve(SERVICE));
        Assert.assertTrue(service.startsWith("// AUTO-GENERATED FILE. DO NOT MODIFY."), service);
        Assert.assertTrue(service.contains("service /api on dataserviceListener {"), service);
        Assert.assertTrue(service.contains("resource function get assignments/[string projectCode]/[int seq]()"));
        Assert.assertFalse(service.contains("auditlogs"), service);
        Assert.assertTrue(Files.exists(project.resolve("generated").resolve(CONFIG)));
        String types = Files.readString(project.resolve("generated").resolve(TYPES));
        Assert.assertTrue(types.contains("public type EmployeePayload record {|"), types);
        Assert.assertTrue(types.contains("public type AssignmentList record {|"), types);
        Assert.assertFalse(service.contains("public type "), service);
    }

    @Test(description = "Changing the base path regenerates the service, and removing the options deletes it")
    public void testRegenerationAndCleanup() throws IOException, InterruptedException {
        Path project = project("ds_regenerate", entry("inmemory", "options.dataservice.protocol = \"http\""),
                MODEL);
        assertSucceeded(build(project));
        Path service = project.resolve("generated").resolve(SERVICE);
        Assert.assertTrue(Files.readString(service).contains("service /api on"));

        writeToml(project, entry("inmemory", "options.dataservice.protocol = \"http\"\n" +
                "options.dataservice.basePath = \"/hr/v2/\""));
        assertSucceeded(build(project));
        Assert.assertTrue(Files.readString(service).contains("service /hr/v2 on"));
        Assert.assertTrue(Files.readString(service).contains("dsHttp:location(\"/hr/v2/employees\""));

        writeToml(project, entry("inmemory", ""));
        Result result = build(project);
        Assert.assertFalse(Files.exists(service), result.output());
        Assert.assertFalse(Files.exists(project.resolve("generated").resolve(CONFIG)));
        Assert.assertFalse(Files.exists(project.resolve("generated").resolve(TYPES)));
        Assert.assertTrue(Files.exists(project.resolve("generated").resolve("persist_client.bal")));
        Assert.assertTrue(result.output().contains("WARNING: the model has @dataservice:Expose annotations, but no " +
                "data service is generated. Set 'options.dataservice.protocol' in the [[tool.persist]] entry to " +
                "generate one."), result.output());
    }

    @Test(description = "Without options.dataservice and annotations, persist behaves as it does today")
    public void testNoDataService() throws IOException, InterruptedException {
        Path project = project("ds_none", entry("inmemory", ""), PLAIN_MODEL);
        Result result = build(project);
        assertSucceeded(result);
        Assert.assertFalse(result.output().contains("@dataservice:Expose"), result.output());
        Assert.assertFalse(Files.exists(project.resolve("generated").resolve(SERVICE)));
    }

    @Test(dataProvider = "datastores", description = "The generated package compiles on every persist datastore")
    public void testCompilesOnEveryDatastore(String datastore) throws IOException, InterruptedException {
        Path project = project("ds_datastore_" + datastore,
                entry(datastore, "options.dataservice.protocol = \"http\""), MODEL);
        assertSucceeded(build(project));
        Assert.assertTrue(Files.exists(project.resolve("generated").resolve(SERVICE)));
    }

    @DataProvider
    public Object[][] datastores() {
        return new Object[][]{{"inmemory"}, {"h2"}, {"mysql"}, {"mssql"}, {"postgresql"}, {"googlesheets"},
                {"redis"}};
    }

    // Messages from bal build

    @Test(dataProvider = "buildErrors", description = "Each Ballerina.toml and model error fails the build")
    public void testBuildError(String name, String options, String filePath, String model, String message)
            throws IOException, InterruptedException {
        String toml = entry("inmemory", options);
        if (filePath != null) {
            toml = toml.replace("filePath = \"persist/model.bal\"", "filePath = \"" + filePath + "\"");
        }
        Path project = project("ds_error_" + name, toml, model);
        if (filePath != null) {
            Files.createDirectories(project.resolve(filePath).getParent());
            Files.move(project.resolve("persist").resolve("model.bal"), project.resolve(filePath));
        }
        Result result = build(project);
        Assert.assertNotEquals(result.exitCode(), 0, result.output());
        Assert.assertTrue(result.output().contains(message), "expected '" + message + "' in:\n" + result.output());
        Assert.assertFalse(Files.exists(project.resolve("generated").resolve(SERVICE)), result.output());
    }

    @DataProvider
    public Object[][] buildErrors() {
        String http = "options.dataservice.protocol = \"http\"";
        return new Object[][]{
                {"protocol_missing", "options.dataservice.basePath = \"/api\"", null, MODEL,
                        "'options.dataservice.protocol' is required. Supported values: http."},
                {"graphql", "options.dataservice.protocol = \"graphql\"", null, MODEL,
                        "the protocol 'graphql' is not supported yet. Supported values: http."},
                {"mcp", "options.dataservice.protocol = \"mcp\"", null, MODEL,
                        "the protocol 'mcp' is not supported yet. Supported values: http."},
                {"odata", "options.dataservice.protocol = \"odata\"", null, MODEL,
                        "the protocol 'odata' is not supported yet. Supported values: http."},
                {"soap", "options.dataservice.protocol = \"soap\"", null, MODEL,
                        "the protocol 'soap' is not supported yet. Supported values: http."},
                {"rest", "options.dataservice.protocol = \"rest\"", null, MODEL,
                        "invalid 'options.dataservice.protocol' 'rest'. Supported values: http."},
                {"base_path_slash", http + "\noptions.dataservice.basePath = \"api\"", null, MODEL,
                        "invalid 'options.dataservice.basePath' 'api': the base path must start with '/'."},
                {"base_path_segment", http + "\noptions.dataservice.basePath = \"/api//v1\"", null, MODEL,
                        "invalid 'options.dataservice.basePath' '/api//v1': each path segment must be non-empty " +
                                "and contain only letters, digits, '_', '-' and '.'."},
                {"target_module", http + "\ntargetModule = \"ds_error_target_module.db\"", null, MODEL,
                        "a data service is generated only into the default module 'ds_error_target_module'. " +
                                "Remove 'targetModule' or set it to 'ds_error_target_module'."},
                {"init_params", http + "\noptions.withInitParams = true", null, MODEL,
                        "a data service cannot be generated with 'options.withInitParams', because the service " +
                                "creates the client without arguments."},
                {"named_model", http, "persist/hr/model.bal", MODEL,
                        "a data service is not supported for a named persist model. Use 'persist/model.bal' or " +
                                "remove 'options.dataservice'."},
                {"no_entity", http, null, PLAIN_MODEL.replace("import ballerina/persist as _;",
                        "import ballerina/dataservice as _;\nimport ballerina/persist as _;"),
                        "the data service exposes no entity. Add the @dataservice:Expose annotation to the " +
                                "entities to expose: Employee, Department."},
                {"unsupported", http, null, """
                        import ballerina/dataservice;
                        import ballerina/persist as _;

                        @dataservice:Expose
                        type Gadget record {|
                            readonly int id;
                            //Unsupported[GEOMETRY] location;
                        |};

                        type Widget record {|
                            readonly int id;
                        |};
                        """, "the entity 'Gadget' has an unsupported data type and cannot be exposed. Remove its " +
                        "@dataservice:Expose annotation."},
                {"placement", http, null, MODEL + "\n@dataservice:Expose\nenum Status {\n    ACTIVE\n}\n",
                        "the @dataservice:Expose annotation is allowed only on a record type in the persist model."},
                {"duplicate", http, null, MODEL.replace("operations: [dataservice:READ]",
                        "operations: [dataservice:READ, \"read\"]"),
                        "duplicate operation '\"read\"' in the @dataservice:Expose annotation of the entity " +
                                "'Assignment'."},
                {"empty_operations", http, null, MODEL.replace("operations: [dataservice:READ]", "operations: []"),
                        "invalid usage of list constructor"},
                {"unknown_operation", http, null, MODEL.replace("operations: [dataservice:READ]",
                        "operations: [\"write\"]"), "incompatible types"},
        };
    }

    // Ballerina reports a schema error and skips the tool, but does not fail the build.
    @Test(description = "The options schema rejects a key that options.dataservice does not define")
    public void testUnknownOptionKey() throws IOException, InterruptedException {
        Path project = project("ds_unknown_key", entry("inmemory", "options.dataservice.protocol = \"http\"\n" +
                "options.dataservice.port = 8080"), MODEL);
        Result result = build(project);
        Assert.assertTrue(result.output().contains("key 'port' not supported in schema 'dataservice'"),
                result.output());
        Assert.assertFalse(Files.exists(project.resolve("generated").resolve(SERVICE)), result.output());
    }

    // bal persist add

    @Test(description = "add --dataservice writes the protocol into the new entry")
    public void testAdd() throws IOException, InterruptedException {
        Path project = emptyProject("ds_add");
        Result result = bal(project, Map.of(), "persist", "add", "--datastore", "h2", "--dataservice", "http");
        assertSucceeded(result);
        String toml = Files.readString(project.resolve("Ballerina.toml"));
        Assert.assertTrue(toml.contains("options.datastore = \"h2\""), toml);
        Assert.assertTrue(toml.contains("options.dataservice.protocol = \"http\""), toml);
        Assert.assertFalse(toml.contains("basePath"), toml);
        Assert.assertTrue(result.output().contains("2. Add the @dataservice:Expose annotation to each entity to " +
                "expose.\n3. Execute `bal build` to generate the persist client and the HTTP data service during " +
                "package build."), result.output());
    }

    @Test(description = "add outside a package names the missing Ballerina.toml once")
    public void testAddOutsidePackage() throws IOException, InterruptedException {
        Path directory = SOURCES.resolve("ds_add_no_package");
        deleteDirectory(directory);
        Files.createDirectories(directory);
        Result result = bal(directory, Map.of(), "persist", "add", "--dataservice", "http");
        Assert.assertEquals(result.output().strip(), "ERROR: invalid Ballerina package directory: " + directory +
                ", cannot find 'Ballerina.toml' file.", result.output());
    }

    @Test(description = "pull outside a package names the missing Ballerina.toml with one ERROR prefix")
    public void testPullOutsidePackage() throws IOException, InterruptedException {
        Path directory = SOURCES.resolve("ds_pull_no_package");
        deleteDirectory(directory);
        Files.createDirectories(directory);
        Result result = bal(directory, Map.of(), "persist", "pull", "--dataservice", "http", "--host", "localhost",
                "--port", "3307", "--user", "root", "--database", "persist");
        Assert.assertTrue(result.output().startsWith("ERROR: invalid Ballerina package directory"), result.output());
        Assert.assertFalse(result.output().contains("ERROR: ERROR:"), result.output());
    }

    @Test(description = "An empty model is reported as a model without entities")
    public void testEmptyModel() throws IOException, InterruptedException {
        Path project = project("ds_empty_model", entry("h2", "options.dataservice.protocol = \"http\""),
                "import ballerina/persist as _;\n");
        Result result = build(project);
        Assert.assertTrue(result.output().contains("the model definition file(model.bal) does not contain any " +
                "entity definition."), result.output());
        Assert.assertFalse(result.output().contains("unsupported data type"), result.output());
    }

    @Test(dataProvider = "addErrors", description = "add rejects --dataservice with the options it cannot combine")
    public void testAddError(String name, List<String> args, String message)
            throws IOException, InterruptedException {
        Path project = emptyProject("ds_add_" + name);
        String toml = Files.readString(project.resolve("Ballerina.toml"));
        List<String> command = new ArrayList<>(List.of("persist", "add"));
        command.addAll(args);
        Result result = bal(project, Map.of(), command.toArray(String[]::new));
        Assert.assertTrue(result.output().contains("ERROR: " + message), result.output());
        Assert.assertEquals(Files.readString(project.resolve("Ballerina.toml")), toml);
        Assert.assertFalse(Files.exists(project.resolve("persist")));
    }

    @DataProvider
    public Object[][] addErrors() {
        return new Object[][]{
                {"module", List.of("--dataservice", "http", "--module", "db"),
                        "a data service is generated only into the default module 'ds_add_module'. Remove " +
                                "'targetModule' or set it to 'ds_add_module'."},
                {"model", List.of("--dataservice", "http", "--model", "hr"),
                        "a data service is not supported for a named persist model."},
                {"init_params", List.of("--dataservice", "http", "--with-init-params"),
                        "a data service cannot be generated with 'options.withInitParams'"},
                {"graphql", List.of("--dataservice", "graphql"),
                        "the protocol 'graphql' is not supported yet. Supported values: http."},
                {"rest", List.of("--dataservice", "rest"),
                        "invalid 'options.dataservice.protocol' 'rest'. Supported values: http."},
        };
    }

    // bal persist generate

    @Test(description = "generate --dataservice writes the service into the package root for the user to own")
    public void testGenerate() throws IOException, InterruptedException {
        Path project = emptyProject("ds_generate");
        writeModel(project, MODEL);
        Result result = bal(project, Map.of(), "persist", "generate", "--datastore", "inmemory", "--dataservice",
                "http");
        assertSucceeded(result);
        Path service = project.resolve(SERVICE);
        String generated = Files.readString(service);
        Assert.assertTrue(generated.startsWith("// AUTO-GENERATED FILE.\n"), generated);
        Assert.assertFalse(generated.contains("DO NOT MODIFY") || generated.contains("should not be modified"));
        Assert.assertTrue(Files.readString(project.resolve(CONFIG)).startsWith("// AUTO-GENERATED FILE.\n"));
        Assert.assertTrue(Files.readString(project.resolve(TYPES)).startsWith("// AUTO-GENERATED FILE.\n"));
        assertSucceeded(build(project));

        Files.writeString(service, generated + "\n// edited by the user\n");
        assertSucceeded(bal(project, Map.of(), "persist", "generate", "--datastore", "inmemory", "--dataservice",
                "http"));
        Assert.assertEquals(Files.readString(service), generated);
    }

    @Test(description = "generate without --dataservice on an annotated model says what the option would do")
    public void testGenerateWithoutOption() throws IOException, InterruptedException {
        Path project = emptyProject("ds_generate_info");
        writeModel(project, MODEL);
        Result result = bal(project, Map.of(), "persist", "generate", "--datastore", "inmemory");
        assertSucceeded(result);
        Assert.assertTrue(result.output().contains("the model has @dataservice:Expose annotations. Use the " +
                "'--dataservice' option to also generate a data service."), result.output());
        Assert.assertFalse(Files.exists(project.resolve(SERVICE)));
    }

    @Test(dataProvider = "generateErrors", description = "generate --dataservice errors leave the package unchanged")
    public void testGenerateError(String name, String model, List<String> args, String message)
            throws IOException, InterruptedException {
        Path project = emptyProject("ds_generate_" + name);
        writeModel(project, model);
        String toml = Files.readString(project.resolve("Ballerina.toml"));
        List<String> command = new ArrayList<>(List.of("persist", "generate", "--datastore", "inmemory"));
        command.addAll(args);
        Result result = bal(project, Map.of(), command.toArray(String[]::new));
        Assert.assertTrue(result.output().contains("ERROR: " + message), result.output());
        Assert.assertEquals(Files.readString(project.resolve("Ballerina.toml")), toml);
        Assert.assertFalse(Files.exists(project.resolve(SERVICE)));
        Assert.assertFalse(Files.exists(project.resolve("persist_client.bal")));
    }

    @DataProvider
    public Object[][] generateErrors() {
        return new Object[][]{
                {"module", MODEL, List.of("--dataservice", "http", "--module", "db"),
                        "a data service is generated only into the default module 'ds_generate_module'."},
                {"model", MODEL, List.of("--dataservice", "http", "--model", "hr"),
                        "a data service is not supported for a named persist model."},
                {"init_params", MODEL, List.of("--dataservice", "http", "--with-init-params"),
                        "a data service cannot be generated with 'options.withInitParams'"},
                {"mcp", MODEL, List.of("--dataservice", "mcp"),
                        "the protocol 'mcp' is not supported yet. Supported values: http."},
                {"no_entity", PLAIN_MODEL.replace("import ballerina/persist as _;",
                        "import ballerina/dataservice as _;\nimport ballerina/persist as _;"),
                        List.of("--dataservice", "http"), "the data service exposes no entity. Add the " +
                        "@dataservice:Expose annotation to the entities to expose: Employee, Department."},
        };
    }

    // bal persist pull: the errors that are reported before the database is reached

    @Test(dataProvider = "pullErrors", description = "pull rejects data service options before introspecting")
    public void testPullError(String name, String entryOptions, List<String> args, String message)
            throws IOException, InterruptedException {
        Path project = emptyProject("ds_pull_" + name);
        if (entryOptions != null) {
            writeToml(project, entry("mysql", entryOptions));
        }
        String toml = Files.readString(project.resolve("Ballerina.toml"));
        List<String> command = new ArrayList<>(List.of("persist", "pull", "--datastore", "mysql", "--host",
                "localhost", "--port", "3307", "--user", "root", "--database", "persist"));
        command.addAll(args);
        Result result = bal(project, Map.of(), command.toArray(String[]::new));
        Assert.assertTrue(result.output().contains("ERROR: " + message), result.output());
        Assert.assertFalse(result.output().contains("password"), result.output());
        Assert.assertEquals(Files.readString(project.resolve("Ballerina.toml")), toml);
        Assert.assertFalse(Files.exists(project.resolve("persist").resolve("model.bal")));
    }

    @DataProvider
    public Object[][] pullErrors() {
        return new Object[][]{
                {"expose_without_dataservice", null, List.of("--expose-tables", "employees"),
                        "the '--expose-tables' option requires the '--dataservice' option."},
                {"soap", null, List.of("--dataservice", "soap"),
                        "the protocol 'soap' is not supported yet. Supported values: http."},
                {"named_model", null, List.of("--dataservice", "http", "--model", "hr"),
                        "a data service is not supported for a named persist model."},
                {"target_module", "targetModule = \"ds_pull_target_module.db\"", List.of("--dataservice", "http"),
                        "a data service is generated only into the default module 'ds_pull_target_module'."},
                {"init_params", "options.withInitParams = true", List.of("--dataservice", "http"),
                        "a data service cannot be generated with 'options.withInitParams'"},
                {"expose_entry_without_dataservice", "", List.of("--expose-tables", "employees"),
                        "the '--expose-tables' option requires the '--dataservice' option."},
                {"entry_protocol", "options.dataservice.protocol = \"graphql\"", List.of(),
                        "the protocol 'graphql' is not supported yet. Supported values: http."},
                {"entry_base_path", "options.dataservice.protocol = \"http\"\noptions.dataservice.basePath = \"api\"",
                        List.of(), "invalid 'options.dataservice.basePath' 'api': the base path must start with '/'."},
                {"entry_expose_tables", "options.dataservice.protocol = \"http\"\n" +
                        "targetModule = \"ds_pull_entry_expose_tables.db\"", List.of("--expose-tables", "employees"),
                        "a data service is generated only into the default module 'ds_pull_entry_expose_tables'."},
        };
    }

    private static Result build(Path project) throws IOException, InterruptedException {
        return bal(project, Map.of(), "build", "--offline");
    }

    // The persist tool under test is registered only in the test distribution, so builds run offline.
    private static Result bal(Path directory, Map<String, String> environment, String... args)
            throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(DISTRIBUTION.resolve("bin").resolve(System.getProperty("os.name").startsWith("Windows") ?
                "bal.bat" : "bal").toString());
        command.addAll(List.of(args));
        ProcessBuilder builder = new ProcessBuilder(command).directory(directory.toFile()).redirectErrorStream(true);
        builder.environment().putAll(environment);
        Process process = builder.start();
        process.getOutputStream().close();
        String output;
        try (InputStream stream = process.getInputStream()) {
            output = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new Result(process.waitFor(), output);
    }

    private static Path emptyProject(String name) throws IOException {
        Path project = SOURCES.resolve(name);
        deleteDirectory(project);
        Files.createDirectories(project);
        writeToml(project, "");
        Files.writeString(project.resolve("main.bal"), "public function main() {\n}\n");
        return project;
    }

    private static Path project(String name, String toolEntry, String model) throws IOException {
        Path project = emptyProject(name);
        writeToml(project, toolEntry);
        writeModel(project, model);
        return project;
    }

    private static void writeModel(Path project, String model) throws IOException {
        Files.createDirectories(project.resolve("persist"));
        Files.writeString(project.resolve("persist").resolve("model.bal"), model);
    }

    private static void writeToml(Path project, String toolEntry) throws IOException {
        Path name = project.getFileName();
        Files.writeString(project.resolve("Ballerina.toml"), String.format("""
                [package]
                org = "dataservice_test"
                name = "%s"
                version = "0.1.0"
                distribution = "2201.14.0"
                %s""", name, toolEntry.isEmpty() ? "" : System.lineSeparator() + toolEntry));
    }

    private static String entry(String datastore, String options) {
        return "[[tool.persist]]\nid = \"generate-db-client\"\nfilePath = \"persist/model.bal\"\n" +
                "options.datastore = \"" + datastore + "\"\n" + (options.isEmpty() ? "" : options + "\n");
    }

    private static void assertSucceeded(Result result) {
        Assert.assertEquals(result.exitCode(), 0, result.output());
        Assert.assertFalse(result.output().contains("ERROR"), result.output());
    }

    private static void assertTestsPassed(Result result, int count) {
        Assert.assertEquals(result.exitCode(), 0, result.output());
        Assert.assertTrue(result.output().contains(count + " passing"), result.output());
        Assert.assertTrue(result.output().contains("0 failing"), result.output());
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
