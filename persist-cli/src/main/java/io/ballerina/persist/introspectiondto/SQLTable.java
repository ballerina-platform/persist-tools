/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
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

public class SQLTable {
    private final String tableName;
    private final String createOptions;
    private final String tableComment;
    private final List<SQLColumn> columns;


//    private List<SQLConstraint> constraints;
    private final List<SQLForeignKey> sqlForeignKeys;

    private final List<SQLIndex> indexes;

    public SQLTable(String tableName, String createOptions, String tableComment) {
        this.tableName = tableName;
        this.createOptions = createOptions;
        this.tableComment = tableComment;
        this.columns = new ArrayList<>();
        this.sqlForeignKeys = new ArrayList<>();
        this.indexes = new ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }


    public String getCreateOptions() {
        return createOptions;
    }


    public String getTableComment() {
        return tableComment;
    }


    public List<SQLColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }




//    public List<Constraint> getConstraints() {
//        return constraints;
//    }
//
//    public void setConstraints(List<Constraint> constraints) {
//        this.constraints = constraints;
//    }

    public List<SQLForeignKey> getSqlForeignKeys() {
        return Collections.unmodifiableList(sqlForeignKeys);
    }

    public List<SQLIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public void addColumn(SQLColumn column) {
        this.columns.add(column);
    }



    public void addForeignKey(SQLForeignKey foreignKey) {
        this.sqlForeignKeys.add(foreignKey);
    }

    public void addIndex(SQLIndex index) {
        this.indexes.add(index);
    }

    public static Builder newBuilder(String tableName) {
        return new Builder(tableName);
    }

    public static class Builder {
        private String tableName;
        private String createOptions;
        private String tableComment;

        private Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder setCreateOptions(String createOptions) {
            this.createOptions = createOptions;
            return this;
        }

        public Builder setTableComment(String tableComment) {
            this.tableComment = tableComment;
            return this;
        }


        public SQLTable build() {
            return new SQLTable(tableName, createOptions,
                    tableComment);
        }
    }



}
