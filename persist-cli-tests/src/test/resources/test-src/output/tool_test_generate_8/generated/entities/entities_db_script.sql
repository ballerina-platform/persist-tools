-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for entities.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS MedicalNeed;

CREATE TABLE MedicalNeed (
	needId VARCHAR(191) NOT NULL,
	itemId INT NOT NULL,
	beneficiaryId INT NOT NULL,
	period DATETIME NOT NULL,
	urgency VARCHAR(191) NOT NULL,
	quantity INT NOT NULL,
	PRIMARY KEY(needId)
);
