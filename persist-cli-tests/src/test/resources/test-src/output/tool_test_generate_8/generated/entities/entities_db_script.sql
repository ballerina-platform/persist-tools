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
