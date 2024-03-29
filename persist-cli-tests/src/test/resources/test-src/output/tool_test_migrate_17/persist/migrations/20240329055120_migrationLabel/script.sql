-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

CREATE TABLE users (
    id INT PRIMARY KEY,
    user_name VARCHAR,
    addres_s VARCHAR,
    user_name VARCHAR,
    user_age INT,
    addres_s VARCHAR
);

CREATE TABLE t_cars (
    id INT PRIMARY KEY,
    car_make VARCHAR,
    year_manufactured INT,
    car_make VARCHAR,
    model VARCHAR,
    year_manufactured INT
);

ALTER TABLE cars
DROP COLUMN make;

ALTER TABLE cars
DROP COLUMN year_man;

ALTER TABLE User
DROP COLUMN name;

ALTER TABLE User
DROP COLUMN home_address;

