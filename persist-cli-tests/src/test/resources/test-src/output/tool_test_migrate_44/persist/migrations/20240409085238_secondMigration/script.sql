-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Person
ADD COLUMN field4 VARCHAR(191) NOT NULL;

DROP INDEX name_idx ON Person;

DROP INDEX unique_idx_age ON Person;

DROP INDEX address_idx ON Person;

DROP INDEX new_field_idx ON Person;

CREATE UNIQUE INDEX new_field_idx ON Person(field3, field4);

CREATE UNIQUE INDEX field1_index ON Person(field1);

