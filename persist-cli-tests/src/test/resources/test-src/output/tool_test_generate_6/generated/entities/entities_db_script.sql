-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for entities.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `DataType`;

CREATE TABLE `DataType` (
	`a` INT NOT NULL,
	`b1` VARCHAR(191) NOT NULL,
	`c1` INT NOT NULL,
	`d1` BOOLEAN NOT NULL,
	`e1` DOUBLE NOT NULL,
	`f1` DECIMAL(65,30) NOT NULL,
	`j1` TIMESTAMP NOT NULL,
	`k1` DATETIME NOT NULL,
	`l1` DATE NOT NULL,
	`m1` TIME NOT NULL,
	PRIMARY KEY(`a`)
);
