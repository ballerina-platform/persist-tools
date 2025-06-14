-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Workspace`;
DROP TABLE IF EXISTS `Employee`;
DROP TABLE IF EXISTS `Department`;
DROP TABLE IF EXISTS `Building`;

CREATE TABLE `Building` (
	`buildingCode` VARCHAR(191) NOT NULL,
	`city` VARCHAR(191) NOT NULL,
	`state` VARCHAR(191) NOT NULL,
	`country` VARCHAR(191) NOT NULL,
	`postalCode` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`buildingCode`)
);

CREATE TABLE `Department` (
	`deptNo` VARCHAR(191) NOT NULL,
	`deptName` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`deptNo`)
);

CREATE TABLE `Employee` (
	`empNo` VARCHAR(191) NOT NULL,
	`firstName` VARCHAR(191) NOT NULL,
	`lastName` VARCHAR(191) NOT NULL,
	`birthDate` DATE NOT NULL,
	`gender` VARCHAR(191) NOT NULL,
	`hireDate` DATE NOT NULL,
	`departmentDeptNo` VARCHAR(191) NOT NULL,
	FOREIGN KEY(`departmentDeptNo`) REFERENCES `Department`(`deptNo`),
	PRIMARY KEY(`empNo`)
);

CREATE TABLE `Workspace` (
	`workspaceId` VARCHAR(191) NOT NULL,
	`workspaceType` VARCHAR(191) NOT NULL,
	`locationBuildingCode` VARCHAR(191) NOT NULL,
	FOREIGN KEY(`locationBuildingCode`) REFERENCES `Building`(`buildingCode`),
	`employeeEmpNo` VARCHAR(191) UNIQUE NOT NULL,
	FOREIGN KEY(`employeeEmpNo`) REFERENCES `Employee`(`empNo`),
	PRIMARY KEY(`workspaceId`)
);


