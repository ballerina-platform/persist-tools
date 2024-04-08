-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.


CREATE TABLE `Engine` (
	`id` INT NOT NULL,
	`make` VARCHAR(191) NOT NULL,
	`usedinId` INT UNIQUE NOT NULL,
	FOREIGN KEY(`usedinId`) REFERENCES `Car`(`id`),
	PRIMARY KEY(`id`)
);

