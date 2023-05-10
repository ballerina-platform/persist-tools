CREATE TABLE MedicalNeed (needId INT PRIMARY KEY, itemId BOOLEAN, beneficiaryId VARCHAR(191), period DATETIME);
CREATE TABLE MedicalItem (name VARCHAR(191) PRIMARY KEY, itemId INT, types VARCHAR(191), unit INT, num INT, needNeedId INT);
ALTER TABLE MedicalItem ADD CONSTRAINT FK_MedicalItem_MedicalNeed FOREIGN KEY (needNeedId) REFERENCES MedicalNeed(needId);
