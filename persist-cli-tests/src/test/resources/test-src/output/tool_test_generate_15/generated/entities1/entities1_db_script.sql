-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for entities1.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS Profile;
DROP TABLE IF EXISTS MultipleAssociations;

CREATE TABLE MultipleAssociations (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE Profile (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	multipleassociationsId INT UNIQUE NOT NULL,
	CONSTRAINT FK_PROFILE_MULTIPLEASSOCIATIONS FOREIGN KEY(multipleassociationsId) REFERENCES MultipleAssociations(id),
	PRIMARY KEY(id)
);
