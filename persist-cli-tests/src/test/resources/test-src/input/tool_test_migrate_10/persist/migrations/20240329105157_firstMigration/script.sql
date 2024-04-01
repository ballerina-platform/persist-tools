-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

CREATE TABLE Car (
    id INT PRIMARY KEY,
    make VARCHAR(191),
    model VARCHAR(191),
    year INT
);

