-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE Car
ADD COLUMN ownerId INT NOT NULL;

ALTER TABLE Car
ADD COLUMN ownerNic VARCHAR(191) NOT NULL;

ALTER TABLE Car
ADD CONSTRAINT FK_Car_Person FOREIGN KEY (ownerId, ownerNic) REFERENCES Person(id, nic);

