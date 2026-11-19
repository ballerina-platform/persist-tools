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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.SqlType;
import io.ballerina.persist.utils.BalProjectUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * Tests the selection of the entities that `bal persist pull --dataservice` annotates.
 */
public class PullExposureTest {

    private Module pulled;

    @BeforeClass
    public void loadModel() throws BalException {
        pulled = BalProjectUtils.getEntities(Paths.get("src", "test", "resources", "dataservice", "pulled",
                "model.bal").toAbsolutePath());
    }

    @Test
    public void testEverySupportedEntityIsExposed() throws BalException {
        PullExposure exposure = PullExposure.select(pulled, DataServiceOptions.HTTP, null);
        Assert.assertEquals(exposure.exposed(), Set.of("OrderItem", "Customer"));
        Assert.assertEquals(exposure.warnings(), List.of("the table 'gadgets' has an unsupported data type, so " +
                "its entity 'Gadget' is not exposed."));
    }

    @Test
    public void testExposeTables() throws BalException {
        PullExposure exposure = PullExposure.select(pulled, DataServiceOptions.HTTP, " Customer , ");
        Assert.assertEquals(exposure.exposed(), Set.of("Customer"));
        Assert.assertEquals(exposure.warnings(), List.of());
    }

    @Test
    public void testExposeTablesMatchesExactly() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulled, DataServiceOptions.HTTP, "ORDER_ITEMS,orders"));
        Assert.assertEquals(error.getMessage(),
                "the table 'ORDER_ITEMS' in '--expose-tables' is not a pulled table." + System.lineSeparator() +
                "the table 'orders' in '--expose-tables' is not a pulled table.");
    }

    @Test
    public void testExposeTablesWithUnsupportedType() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulled, DataServiceOptions.HTTP, "order_items,gadgets"));
        Assert.assertEquals(error.getMessage(), "the table 'gadgets' in '--expose-tables' has an unsupported " +
                "data type, so its entity 'Gadget' cannot be exposed.");
    }

    @Test
    public void testIntrospectedUnsupportedColumnIsNotExposed() {
        Module introspected = introspectedGadgets();
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(introspected, DataServiceOptions.HTTP, "gadgets"));
        Assert.assertEquals(error.getMessage(), "the table 'gadgets' in '--expose-tables' has an unsupported " +
                "data type, so its entity 'Gadget' cannot be exposed.");
    }

    @Test
    public void testIntrospectedUnsupportedColumnIsSkipped() throws BalException {
        PullExposure exposure = PullExposure.select(introspectedGadgets(), DataServiceOptions.HTTP, null);
        Assert.assertEquals(exposure.exposed(), Set.of());
        Assert.assertEquals(exposure.warnings(), List.of("the table 'gadgets' has an unsupported data type, so " +
                "its entity 'Gadget' is not exposed."));
    }

    @Test
    public void testEntitiesTheBuildRejectsAreSkipped() throws BalException {
        PullExposure exposure = PullExposure.select(conflicts(), DataServiceOptions.HTTP, null);
        Assert.assertEquals(exposure.exposed(), Set.of("Person", "Input"));
        Assert.assertEquals(exposure.warnings(), List.of("the key field 'key' of the table 'settings' uses a name " +
                "reserved by the data service, so its entity 'Setting' is not exposed.", "the entity 'People' of " +
                "the table 'people' maps to the collection 'people' of the entity 'Person', so it is not exposed."));
    }

    @Test
    public void testGraphqlKeyNamesAreSkipped() throws BalException {
        PullExposure exposure = PullExposure.select(conflicts(), DataServiceOptions.GRAPHQL, null);
        Assert.assertEquals(exposure.exposed(), Set.of("Person"));
        Assert.assertEquals(exposure.warnings().get(2), "the key field 'input' of the table 'inputs' uses a name " +
                "reserved by the data service, so its entity 'Input' is not exposed.");
    }

    @Test
    public void testExposeTablesWithAGraphqlKeyName() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(conflicts(), DataServiceOptions.GRAPHQL, "inputs"));
        Assert.assertEquals(error.getMessage(), "the table 'inputs' in '--expose-tables' has the key field " +
                "'input', whose name the data service reserves, so its entity 'Input' cannot be exposed.");
    }

    @Test
    public void testExposeTablesTheBuildRejects() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(conflicts(), DataServiceOptions.HTTP, "settings,person,people"));
        Assert.assertEquals(error.getMessage(), "the table 'settings' in '--expose-tables' has the key field " +
                "'key', whose name the data service reserves, so its entity 'Setting' cannot be exposed." +
                System.lineSeparator() + "the table 'people' in '--expose-tables' maps to the collection 'people' " +
                "of the table 'person'. Expose only one of them.");
    }

    @Test(dataProvider = "noTables")
    public void testExposeTablesWithoutTables(String value) {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulled, DataServiceOptions.HTTP, value));
        Assert.assertEquals(error.getMessage(),
                "'--expose-tables' names no table. Give the tables to expose, separated by commas.");
    }

    @DataProvider
    public Object[][] noTables() {
        return new Object[][]{{""}, {","}, {" , "}};
    }

    private static Module introspectedGadgets() {
        // As introspection builds it: the unsupported column is a field typed `Unsupported`, and the entity is not
        // flagged.
        EntityField id = field("id", "int");
        Entity.Builder gadget = Entity.newBuilder("Gadget");
        gadget.setTableName("gadgets");
        gadget.addField(id);
        gadget.addField(field("location", PersistToolsConstants.UNSUPPORTED_TYPE));
        gadget.setKeys(List.of(id));
        Module.Builder module = Module.newBuilder("db");
        module.addEntity("Gadget", gadget.build());
        return module.build();
    }

    private static EntityField field(String name, String type) {
        EntityField.Builder field = EntityField.newBuilder(name);
        field.setType(type);
        field.setFieldColumnName(name);
        return field.build();
    }

    private static Module conflicts() throws BalException {
        return BalProjectUtils.getEntities(Paths.get("src", "test", "resources", "dataservice", "pulled_conflicts",
                "model.bal").toAbsolutePath());
    }


    @Test
    public void testEntitiesNamedAsGeneratedTypes() throws BalException {
        Module.Builder module = Module.newBuilder("db");
        for (String[] entity : new String[][]{{"Employee", "employee"}, {"EmployeeList", "employee_list"}}) {
            EntityField id = column("id", "id", "int");
            Entity.Builder builder = Entity.newBuilder(entity[0]);
            builder.setTableName(entity[1]);
            builder.addField(id);
            builder.setKeys(List.of(id));
            module.addEntity(entity[0], builder.build());
        }
        Module pulledTables = module.build();
        PullExposure exposure = PullExposure.select(pulledTables, DataServiceOptions.HTTP, null);
        Assert.assertEquals(exposure.exposed(), Set.of("EmployeeList"));
        Assert.assertEquals(exposure.warnings(), List.of("the entity 'Employee' of the table 'employee' would " +
                "generate the type 'EmployeeList', which is the entity of the table 'employee_list', so it is not " +
                "exposed."));
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulledTables, DataServiceOptions.HTTP, "employee"));
        Assert.assertEquals(error.getMessage(), "the table 'employee' in '--expose-tables' would generate the type " +
                "'EmployeeList' for its entity 'Employee', which is the entity of the table 'employee_list'. Remove " +
                "the table from '--expose-tables'.");
    }

    @Test
    public void testEntitiesThatGenerateOneType() throws BalException {
        Module.Builder module = Module.newBuilder("db");
        for (String[] entity : new String[][]{{"Order", "orders"}, {"OrderRelated", "order_related"}}) {
            EntityField id = column("id", "id", "int");
            Entity.Builder builder = Entity.newBuilder(entity[0]);
            builder.setTableName(entity[1]);
            builder.addField(id);
            builder.setKeys(List.of(id));
            module.addEntity(entity[0], builder.build());
        }
        Module pulledTables = module.build();
        PullExposure exposure = PullExposure.select(pulledTables, DataServiceOptions.HTTP, null);
        Assert.assertEquals(exposure.exposed(), Set.of("Order"));
        Assert.assertEquals(exposure.warnings(), List.of("the entity 'OrderRelated' of the table 'order_related' " +
                "would generate the type 'OrderRelatedPartial', which the data service also generates for the entity " +
                "'Order', so it is not exposed."));
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulledTables, DataServiceOptions.HTTP, "orders,order_related"));
        Assert.assertEquals(error.getMessage(), "the table 'order_related' in '--expose-tables' would generate the " +
                "type 'OrderRelatedPartial', which the data service also generates for the table 'orders'. Expose " +
                "only one of them.");
    }

    @Test
    public void testIntrospectedRelationFields() throws BalException {
        Module introspected = introspectedModule();
        Assert.assertEquals(PullExposure.select(introspected, DataServiceOptions.HTTP, null).exposed(),
                Set.of("Department", "Employee"));
        Assert.assertEquals(PullExposure.select(introspected, DataServiceOptions.HTTP, "employees").exposed(),
                Set.of("Employee"));
    }

    // As the introspector builds them: a column has its column name, and a relation field has only its type and,
    // on the owner, the fields of its foreign key.
    private static Module introspectedModule() {
        EntityField departmentId = column("id", "id", "int");
        Entity.Builder department = Entity.newBuilder("Department");
        department.setTableName("departments");
        department.addField(departmentId);
        department.addField(column("name", "name", "string"));
        department.setKeys(List.of(departmentId));
        EntityField.Builder employees = EntityField.newBuilder("employees");
        employees.setType("Employee");
        employees.setArrayType(true);
        department.addField(employees.build());

        EntityField employeeId = column("id", "id", "int");
        Entity.Builder employee = Entity.newBuilder("Employee");
        employee.setTableName("employees");
        employee.addField(employeeId);
        employee.addField(column("departmentId", "department_id", "int"));
        employee.setKeys(List.of(employeeId));
        EntityField.Builder relation = EntityField.newBuilder("department");
        relation.setType("Department");
        relation.setRelationRefs(List.of("departmentId"));
        employee.addField(relation.build());

        Module.Builder module = Module.newBuilder("db");
        module.addEntity("Department", department.build());
        module.addEntity("Employee", employee.build());
        return module.build();
    }

    private static EntityField column(String name, String columnName, String type) {
        EntityField.Builder field = EntityField.newBuilder(name);
        field.setFieldColumnName(columnName);
        field.setType(type);
        String sqlType = type.equals("int") ? "INT" : "VARCHAR";
        field.setSqlType(new SqlType(sqlType, sqlType, null, 0, 0, 0,
                PersistToolsConstants.SupportedDataSources.MYSQL_DB));
        return field.build();
    }
}
