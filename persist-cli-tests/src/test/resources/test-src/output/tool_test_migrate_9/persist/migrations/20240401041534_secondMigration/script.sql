-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Car
DROP COLUMN model;

ALTER TABLE Person
DROP COLUMN age;

ALTER TABLE Car
ADD COLUMN modell INT NOT NULL;

ALTER TABLE Person
ADD COLUMN yearsOld INT NOT NULL;

