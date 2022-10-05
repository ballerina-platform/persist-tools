DROP TABLE IF EXISTS MedicalItems;
CREATE TABLE MedicalItems (
	itemId INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	type VARCHAR(191) NOT NULL,
	unit VARCHAR(191) NOT NULL,
	PRIMARY KEY(itemId)
);

DROP TABLE IF EXISTS MedicalNeeds1;
CREATE TABLE MedicalNeeds1 (
	needId INT NOT NULL AUTO_INCREMENT,
	itemId INT NOT NULL,
	beneficiaryId INT NOT NULL,
	period VARCHAR(191) NOT NULL,
	urgency VARCHAR(191) NOT NULL,
	quantity INT NOT NULL,
	PRIMARY KEY(needId)
);

DROP TABLE IF EXISTS MedicalNeeds;
CREATE TABLE MedicalNeeds (
	needId INT NOT NULL AUTO_INCREMENT,
	itemId INT NOT NULL,
	beneficiaryId INT NOT NULL,
	period VARCHAR(191) NOT NULL,
	urgency VARCHAR(191) NOT NULL,
	quantity INT NOT NULL,
	PRIMARY KEY(needId)
);