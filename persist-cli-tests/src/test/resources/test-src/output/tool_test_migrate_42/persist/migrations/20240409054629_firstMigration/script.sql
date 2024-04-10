-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Person
ADD COLUMN new_field VARCHAR(191) NOT NULL;

CREATE INDEX name_idx ON Person(name);

CREATE INDEX idx_age ON Person(age);

CREATE INDEX fields_idx ON Person(field1, field2);

CREATE INDEX new_field_idx ON Person(new_field);

