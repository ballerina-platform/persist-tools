-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Post`;
DROP TABLE IF EXISTS `Follower`;
DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`birthDate` DATE NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Follower` (
	`id` INT NOT NULL,
	`created_date` DATETIME NOT NULL,
	`leaderId` INT NOT NULL,
	CONSTRAINT FK_FOLLOWER_USER FOREIGN KEY(`leaderId`) REFERENCES `User`(`id`),
	`followerId` INT NOT NULL,
	CONSTRAINT FK_FOLLOWER_USER FOREIGN KEY(`followerId`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `Post` (
	`id` INT NOT NULL,
	`description` VARCHAR(191) NOT NULL,
	`tags` VARCHAR(191) NOT NULL,
	`category` VARCHAR(191) NOT NULL,
	`created_date` DATE NOT NULL,
	`userId` INT NOT NULL,
	CONSTRAINT FK_POST_USER FOREIGN KEY(`userId`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);
