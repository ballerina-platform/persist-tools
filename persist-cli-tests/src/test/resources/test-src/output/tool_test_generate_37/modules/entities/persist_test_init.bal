// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final H2Client h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Profile";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "User";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Dept";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Customer";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MultipleAssociations";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Student";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Student" (
	"id" INT NOT NULL,
	"firstName" VARCHAR(191) NOT NULL,
	"age" INT NOT NULL,
	"lastName" VARCHAR(191) NOT NULL,
	"nicNo" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id","firstName")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "MultipleAssociations" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Customer" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"age" INT NOT NULL,
	"multipleassociationsId" INT UNIQUE NOT NULL,
	FOREIGN KEY("multipleassociationsId") REFERENCES "MultipleAssociations"("id"),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Dept" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"multipleassociationsId" INT UNIQUE NOT NULL,
	FOREIGN KEY("multipleassociationsId") REFERENCES "MultipleAssociations"("id"),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "User" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"multipleassociationsId" INT UNIQUE NOT NULL,
	FOREIGN KEY("multipleassociationsId") REFERENCES "MultipleAssociations"("id"),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Profile" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"isAdult" BOOLEAN NOT NULL,
	"salary" FLOAT NOT NULL,
	"age" DECIMAL(65,30) NOT NULL,
	"isRegistered" LONGBLOB NOT NULL,
	"ownerId" INT UNIQUE NOT NULL,
	FOREIGN KEY("ownerId") REFERENCES "User"("id"),
	PRIMARY KEY("id")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Profile";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "User";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Dept";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Customer";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "MultipleAssociations";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Student";`);
}

