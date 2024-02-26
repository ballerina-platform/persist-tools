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
package io.ballerina.persist.models;


import io.ballerina.persist.PersistToolsConstants;

public class SQLType {
    private final String typeName;
    protected final String columnDefaultValue;
    private final int numericPrecision;
    private final int numericScale;
    private final String dateTimePrecision;
    private final int maxLength;

    public SQLType(String typeName, String columnDefaultValue, int numericPrecision, int numericScale,
                   String dateTimePrecision, int maxCharLength) {
        this.typeName = typeName;
        this.columnDefaultValue = columnDefaultValue;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.dateTimePrecision = dateTimePrecision;
        this.maxLength = maxCharLength;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getColumnDefaultValue() {
        return columnDefaultValue;
    }

    public int getNumericPrecision() {
        return numericPrecision;
    }

    public int getNumericScale() {
        return numericScale;
    }

    public String getDateTimePrecisionLevel() {
        return dateTimePrecision;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getBalType() {
        switch (this.typeName) {

            // Ballerina --> int
            // MySQL --> INT
            // MSSQL --> INT
            // PostgreSQL --> INT
            case PersistToolsConstants.SqlTypes.INT:
                return PersistToolsConstants.BallerinaTypes.INT;

            // Ballerina --> boolean
            // MySQL --> BOOLEAN
            // MSSQL --> BIT
            // PostgreSQL --> BOOLEAN
            case PersistToolsConstants.SqlTypes.BIT:
                case PersistToolsConstants.SqlTypes.BOOLEAN:
                    return PersistToolsConstants.BallerinaTypes.BOOLEAN;

            // Ballerina --> decimal
            // MySQL --> DECIMAL(65,30)
            // MSSQL --> DECIMAL(38,30)
            // PostgreSQL --> DECIMAL(65,30)
            case PersistToolsConstants.SqlTypes.DECIMAL:
                return PersistToolsConstants.BallerinaTypes.DECIMAL;

            // Ballerina --> float
            // MySQL --> DOUBLE
            // MSSQL --> FLOAT
            // PostgreSQL --> FLOAT
            case PersistToolsConstants.SqlTypes.DOUBLE:
                case PersistToolsConstants.SqlTypes.FLOAT:
                    return PersistToolsConstants.BallerinaTypes.FLOAT;

            // Ballerina --> time:Date
            // MySQL --> DATE
            // MSSQL --> DATE
            // PostgreSQL --> DATE
            case PersistToolsConstants.SqlTypes.DATE:
                return PersistToolsConstants.BallerinaTypes.DATE;

            // Ballerina --> time:TimeOfDay
            // MySQL --> TIME
            // MSSQL --> TIME
            // PostgreSQL --> TIME
            case PersistToolsConstants.SqlTypes.TIME:
                return PersistToolsConstants.BallerinaTypes.TIME_OF_DAY;

            // Ballerina --> time:Utc
            // MySQL --> TIMESTAMP
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            case PersistToolsConstants.SqlTypes.TIME_STAMP:
                return PersistToolsConstants.BallerinaTypes.UTC;
            case PersistToolsConstants.SqlTypes.DATE_TIME2:
            case PersistToolsConstants.SqlTypes.DATE_TIME:
                return PersistToolsConstants.BallerinaTypes.CIVIL;

            // Ballerina --> time:Civil
            // MySQL --> DATETIME
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
//            case PersistToolsConstants.SqlTypes.DATE_TIME:
//            case PersistToolsConstants.SqlTypes.DATE_TIME2:
//            case PersistToolsConstants.SqlTypes.TIME_STAMP:
//                return PersistToolsConstants.BallerinaTypes.UTC;

            // Ballerina --> string
            // MySQL --> VARCHAR
            // MSSQL --> VARCHAR
            // PostgreSQL --> VARCHAR
            case PersistToolsConstants.SqlTypes.VARCHAR:
            case PersistToolsConstants.SqlTypes.CHAR:
                return PersistToolsConstants.BallerinaTypes.STRING;

            default:
                throw new RuntimeException
                        ("ERROR: Couldn't find equivalent Ballerina type for the field type: " + this.typeName);
        }
    }
}
