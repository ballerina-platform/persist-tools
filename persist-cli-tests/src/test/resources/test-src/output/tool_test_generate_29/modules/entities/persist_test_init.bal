// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final H2Client h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MedicalNeed";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "MedicalNeed" (
	"needId" INT NOT NULL,
	"itemId" INT NOT NULL,
	"beneficiaryId" INT NOT NULL,
	"period" DATETIME NOT NULL,
	"urgency" VARCHAR(191) NOT NULL,
	"quantity" INT NOT NULL,
	PRIMARY KEY("needId")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MedicalNeed";`);
}

