-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Car
DROP PRIMARY KEY;

ALTER TABLE Person
DROP PRIMARY KEY;

ALTER TABLE Car
ADD PRIMARY KEY (id, plateNumber);

ALTER TABLE Person
ADD PRIMARY KEY (id);

ALTER TABLE Car
DROP COLUMN ownerNic;

ALTER TABLE Person
DROP COLUMN nic;

