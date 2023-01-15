// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    isolated resource function get medicalneed() returns stream<MedicalNeed, persist:Error?> = external;
    isolated resource function get medicalneed/[string needId]() returns MedicalNeed|persist:Error = external;
    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns string[]|persist:Error = external;
    isolated resource function put medicalneed/[string needId](MedicalNeed value) returns MedicalNeed|persist:Error = external;
    isolated resource function delete medicalneed/[string needId]() returns MedicalNeed|persist:Error = external;
}

