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
        PullExposure exposure = PullExposure.select(pulled, null);
        Assert.assertEquals(exposure.exposed(), Set.of("OrderItem", "Customer"));
        Assert.assertEquals(exposure.warnings(), List.of("the table 'gadgets' has an unsupported data type, so " +
                "its entity 'Gadget' is not exposed."));
    }

    @Test
    public void testExposeTables() throws BalException {
        PullExposure exposure = PullExposure.select(pulled, " Customer , ");
        Assert.assertEquals(exposure.exposed(), Set.of("Customer"));
        Assert.assertEquals(exposure.warnings(), List.of());
    }

    @Test
    public void testExposeTablesMatchesExactly() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulled, "ORDER_ITEMS,orders"));
        Assert.assertEquals(error.getMessage(),
                "the table 'ORDER_ITEMS' in '--expose-tables' is not a pulled table." + System.lineSeparator() +
                "the table 'orders' in '--expose-tables' is not a pulled table.");
    }

    @Test
    public void testExposeTablesWithUnsupportedType() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(pulled, "order_items,gadgets"));
        Assert.assertEquals(error.getMessage(), "the table 'gadgets' in '--expose-tables' has an unsupported " +
                "data type, so its entity 'Gadget' cannot be exposed.");
    }

    @Test
    public void testEntitiesTheBuildRejectsAreSkipped() throws BalException {
        PullExposure exposure = PullExposure.select(conflicts(), null);
        Assert.assertEquals(exposure.exposed(), Set.of("Person"));
        Assert.assertEquals(exposure.warnings(), List.of("the key field 'key' of the table 'settings' uses a name " +
                "reserved by the data service, so its entity 'Setting' is not exposed.", "the entity 'People' of " +
                "the table 'people' maps to the collection 'people' of the entity 'Person', so it is not exposed."));
    }

    @Test
    public void testExposeTablesTheBuildRejects() {
        BalException error = Assert.expectThrows(BalException.class,
                () -> PullExposure.select(conflicts(), "settings,person,people"));
        Assert.assertEquals(error.getMessage(), "the table 'settings' in '--expose-tables' has the key field " +
                "'key', whose name the data service reserves, so its entity 'Setting' cannot be exposed." +
                System.lineSeparator() + "the table 'people' in '--expose-tables' maps to the collection 'people' " +
                "of the table 'person'. Expose only one of them.");
    }

    @Test(dataProvider = "noTables")
    public void testExposeTablesWithoutTables(String value) {
        BalException error = Assert.expectThrows(BalException.class, () -> PullExposure.select(pulled, value));
        Assert.assertEquals(error.getMessage(),
                "'--expose-tables' names no table. Give the tables to expose, separated by commas.");
    }

    @DataProvider
    public Object[][] noTables() {
        return new Object[][]{{""}, {","}, {" , "}};
    }

    private static Module conflicts() throws BalException {
        return BalProjectUtils.getEntities(Paths.get("src", "test", "resources", "dataservice", "pulled_conflicts",
                "model.bal").toAbsolutePath());
    }

}
