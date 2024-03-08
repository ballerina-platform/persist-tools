/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
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

public class SqlColumn {
    private final String columnName;
    private final String tableName;
    private final String dataType;
    private final String fullDataType;
    private final String characterMaximumLength;
    private final String numericPrecision;
    private final String numericScale;
    private final String datetimePrecision;
    private final String columnDefault;
    private final String isNullable;
    private final String extra;
    private final String columnComment;
    private final boolean isPrimaryKey;
    private final boolean isDbGenerated;

    private SqlColumn(String columnName, String tableName, String dataType, String fullDataType,
                      String characterMaximumLength, String numericPrecision, String numericScale,
                      String datetimePrecision, String columnDefault, String isNullable, String extra,
                      String columnComment, Boolean isPrimaryKey, Boolean isDbGenerated) {
        this.columnName = columnName;
        this.tableName = tableName;
        this.dataType = dataType;
        this.fullDataType = fullDataType;
        this.characterMaximumLength = characterMaximumLength;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.datetimePrecision = datetimePrecision;
        this.columnDefault = columnDefault;
        this.isNullable = isNullable;
        this.extra = extra;
        this.columnComment = columnComment;
        this.isPrimaryKey = isPrimaryKey;
        this.isDbGenerated = isDbGenerated;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getFullDataType() {
        return fullDataType;
    }

    public String getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public String getNumericPrecision() {
        return numericPrecision;
    }

    public String getNumericScale() {
        return numericScale;
    }

    public String getDatetimePrecision() {
        return datetimePrecision;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public Boolean isDbGenerated() {
        return isDbGenerated;
    }

    public String getExtra() {
        return extra;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public static SqlColumn.Builder newBuilder(String columnName) {
        return new Builder(columnName);
    }

    public static class Builder {
        private String columnName;
        private String dataType;
        private String fullDataType;
        private String characterMaximumLength;
        private String numericPrecision;
        private String numericScale;
        private String datetimePrecision;
        private String columnDefault;
        private String isNullable;
        private String extra;
        private String columnComment;
        private String tableName;
        private Boolean isPrimaryKey;
        private Boolean isDbGenerated;

        private Builder(String columnName) {
            this.columnName = columnName;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder setDataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder setIsDbGenerated(Boolean isDbGenerated) {
            this.isDbGenerated = isDbGenerated;
            return this;
        }

        public Builder setFullDataType(String fullDataType) {
            this.fullDataType = fullDataType;
            return this;
        }

        public Builder setCharacterMaximumLength(String characterMaximumLength) {
            this.characterMaximumLength = characterMaximumLength;
            return this;
        }

        public Builder setNumericPrecision(String numericPrecision) {
            this.numericPrecision = numericPrecision;
            return this;
        }

        public Builder setNumericScale(String numericScale) {
            this.numericScale = numericScale;
            return this;
        }

        public Builder setDatetimePrecision(String datetimePrecision) {
            this.datetimePrecision = datetimePrecision;
            return this;
        }

        public Builder setColumnDefault(String columnDefault) {
            this.columnDefault = columnDefault;
            return this;
        }

        public Builder setIsNullable(String isNullable) {
            this.isNullable = isNullable;
            return this;
        }

        public Builder setExtra(String extra) {
            this.extra = extra;
            return this;
        }

        public Builder setColumnComment(String columnComment) {
            this.columnComment = columnComment;
            return this;
        }
        public Builder setIsPrimaryKey(Boolean isPrimaryKey) {
            this.isPrimaryKey = isPrimaryKey;
            return this;
        }

        public SqlColumn build() {
            return new SqlColumn(this.columnName, this.tableName, this.dataType, this.fullDataType,
                    this.characterMaximumLength, this.numericPrecision, this.numericScale, this.datetimePrecision,
                    this.columnDefault, this.isNullable, this.extra, this.columnComment, this.isPrimaryKey,
                    this.isDbGenerated);
        }
    }
}
