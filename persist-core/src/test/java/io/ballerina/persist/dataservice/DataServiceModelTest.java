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

    private static Map<String, EnumSet<Operation>> annotations(Path model) throws BalException {
        return ExposeAnnotations.read(ExposeAnnotations.parse(model));
    }
}
