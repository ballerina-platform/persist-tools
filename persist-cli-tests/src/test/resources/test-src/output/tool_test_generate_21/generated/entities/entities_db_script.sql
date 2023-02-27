-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for entities.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS MedicalNeed;
DROP TABLE IF EXISTS AidPackageOrderItem;

CREATE TABLE AidPackageOrderItem (
	id INT NOT NULL,
	quantity INT NOT NULL,
	totalAmount INT NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE MedicalNeed (
	needId INT NOT NULL,
	beneficiaryId INT NOT NULL,
	period DATETIME NOT NULL,
	urgency VARCHAR(191) NOT NULL,
	quantity INT NOT NULL,
	aidpackageorderitemId INT NOT NULL,
	CONSTRAINT FK_MEDICALNEED_AIDPACKAGEORDERITEM FOREIGN KEY(aidpackageorderitemId) REFERENCES AidPackageOrderItem(id),
	PRIMARY KEY(needId)
);
