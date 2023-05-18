-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `MedicalNeed`;
DROP TABLE IF EXISTS `MedicalItem`;

CREATE TABLE `MedicalItem` (
	`name` VARCHAR(191) NOT NULL,
	`itemId` INT NOT NULL,
	`decrip` VARCHAR(191) NOT NULL,
	`unit` VARCHAR(191) NOT NULL,
	`num` INT NOT NULL,
	PRIMARY KEY(`name`)
);

CREATE TABLE `MedicalNeed` (
	`id` INT NOT NULL,
	`needId` INT NOT NULL,
	`itemId` VARCHAR(191) NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`period` DATETIME NOT NULL,
	PRIMARY KEY(`id`)
);
