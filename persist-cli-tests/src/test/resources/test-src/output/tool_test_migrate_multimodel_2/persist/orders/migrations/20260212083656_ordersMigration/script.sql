-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Order`;

CREATE TABLE `Order` (
	`id` INT NOT NULL,
	`total` DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(`id`)
);


