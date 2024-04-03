-- AUTO-GENERATED FILE.
-- This file is an auto-generated file by Ballerina persistence layer for the migrate command.
-- Please verify the generated scripts and execute them against the target DB server.

ALTER TABLE TypeNewFieldTest
ADD COLUMN charAnnot CHAR(20) NOT NULL;

ALTER TABLE TypeNewFieldTest
ADD COLUMN varcharAnnot VARCHAR(20) NOT NULL;

ALTER TABLE TypeNewFieldTest
ADD COLUMN decimalAnnot DECIMAL(20,2) NOT NULL;

ALTER TABLE TypeRemoveTest
MODIFY COLUMN charAnnot VARCHAR(191) NOT NULL;

ALTER TABLE TypeRemoveTest
MODIFY COLUMN varcharAnnot VARCHAR(191) NOT NULL;

ALTER TABLE TypeRemoveTest
MODIFY COLUMN decimalAnnot DECIMAL(65,30) NOT NULL;

ALTER TABLE TypeChangeTest
MODIFY COLUMN charAnnot CHAR(25) NOT NULL;

ALTER TABLE TypeChangeTest
MODIFY COLUMN varcharAnnot VARCHAR(25) NOT NULL;

ALTER TABLE TypeChangeTest
MODIFY COLUMN decimalAnnot DECIMAL(50,2) NOT NULL;

ALTER TABLE TypeChangeTest
MODIFY COLUMN charToVarchar VARCHAR(12) NOT NULL;

ALTER TABLE TypeChangeTest
MODIFY COLUMN varcharToChar CHAR(12) NOT NULL;

ALTER TABLE TypeExistingFieldTest
MODIFY COLUMN charAnnot CHAR(10) NOT NULL;

ALTER TABLE TypeExistingFieldTest
MODIFY COLUMN varcharAnnot VARCHAR(10) NOT NULL;

ALTER TABLE TypeExistingFieldTest
MODIFY COLUMN decimalAnnot DECIMAL(10,2) NOT NULL;

