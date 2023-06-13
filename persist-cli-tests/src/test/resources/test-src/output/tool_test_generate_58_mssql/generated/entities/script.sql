-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS [CompositeAssociationRecord];
DROP TABLE IF EXISTS [AllTypesIdRecord];
DROP TABLE IF EXISTS [IntIdRecord];
DROP TABLE IF EXISTS [BooleanIdRecord];
DROP TABLE IF EXISTS [AllTypes];
DROP TABLE IF EXISTS [DecimalIdRecord];
DROP TABLE IF EXISTS [StringIdRecord];
DROP TABLE IF EXISTS [FloatIdRecord];

CREATE TABLE [FloatIdRecord] (
	[id] FLOAT NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	PRIMARY KEY([id])
);

CREATE TABLE [StringIdRecord] (
	[id] VARCHAR(191) NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	PRIMARY KEY([id])
);

CREATE TABLE [DecimalIdRecord] (
	[id] DECIMAL(38,30) NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	PRIMARY KEY([id])
);

CREATE TABLE [AllTypes] (
	[id] INT NOT NULL,
	[booleanType] BIT NOT NULL,
	[intType] INT NOT NULL,
	[floatType] FLOAT NOT NULL,
	[decimalType] DECIMAL(38,30) NOT NULL,
	[stringType] VARCHAR(191) NOT NULL,
	[byteArrayType] VARBINARY(MAX) NOT NULL,
	[dateType] DATE NOT NULL,
	[timeOfDayType] TIME NOT NULL,
	[utcType] DATETIME2 NOT NULL,
	[civilType] DATETIME2 NOT NULL,
	[booleanTypeOptional] BIT,
	[intTypeOptional] INT,
	[floatTypeOptional] FLOAT,
	[decimalTypeOptional] DECIMAL(38,30),
	[stringTypeOptional] VARCHAR(191),
	[byteArrayTypeOptional] VARBINARY(MAX),
	[dateTypeOptional] DATE,
	[timeOfDayTypeOptional] TIME,
	[utcTypeOptional] DATETIME2,
	[civilTypeOptional] DATETIME2,
	[enumType] VARCHAR(6) CHECK ([enumType] IN ('TYPE_1', 'TYPE_2', 'TYPE_3', 'TYPE_4')) NOT NULL,
	[enumTypeOptional] VARCHAR(6) CHECK ([enumTypeOptional] IN ('TYPE_1', 'TYPE_2', 'TYPE_3', 'TYPE_4')),
	PRIMARY KEY([id])
);

CREATE TABLE [BooleanIdRecord] (
	[id] BIT NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	PRIMARY KEY([id])
);

CREATE TABLE [IntIdRecord] (
	[id] INT NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	PRIMARY KEY([id])
);

CREATE TABLE [AllTypesIdRecord] (
	[booleanType] BIT NOT NULL,
	[intType] INT NOT NULL,
	[floatType] FLOAT NOT NULL,
	[decimalType] DECIMAL(38,30) NOT NULL,
	[stringType] VARCHAR(191) NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	PRIMARY KEY([booleanType],[intType],[floatType],[decimalType],[stringType])
);

CREATE TABLE [CompositeAssociationRecord] (
	[id] VARCHAR(191) NOT NULL,
	[randomField] VARCHAR(191) NOT NULL,
	[alltypesidrecordBooleanType] BIT NOT NULL,
	[alltypesidrecordIntType] INT NOT NULL,
	[alltypesidrecordFloatType] FLOAT NOT NULL,
	[alltypesidrecordDecimalType] DECIMAL(38,30) NOT NULL,
	[alltypesidrecordStringType] VARCHAR(191) NOT NULL,
	UNIQUE ([alltypesidrecordBooleanType], [alltypesidrecordIntType], [alltypesidrecordFloatType], [alltypesidrecordDecimalType], [alltypesidrecordStringType]),
	CONSTRAINT FK_ALL_TYPES_ID_RECORD FOREIGN KEY([alltypesidrecordBooleanType], [alltypesidrecordIntType], [alltypesidrecordFloatType], [alltypesidrecordDecimalType], [alltypesidrecordStringType]) REFERENCES [AllTypesIdRecord]([booleanType], [intType], [floatType], [decimalType], [stringType]),
	PRIMARY KEY([id])
);
