-- Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
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

CREATE TABLE patients (
  id INT,
  name VARCHAR(191) NOT NULL,
  GENDER ENUM ('MALE', 'FEMALE') NOT NULL,
  NIC VARCHAR(191) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE DOCTOR (
  id INT,
  name VARCHAR(191) NOT NULL,
  doctor_Specialty VARCHAR(191) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE appointment (
  id INT,
  patient_Id INT NOT NULL,
  Doctor_Id INT NOT NULL,
  date DATE NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (patient_Id) REFERENCES patients(id),
  FOREIGN KEY (Doctor_Id) REFERENCES DOCTOR(id)
);
