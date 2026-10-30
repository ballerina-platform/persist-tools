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

    private static final FieldRules.Result NO_RULES = new FieldRules.Result(Map.of(), List.of());

    private static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice").toAbsolutePath();

    @Test
    public void testExposedEntitiesInModelOrder() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        List<ExposedEntity> exposed = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                ExposeAnnotations.read(model), Map.of(), NO_RULES);
        Assert.assertEquals(exposed.stream().map(ExposedEntity::name).toList(), List.of("Employee", "Department"));
        Assert.assertEquals(exposed.get(1).operations(), EnumSet.of(Operation.READ));
        Assert.assertEquals(exposed.get(0).resourceName(), "employees");
    }

    @Test
    public void testFieldsFollowThePersistTypes() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        ExposedEntity employee = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                ExposeAnnotations.read(model), Map.of(), NO_RULES).get(0);
        Assert.assertEquals(employee.fields(), List.of(
                new ServiceField("id", "int", false, false, true),
                new ServiceField("name", "string", false, false, false),
                new ServiceField("email", "string", true, false, false),
                new ServiceField("departmentId", "int", false, false, false)));
        Assert.assertTrue(employee.insertFields().isEmpty());
        Assert.assertEquals(employee.updateFields().stream().map(ServiceField::name).toList(),
                List.of("name", "email", "departmentId"));
        Assert.assertFalse(employee.hasWireTypes());
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
                new FieldRules.Applied(FieldRule.INTERNAL, Optional.of("1")))), List.of()));
        Assert.assertTrue(exposed.get(0).relations().isEmpty());
        Assert.assertTrue(exposed.get(1).relations().isEmpty());
    }

    @Test
    public void testInternalOnTheOwningSideHidesOnlyThatSide() throws BalException {
        List<ExposedEntity> exposed = employees(new FieldRules.Result(Map.of("Employee", Map.of("department",
                new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty()))), List.of()));
        Assert.assertTrue(exposed.get(0).relations().isEmpty());
        Assert.assertEquals(exposed.get(1).relations().size(), 1);
        Assert.assertTrue(exposed.get(0).columns().stream().anyMatch(column -> column.name().equals("departmentId")));
    }

    @Test
    public void testInternalOnTheOtherSideHidesOnlyThatSide() throws BalException {
        List<ExposedEntity> exposed = employees(new FieldRules.Result(Map.of("Department", Map.of("employees",
                new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty()))), List.of()));
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
        List<ExposedEntity> exposed = DataServiceModel.resolve(model, BalProjectUtils.getEntities(model)).entities();
        Assert.assertEquals(exposed.get(0).scopes(Operation.READ), List.of("hr:read"));
        Assert.assertEquals(exposed.get(0).scopes(Operation.CREATE), List.of("employees:create"));
        Assert.assertEquals(exposed.get(1).scopes(Operation.CREATE), List.of());
        Assert.assertEquals(DataServiceModel.scopeWarnings(exposed, true), List.of("the relation 'employees' of the " +
                "entity 'Department' is always refused in 'fields' and get related, because the 'read' scopes of " +
                "'Department' do not cover those of 'Employee'. Give both entities a shared 'read' scope."));
        Assert.assertEquals(DataServiceModel.scopeWarnings(exposed, false), List.of(
                "the @dataservice:Expose annotation of the entity 'Employee' sets scopes, but " +
                        "'options.dataservice.auth' is not true, so no scope is checked.",
                "the @dataservice:Expose annotation of the entity 'Department' sets scopes, but " +
                        "'options.dataservice.auth' is not true, so no scope is checked."));
    }

    @Test
    public void testDefaultScopesNeverCoverAnotherEntity() throws BalException {
        List<ExposedEntity> exposed = employees(NO_RULES);
        Assert.assertEquals(DataServiceModel.scopeWarnings(exposed, true).size(), 2);
    }

    private static List<ExposedEntity> employees(FieldRules.Result rules) throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        return DataServiceModel.resolve(BalProjectUtils.getEntities(model), ExposeAnnotations.read(model), Map.of(),
                rules);
    }

    @Test
    public void testGeneratedKeyGivesAnInsertRecord() throws BalException {
        Path model = RESOURCES.resolve("generated_key").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        Map<String, EnumSet<Operation>> annotations = ExposeAnnotations.read(model);
        ExposedEntity ticket = DataServiceModel.resolve(module, annotations, Map.of(), NO_RULES).get(0);
        Assert.assertEquals(ticket.insertFields().orElseThrow().stream().map(ServiceField::name).toList(),
                List.of("subject", "openedOn"));
    }

    @Test
    public void testUnsupportedEntityCannotBeExposed() throws BalException {
        Path model = RESOURCES.resolve("unsupported").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceModel.resolve(module, ExposeAnnotations.read(model), Map.of(), NO_RULES));
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
}
