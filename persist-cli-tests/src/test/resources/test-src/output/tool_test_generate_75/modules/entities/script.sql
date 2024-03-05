-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Car`;
DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`gender` ENUM('MALE', 'FEMALE') NOT NULL,
	`nic` VARCHAR(12) NOT NULL,
	`salary` DECIMAL(10,2),
	PRIMARY KEY(`id`)
);

CREATE TABLE `Car` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`model` CHAR(10) NOT NULL,
	`ownerId` INT NOT NULL,
	FOREIGN KEY(`ownerId`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);


CREATE INDEX `ownerId` ON `Car` (`ownerId`);
