-- Copyright (c) 2022 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

CREATE TABLE Employee (
  id INT,
  name VARCHAR(191),
  email VARCHAR(191),
  age INT,
  salary DECIMAL(10,2),
  managed_by INT,
  PRIMARY KEY (id),
  FOREIGN KEY (managed_by) REFERENCES Employee(id)
);