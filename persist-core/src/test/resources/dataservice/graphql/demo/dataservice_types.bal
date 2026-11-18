// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;

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

# The conditions that departments match; every condition set must hold.
public type DepartmentFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `name`
    dsGraphql:StringFilter? name;
|};

# One entry of the order of departments; set exactly one field.
public type DepartmentSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `name`
    dsGraphql:SortDirection? name;
|};

# The fields of `Department` that callers can see.
final readonly & dataservice:Field[] dataserviceDepartmentFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true, generated: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `Department` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceDepartmentEntity = {
    name: "Department",
    collection: "departments",
    'resource: "departments",
    datastore: dataservice:H2,
    fields: dataserviceDepartmentFields,
    relations: [
        {
            'field: "employees",
            many: true,
            fields: ["id"],
            targetFields: ["departmentId"],
            target: {
                name: "Employee",
                collection: "employees",
                'resource: "employees",
                datastore: dataservice:H2,
                fields: dataserviceEmployeeFields
            }
        }
    ]
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

# The conditions that employees match; every condition set must hold.
public type EmployeeFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `name`
    dsGraphql:StringFilter? name;
    # The conditions on `email`
    dsGraphql:StringNullableFilter? email;
    # The conditions on `hiredOn`
    dsGraphql:DateFilter? hiredOn;
    # The conditions on `departmentId`
    dsGraphql:IntFilter? departmentId;
|};

# One entry of the order of employees; set exactly one field.
public type EmployeeSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `name`
    dsGraphql:SortDirection? name;
    # Sorts by `email`
    dsGraphql:SortDirection? email;
    # Sorts by `hiredOn`
    dsGraphql:SortDirection? hiredOn;
    # Sorts by `departmentId`
    dsGraphql:SortDirection? departmentId;
|};

# The fields of `Employee` that callers can see.
final readonly & dataservice:Field[] dataserviceEmployeeFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING},
    {name: "email", column: "email", 'type: dataservice:STRING, nullable: true},
    {name: "hiredOn", column: "hiredOn", 'type: dataservice:DATE},
    {name: "departmentId", column: "departmentId", 'type: dataservice:INT}
];

# The description of `Employee` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceEmployeeEntity = {
    name: "Employee",
    collection: "employees",
    'resource: "employees",
    datastore: dataservice:H2,
    fields: dataserviceEmployeeFields,
    relations: [
        {
            'field: "assignments",
            many: true,
            fields: ["id"],
            targetFields: ["employeeId"],
            target: {
                name: "Assignment",
                collection: "assignments",
                'resource: "assignments",
                datastore: dataservice:H2,
                fields: dataserviceAssignmentFields
            }
        }
    ]
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

# The conditions that assignments match; every condition set must hold.
public type AssignmentFilter record {|
    # The conditions on `projectCode`
    dsGraphql:StringFilter? projectCode;
    # The conditions on `seq`
    dsGraphql:IntFilter? seq;
    # The conditions on `hours`
    dsGraphql:DecimalFilter? hours;
    # The conditions on `employeeId`
    dsGraphql:IntFilter? employeeId;
|};

# One entry of the order of assignments; set exactly one field.
public type AssignmentSort record {|
    # Sorts by `projectCode`
    dsGraphql:SortDirection? projectCode;
    # Sorts by `seq`
    dsGraphql:SortDirection? seq;
    # Sorts by `hours`
    dsGraphql:SortDirection? hours;
    # Sorts by `employeeId`
    dsGraphql:SortDirection? employeeId;
|};

# The fields of `Assignment` that callers can see.
final readonly & dataservice:Field[] dataserviceAssignmentFields = [
    {name: "projectCode", column: "projectCode", 'type: dataservice:STRING, key: true},
    {name: "seq", column: "seq", 'type: dataservice:INT, key: true},
    {name: "hours", column: "hours", 'type: dataservice:DECIMAL},
    {name: "employeeId", column: "employeeId", 'type: dataservice:INT}
];

# The description of `Assignment` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceAssignmentEntity = {
    name: "Assignment",
    collection: "assignments",
    'resource: "assignments",
    datastore: dataservice:H2,
    fields: dataserviceAssignmentFields,
    relations: [
        {
            'field: "employee",
            many: false,
            fields: ["employeeId"],
            targetFields: ["id"],
            target: {
                name: "Employee",
                collection: "employees",
                'resource: "employees",
                datastore: dataservice:H2,
                fields: dataserviceEmployeeFields
            }
        }
    ]
};
