// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final H2Client h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MedicalNeed";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MedicalItem";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "MedicalItem" (
	"itemId" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"type" VARCHAR(191) NOT NULL,
	"unit" INT NOT NULL,
	PRIMARY KEY("itemId")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "MedicalNeed" (
	"needId" INT NOT NULL,
	"itemId" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"beneficiaryId" INT NOT NULL,
	"period" DATETIME NOT NULL,
	"urgency" VARCHAR(191) NOT NULL,
	"quantity" VARCHAR(191) NOT NULL,
	PRIMARY KEY("needId")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MedicalNeed";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MedicalItem";`);
}

