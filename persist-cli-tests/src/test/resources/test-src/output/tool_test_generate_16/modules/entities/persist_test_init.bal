// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final MockClient h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Employee";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Company";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Company" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Employee" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"companyId" INT NOT NULL,
	FOREIGN KEY("companyId") REFERENCES "Company"("id"),
	PRIMARY KEY("id")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Employee";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Company";`);
}

