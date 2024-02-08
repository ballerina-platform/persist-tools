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

public class SQLIndex {
    private String tableName;
    private String indexName;
    private List<String> columnNames;
    private String partial;
    private String columnOrder;
    private String nonUnique;
    private String indexType;

    private SQLIndex(String indexName, String tableName, List<String> columnNames, String partial,
                    String columnOrder, String nonUnique, String indexType) {

        this.indexName = indexName;
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.partial = partial;
        this.columnOrder = columnOrder;
        this.nonUnique = nonUnique;
        this.indexType = indexType;
    }

    public String getTableName() {
        return this.tableName;
    }


    public String getIndexName() {
        return indexName;
    }


    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }


    public String getPartial() {
        return partial;
    }




    public String getColumnOrder() {
        return columnOrder;
    }


    public String getNonUnique() {
        return nonUnique;
    }


    public String getIndexType() {
        return indexType;
    }

    public void addColumnName(String columnName) {
        this.columnNames.add(columnName);
    }

    public static Builder newBuilder(String indexName) {
        return new Builder(indexName);
    }

    public static class Builder {
        private String indexName;
        private String tableName;
        private List<String> columnNames;
        private String partial;
        private String columnOrder;
        private String nonUnique;
        private String indexType;

        public static Builder newBuilder(String indexName) {
            return new Builder(indexName);
        }

        private Builder(String indexName) {
            this.indexName = indexName;
            this.columnNames = new ArrayList<>();

        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder addColumnName(String columnName) {
            this.columnNames.add(columnName);
            return this;
        }

        public Builder setPartial(String partial) {
            this.partial = partial;
            return this;
        }



        public Builder setColumnOrder(String columnOrder) {
            this.columnOrder = columnOrder;
            return this;
        }

        public Builder setNonUnique(String nonUnique) {
            this.nonUnique = nonUnique;
            return this;
        }

        public Builder setIndexType(String indexType) {
            this.indexType = indexType;
            return this;
        }

        public SQLIndex build() {
            return new SQLIndex(indexName, tableName, columnNames, partial,
                     columnOrder, nonUnique, indexType);
        }
    }

}
