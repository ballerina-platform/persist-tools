-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Person
ADD COLUMN new_field VARCHAR(191) NOT NULL;

CREATE UNIQUE INDEX name_idx ON Person(name);

CREATE UNIQUE INDEX unique_idx_age ON Person(age);

CREATE UNIQUE INDEX fields_idx ON Person(field1, field2);

CREATE UNIQUE INDEX new_field_idx ON Person(new_field);

