// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    isolated resource function get user() returns stream<User, persist:Error?> = external;
    isolated resource function get user/[int id]() returns User|persist:Error = external;
    isolated resource function post user(UserInsert[] data) returns int[]|persist:Error = external;
    isolated resource function put user/[int id](UserUpdate value) returns User|persist:Error = external;
    isolated resource function delete user/[int id]() returns User|persist:Error = external;
}

