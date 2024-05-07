-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `people2`;
DROP TABLE IF EXISTS `Person`;

CREATE TABLE `Person` (
	`name` VARCHAR(191) NOT NULL,
	`age` INT NOT NULL,
	`nic` VARCHAR(191) NOT NULL,
	`salary` DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(`name`)
);

CREATE TABLE `people2` (
	`name` VARCHAR(191) NOT NULL,
	`age` INT NOT NULL,
	`nic` VARCHAR(191) NOT NULL,
	`salary` DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(`name`)
);

CREATE TABLE `User` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`gender` ENUM('MALE', 'FEMALE') NOT NULL,
	`nic` VARCHAR(191) NOT NULL,
	`salary` DECIMAL(65,30),
	PRIMARY KEY(`id`)
);

CREATE TABLE `cars` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`MODEL` VARCHAR(191) NOT NULL,
	`ownerId` INT NOT NULL,
	FOREIGN KEY(`ownerId`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);


CREATE INDEX `ownerId` ON `cars` (`ownerId`);
