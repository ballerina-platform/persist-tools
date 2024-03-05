-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Profile`;
DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
	`id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Profile` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`gender` VARCHAR(191),
	`ownerId` INT UNIQUE NOT NULL,
	FOREIGN KEY(`ownerId`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);
