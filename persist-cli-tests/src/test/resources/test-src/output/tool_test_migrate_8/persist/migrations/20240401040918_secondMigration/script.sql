-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Car
MODIFY COLUMN make INT NOT NULL;

ALTER TABLE Person
MODIFY COLUMN age DECIMAL(65,30) NOT NULL;

