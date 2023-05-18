-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

CREATE TABLE MedicalObject (
    objId INT PRIMARY KEY,
    objName VARCHAR(191),
    objDecrip VARCHAR(191),
    inStock BOOLEAN
);

