-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Profile`;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `Dept`;
DROP TABLE IF EXISTS `Customer`;
DROP TABLE IF EXISTS `MultipleAssociations`;
DROP TABLE IF EXISTS `Student`;

CREATE TABLE `Student` (
	`id` INT NOT NULL,
	`firstName` VARCHAR(191) NOT NULL,
	`age` INT NOT NULL,
	`lastName` VARCHAR(191) NOT NULL,
	`nicNo` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`,`firstName`)
);

CREATE TABLE `MultipleAssociations` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Customer` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`age` INT NOT NULL,
	`multipleassociationsId` INT UNIQUE NOT NULL,
	FOREIGN KEY(`multipleassociationsId`) REFERENCES `MultipleAssociations`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `Dept` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`multipleassociationsId` INT UNIQUE NOT NULL,
	FOREIGN KEY(`multipleassociationsId`) REFERENCES `MultipleAssociations`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `User` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`multipleassociationsId` INT UNIQUE NOT NULL,
	FOREIGN KEY(`multipleassociationsId`) REFERENCES `MultipleAssociations`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `Profile` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`isAdult` BOOLEAN NOT NULL,
	`salary` DOUBLE NOT NULL,
	`age` DECIMAL(65,30) NOT NULL,
	`isRegistered` LONGBLOB NOT NULL,
	`ownerId` INT UNIQUE NOT NULL,
	FOREIGN KEY(`ownerId`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);
