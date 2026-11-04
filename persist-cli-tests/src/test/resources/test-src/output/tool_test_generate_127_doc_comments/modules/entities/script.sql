-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Employee`;
DROP TABLE IF EXISTS `Department`;
DROP TABLE IF EXISTS `Building`;

CREATE TABLE `Building` (
	`code` VARCHAR(191) NOT NULL,
	`city` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`code`)
);

CREATE TABLE `Department` (
	`id` VARCHAR(191) NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Employee` (
	`id` INT AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`age` INT NOT NULL,
	`departmentId` VARCHAR(191) NOT NULL,
	FOREIGN KEY(`departmentId`) REFERENCES `Department`(`id`),
	PRIMARY KEY(`id`)
);


