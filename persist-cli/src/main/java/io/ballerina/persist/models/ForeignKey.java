/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.persist.models;

/**
 * Foreign Key class.
 *
 * @since 0.4.0
 */
public class ForeignKey {
    private String name;
    private String columnName;
    private String referenceTable;
    private String referenceColumn;

    public ForeignKey(String name, String columnName, String referenceTable, String referenceColumn) {
        this.name = name;
        this.columnName = columnName;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    public String getName() {
        return name;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }

    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

}
