// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;

public client class EntitiesClient {
    *persist:AbstractPersistClient;

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

