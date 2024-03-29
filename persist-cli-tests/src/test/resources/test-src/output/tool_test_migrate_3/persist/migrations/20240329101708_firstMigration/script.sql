-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Person`;

CREATE TABLE `Person` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`age` INT NOT NULL,
	PRIMARY KEY(`id`)
);


