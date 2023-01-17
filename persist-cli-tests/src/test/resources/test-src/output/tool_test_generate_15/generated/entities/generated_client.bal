// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

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
}

