// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities1.
// It should not be modified by hand.

import ballerina/persist;

public client class Entities1Client {
    *persist:AbstractPersistClient;

    isolated resource function get multipleassociations() returns stream<MultipleAssociations, persist:Error?> = external;
    isolated resource function get multipleassociations/[int id]() returns MultipleAssociations|persist:Error = external;
    isolated resource function post multipleassociations(MultipleAssociationsInsert[] data) returns int[]|persist:Error = external;
    isolated resource function put multipleassociations/[int id](MultipleAssociations value) returns MultipleAssociations|persist:Error = external;
    isolated resource function delete multipleassociations/[int id]() returns MultipleAssociations|persist:Error = external;

    isolated resource function get profile() returns stream<Profile, persist:Error?> = external;
    isolated resource function get profile/[int id]() returns Profile|persist:Error = external;
    isolated resource function post profile(ProfileInsert[] data) returns int[]|persist:Error = external;
    isolated resource function put profile/[int id](Profile value) returns Profile|persist:Error = external;
    isolated resource function delete profile/[int id]() returns Profile|persist:Error = external;
}

