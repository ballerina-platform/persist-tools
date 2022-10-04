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

package io.ballerina.persist.tools.utils;

import java.util.ArrayList;

/**
 * Table corresponding to @persist:Entity.
 */
public class PersistTable {

    private final String tableName;
    private final String primaryKey;
    private final ArrayList<PersistTableColumn> columns;

    public PersistTable(String tableName, String primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        columns = new ArrayList<>();
    }

    public PersistTable addColumn(PersistTableColumn column) {
        columns.add(column);
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public boolean hasColumn(String columnName) {
        for (PersistTableColumn column : this.columns) {
            if (column.getName().equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    public String getColumnType(String columnName) {
        for (PersistTableColumn column : this.columns) {
            if (column.getName().equals(columnName)) {
                return column.getType();
            }
        }
        return "";
    }

    public String isAutoIncrement(String columnName) {
        for (PersistTableColumn column : this.columns) {
            if (column.getName().equals(columnName)) {
                return column.isAutoIncrement();
            }
        }
        return "No";
    }

    public String isNullable(String columnName) {
        for (PersistTableColumn column : this.columns) {
            if (column.getName().equals(columnName)) {
                return column.isNullable();
            }
        }
        return "No";
    }
}
