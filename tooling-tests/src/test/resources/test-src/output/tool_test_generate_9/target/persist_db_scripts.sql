DROP TABLE IF EXISTS MedicalNeeds;
CREATE TABLE MedicalNeeds (
	needId INT NOT NULL AUTO_INCREMENT,
	itemId INT NOT NULL,
	beneficiaryId INT NOT NULL,
	period VARCHAR(191) NOT NULL,
	urgency VARCHAR(191) NOT NULL,
	quantity INT NOT NULL,
	PRIMARY KEY(needId, itemId)
);