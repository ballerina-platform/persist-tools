-- Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
--
-- WSO2 LLC. licenses this file to you under the Apache License,
-- Version 2.0 (the "License"); you may not use this file except
-- in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

CREATE TABLE [ManyTypes] (
    [id] INT IDENTITY(3, 2),
    [bigIntType] BIGINT,
    [decimalType] DECIMAL,
    [numericType] NUMERIC,
    [numericTypeLen] NUMERIC(10, 8),
    [bitType] BIT,
    [smallIntType] SMALLINT,
    [intType] INT,
    [tinyIntType] TINYINT,
    [moneyType] MONEY,
    [smallMoneyType] SMALLMONEY,
    [floatType] FLOAT,
    [floatTypeLen] FLOAT(2),
    [realType] REAL,
    [dateType] DATE,
    [dateTimeType] DATETIME,
    [dateTime2Type] DATETIME2,
    [smallDateTimeType] SMALLDATETIME,
    [timeType] TIME,
    [dateTimeOffsetType] DATETIMEOFFSET,
    [charType] CHAR,
    [charTypeLen] CHAR(10),
    [varcharType] VARCHAR,
    [varcharTypeLen] VARCHAR(10),
    [ncharType] NCHAR,
    [ncharTypeLen] NCHAR(10),
    [nvarcharType] NVARCHAR,
    [nvarcharTypeLen] NVARCHAR(10),
    [binaryType] BINARY,
    [binaryTypeLen] BINARY(10),
    [varBinaryType] VARBINARY,
    [varBinaryTypeLen] VARBINARY(10),
    PRIMARY KEY ([id])
);
