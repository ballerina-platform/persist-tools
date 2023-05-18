-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `MedicalItem`;
DROP TABLE IF EXISTS `MedicalNeed`;

CREATE TABLE `MedicalNeed` (
	`needId` INT NOT NULL,
	`itemId` BOOLEAN NOT NULL,
	`beneficiaryId` VARCHAR(191) NOT NULL,
	`period` DATETIME NOT NULL,
	PRIMARY KEY(`needId`)
);

CREATE TABLE `MedicalItem` (
	`name` VARCHAR(191) NOT NULL,
	`itemId` INT NOT NULL,
	`types` VARCHAR(191) NOT NULL,
	`unit` INT NOT NULL,
	`num` INT NOT NULL,
	`needNeedId` INT NOT NULL,
	CONSTRAINT FK_NEED FOREIGN KEY(`needNeedId`) REFERENCES `MedicalNeed`(`needId`),
	PRIMARY KEY(`name`)
);
