// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;

# A department.
public isolated service class DepartmentObject {
    private final readonly & Department row;

    isolated function init(Department row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Department`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `name` field of `Department`
    # + return - The value
    isolated resource function get name() returns string => self.row.name;
}

# A page of departments.
#
# + items - The departments
# + hasMore - Whether records exist after this page
public type DepartmentList record {
    DepartmentObject[] items;
    boolean hasMore;
};

# An employee.
public isolated service class EmployeeObject {
    private final readonly & Employee row;

    isolated function init(Employee row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Employee`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `name` field of `Employee`
    # + return - The value
    isolated resource function get name() returns string => self.row.name;

    # The `email` field of `Employee`
    # + return - The value
    isolated resource function get email() returns string? => self.row.email;

    # The `hiredOn` field of `Employee`, as an ISO 8601 date
    # + return - The value
    isolated resource function get hiredOn() returns string => dataservice:dateToString(self.row.hiredOn);

    # The key of the related 'Department'.
    # + return - The value
    isolated resource function get departmentId() returns int => self.row.departmentId;
}

# A page of employees.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeeList record {
    EmployeeObject[] items;
    boolean hasMore;
};

# An assignment.
public isolated service class AssignmentObject {
    private final readonly & Assignment row;

    isolated function init(Assignment row) {
        self.row = row.cloneReadOnly();
    }

    # The `projectCode` field of `Assignment`
    # + return - The value
    isolated resource function get projectCode() returns string => self.row.projectCode;

    # The `seq` field of `Assignment`
    # + return - The value
    isolated resource function get seq() returns int => self.row.seq;

    # The `hours` field of `Assignment`
    # + return - The value
    isolated resource function get hours() returns decimal => self.row.hours;

    # The key of the related 'Employee'.
    # + return - The value
    isolated resource function get employeeId() returns int => self.row.employeeId;
}

# A page of assignments.
#
# + items - The assignments
# + hasMore - Whether records exist after this page
public type AssignmentList record {
    AssignmentObject[] items;
    boolean hasMore;
};
