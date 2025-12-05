-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Product`;

CREATE TABLE `Product` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`price` DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(`id`)
);


