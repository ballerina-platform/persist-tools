-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Person
DROP PRIMARY KEY;

ALTER TABLE Person
DROP COLUMN id;

ALTER TABLE Person
ADD COLUMN personId INT NOT NULL AUTO_INCREMENT;

ALTER TABLE Person
ADD PRIMARY KEY (personId);

