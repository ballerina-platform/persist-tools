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

CREATE TYPE mood AS ENUM ('sad', 'ok', 'happy');

CREATE TABLE "ManyTypes" (
  "id" INT,
  "smallIntType" SMALLINT,
  "intType" INT,
  "integerType" INTEGER,
  "bigIntType" BIGINT,
  "decimalType" DECIMAL,
  "numericType" NUMERIC,
  "realType" REAL,
  "doublePrecisionType" DOUBLE PRECISION,
  "smallSerialType" SMALLSERIAL,
  "serialType" SERIAL,
  "bigSerialType" BIGSERIAL,
  "moneyType" MONEY,
  "characterVaryingType" CHARACTER VARYING,
  "varcharType" VARCHAR,
  "characterType" CHARACTER,
  "charType" CHAR, 
  "bpCharType" BPCHAR(3),
  "textType" TEXT,
  "byteaType" BYTEA,
  "timestampType" TIMESTAMP,
  "timestampWithTimeZoneType" TIMESTAMP WITH TIME ZONE,
  "timestampWithOutTimeZoneType" TIMESTAMP WITHOUT TIME ZONE,
  "dateType" DATE,
  "timeType" TIME,
  "timeWithTimeZoneType" TIME WITH TIME ZONE,
  "timeWithOutTimeZoneType" TIME WITHOUT TIME ZONE,
  "intervalType" INTERVAL,
  "booleanType" BOOLEAN,
  "enumType" mood,
  "circleType" CIRCLE,
  "bitType" BIT,
  "bitVaryingType" BIT VARYING,
  "uuidType" UUID,
  "arrayType" INT[],
  PRIMARY KEY ("id")
);
