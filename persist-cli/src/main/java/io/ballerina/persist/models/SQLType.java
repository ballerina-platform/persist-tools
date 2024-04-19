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
package io.ballerina.persist.models;


import io.ballerina.persist.PersistToolsConstants;

import java.util.Objects;

public class SQLType {
    private final String typeName;
    private final String fullDataType;
    private final String columnDefaultValue;
    private final int numericPrecision;
    private final int numericScale;
    private final int maxLength;

    public SQLType(String typeName, String fullDataType, String columnDefaultValue, int numericPrecision,
                   int numericScale, int maxCharLength, String datastore) {
        this.typeName = typeName;
        this.fullDataType = fullDataType;
        this.columnDefaultValue = columnDefaultValue;
        int precisionValue = PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_POSTGRESQL;
        if (datastore.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
            precisionValue = PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_MSSQL;
        }
        this.numericPrecision = numericPrecision > 0 ? numericPrecision : precisionValue;
        this.numericScale = numericScale > 0 ? numericScale : PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE;
        this.maxLength = maxCharLength > 0 ? maxCharLength : PersistToolsConstants.DefaultMaxLength.VARCHAR_LENGTH;
    }

    public SQLType(String typeName, String fullDataType, String columnDefaultValue, int numericPrecision,
                   int numericScale, int maxCharLength) {
        this.typeName = typeName;
        this.fullDataType = fullDataType;
        this.columnDefaultValue = columnDefaultValue;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.maxLength = maxCharLength;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getNumericPrecision() {
        return numericPrecision;
    }

    public int getNumericScale() {
        return numericScale;
    }
    public String getColumnDefaultValue() {
        return columnDefaultValue;
    }

    public int getMaxLength() {
        return maxLength;
    }
    public String getFullDataType() {
        return fullDataType;
    }

    public boolean isArrayType() {
        return
                this.typeName.equals(PersistToolsConstants.SqlTypes.BLOB) ||
                this.typeName.equals(PersistToolsConstants.SqlTypes.LONG_BLOB) ||
                this.typeName.equals(PersistToolsConstants.SqlTypes.MEDIUM_BLOB) ||
                this.typeName.equals(PersistToolsConstants.SqlTypes.TINY_BLOB) ||
                this.typeName.equals(PersistToolsConstants.SqlTypes.BINARY) ||
                this.typeName.equals(PersistToolsConstants.SqlTypes.BYTEA) ||
                this.typeName.equals(PersistToolsConstants.SqlTypes.VARBINARY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SQLType sqlType = (SQLType) obj;
        return Objects.equals(typeName, sqlType.typeName) && Objects.equals(fullDataType, sqlType.fullDataType) &&
                Objects.equals(columnDefaultValue, sqlType.columnDefaultValue) &&
                Objects.equals(numericPrecision, sqlType.numericPrecision) &&
                Objects.equals(numericScale, sqlType.numericScale) && Objects.equals(maxLength, sqlType.maxLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, fullDataType, columnDefaultValue, numericPrecision, numericScale, maxLength);
    }

}
