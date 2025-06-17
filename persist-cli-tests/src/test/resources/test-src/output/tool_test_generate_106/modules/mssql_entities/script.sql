-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS [Car2];
DROP TABLE IF EXISTS [Car];
DROP TABLE IF EXISTS [User2];
DROP TABLE IF EXISTS [User];

CREATE TABLE [User] (
	[id] INT NOT NULL,
	[name] VARCHAR(191) NOT NULL,
	[nic] VARCHAR(191) NOT NULL,
	[salary] DECIMAL(38,30),
	PRIMARY KEY([id])
);

CREATE TABLE [User2] (
	[id] INT NOT NULL,
	[nic] VARCHAR(191) NOT NULL,
	[name] VARCHAR(191) NOT NULL,
	[salary] DECIMAL(38,30),
	PRIMARY KEY([id],[nic])
);

CREATE TABLE [Car] (
	[id] INT NOT NULL,
	[name] VARCHAR(191) NOT NULL,
	[model] VARCHAR(191) NOT NULL,
	[driverId] INT NOT NULL,
	FOREIGN KEY([driverId]) REFERENCES [User]([id]),
	PRIMARY KEY([id])
);

CREATE TABLE [Car2] (
	[id] INT NOT NULL,
	[name] VARCHAR(191) NOT NULL,
	[model] VARCHAR(191) NOT NULL,
	[driverId] INT NOT NULL,
	[driverNic] VARCHAR(191) NOT NULL,
	FOREIGN KEY([driverId], [driverNic]) REFERENCES [User2]([id], [nic]),
	PRIMARY KEY([id])
);


CREATE UNIQUE INDEX [unique_idx_driverid] ON [Car] ([driverId]);
CREATE UNIQUE INDEX [driver_idx] ON [Car2] ([driverId], [driverNic]);
