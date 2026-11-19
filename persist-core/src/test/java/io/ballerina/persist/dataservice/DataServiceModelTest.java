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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tests resolving the entities a data service exposes.
 */
public class DataServiceModelTest {

    private static final FieldRules.Result NO_RULES = new FieldRules.Result(Map.of());

    private static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice").toAbsolutePath();

    @Test
    public void testExposedEntitiesInModelOrder() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        List<ExposedEntity> exposed = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                annotations(model), Map.of(), NO_RULES);
        Assert.assertEquals(exposed.stream().map(ExposedEntity::name).toList(), List.of("Employee", "Department"));
        Assert.assertEquals(exposed.get(1).operations(), EnumSet.of(Operation.READ));
        Assert.assertEquals(exposed.get(0).resourceName(), "employees");
    }

    @Test
    public void testFieldsFollowThePersistTypes() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        ExposedEntity employee = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                annotations(model), Map.of(), NO_RULES).get(0);
        Assert.assertEquals(employee.fields(), List.of(
                new ServiceField("id", "int", false, false, true),
                new ServiceField("name", "string", false, false, false),
                new ServiceField("email", "string", true, false, false)
                        .withDoc("The work email, if the employee has one"),
                new ServiceField("departmentId", "int", false, false, false)));
        Assert.assertTrue(employee.insertFields().isEmpty());
        Assert.assertEquals(employee.updateFields().stream().map(ServiceField::name).toList(),
                List.of("name", "email", "departmentId"));
        Assert.assertFalse(employee.hasWireTypes());
    }

    @Test
    public void testFieldDocsFromTheModel() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        ExposedEntity employee = DataServiceModel.resolve(model, BalProjectUtils.getEntities(model), false).entities()
                .get(0);
        Assert.assertEquals(employee.fields().stream().map(field -> field.doc().orElse("")).toList(),
                List.of("", "The full name", "The work email, if the employee has one", ""));
    }

    @Test
    public void testCollectionNames() throws BalException, IOException {
        String source = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n" +
                "@dataservice:Expose\ntype ProjectAssignment record {|\n    readonly int id;\n|};\n\n" +
                "@dataservice:Expose\ntype Person record {|\n    readonly int id;\n|};\n\n" +
                "@dataservice:Expose\ntype SalesPerson record {|\n    readonly int id;\n|};\n\n" +
                "@dataservice:Expose\ntype HTTPLog record {|\n    readonly int id;\n|};\n\n" +
                "@dataservice:Expose\ntype CustomerHTTPLog record {|\n    readonly int id;\n|};\n\n" +
                "@dataservice:Expose\ntype URL record {|\n    readonly int id;\n|};\n";
        Path model = Files.createTempDirectory("collections").resolve("model.bal");
        Files.writeString(model, source);
        List<ExposedEntity> exposed = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                annotations(model), Map.of(), NO_RULES);
        Assert.assertEquals(exposed.stream().map(ExposedEntity::collection).toList(),
                List.of("projectAssignments", "people", "salesPeople", "httpLogs", "customerHTTPLogs", "urls"));
        Assert.assertEquals(exposed.get(0).resourceName(), "projectassignments");
        Assert.assertEquals(exposed.get(0).scopes(Operation.READ), List.of("projectAssignments:read"));
    }

    @Test
    public void testRelationsReadableFromBothSides() throws BalException {
        List<ExposedEntity> exposed = employees(NO_RULES);
        Assert.assertEquals(exposed.get(0).relations(), List.of(new ExposedRelation("department", "Department",
                false, true, List.of("departmentId"), List.of("id"))));
        Assert.assertEquals(exposed.get(1).relations(), List.of(new ExposedRelation("employees", "Employee",
                true, false, List.of("id"), List.of("departmentId"))));
    }

    @Test
    public void testInternalForeignKeyHidesBothSides() throws BalException {
        List<ExposedEntity> exposed = employees(new FieldRules.Result(Map.of("Employee", Map.of("departmentId",
                new FieldRules.Applied(FieldRule.INTERNAL, Optional.of("1"))))));
        Assert.assertTrue(exposed.get(0).relations().isEmpty());
        Assert.assertTrue(exposed.get(1).relations().isEmpty());
    }

    @Test
    public void testInternalOnTheOwningSideHidesOnlyThatSide() throws BalException {
        List<ExposedEntity> exposed = employees(new FieldRules.Result(Map.of("Employee", Map.of("department",
                new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty())))));
        Assert.assertTrue(exposed.get(0).relations().isEmpty());
        Assert.assertEquals(exposed.get(1).relations().size(), 1);
        Assert.assertTrue(exposed.get(0).columns().stream().anyMatch(column -> column.name().equals("departmentId")));
    }

    @Test
    public void testInternalOnTheOtherSideHidesOnlyThatSide() throws BalException {
        List<ExposedEntity> exposed = employees(new FieldRules.Result(Map.of("Department", Map.of("employees",
                new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty())))));
        Assert.assertEquals(exposed.get(0).relations().size(), 1);
        Assert.assertTrue(exposed.get(1).relations().isEmpty());
    }

    @Test
    public void testRelationNeedsReadOnBothEntities() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        Map<String, EnumSet<Operation>> annotations = Map.of("Employee", EnumSet.of(Operation.READ),
                "Department", EnumSet.of(Operation.CREATE));
        List<ExposedEntity> exposed = DataServiceModel.resolve(BalProjectUtils.getEntities(model), annotations,
                Map.of(), NO_RULES);
        Assert.assertTrue(exposed.stream().allMatch(entity -> entity.relations().isEmpty()));
    }

    @Test
    public void testScopeWarnings() throws BalException {
        Path model = RESOURCES.resolve("auth").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        DataServiceModel.Resolution withoutAuth = DataServiceModel.resolve(model, module, false);
        List<ExposedEntity> exposed = withoutAuth.entities();
        Assert.assertEquals(exposed.get(0).scopes(Operation.READ), List.of("hr:read"));
        Assert.assertEquals(exposed.get(0).scopes(Operation.CREATE), List.of("employees:create"));
        Assert.assertEquals(exposed.get(1).scopes(Operation.CREATE), List.of());
        Assert.assertEquals(DataServiceModel.resolve(model, module, true).warnings(), List.of());
        Assert.assertEquals(withoutAuth.warnings(), List.of(
                "the @dataservice:Expose annotation of the entity 'Employee' sets scopes, but " +
                        "'options.dataservice.auth' is not true. Set it to true, or remove the scopes.",
                "the @dataservice:Expose annotation of the entity 'Department' sets scopes, but " +
                        "'options.dataservice.auth' is not true. Set it to true, or remove the scopes."));
    }

    private static List<ExposedEntity> employees(FieldRules.Result rules) throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        return DataServiceModel.resolve(BalProjectUtils.getEntities(model), annotations(model), Map.of(),
                rules);
    }

    @Test
    public void testGeneratedKeyGivesAnInsertRecord() throws BalException {
        Path model = RESOURCES.resolve("generated_key").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        Map<String, EnumSet<Operation>> annotations = annotations(model);
        ExposedEntity ticket = DataServiceModel.resolve(module, annotations, Map.of(), NO_RULES).get(0);
        Assert.assertEquals(ticket.insertFields().orElseThrow().stream().map(ServiceField::name).toList(),
                List.of("subject", "openedOn"));
    }

    @Test
    public void testUnsupportedEntityCannotBeExposed() throws BalException {
        Path model = RESOURCES.resolve("unsupported").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceModel.resolve(module, annotations(model), Map.of(), NO_RULES));
        Assert.assertEquals(error.getMessage(), "the entity 'Gadget' has an unsupported data type and cannot be " +
                "exposed. Remove its @dataservice:Expose annotation.");
    }

    @Test
    public void testNoExposedEntityListsTheEntities() throws BalException {
        Path model = RESOURCES.resolve("unsupported").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceModel.resolve(module, Map.of(), Map.of(), NO_RULES));
        Assert.assertEquals(error.getMessage(), "the data service exposes no entity. Add the @dataservice:Expose " +
                "annotation to the entities to expose: Widget, Part.");
    }

    @Test
    public void testReservedKeyNameAndCollectionClash() throws IOException {
        String source = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n" +
                "@dataservice:Expose\ntype Order record {|\n    readonly int item;\n|};\n\n" +
                "@dataservice:Expose\ntype Person record {|\n    readonly int id;\n|};\n\n" +
                "@dataservice:Expose\ntype People record {|\n    readonly int id;\n|};\n";
        Path model = Files.createTempDirectory("clashes").resolve("model.bal");
        Files.writeString(model, source);
        BalException error = Assert.expectThrows(BalException.class, () -> DataServiceModel.resolve(
                BalProjectUtils.getEntities(model), annotations(model), Map.of(), NO_RULES));
        Assert.assertEquals(error.getMessage(), String.join(System.lineSeparator(),
                "the key field 'item' of the entity 'Order' uses a name reserved by the data service. Rename " +
                        "the field, or remove the entity's @dataservice:Expose annotation.",
                "the entities 'Person' and 'People' map to the same collection 'people' in the data service. " +
                        "Rename one of them, or remove its @dataservice:Expose annotation."));
    }

    @Test(dataProvider = "generatedTypeNames")
    public void testEntityNamedAsAGeneratedType(String protocol, String typeName) throws IOException, BalException {
        // The type is not exposed, but its persist type shares the module with the generated one.
        Path model = generatedTypeModel(typeName);
        Module entities = BalProjectUtils.getEntities(model);
        List<ExposedEntity> exposed = DataServiceModel.resolve(entities, annotations(model), Map.of(), NO_RULES);
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceGenerator.validate(protocol, exposed, entities));
        Assert.assertEquals(error.getMessage(), "the entity '" + typeName + "' has the name of a type that the " +
                "data service generates for the entity 'Order'. Rename one of them, or remove the " +
                "@dataservice:Expose annotation of 'Order'.");
    }

    @DataProvider
    public Object[][] generatedTypeNames() {
        return new Object[][]{
                {DataServiceOptions.HTTP, "OrderList"},
                {DataServiceOptions.HTTP, "OrderPartial"},
                {DataServiceOptions.GRAPHQL, "OrderList"},
                {DataServiceOptions.GRAPHQL, "OrderObject"},
        };
    }

    @Test(dataProvider = "otherProtocolTypeNames")
    public void testEntityNamedAsAnotherProtocolsType(String protocol, String typeName)
            throws IOException, BalException {
        Path model = generatedTypeModel(typeName);
        Module entities = BalProjectUtils.getEntities(model);
        DataServiceGenerator.validate(protocol, DataServiceModel.resolve(entities, annotations(model), Map.of(),
                NO_RULES), entities);
    }

    @DataProvider
    public Object[][] otherProtocolTypeNames() {
        return new Object[][]{
                {DataServiceOptions.HTTP, "OrderObject"},
                {DataServiceOptions.GRAPHQL, "OrderPartial"},
        };
    }

    private static Path generatedTypeModel(String typeName) throws IOException {
        String source = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n" +
                "@dataservice:Expose\ntype Order record {|\n    readonly int id;\n|};\n\n" +
                "type " + typeName + " record {|\n    readonly int id;\n|};\n";
        Path model = Files.createTempDirectory("types").resolve("model.bal");
        Files.writeString(model, source);
        return model;
    }

    @Test(dataProvider = "sharedTypes")
    public void testTwoEntitiesGenerateOneType(String protocol, String typeName) throws IOException, BalException {
        // The relation target Order gives OrderRelated<suffix>, and so does OrderRelated's own type.
        String source = "import ballerina/dataservice;\nimport ballerina/persist as _;\n" +
                "import ballerinax/persist.sql;\n\n" +
                "@dataservice:Expose\ntype Order record {|\n    readonly int id;\n    OrderRelated[] notes;\n|};\n\n" +
                "@dataservice:Expose\ntype OrderRelated record {|\n    readonly int id;\n    int orderId;\n" +
                "    @sql:Relation {keys: [\"orderId\"]}\n    Order 'order;\n|};\n";
        BalException error = Assert.expectThrows(BalException.class, () -> validate(protocol, source));
        Assert.assertEquals(error.getMessage(), "the data service generates the type '" + typeName + "' for " +
                "both the entity 'Order' and the entity 'OrderRelated'. Rename one of them, or remove its " +
                "@dataservice:Expose annotation.");
    }

    @DataProvider
    public Object[][] sharedTypes() {
        return new Object[][]{
                {DataServiceOptions.HTTP, "OrderRelatedPartial"},
                {DataServiceOptions.GRAPHQL, "OrderRelatedObject"},
        };
    }

    @Test(dataProvider = "protocols")
    public void testEnumNamedAsAGeneratedType(String protocol) throws IOException, BalException {
        String source = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n" +
                "enum OrderFilter {\n    OPEN,\n    CLOSED\n}\n\n@dataservice:Expose\ntype Order record {|\n" +
                "    readonly int id;\n    OrderFilter state;\n|};\n";
        BalException error = Assert.expectThrows(BalException.class, () -> validate(protocol, source));
        Assert.assertEquals(error.getMessage(), "the enum 'OrderFilter' has the name of a type that the data " +
                "service generates for the entity 'Order'. Rename one of them, or remove the @dataservice:Expose " +
                "annotation of 'Order'.");
    }

    @DataProvider
    public Object[][] protocols() {
        return new Object[][]{{DataServiceOptions.HTTP}, {DataServiceOptions.GRAPHQL}};
    }

    @Test(dataProvider = "notEmitted")
    public void testOnlyEmittedTypesAreReserved(String protocol, String operation, String typeName)
            throws IOException, BalException {
        // An Order that only allows create declares no HTTP filter, and one that only allows read no update input.
        validate(protocol, "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n" +
                "@dataservice:Expose {operations: [dataservice:" + operation + "]}\ntype Order record {|\n" +
                "    readonly int id;\n|};\n\ntype " + typeName + " record {|\n    readonly int id;\n|};\n");
    }

    @DataProvider
    public Object[][] notEmitted() {
        return new Object[][]{
                {DataServiceOptions.HTTP, "CREATE", "OrderFilter"},
                {DataServiceOptions.GRAPHQL, "READ", "OrderUpdateInput"},
        };
    }

    private static void validate(String protocol, String source) throws IOException, BalException {
        Path model = Files.createTempDirectory("types").resolve("model.bal");
        Files.writeString(model, source);
        Module entities = BalProjectUtils.getEntities(model);
        DataServiceGenerator.validate(protocol, DataServiceModel.resolve(entities, annotations(model), Map.of(),
                NO_RULES), entities);
    }

    private static Map<String, EnumSet<Operation>> annotations(Path model) throws BalException {
        return ExposeAnnotations.read(ExposeAnnotations.parse(model));
    }
}
