-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Vehicle`;
DROP TABLE IF EXISTS `Employee`;
DROP TABLE IF EXISTS `Company`;

CREATE TABLE `Company` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Employee` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`companyId` INT NOT NULL,
	CONSTRAINT FK_COMPANY FOREIGN KEY(`companyId`) REFERENCES `Company`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `Vehicle` (
	`model` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`employeeId` INT NOT NULL,
	CONSTRAINT FK_EMPLOYEE FOREIGN KEY(`employeeId`) REFERENCES `Employee`(`id`),
	PRIMARY KEY(`model`)
);
