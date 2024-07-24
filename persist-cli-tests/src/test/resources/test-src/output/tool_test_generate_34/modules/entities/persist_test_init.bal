// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final MockClient h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Profile";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "User";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "User" (
	"id" INT NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Profile" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"gender" VARCHAR(191),
	"ownerId" INT UNIQUE NOT NULL,
	FOREIGN KEY("ownerId") REFERENCES "User"("id"),
	PRIMARY KEY("id")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Profile";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "User";`);
}

