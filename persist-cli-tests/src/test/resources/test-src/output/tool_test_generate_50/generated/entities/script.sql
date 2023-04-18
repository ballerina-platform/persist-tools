-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Follow`;
DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Follow` (
	`id` INT NOT NULL,
	`followId` INT UNIQUE NOT NULL,
	CONSTRAINT FK_FOLLOW_USER_LEADER FOREIGN KEY(`followId`) REFERENCES `User`(`id`),
	`follow1Id` INT UNIQUE NOT NULL,
	CONSTRAINT FK_FOLLOW_USER_FOLLOWER FOREIGN KEY(`follow1Id`) REFERENCES `User`(`id`),
	PRIMARY KEY(`id`)
);
