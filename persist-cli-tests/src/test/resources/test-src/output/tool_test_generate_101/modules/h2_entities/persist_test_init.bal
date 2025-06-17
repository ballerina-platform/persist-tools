// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final H2Client h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Car";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Car" (
	"id" INT NOT NULL,
	"make" VARCHAR(191) NOT NULL,
	"model" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Car";`);
}

