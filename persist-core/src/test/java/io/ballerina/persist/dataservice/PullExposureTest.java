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
        Assert.assertEquals(exposure.mappings(), List.of("The table 'order_items' is exposed as the entity " +
                "'OrderItem'."));
    }

    @Test
    public void testExposeTables() throws BalException {
        PullExposure exposure = PullExposure.select(pulled, " Customer , ");
        Assert.assertEquals(exposure.exposed(), Set.of("Customer"));
        Assert.assertEquals(exposure.warnings(), List.of());
        Assert.assertEquals(exposure.mappings(), List.of());
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
}
