-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Profile`;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `MultipleAssociations`;

CREATE TABLE `MultipleAssociations` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `User` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`multipleassociationsId` INT UNIQUE NOT NULL,
	CONSTRAINT FK_USER_MULTIPLEASSOCIATIONS FOREIGN KEY(`multipleassociationsId`) REFERENCES `MultipleAssociations`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `Profile` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`userId` INT UNIQUE NOT NULL,
	CONSTRAINT FK_PROFILE_USER FOREIGN KEY(`userId`) REFERENCES `User`(`id`),
	`multipleassociationsId` INT UNIQUE NOT NULL,
	CONSTRAINT FK_PROFILE_MULTIPLEASSOCIATIONS FOREIGN KEY(`multipleassociationsId`) REFERENCES `MultipleAssociations`(`id`),
	PRIMARY KEY(`id`)
);
