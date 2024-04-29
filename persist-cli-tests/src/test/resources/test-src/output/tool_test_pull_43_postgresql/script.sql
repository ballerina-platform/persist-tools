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

\c "postgres";
DROP DATABASE IF EXISTS "persist";
CREATE DATABASE "persist";
\c "persist";

CREATE TABLE "User" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"gender" VARCHAR(6) CHECK ("gender" IN ('MALE', 'FEMALE')) NOT NULL,
	"nic" VARCHAR(191) NOT NULL,
	"salary" DECIMAL(65,30) NOT NULL,
	PRIMARY KEY("id")
);


CREATE INDEX "user" ON "User" ("id", "nic");
CREATE INDEX "user_nic" ON "User" ("nic");
