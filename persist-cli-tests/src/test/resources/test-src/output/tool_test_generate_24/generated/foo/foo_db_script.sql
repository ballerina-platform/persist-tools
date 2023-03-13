-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for foo.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `ByteTest`;

CREATE TABLE `ByteTest` (
	`id` INT NOT NULL,
	`binary1` BINARY NOT NULL,
	`binaryOptional` BINARY,
	PRIMARY KEY(`id`)
);
