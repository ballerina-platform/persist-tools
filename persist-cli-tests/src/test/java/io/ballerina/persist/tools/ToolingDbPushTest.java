/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.persist.tools;

import io.ballerina.persist.tools.utils.PersistTable;
import io.ballerina.persist.tools.utils.PersistTableColumn;
import jdk.jfr.Description;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.ballerina.persist.tools.utils.DatabaseTestUtils.assertCreateDatabaseTables;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.Command.DB_PUSH;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSources;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertGeneratedSourcesNegative;

/**
 * persist tool db push command tests.
 */
public class ToolingDbPushTest {

    private static final String sqlInt = "INT";
    private static final String sqlVarchar = "VARCHAR";
    private static final String yes = "YES";
    private static final String no = "NO";

    @Test()
    @Description("Database is not available and it is created while running the db push command")
    public void testDbPushWithoutDatabase() {
        ArrayList<PersistTable> tables = new ArrayList<>();
        tables.add(
                new PersistTable("MedicalNeeds", "needId")
                        .addColumn(new PersistTableColumn("needId", sqlInt, yes, no))
                        .addColumn(new PersistTableColumn("itemId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("beneficiaryId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("period", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("urgency", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("quantity", sqlInt, no, no))
        );
        tables.add(
                new PersistTable("MedicalItems", "itemId")
                        .addColumn(new PersistTableColumn("itemId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("name", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("type", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("unit", sqlVarchar, no, no))
        );
        assertGeneratedSources("tool_test_db_push_1", DB_PUSH);
        assertCreateDatabaseTables("tool_test_db_push_1", tables);
    }

    @Test()
    @Description("When the db push command is executed outside a Ballerina project")
    public void testDbPushOutsideBallerinaProject() {
        assertGeneratedSourcesNegative("tool_test_db_push_2", DB_PUSH, null);
    }

    @Test(dependsOnMethods = { "testDbPushWithoutDatabase" })
    @Description("Database already exists. An entity is removed. The database tables should not be affected.")
    public void testDbPushEntityRemoved() {
        ArrayList<PersistTable> tables = new ArrayList<>();
        tables.add(
                new PersistTable("MedicalNeeds", "needId")
                        .addColumn(new PersistTableColumn("needId", sqlInt, yes, no))
                        .addColumn(new PersistTableColumn("itemId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("beneficiaryId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("period", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("urgency", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("quantity", sqlInt, no, no))
        );
        tables.add(
                new PersistTable("MedicalItems", "itemId")
                        .addColumn(new PersistTableColumn("itemId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("name", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("type", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("unit", sqlVarchar, no, no))
        );
        assertGeneratedSources("tool_test_db_push_3", DB_PUSH);
        assertCreateDatabaseTables("tool_test_db_push_3", tables);
    }

    @Test()
    @Description("When the db push command is executed without the database configurations")
    public void testDbPushWithoutConfigFile() {
        assertGeneratedSourcesNegative("tool_test_db_push_4", DB_PUSH, null);
    }

    @Test(dependsOnMethods = { "testDbPushEntityRemoved" })
    @Description("Database already exists. An entity is updated. The respective table should be updated.")
    public void testDbPushEntityUpdated() {
        ArrayList<PersistTable> tables = new ArrayList<>();
        tables.add(
                new PersistTable("MedicalNeeds", "fooNeedId")
                        .addColumn(new PersistTableColumn("fooNeedId", sqlInt, yes, no))
                        .addColumn(new PersistTableColumn("fooItemId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("fooBeneficiaryId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("period", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("urgency", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("foo", sqlInt, no, no))
        );
        tables.add(
                new PersistTable("MedicalItems", "itemId")
                        .addColumn(new PersistTableColumn("itemId", sqlInt, no, no))
                        .addColumn(new PersistTableColumn("name", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("type", sqlVarchar, no, no))
                        .addColumn(new PersistTableColumn("unit", sqlVarchar, no, no))
        );
        assertGeneratedSources("tool_test_db_push_5", DB_PUSH);
        assertCreateDatabaseTables("tool_test_db_push_5", tables);
    }
}
