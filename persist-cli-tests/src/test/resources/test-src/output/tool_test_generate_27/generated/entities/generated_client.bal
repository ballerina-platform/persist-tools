// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    isolated resource function get multipleassociations() returns stream<MultipleAssociations, persist:Error?> {
        return new ();
    }
    isolated resource function get multipleassociations/[int id]() returns MultipleAssociations|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function post multipleassociations(MultipleAssociationsInsert[] data) returns int[]|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function put multipleassociations/[int id](MultipleAssociationsUpdate value) returns MultipleAssociations|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function delete multipleassociations/[int id]() returns MultipleAssociations|persist:Error {
        return error persist:Error("unsupported operation");
    }

    isolated resource function get user() returns stream<User, persist:Error?> {
        return new ();
    }
    isolated resource function get user/[int id]() returns User|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function post user(UserInsert[] data) returns int[]|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function put user/[int id](UserUpdate value) returns User|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function delete user/[int id]() returns User|persist:Error {
        return error persist:Error("unsupported operation");
    }

    isolated resource function get profile() returns stream<Profile, persist:Error?> {
        return new ();
    }
    isolated resource function get profile/[int id]() returns Profile|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function post profile(ProfileInsert[] data) returns int[]|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function put profile/[int id](ProfileUpdate value) returns Profile|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function delete profile/[int id]() returns Profile|persist:Error {
        return error persist:Error("unsupported operation");
    }
}

