-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS "MedicalNeed";

CREATE TABLE "MedicalNeed" (
	"fooNeedId" INT NOT NULL,
	"fooItemId" INT NOT NULL,
	"fooBeneficiaryId" INT NOT NULL,
	"period" TIMESTAMP NOT NULL,
	"urgency" INT NOT NULL,
	"foo" INT NOT NULL,
	PRIMARY KEY("fooNeedId")
);
