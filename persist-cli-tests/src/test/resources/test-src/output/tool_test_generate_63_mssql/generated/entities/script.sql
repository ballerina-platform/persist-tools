-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS [Comment];
DROP TABLE IF EXISTS [Follow];
DROP TABLE IF EXISTS [Post];
DROP TABLE IF EXISTS [User];

CREATE TABLE [User] (
	[id] INT NOT NULL,
	[name] VARCHAR(191) NOT NULL,
	[birthDate] DATE NOT NULL,
	[mobileNumber] VARCHAR(191) NOT NULL,
	PRIMARY KEY([id])
);

CREATE TABLE [Post] (
	[id] INT NOT NULL,
	[description] VARCHAR(191) NOT NULL,
	[tags] VARCHAR(191) NOT NULL,
	[category] VARCHAR(191) NOT NULL,
	[timestamp] DATETIME2 NOT NULL,
	[userId] INT NOT NULL,
	CONSTRAINT FK_USER FOREIGN KEY([userId]) REFERENCES [User]([id]),
	PRIMARY KEY([id])
);

CREATE TABLE [Follow] (
	[id] INT NOT NULL,
	[timestamp] DATETIME2 NOT NULL,
	[leaderId] INT NOT NULL,
	CONSTRAINT FK_LEADER FOREIGN KEY([leaderId]) REFERENCES [User]([id]),
	[followerId] INT NOT NULL,
	CONSTRAINT FK_FOLLOWER FOREIGN KEY([followerId]) REFERENCES [User]([id]),
	PRIMARY KEY([id])
);

CREATE TABLE [Comment] (
	[id] INT NOT NULL,
	[comment] VARCHAR(191) NOT NULL,
	[timesteamp] DATETIME2 NOT NULL,
	[userId] INT NOT NULL,
	CONSTRAINT FK_USER FOREIGN KEY([userId]) REFERENCES [User]([id]),
	[postId] INT NOT NULL,
	CONSTRAINT FK_POST FOREIGN KEY([postId]) REFERENCES [Post]([id]),
	PRIMARY KEY([id])
);
