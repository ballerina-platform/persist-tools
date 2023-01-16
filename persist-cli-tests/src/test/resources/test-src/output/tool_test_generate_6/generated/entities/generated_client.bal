// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    isolated resource function get datatype() returns stream<DataType, persist:Error?> = external;
    isolated resource function get datatype/[int a]() returns DataType|persist:Error = external;
    isolated resource function post datatype(DataTypeInsert[] data) returns int[]|persist:Error = external;
    isolated resource function put datatype/[int a](DataTypeUpdate value) returns DataType|persist:Error = external;
    isolated resource function delete datatype/[int a]() returns DataType|persist:Error = external;
}

