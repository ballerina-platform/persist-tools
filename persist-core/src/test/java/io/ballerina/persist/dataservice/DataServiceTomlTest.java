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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Optional;

/**
 * Tests reading and extending the default model's `[[tool.persist]]` entry.
 */
public class DataServiceTomlTest {

    private static final String PACKAGE = "[package]\norg = \"hr\"\nname = \"hr\"\nversion = \"0.1.0\"\n\n";

    @Test
    public void testNoEntry() {
        Assert.assertTrue(DataServiceToml.defaultModelEntry(PACKAGE).isEmpty());
    }

    @Test
    public void testEntryOfTheDefaultModelAmongSeveral() {
        String toml = PACKAGE + "[[tool.persist]]\nid = \"users\"\nfilePath = \"persist/users/model.bal\"\n" +
                "targetModule = \"hr.users\"\n\n[[tool.persist]]\nid = \"generate-db-client\"\n" +
                "targetModule = \"hr\"\nfilePath = \"persist/model.bal\"\noptions.datastore = \"mysql\"\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(entry.values().get("id"), "generate-db-client");
        Assert.assertEquals(entry.targetModule("hr"), "hr");
        Assert.assertFalse(entry.withInitParams());
        Assert.assertTrue(entry.dataService().isEmpty());
        Assert.assertEquals(DataServiceToml.addDataService(toml, entry, "http"), toml.substring(0,
                toml.length() - 1) + "\n\n[tool.persist.options.dataservice]\nprotocol = \"http\"\n");
    }

    @Test
    public void testEntryWithoutFilePathIsTheDefaultModel() {
        String toml = PACKAGE + "[[tool.persist]]\ntargetModule = \"hr.db\"\noptions.withInitParams = true\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(entry.targetModule("hr"), "hr.db");
        Assert.assertTrue(entry.withInitParams());
    }

    @Test
    public void testDottedGroup() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n" +
                "options.dataservice.protocol = \"http\"\noptions.dataservice.basePath = \"/v1\"\n";
        Assert.assertEquals(DataServiceToml.defaultModelEntry(toml).orElseThrow().dataService().orElseThrow(),
                Map.of("protocol", "http", "basePath", "/v1"));
    }

    @Test
    public void testGroupInATable() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n\n" +
                "[tool.persist.options.dataservice]\nprotocol = \"http\"\nbasePath = \"/v1\"\n";
        Assert.assertEquals(DataServiceToml.defaultModelEntry(toml).orElseThrow().dataService().orElseThrow(),
                Map.of("protocol", "http", "basePath", "/v1"));
    }

    @Test
    public void testGroupInTheOptionsTable() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n\n" +
                "[tool.persist.options]\ndatastore = \"h2\"\ndataservice.basePath = \"/hr\"\n";
        Assert.assertEquals(DataServiceToml.defaultModelEntry(toml).orElseThrow().dataService().orElseThrow(),
                Map.of("basePath", "/hr"));
    }

    @Test
    public void testInlineGroup() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n" +
                "options = {datastore = \"h2\", dataservice = {protocol = \"http\"}}\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(entry.values().get("options.datastore"), "h2");
        Assert.assertEquals(entry.dataService().orElseThrow(), Map.of("protocol", "http"));
    }

    @Test
    public void testEmptyGroupIsDeclared() {
        String table = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n\n" +
                "[tool.persist.options.dataservice]\n";
        Assert.assertEquals(DataServiceToml.defaultModelEntry(table).orElseThrow().dataService(),
                Optional.of(Map.of()));
        String inline = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n" +
                "options.dataservice = {}\n";
        Assert.assertEquals(DataServiceToml.defaultModelEntry(inline).orElseThrow().dataService(),
                Optional.of(Map.of()));
    }

    @Test
    public void testGroupIsAddedAfterTheEntryAndItsTables() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n\n" +
                "[tool.persist.options]\ndatastore = \"h2\"\n\n[[platform.java21.dependency]]\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(DataServiceToml.addDataService(toml, entry, "http"), toml.replace("datastore = \"h2\"\n",
                "datastore = \"h2\"\n\n[tool.persist.options.dataservice]\nprotocol = \"http\"\n"));
    }

    @Test
    public void testGroupIsAddedToInlineOptions() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n" +
                "options = {datastore = \"h2\"}\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(DataServiceToml.addDataService(toml, entry, "http"),
                toml.replace("\"h2\"}", "\"h2\", dataservice = {protocol = \"http\"}}"));
    }

    @Test
    public void testTableBelongsToItsOwnEntry() {
        String toml = PACKAGE + "[[tool.persist]]\nid = \"users\"\nfilePath = \"persist/users/model.bal\"\n\n" +
                "[tool.persist.options.dataservice]\nprotocol = \"http\"\n\n" +
                "[[tool.persist]]\nid = \"generate-db-client\"\nfilePath = \"persist/model.bal\"\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(entry.values().get("id"), "generate-db-client");
        Assert.assertTrue(entry.dataService().isEmpty());
    }
}
