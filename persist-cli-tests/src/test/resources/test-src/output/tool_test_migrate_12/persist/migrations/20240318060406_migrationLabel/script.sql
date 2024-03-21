-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE MedicalNeed
ADD COLUMN needDetails CHAR(50);

ALTER TABLE MedicalNeed
ADD COLUMN needType VARCHAR(50);

ALTER TABLE MedicalNeed
ADD COLUMN amount DECIMAL(10,2);

