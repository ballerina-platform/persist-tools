-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS "CompositeAssociationRecord";
DROP TABLE IF EXISTS "Comment";
DROP TABLE IF EXISTS "Follow";
DROP TABLE IF EXISTS "Employee";
DROP TABLE IF EXISTS "Post";
DROP TABLE IF EXISTS "Workspace";
DROP TABLE IF EXISTS "Building";
DROP TABLE IF EXISTS "IntIdRecord";
DROP TABLE IF EXISTS "User";
DROP TABLE IF EXISTS "AllTypes";
DROP TABLE IF EXISTS "DecimalIdRecord";
DROP TABLE IF EXISTS "StringIdRecord";
DROP TABLE IF EXISTS "AllTypesIdRecord";
DROP TABLE IF EXISTS "Department";
DROP TABLE IF EXISTS "BooleanIdRecord";
DROP TABLE IF EXISTS "OrderItem";
DROP TABLE IF EXISTS "FloatIdRecord";

CREATE TABLE "FloatIdRecord" (
	"id" FLOAT NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE "OrderItem" (
	"orderId" VARCHAR(191) NOT NULL,
	"itemId" VARCHAR(191) NOT NULL,
	"quantity" INT NOT NULL,
	"notes" VARCHAR(191) NOT NULL,
	PRIMARY KEY("orderId","itemId")
);

CREATE TABLE "BooleanIdRecord" (
	"id" BOOLEAN NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE "Department" (
	"deptNo" VARCHAR(191) NOT NULL,
	"deptName" VARCHAR(191) NOT NULL,
	PRIMARY KEY("deptNo")
);

CREATE TABLE "AllTypesIdRecord" (
	"booleanType" BOOLEAN NOT NULL,
	"intType" INT NOT NULL,
	"floatType" FLOAT NOT NULL,
	"decimalType" DECIMAL(65,30) NOT NULL,
	"stringType" VARCHAR(191) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("booleanType","intType","floatType","decimalType","stringType")
);

CREATE TABLE "StringIdRecord" (
	"id" VARCHAR(191) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE "DecimalIdRecord" (
	"id" DECIMAL(65,30) NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE "AllTypes" (
	"id" INT NOT NULL,
	"booleanType" BOOLEAN NOT NULL,
	"intType" INT NOT NULL,
	"floatType" FLOAT NOT NULL,
	"decimalType" DECIMAL(65,30) NOT NULL,
	"stringType" VARCHAR(191) NOT NULL,
	"byteArrayType" BYTEA NOT NULL,
	"dateType" DATE NOT NULL,
	"timeOfDayType" TIME NOT NULL,
	"utcType" TIMESTAMP NOT NULL,
	"civilType" TIMESTAMP NOT NULL,
	"booleanTypeOptional" BOOLEAN,
	"intTypeOptional" INT,
	"floatTypeOptional" FLOAT,
	"decimalTypeOptional" DECIMAL(65,30),
	"stringTypeOptional" VARCHAR(191),
	"byteArrayTypeOptional" BYTEA,
	"dateTypeOptional" DATE,
	"timeOfDayTypeOptional" TIME,
	"utcTypeOptional" TIMESTAMP,
	"civilTypeOptional" TIMESTAMP,
	"enumType" VARCHAR(6) CHECK ("enumType" IN ('TYPE_1', 'TYPE_2', 'TYPE_3', 'TYPE_4')) NOT NULL,
	"enumTypeOptional" VARCHAR(6) CHECK ("enumTypeOptional" IN ('TYPE_1', 'TYPE_2', 'TYPE_3', 'TYPE_4')),
	PRIMARY KEY("id")
);

CREATE TABLE "User" (
	"id" INT NOT NULL,
	"name" VARCHAR(191) NOT NULL,
	"birthDate" DATE NOT NULL,
	"mobileNumber" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE "IntIdRecord" (
	"id" INT NOT NULL,
	"randomField" VARCHAR(191) NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE "Building" (
	"buildingCode" VARCHAR(191) NOT NULL,
	"city" VARCHAR(191) NOT NULL,
	"state" VARCHAR(191) NOT NULL,
	"country" VARCHAR(191) NOT NULL,
	"postalCode" VARCHAR(191) NOT NULL,
	"type" VARCHAR(191) NOT NULL,
	PRIMARY KEY("buildingCode")
);

CREATE TABLE "Workspace" (
	"workspaceId" VARCHAR(191) NOT NULL,
	"workspaceType" VARCHAR(191) NOT NULL,
	"locationBuildingCode" VARCHAR(191) NOT NULL,
	FOREIGN KEY("locationBuildingCode") REFERENCES "Building"("buildingCode"),
	PRIMARY KEY("workspaceId")
);

CREATE TABLE "Post" (
	"id" INT NOT NULL,
	"description" VARCHAR(191) NOT NULL,
	"tags" VARCHAR(191) NOT NULL,
	"category" VARCHAR(10) CHECK ("category" IN ('FOOD', 'TRAVEL', 'fashion', 'SPORTS', 'TECHNOLOGY', 'OTHERS')) NOT NULL,
	"timestamp" TIMESTAMP NOT NULL,
	"userId" INT NOT NULL,
	FOREIGN KEY("userId") REFERENCES "User"("id"),
	PRIMARY KEY("id")
);

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
);

CREATE TABLE "Follow" (
	"id" INT NOT NULL,
	"timestamp" TIMESTAMP NOT NULL,
	"leaderId" INT NOT NULL,
	FOREIGN KEY("leaderId") REFERENCES "User"("id"),
	"followerId" INT NOT NULL,
	FOREIGN KEY("followerId") REFERENCES "User"("id"),
	PRIMARY KEY("id")
);

CREATE TABLE "Comment" (
	"id" INT NOT NULL,
	"comment" VARCHAR(191) NOT NULL,
	"timesteamp" TIMESTAMP NOT NULL,
	"userId" INT NOT NULL,
	FOREIGN KEY("userId") REFERENCES "User"("id"),
	"postId" INT NOT NULL,
	FOREIGN KEY("postId") REFERENCES "Post"("id"),
	PRIMARY KEY("id")
);

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
);
