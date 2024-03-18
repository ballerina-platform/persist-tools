-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE MedicalNeed
MODIFY COLUMN name CHAR(10) ;

ALTER TABLE MedicalItem
MODIFY COLUMN unit VARCHAR(10) ;

ALTER TABLE MedicalItem
MODIFY COLUMN price DECIMAL(10,2) ;

