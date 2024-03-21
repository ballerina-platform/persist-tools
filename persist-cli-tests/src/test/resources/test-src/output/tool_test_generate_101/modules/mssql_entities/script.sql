-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS [Car];

CREATE TABLE [Car] (
	[id] INT NOT NULL,
	[make] VARCHAR(191) NOT NULL,
	[model] VARCHAR(191) NOT NULL,
	[ownerId] INT NOT NULL,
	FOREIGN KEY([ownerId]) REFERENCES [User]([id]),
	PRIMARY KEY([id])
);


