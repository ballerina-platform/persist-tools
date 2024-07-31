// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final H2Client h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "DataType";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "DataType" (
	"a" INT NOT NULL,
	"b1" VARCHAR(191) NOT NULL,
	"c1" INT NOT NULL,
	"d1" BOOLEAN NOT NULL,
	"bA" LONGBLOB NOT NULL,
	"e1" FLOAT NOT NULL,
	"f1" DECIMAL(65,30) NOT NULL,
	"j1" TIMESTAMP NOT NULL,
	"k1" DATETIME NOT NULL,
	"l1" DATE NOT NULL,
	"m1" TIME NOT NULL,
	PRIMARY KEY("a")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "DataType";`);
}

