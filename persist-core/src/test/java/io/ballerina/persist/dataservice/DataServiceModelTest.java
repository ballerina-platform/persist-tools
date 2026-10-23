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

/**
 * Tests resolving the entities a data service exposes.
 */
public class DataServiceModelTest {

    private static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice").toAbsolutePath();

    @Test
    public void testExposedEntitiesInModelOrder() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        List<ExposedEntity> exposed = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                ExposeAnnotations.read(model), "inmemory");
        Assert.assertEquals(exposed.stream().map(ExposedEntity::name).toList(), List.of("Employee", "Department"));
        Assert.assertEquals(exposed.get(1).operations(), EnumSet.of(Operation.READ));
        Assert.assertEquals(exposed.get(0).resourceName(), "employees");
    }

    @Test
    public void testFieldsFollowThePersistTypes() throws BalException {
        Path model = RESOURCES.resolve("employees").resolve("model.bal");
        ExposedEntity employee = DataServiceModel.resolve(BalProjectUtils.getEntities(model),
                ExposeAnnotations.read(model), "inmemory").get(0);
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
    public void testGeneratedKeyGivesAnInsertRecordOnSql() throws BalException {
        Path model = RESOURCES.resolve("generated_key").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        Map<String, EnumSet<Operation>> annotations = ExposeAnnotations.read(model);
        ExposedEntity ticket = DataServiceModel.resolve(module, annotations, "h2").get(0);
        Assert.assertEquals(ticket.insertFields().orElseThrow().stream().map(ServiceField::name).toList(),
                List.of("subject", "openedOn"));
        Assert.assertTrue(DataServiceModel.resolve(module, annotations, "inmemory").get(0).insertFields().isEmpty());
    }

    @Test
    public void testUnsupportedEntityCannotBeExposed() throws BalException {
        Path model = RESOURCES.resolve("unsupported").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceModel.resolve(module, ExposeAnnotations.read(model), "mysql"));
        Assert.assertEquals(error.getMessage(), "the entity 'Gadget' has an unsupported data type and cannot be " +
                "exposed. Remove its @dataservice:Expose annotation.");
    }

    @Test
    public void testNoExposedEntityListsTheEntities() throws BalException {
        Path model = RESOURCES.resolve("unsupported").resolve("model.bal");
        Module module = BalProjectUtils.getEntities(model);
        BalException error = Assert.expectThrows(BalException.class,
                () -> DataServiceModel.resolve(module, Map.of(), "mysql"));
        Assert.assertEquals(error.getMessage(), "the data service exposes no entity. Add the @dataservice:Expose " +
                "annotation to the entities to expose: Widget, Part.");
    }
}
