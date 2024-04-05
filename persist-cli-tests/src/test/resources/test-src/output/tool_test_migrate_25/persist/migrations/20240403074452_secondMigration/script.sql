-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE test_3;

DROP TABLE test_5;


CREATE TABLE `test_3` (
	`nic` VARCHAR(191) NOT NULL,
	`user_name` VARCHAR(191) NOT NULL,
	`user_age` INT NOT NULL,
	`salary` DOUBLE NOT NULL,
	`isEmployed` BOOLEAN NOT NULL,
	PRIMARY KEY(`nic`)
);


CREATE TABLE `test_6` (
	`nic` VARCHAR(191) NOT NULL,
	`user_name` VARCHAR(191) NOT NULL,
	`user_age` INT NOT NULL,
	`salary` DOUBLE NOT NULL,
	`isEmployed` BOOLEAN NOT NULL,
	PRIMARY KEY(`nic`)
);

RENAME TABLE Test1 TO test_1;

RENAME TABLE test_2 TO Test2;

RENAME TABLE test_4 TO testT_4;

RENAME TABLE test_7 TO testT_7;

ALTER TABLE testT_7
ADD COLUMN newField VARCHAR(191) NOT NULL;

