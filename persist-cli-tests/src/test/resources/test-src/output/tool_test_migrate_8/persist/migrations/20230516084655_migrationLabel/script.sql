-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE MedicalItem
ADD COLUMN needId INT;

ALTER TABLE MedicalItem
ADD CONSTRAINT FK_MedicalItem_MedicalNeed FOREIGN KEY (needId) REFERENCES MedicalNeed(id);

