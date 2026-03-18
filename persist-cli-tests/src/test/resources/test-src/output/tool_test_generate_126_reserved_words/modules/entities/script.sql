-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Enrollment`;
DROP TABLE IF EXISTS `Class`;

CREATE TABLE `Class` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Enrollment` (
	`id` INT NOT NULL,
	`classId` INT NOT NULL,
	FOREIGN KEY(`classId`) REFERENCES `Class`(`id`),
	PRIMARY KEY(`id`)
);


