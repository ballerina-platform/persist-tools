/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.introspectiondto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlTable {
    private final String tableName;
    private final List<SqlColumn> columns;
    private final List<SqlForeignKey> sqlForeignKeys;

    private final List<SqlIndex> indexes;

    public SqlTable(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
        this.sqlForeignKeys = new ArrayList<>();
        this.indexes = new ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }

    public List<SqlColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<SqlForeignKey> getSqlForeignKeys() {
        return Collections.unmodifiableList(sqlForeignKeys);
    }

    public List<SqlIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public void addColumn(SqlColumn column) {
        this.columns.add(column);
    }

    public void addForeignKey(SqlForeignKey foreignKey) {
        this.sqlForeignKeys.add(foreignKey);
    }

    public void addIndex(SqlIndex index) {
        this.indexes.add(index);
    }

    public static Builder newBuilder(String tableName) {
        return new Builder(tableName);
    }

    public static class Builder {
        private final String tableName;

        private Builder(String tableName) {
            this.tableName = tableName;
        }

        public SqlTable build() {
            return new SqlTable(tableName);
        }
    }
}
