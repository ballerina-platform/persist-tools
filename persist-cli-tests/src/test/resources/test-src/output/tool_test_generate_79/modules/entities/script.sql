-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Car`;
DROP TABLE IF EXISTS `USERS`;

CREATE TABLE `USERS` (
	`ID` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`gender` ENUM('MALE', 'FEMALE') NOT NULL,
	`nic` VARCHAR(191) NOT NULL,
	`salary` DECIMAL(65,30),
	PRIMARY KEY(`ID`)
);

CREATE TABLE `Car` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`model` VARCHAR(191) NOT NULL,
	`ownerId` INT NOT NULL,
	FOREIGN KEY(`ownerId`) REFERENCES `USERS`(`ID`),
	PRIMARY KEY(`id`)
);


CREATE INDEX `ownerId` ON `Car` (`ownerId`);
