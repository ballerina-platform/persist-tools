-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

-- Please verify the foreign key constraint name before executing the query
ALTER TABLE Car
DROP FOREIGN KEY FK_Car_Engine;

ALTER TABLE Car
DROP COLUMN engineId;

ALTER TABLE Car
ADD COLUMN fittedengineId INT NOT NULL;

ALTER TABLE Car
ADD CONSTRAINT FK_Car_Engine FOREIGN KEY (fittedengineId) REFERENCES Engine(id);

