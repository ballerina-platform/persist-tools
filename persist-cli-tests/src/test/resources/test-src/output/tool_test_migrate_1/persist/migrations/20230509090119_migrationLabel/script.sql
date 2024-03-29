-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

CREATE TABLE MedicalObject (
    objectId INT PRIMARY KEY,
    objectName VARCHAR(191),
    types VARCHAR(191),
    objectFlag BOOLEAN
);

CREATE TABLE MedicalTest (
    testId VARCHAR(191) PRIMARY KEY,
    object1ObjectId INT
);

ALTER TABLE MedicalNeed
ADD COLUMN description VARCHAR(191);

ALTER TABLE MedicalNeed
DROP PRIMARY KEY;

ALTER TABLE MedicalNeed
ADD PRIMARY KEY (itemId);

ALTER TABLE MedicalTest
ADD CONSTRAINT FK_MedicalTest_MedicalObject FOREIGN KEY (object1ObjectId) REFERENCES MedicalObject(objectId);

ALTER TABLE MedicalNeed
DROP COLUMN beneficiaryId;

DROP TABLE MedicalItem;

ALTER TABLE MedicalNeed
MODIFY COLUMN needId VARCHAR(191);

ALTER TABLE MedicalNeed
MODIFY COLUMN itemId VARCHAR(191);

