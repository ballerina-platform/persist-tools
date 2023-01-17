// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    isolated resource function get vehicle() returns stream<Vehicle, persist:Error?> {
        return new ();
    }
    isolated resource function get vehicle/[int model]() returns Vehicle|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function post vehicle(VehicleInsert[] data) returns int[]|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function put vehicle/[int model](VehicleUpdate value) returns Vehicle|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function delete vehicle/[int model]() returns Vehicle|persist:Error {
        return error persist:Error("unsupported operation");
    }

    isolated resource function get company() returns stream<Company, persist:Error?> {
        return new ();
    }
    isolated resource function get company/[int id]() returns Company|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function post company(CompanyInsert[] data) returns int[]|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function put company/[int id](CompanyUpdate value) returns Company|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function delete company/[int id]() returns Company|persist:Error {
        return error persist:Error("unsupported operation");
    }

    isolated resource function get employee() returns stream<Employee, persist:Error?> {
        return new ();
    }
    isolated resource function get employee/[int id]() returns Employee|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function post employee(EmployeeInsert[] data) returns int[]|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function put employee/[int id](EmployeeUpdate value) returns Employee|persist:Error {
        return error persist:Error("unsupported operation");
    }
    isolated resource function delete employee/[int id]() returns Employee|persist:Error {
        return error persist:Error("unsupported operation");
    }
}

