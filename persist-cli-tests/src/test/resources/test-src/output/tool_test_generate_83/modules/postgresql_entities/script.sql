-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS "Car";
DROP TABLE IF EXISTS "User";

CREATE TABLE "User" (
	"id" INT NOT NULL,
	"nic" VARCHAR(191) NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"gender" VARCHAR(6) CHECK ("gender" IN ('MALE', 'FEMALE')) NOT NULL,
	"salary" DECIMAL(65,30),
	PRIMARY KEY("id","nic")
);

CREATE TABLE "Car" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"model" VARCHAR(191) NOT NULL,
	"OWNER_ID" INT NOT NULL,
	"OWNER_NIC" VARCHAR(191) NOT NULL,
	FOREIGN KEY("OWNER_ID", "OWNER_NIC") REFERENCES "User"("id", "nic"),
	PRIMARY KEY("id")
);


CREATE INDEX "ownerId" ON "Car" ("OWNER_ID");
CREATE INDEX "ownerNic" ON "Car" ("OWNER_NIC");
