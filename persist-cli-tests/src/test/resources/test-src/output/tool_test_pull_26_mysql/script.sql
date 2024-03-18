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

DROP DATABASE IF EXISTS persist;
CREATE DATABASE persist;
USE persist;

CREATE TABLE ManyTypes (
  jsonType JSON NOT NULL,
  id INT,
  name VARCHAR(191) NOT NULL,
  bigIntType BIGINT NOT NULL,
  smallIntType SMALLINT NOT NULL,
  tinyIntType TINYINT NOT NULL,
  mediumIntType MEDIUMINT NOT NULL,
  booleanType TINYINT(1) NOT NULL,
  booleanType2 BOOL NOT NULL,
  setType SET('a', 'b', 'c') NOT NULL,
  tinyTextType TINYTEXT NOT NULL,
  textTypeType TEXT NOT NULL,
  mediumTextType MEDIUMTEXT NOT NULL,
  longTextType LONGTEXT NOT NULL,
  binaryType BINARY(20) NOT NULL,
  varBinaryType VARBINARY(20) NOT NULL,
  mediumBlobType MEDIUMBLOB NOT NULL,
  longBlobType LONGBLOB NOT NULL,
  blobType BLOB NOT NULL,
  tinyBlobType TINYBLOB NOT NULL,
  geometryType GEOMETRY NOT NULL,
  bitType BIT(8) NOT NULL,
  PRIMARY KEY (id)
);
