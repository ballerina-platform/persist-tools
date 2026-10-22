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

import java.util.Optional;

/**
 * Tests reading and updating the default model's `[[tool.persist]]` entry.
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
        Assert.assertEquals(DataServiceToml.setProtocol(toml, entry, "http"),
                toml.substring(0, toml.length() - 1) + "\noptions.dataservice.protocol = \"http\"\n");
    }

    @Test
    public void testEntryWithoutFilePathIsTheDefaultModel() {
        String toml = PACKAGE + "[[tool.persist]]\ntargetModule = \"hr.db\"\noptions.withInitParams = true\n";
        DataServiceToml.Entry entry = DataServiceToml.defaultModelEntry(toml).orElseThrow();
        Assert.assertEquals(entry.targetModule("hr"), "hr.db");
        Assert.assertTrue(entry.withInitParams());
    }

    @Test
    public void testProtocolIsReplacedAndBasePathKept() {
        String toml = PACKAGE + "[[tool.persist]]\nfilePath = \"persist/model.bal\"\n" +
                "options.dataservice.protocol = \"graphql\"\noptions.dataservice.basePath = \"/v1\"\n";
        Optional<DataServiceToml.Entry> entry = DataServiceToml.defaultModelEntry(toml);
        Assert.assertEquals(DataServiceToml.setProtocol(toml, entry.orElseThrow(), "http"),
                toml.replace("\"graphql\"", "\"http\""));
    }
}
