-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

CREATE TABLE cars (
    id INT PRIMARY KEY,
    make VARCHAR,
    model VARCHAR,
    year_man INT,
    home_address VARCHAR,
);

ALTER TABLE User
ADD COLUMN user_age INT;

ALTER TABLE User
ADD COLUMN home_address VARCHAR;

ALTER TABLE User
DROP COLUMN age;

