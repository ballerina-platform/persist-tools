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
import io.ballerina.persist.dataservice.DataServiceOptions.Operation;
import io.ballerina.persist.utils.BalProjectUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tests how the exposed entities and their actions are resolved from the model.
 */
public class DataServiceModelTest {

    static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice").toAbsolutePath();

    @Test
    public void testOnlyAnnotatedEntitiesAreExposed() throws BalException {
        Assert.assertEquals(operations(resolve("optin")), Map.of(
                "Department", EnumSet.allOf(Operation.class),
                "Employee", EnumSet.of(Operation.READ)));
    }

    @Test
    public void testOperationValues() throws BalException {
        Assert.assertEquals(operations(resolve("annotated")), Map.of(
                "Department", EnumSet.of(Operation.READ),
                "Employee", EnumSet.of(Operation.READ, Operation.UPDATE, Operation.DELETE),
                "Project", EnumSet.of(Operation.READ, Operation.UPDATE, Operation.DELETE)));
    }

    @Test
    public void testEmptyAnnotationValue() throws BalException {
        Assert.assertEquals(operations(resolve("hr")).get("Assignment"), EnumSet.allOf(Operation.class));
    }

    @Test
    public void testNoExposedEntity() {
        assertError("not_exposed", "the data service does not expose any entity. add the @dataservice:Expose " +
                "annotation to the entities to expose: Department, Employee.");
    }

    @Test
    public void testEmptyOperations() {
        assertError("empty_operations", "the 'operations' of the @dataservice:Expose annotation of the entity " +
                "'Department' cannot be empty.");
    }

    @Test
    public void testOperationsNotAList() {
        assertError("constant_operations", "the 'operations' of the @dataservice:Expose annotation of the entity " +
                "'Department' must be a list of operations.");
    }

    @Test
    public void testInvalidAnnotationOperation() {
        assertError("invalid_annotation", "invalid operation 'dataservice:EXECUTE' in the @dataservice:Expose " +
                "annotation of the entity 'Department'.");
    }

    static DataServiceModel resolve(String model) throws BalException {
        return resolve(model, Map.of("protocol", "http"));
    }

    static DataServiceModel resolve(String model, Map<String, Object> options) throws BalException {
        Path modelPath = RESOURCES.resolve(model).resolve("model.bal");
        return DataServiceModel.resolve(DataServiceOptions.from(options), BalProjectUtils.getEntities(modelPath),
                modelPath);
    }

    private static Map<String, Set<Operation>> operations(DataServiceModel model) {
        return model.entities().stream().collect(Collectors.toMap(exposed -> exposed.entity().getEntityName(),
                ExposedEntity::operations, (a, b) -> a, LinkedHashMap::new));
    }

    private static void assertError(String model, String message) {
        BalException error = Assert.expectThrows(BalException.class, () -> resolve(model));
        Assert.assertEquals(error.getMessage(), message);
    }
}
