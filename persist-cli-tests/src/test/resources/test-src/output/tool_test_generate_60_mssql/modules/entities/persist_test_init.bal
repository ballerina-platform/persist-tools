// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final MockClient h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Employee";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Workspace";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Building";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Department";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "OrderItem";`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "OrderItem" (
	"orderId" VARCHAR(191) NOT NULL,
	"itemId" VARCHAR(191) NOT NULL,
	"quantity" INT NOT NULL,
	"notes" VARCHAR(191) NOT NULL,
	PRIMARY KEY("orderId","itemId")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Department" (
	"deptNo" VARCHAR(191) NOT NULL,
	"deptName" VARCHAR(191) NOT NULL,
	"location" VARCHAR(191) NOT NULL,
	PRIMARY KEY("deptNo","deptName")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Building" (
	"buildingCode" VARCHAR(191) NOT NULL,
	"city" VARCHAR(191) NOT NULL,
	"state" VARCHAR(191) NOT NULL,
	"country" VARCHAR(191) NOT NULL,
	"postalCode" VARCHAR(191) NOT NULL,
	"type" VARCHAR(191) NOT NULL,
	PRIMARY KEY("buildingCode")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Workspace" (
	"workspaceId" VARCHAR(191) NOT NULL,
	"workspaceType" VARCHAR(191) NOT NULL,
	"locationBuildingCode" VARCHAR(191) NOT NULL,
	FOREIGN KEY("locationBuildingCode") REFERENCES "Building"("buildingCode"),
	PRIMARY KEY("workspaceId","workspaceType")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Employee" (
	"empNo" VARCHAR(191) NOT NULL,
	"firstName" VARCHAR(191) NOT NULL,
	"lastName" VARCHAR(191) NOT NULL,
	"birthDate" DATE NOT NULL,
	"gender" VARCHAR(191) NOT NULL,
	"hireDate" DATE NOT NULL,
	"departmentDeptNo" VARCHAR(191) NOT NULL,
	"departmentDeptName" VARCHAR(191) NOT NULL,
	FOREIGN KEY("departmentDeptNo", "departmentDeptName") REFERENCES "Department"("deptNo", "deptName"),
	"workspaceWorkspaceId" VARCHAR(191) NOT NULL,
	"workspaceWorkspaceType" VARCHAR(191) NOT NULL,
	FOREIGN KEY("workspaceWorkspaceId", "workspaceWorkspaceType") REFERENCES "Workspace"("workspaceId", "workspaceType"),
	PRIMARY KEY("empNo","firstName")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Employee";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Workspace";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Building";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Department";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "OrderItem";`);
}

