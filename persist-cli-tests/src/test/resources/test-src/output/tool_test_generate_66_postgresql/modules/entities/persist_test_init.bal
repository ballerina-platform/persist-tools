// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;

isolated final H2Client h2Client = check new ("jdbc:h2:./test", "sa", "");

public isolated function setupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Comment";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Employee";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "CompositeAssociationRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Post";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Follow";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Workspace";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "FloatIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "OrderItem";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "BooleanIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Department";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "AllTypesIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "StringIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "DecimalIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "AllTypes";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "User";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "IntIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Building";`);
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
CREATE TABLE "IntIdRecord" (
	"id" INT NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "User" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"birthDate" DATE NOT NULL,
	"mobileNumber" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "AllTypes" (
	"id" INT NOT NULL,
	"booleanType" BOOLEAN NOT NULL,
	"intType" INT NOT NULL,
	"floatType" FLOAT NOT NULL,
	"decimalType" DECIMAL(65,30) NOT NULL,
	"stringType" VARCHAR(191) NOT NULL,
	"byteArrayType" LONGBLOB NOT NULL,
	"dateType" DATE NOT NULL,
	"timeOfDayType" TIME NOT NULL,
	"utcType" TIMESTAMP NOT NULL,
	"civilType" DATETIME NOT NULL,
	"booleanTypeOptional" BOOLEAN,
	"intTypeOptional" INT,
	"floatTypeOptional" FLOAT,
	"decimalTypeOptional" DECIMAL(65,30),
	"stringTypeOptional" VARCHAR(191),
	"byteArrayTypeOptional" LONGBLOB,
	"dateTypeOptional" DATE,
	"timeOfDayTypeOptional" TIME,
	"utcTypeOptional" TIMESTAMP,
	"civilTypeOptional" DATETIME,
	"enumType" VARCHAR(6) CHECK ("enumType" IN ('TYPE_1', 'TYPE_2', 'TYPE_3', 'TYPE_4')) NOT NULL,
	"enumTypeOptional" VARCHAR(6) CHECK ("enumTypeOptional" IN ('TYPE_1', 'TYPE_2', 'TYPE_3', 'TYPE_4')),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "DecimalIdRecord" (
	"id" DECIMAL(65,30) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "StringIdRecord" (
	"id" VARCHAR(191) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "AllTypesIdRecord" (
	"booleanType" BOOLEAN NOT NULL,
	"intType" INT NOT NULL,
	"floatType" FLOAT NOT NULL,
	"decimalType" DECIMAL(65,30) NOT NULL,
	"stringType" VARCHAR(191) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("booleanType","intType","floatType","decimalType","stringType")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Department" (
	"deptNo" VARCHAR(191) NOT NULL,
	"deptName" VARCHAR(191) NOT NULL,
	PRIMARY KEY("deptNo")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "BooleanIdRecord" (
	"id" BOOLEAN NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "OrderItem" (
	"orderId" VARCHAR(191) NOT NULL,
	"itemId" VARCHAR(191) NOT NULL,
	"quantity" INT NOT NULL,
	"notes" VARCHAR(191) NOT NULL,
	PRIMARY KEY("orderId","itemId")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "FloatIdRecord" (
	"id" FLOAT NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Workspace" (
	"workspaceId" VARCHAR(191) NOT NULL,
	"workspaceType" VARCHAR(191) NOT NULL,
	"locationBuildingCode" VARCHAR(191) NOT NULL,
	FOREIGN KEY("locationBuildingCode") REFERENCES "Building"("buildingCode"),
	PRIMARY KEY("workspaceId")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Follow" (
	"id" INT NOT NULL,
	"timestamp" DATETIME NOT NULL,
	"leaderId" INT NOT NULL,
	FOREIGN KEY("leaderId") REFERENCES "User"("id"),
	"followerId" INT NOT NULL,
	FOREIGN KEY("followerId") REFERENCES "User"("id"),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Post" (
	"id" INT NOT NULL,
	"description" VARCHAR(191) NOT NULL,
	"tags" VARCHAR(191) NOT NULL,
	"category" VARCHAR(10) CHECK ("category" IN ('FOOD', 'TRAVEL', 'fashion', 'SPORTS', 'TECHNOLOGY', 'OTHERS')) NOT NULL,
	"timestamp" DATETIME NOT NULL,
	"userId" INT NOT NULL,
	FOREIGN KEY("userId") REFERENCES "User"("id"),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "CompositeAssociationRecord" (
	"id" VARCHAR(191) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	"alltypesidrecordBooleanType" BOOLEAN NOT NULL,
	"alltypesidrecordIntType" INT NOT NULL,
	"alltypesidrecordFloatType" FLOAT NOT NULL,
	"alltypesidrecordDecimalType" DECIMAL(65,30) NOT NULL,
	"alltypesidrecordStringType" VARCHAR(191) NOT NULL,
	UNIQUE ("alltypesidrecordBooleanType", "alltypesidrecordIntType", "alltypesidrecordFloatType", "alltypesidrecordDecimalType", "alltypesidrecordStringType"),
	FOREIGN KEY("alltypesidrecordBooleanType", "alltypesidrecordIntType", "alltypesidrecordFloatType", "alltypesidrecordDecimalType", "alltypesidrecordStringType") REFERENCES "AllTypesIdRecord"("booleanType", "intType", "floatType", "decimalType", "stringType"),
	PRIMARY KEY("id")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Employee" (
	"empNo" VARCHAR(191) NOT NULL,
	"firstName" VARCHAR(191) NOT NULL,
	"lastName" VARCHAR(191) NOT NULL,
	"birthDate" DATE NOT NULL,
	"gender" VARCHAR(6) CHECK ("gender" IN ('MALE', 'FEMALE')) NOT NULL,
	"hireDate" DATE NOT NULL,
	"departmentDeptNo" VARCHAR(191) NOT NULL,
	FOREIGN KEY("departmentDeptNo") REFERENCES "Department"("deptNo"),
	"workspaceWorkspaceId" VARCHAR(191) NOT NULL,
	FOREIGN KEY("workspaceWorkspaceId") REFERENCES "Workspace"("workspaceId"),
	PRIMARY KEY("empNo")
);`);
    _ = check h2Client->executeNativeSQL(`
CREATE TABLE "Comment" (
	"id" INT NOT NULL,
	"comment" VARCHAR(191) NOT NULL,
	"timesteamp" DATETIME NOT NULL,
	"userId" INT NOT NULL,
	FOREIGN KEY("userId") REFERENCES "User"("id"),
	"postId" INT NOT NULL,
	FOREIGN KEY("postId") REFERENCES "Post"("id"),
	PRIMARY KEY("id")
);`);
}

public isolated function cleanupTestDB() returns persist:Error? {
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Comment";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Employee";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "CompositeAssociationRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Post";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Follow";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Workspace";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "FloatIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "OrderItem";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "BooleanIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Department";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "AllTypesIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "StringIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "DecimalIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "AllTypes";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "User";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "IntIdRecord";`);
    _ = check h2Client->executeNativeSQL(`DROP TABLE IF EXISTS "Building";`);
}

