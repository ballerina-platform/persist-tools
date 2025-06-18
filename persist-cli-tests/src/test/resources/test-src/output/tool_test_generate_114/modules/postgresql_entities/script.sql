-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS "valueset_compose_include_concepts";
DROP TABLE IF EXISTS "valueset_compose_include_value_sets";
DROP TABLE IF EXISTS "concepts";
DROP TABLE IF EXISTS "valueset_compose_includes";
DROP TABLE IF EXISTS "codesystems";
DROP TABLE IF EXISTS "valuesets";

CREATE TABLE "valuesets" (
	"valueSetId"  SERIAL,
	"id" VARCHAR(191) NOT NULL,
	"url" VARCHAR(191) NOT NULL,
	"version" VARCHAR(191) NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"title" VARCHAR(191) NOT NULL,
	"status" VARCHAR(191) NOT NULL,
	"date" VARCHAR(191) NOT NULL,
	"publisher" VARCHAR(191) NOT NULL,
	"valueSet" BYTEA NOT NULL,
	PRIMARY KEY("valueSetId")
);

CREATE TABLE "codesystems" (
	"codeSystemId"  SERIAL,
	"id" VARCHAR(191) NOT NULL,
	"url" VARCHAR(191) NOT NULL,
	"version" VARCHAR(191) NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"title" VARCHAR(191) NOT NULL,
	"status" VARCHAR(191) NOT NULL,
	"date" VARCHAR(191) NOT NULL,
	"publisher" VARCHAR(191) NOT NULL,
	"codeSystem" BYTEA NOT NULL,
	PRIMARY KEY("codeSystemId")
);

CREATE TABLE "valueset_compose_includes" (
	"valueSetComposeIncludeId"  SERIAL,
	"systemFlag" BOOLEAN NOT NULL,
	"valueSetFlag" BOOLEAN NOT NULL,
	"conceptFlag" BOOLEAN NOT NULL,
	"codeSystemId" INT,
	"valuesetValueSetId" INT NOT NULL,
	FOREIGN KEY("valuesetValueSetId") REFERENCES "valuesets"("valueSetId"),
	PRIMARY KEY("valueSetComposeIncludeId")
);

CREATE TABLE "concepts" (
	"conceptId"  SERIAL,
	"code" VARCHAR(191) NOT NULL,
	"concept" BYTEA NOT NULL,
	"parentConceptId" INT,
	"codesystemCodeSystemId" INT NOT NULL,
	FOREIGN KEY("codesystemCodeSystemId") REFERENCES "codesystems"("codeSystemId"),
	PRIMARY KEY("conceptId")
);

CREATE TABLE "valueset_compose_include_value_sets" (
	"valueSetComposeIncludeValueSetId"  SERIAL,
	"valuesetcomposeValueSetComposeIncludeId" INT NOT NULL,
	FOREIGN KEY("valuesetcomposeValueSetComposeIncludeId") REFERENCES "valueset_compose_includes"("valueSetComposeIncludeId"),
	"valuesetValueSetId" INT NOT NULL,
	FOREIGN KEY("valuesetValueSetId") REFERENCES "valuesets"("valueSetId"),
	PRIMARY KEY("valueSetComposeIncludeValueSetId")
);

CREATE TABLE "valueset_compose_include_concepts" (
	"valueSetComposeIncludeConceptId"  SERIAL,
	"valuesetcomposeValueSetComposeIncludeId" INT NOT NULL,
	FOREIGN KEY("valuesetcomposeValueSetComposeIncludeId") REFERENCES "valueset_compose_includes"("valueSetComposeIncludeId"),
	"conceptConceptId" INT NOT NULL,
	FOREIGN KEY("conceptConceptId") REFERENCES "concepts"("conceptId"),
	PRIMARY KEY("valueSetComposeIncludeConceptId")
);


