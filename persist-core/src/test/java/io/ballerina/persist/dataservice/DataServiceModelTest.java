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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tests how the exposed entities and their effective actions are resolved.
 */
public class DataServiceModelTest {

    static final Path RESOURCES = Paths.get("src", "test", "resources", "dataservice").toAbsolutePath();

    @Test
    public void testAnnotationsNarrowDefaults() throws BalException {
        DataServiceModel model = resolve("annotated", Map.of("protocol", "http"));
        Assert.assertEquals(operations(model), Map.of(
                "Department", EnumSet.of(Operation.READ),
                "Employee", EnumSet.of(Operation.READ, Operation.UPDATE, Operation.DELETE),
                "Project", EnumSet.allOf(Operation.class)));
        Assert.assertTrue(model.warnings().isEmpty());
    }

    @Test
    public void testAnnotationsNeverWiden() throws BalException {
        DataServiceModel model = resolve("annotated",
                Map.of("protocol", "http", "operations", List.of("read", "create")));
        Assert.assertEquals(operations(model), Map.of(
                "Department", EnumSet.of(Operation.READ),
                "Employee", EnumSet.of(Operation.READ),
                "Project", EnumSet.of(Operation.READ, Operation.CREATE)));
    }

    @Test
    public void testEmptyEffectiveOperations() {
        assertError("annotated", Map.of("protocol", "http", "operations", List.of("create")),
                "the entity 'Department' allows no operations: 'options.dataservice.operations' is [create] and " +
                        "its @dataservice:Expose annotation is [read].");
    }

    @Test
    public void testEntitiesIncludeList() throws BalException {
        DataServiceModel model = resolve("annotated",
                Map.of("protocol", "http", "entities", List.of("Project", "Employee")));
        Assert.assertEquals(operations(model).keySet(), Set.of("Employee", "Project"));
        Assert.assertEquals(model.warnings(), List.of("the entity 'Department' has a @dataservice:Expose " +
                "annotation but is not in 'options.dataservice.entities', so it is not exposed."));
    }

    @Test
    public void testUnknownEntity() {
        assertError("annotated", Map.of("protocol", "http", "entities", List.of("Employee", "Manager")),
                "the entity 'Manager' in 'options.dataservice.entities' is not defined in the model.");
    }

    @Test
    public void testInvalidAnnotationOperation() {
        assertError("invalid_annotation", Map.of("protocol", "http"),
                "invalid operation 'dataservice:EXECUTE' in the @dataservice:Expose annotation of the entity " +
                        "'Department'.");
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

    private static void assertError(String model, Map<String, Object> options, String message) {
        BalException error = Assert.expectThrows(BalException.class, () -> resolve(model, options));
        Assert.assertEquals(error.getMessage(), message);
    }
}
