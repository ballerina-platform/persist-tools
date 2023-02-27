-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for foo.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Profile;

CREATE TABLE Profile (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	isAdult BOOLEAN NOT NULL,
	salary DOUBLE NOT NULL,
	age DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE User (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	PRIMARY KEY(id)
);
