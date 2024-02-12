/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

public class SQLForeignKey {
    private final String constraintName;
    private final String tableName;
    private List<String> columnNames;
    private final String referencedTableName;
    private List<String> referencedColumnNames;
    private final String updateRule;
    private final String deleteRule;


    public String getConstraintName() {
        return constraintName;
    }
    public String getTableName() {
        return tableName;
    }
    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    public SQLForeignKey(String constraintName, String tableName, List<String> columnNames, String referencedTableName,
                         List<String> referencedColumnNames, String updateRule, String deleteRule
                          ) {
        this.constraintName = constraintName;
        this.tableName = tableName;
        this.columnNames = Collections.unmodifiableList(columnNames);
        this.referencedTableName = referencedTableName;
        this.referencedColumnNames = Collections.unmodifiableList(referencedColumnNames);
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public List<String> getReferencedColumnNames() {
        return Collections.unmodifiableList(referencedColumnNames);
    }

    public String getUpdateRule() {
        return updateRule;
    }

    public String getDeleteRule() {
        return deleteRule;
    }


    public void addColumnName(String columnName) {
        this.columnNames.add(columnName);
    }

    public void addReferencedColumnName(String referencedColumnName) {
        this.referencedColumnNames.add(referencedColumnName);
    }
    public static class Builder {
        private String constraintName;
        private String tableName;
        private List<String> columnNames;
        private String referencedTableName;
        private List<String> referencedColumnNames;
        private String updateRule;
        private String deleteRule;

        public static Builder newBuilder(String constraintName) {
            return new Builder(constraintName);

        }

        private Builder(String constraintName) {
            this.constraintName = constraintName;
            this.columnNames = new ArrayList<>();
            this.referencedColumnNames = new ArrayList<>();
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder addColumnName(String columnName) {
            this.columnNames.add(columnName);
            return this;
        }

        public Builder setReferencedTableName(String referencedTableName) {
            this.referencedTableName = referencedTableName;
            return this;
        }

        public Builder addReferencedColumnName(String referencedColumnName) {
            this.referencedColumnNames.add(referencedColumnName);
            return this;
        }

        public Builder setUpdateRule(String updateRule) {
            this.updateRule = updateRule;
            return this;
        }

        public Builder setDeleteRule(String deleteRule) {
            this.deleteRule = deleteRule;
            return this;
        }

        public SQLForeignKey build() {
            return new SQLForeignKey(constraintName, tableName, columnNames, referencedTableName, referencedColumnNames,
                    updateRule, deleteRule);
        }
    }
}