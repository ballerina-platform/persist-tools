-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.


CREATE TABLE `Car` (
	`id` INT NOT NULL,
	`make` VARCHAR(191) NOT NULL,
	`model` VARCHAR(191) NOT NULL,
	`year` INT NOT NULL,
	PRIMARY KEY(`id`)
);

