import ballerina/persist;

public client class fooClient {
    isolated resource function get building() returns stream<Building, persist:Error?> = external;
}