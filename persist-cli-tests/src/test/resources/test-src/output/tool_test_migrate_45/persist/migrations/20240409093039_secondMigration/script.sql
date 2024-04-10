-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.


CREATE TABLE `Car` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`model` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

ALTER TABLE Person
ADD COLUMN field4 VARCHAR(191) NOT NULL;

DROP INDEX name_idx ON Person;

DROP INDEX unique_idx_age ON Person;

DROP INDEX address_idx ON Person;

DROP INDEX new_field_idx ON Person;

CREATE INDEX car_model_idx ON Car(model);

CREATE UNIQUE INDEX car_name_idx ON Car(name);

CREATE UNIQUE INDEX new_field_idx ON Person(field3, field4);

CREATE UNIQUE INDEX field1_index ON Person(field1);

