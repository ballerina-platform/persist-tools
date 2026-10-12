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
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.utils.BalProjectUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests how {@code bal persist pull} selects and annotates the entities a data service exposes.
 */
public class ExposeAnnotationsTest {

    private static final Path MODEL = DataServiceModelTest.RESOURCES.resolve("pulled");

    @Test
    public void testSelectEveryEntity() throws BalException {
        Assert.assertEquals(names(ExposeAnnotations.select(entities(), null)), List.of("Department", "Employee"));
    }

    @Test
    public void testSelectByTable() throws BalException {
        Assert.assertEquals(names(ExposeAnnotations.select(entities(), " departments ")), List.of("Department"));
    }

    @Test
    public void testUnknownTable() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> ExposeAnnotations.select(entities(), "Department"));
        Assert.assertEquals(error.getMessage(), "the table 'Department' in the '--expose-tables' option is not " +
                "pulled. Pulled tables: departments, Employee.");
    }

    @Test
    public void testEmptyTableName() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> ExposeAnnotations.select(entities(), "departments,"));
        Assert.assertEquals(error.getMessage(), "the '--expose-tables' option must be a comma-separated list of " +
                "table names.");
    }

    @Test
    public void testAnnotate() throws BalException, IOException {
        String source = Files.readString(MODEL.resolve("model.bal"), StandardCharsets.UTF_8);
        String expected = Files.readString(MODEL.resolve("expected").resolve("model.bal"), StandardCharsets.UTF_8);
        Assert.assertEquals(ExposeAnnotations.annotate(source, List.of("Department", "Employee")), expected);
    }

    private static Module entities() throws BalException {
        return BalProjectUtils.getEntities(MODEL.resolve("model.bal"));
    }

    private static List<String> names(List<Entity> entities) {
        return entities.stream().map(Entity::getEntityName).toList();
    }
}
