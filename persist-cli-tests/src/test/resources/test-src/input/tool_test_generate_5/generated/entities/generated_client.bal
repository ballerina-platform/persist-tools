// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    isolated resource function get medicalneed() returns stream<MedicalNeed, persist:Error?> = external;
    isolated resource function get medicalneed/[int needId]() returns MedicalNeed|persist:Error = external;
    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns int[]|persist:Error = external;
    isolated resource function put medicalneed/[int needId](MedicalNeed value) returns MedicalNeed|persist:Error = external;
    isolated resource function delete medicalneed/[int needId]() returns MedicalNeed|persist:Error = external;

    isolated resource function get medicalitem() returns stream<MedicalItem, persist:Error?> = external;
    isolated resource function get medicalitem/[int itemId]() returns MedicalItem|persist:Error = external;
    isolated resource function post medicalitem(MedicalItemInsert[] data) returns int[]|persist:Error = external;
    isolated resource function put medicalitem/[int itemId](MedicalItem value) returns MedicalItem|persist:Error = external;
    isolated resource function delete medicalitem/[int itemId]() returns MedicalItem|persist:Error = external;
}

