-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE MedicalNeed
MODIFY COLUMN needId VARCHAR(191);

ALTER TABLE MedicalItem
MODIFY COLUMN decrip INT;

