// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for foo.
// It should not be modified by hand.

import ballerina/persist;

public client class RainierClient {
    *persist:AbstractPersistClient;

    isolated resource function get building() returns stream<Building, persist:Error?> = external;
    isolated resource function get building/[string buildingCode]() returns Building|persist:Error = external;
    isolated resource function post building(BuildingInsert[] data) returns string[]|persist:Error = external;
    isolated resource function put building/[string buildingCode](BuildingUpdate value) returns Building|persist:Error = external;
    isolated resource function delete building/[string buildingCode]() returns Building|persist:Error = external;

    isolated resource function get department() returns stream<Department, persist:Error?> = external;
    isolated resource function get department/[string deptNo]() returns Department|persist:Error = external;
    isolated resource function post department(DepartmentInsert[] data) returns string[]|persist:Error = external;
    isolated resource function put department/[string deptNo](DepartmentUpdate value) returns Department|persist:Error = external;
    isolated resource function delete department/[string deptNo]() returns Department|persist:Error = external;

    isolated resource function get employee() returns stream<Employee, persist:Error?> = external;
    isolated resource function get employee/[string empNo]() returns Employee|persist:Error = external;
    isolated resource function post employee(EmployeeInsert[] data) returns string[]|persist:Error = external;
    isolated resource function put employee/[string empNo](EmployeeUpdate value) returns Employee|persist:Error = external;
    isolated resource function delete employee/[string empNo]() returns Employee|persist:Error = external;

    isolated resource function get workspace() returns stream<Workspace, persist:Error?> = external;
    isolated resource function get workspace/[string workspaceId]() returns Workspace|persist:Error = external;
    isolated resource function post workspace(WorkspaceInsert[] data) returns string[]|persist:Error = external;
    isolated resource function put workspace/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error = external;
    isolated resource function delete workspace/[string workspaceId]() returns Workspace|persist:Error = external;
}
