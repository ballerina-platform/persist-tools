// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/persist;
import ballerina/time;

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

    # Gets the employees of a department.
    #
    # + filter - The conditions that the employees match
    # + sort - The order of the employees; each entry names one field
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + return - A page of the employees, or the error
    @graphql:ResourceConfig {complexity: 20}
    isolated resource function get employees(EmployeeFilter? filter = (), EmployeeSort[]? sort = (),
            int? size = (), int? offset = ()) returns EmployeeRelatedList?|error {
        dataservice:Query query = check dsGraphql:toRelatedQuery(dataserviceDepartmentEntity, "employees", self.row,
                dataservicePageConfig, filter, sort, size, offset);
        Employee[]|persist:Error items = dataserviceClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        if items is persist:Error {
            return dsGraphql:toListError(items, "Department", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Employee item in items
                select new EmployeeRelatedObject(item), hasMore};
    }
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

# The fields to create a department with.
public type DepartmentInsertInput record {|
    # The `name` field of `Department`
    string name;
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

isolated function transformInputToDepartmentInsert(DepartmentInsertInput value) returns DepartmentInsert {
    return {name: value.name};
}

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

    # Gets the assignments of an employee.
    #
    # + filter - The conditions that the assignments match
    # + sort - The order of the assignments; each entry names one field
    # + size - The number of assignments in the page
    # + offset - The number of assignments to skip
    # + return - A page of the assignments, or the error
    @graphql:ResourceConfig {complexity: 20}
    isolated resource function get assignments(AssignmentFilter? filter = (), AssignmentSort[]? sort = (),
            int? size = (), int? offset = ()) returns AssignmentRelatedList?|error {
        dataservice:Query query = check dsGraphql:toRelatedQuery(dataserviceEmployeeEntity, "assignments", self.row,
                dataservicePageConfig, filter, sort, size, offset);
        Assignment[]|persist:Error items = dataserviceClient->/assignments(Assignment,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        if items is persist:Error {
            return dsGraphql:toListError(items, "Employee", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Assignment item in items
                select new AssignmentRelatedObject(item), hasMore};
    }
}

# A page of employees.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeeList record {
    EmployeeObject[] items;
    boolean hasMore;
};

# An employee, as a related record: its own fields and no relation fields.
public isolated service class EmployeeRelatedObject {
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

# A page of related employees.
#
# + items - The employees
# + hasMore - Whether records exist after this page; read them with `offset`
public type EmployeeRelatedList record {
    EmployeeRelatedObject[] items;
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

# The fields to create an employee with.
public type EmployeeInsertInput record {|
    # The `id` field of `Employee`
    int id;
    # The `name` field of `Employee`
    string name;
    # The `email` field of `Employee`
    string? email;
    # The `hiredOn` field of `Employee`, as an ISO 8601 date
    string hiredOn;
    # The key of the related 'Department'.
    int departmentId;
|};

# The fields to change in an employee; a field left out is not changed.
public type EmployeeUpdateInput record {|
    # The `name` field of `Employee`
    string? name?;
    # The `email` field of `Employee`
    string? email?;
    # The `hiredOn` field of `Employee`, as an ISO 8601 date
    string? hiredOn?;
    # The key of the related 'Department'.
    int? departmentId?;
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

isolated function transformInputToEmployeeInsert(EmployeeInsertInput value) returns EmployeeInsert|dsGraphql:Error {
    time:Date|dataservice:ConversionError hiredOn = dataservice:dateFromString(value.hiredOn);
    if hiredOn is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(hiredOn, "Employee", "hiredOn");
    }
    return {id: value.id, name: value.name, email: value.email, hiredOn, departmentId: value.departmentId};
}

isolated function transformInputToEmployeeUpdate(EmployeeUpdateInput value) returns EmployeeUpdate|dsGraphql:Error {
    EmployeeUpdate update = {};
    if value.hasKey("name") {
        string? name = value?.name;
        if name is () {
            return dsGraphql:toNullError("Employee", "name");
        }
        update.name = name;
    }
    if value.hasKey("email") {
        update.email = value?.email;
    }
    if value.hasKey("hiredOn") {
        string? hiredOnText = value?.hiredOn;
        if hiredOnText is () {
            return dsGraphql:toNullError("Employee", "hiredOn");
        }
        time:Date|dataservice:ConversionError hiredOn = dataservice:dateFromString(hiredOnText);
        if hiredOn is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(hiredOn, "Employee", "hiredOn");
        }
        update.hiredOn = hiredOn;
    }
    if value.hasKey("departmentId") {
        int? departmentId = value?.departmentId;
        if departmentId is () {
            return dsGraphql:toNullError("Employee", "departmentId");
        }
        update.departmentId = departmentId;
    }
    return update;
}

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

    isolated function prefetchEmployee(graphql:Context ctx) {
        ctx.getDataLoader("Assignment.employee").add({"employeeId": self.row.employeeId});
    }

    # Gets the employee of an assignment.
    #
    # + ctx - The request context, which holds the dataloader of the relation
    # + return - The employee, `()` when there is none, or the error
    @graphql:ResourceConfig {prefetchMethodName: "prefetchEmployee"}
    isolated resource function get employee(graphql:Context ctx) returns EmployeeRelatedObject?|error {
        Employee? row = check ctx.getDataLoader("Assignment.employee").get({"employeeId": self.row.employeeId});
        return row is Employee ? new (row) : ();
    }
}

# A page of assignments.
#
# + items - The assignments
# + hasMore - Whether records exist after this page
public type AssignmentList record {
    AssignmentObject[] items;
    boolean hasMore;
};

# An assignment, as a related record: its own fields and no relation fields.
public isolated service class AssignmentRelatedObject {
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

# A page of related assignments.
#
# + items - The assignments
# + hasMore - Whether records exist after this page; read them with `offset`
public type AssignmentRelatedList record {
    AssignmentRelatedObject[] items;
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

# The fields to create an assignment with.
public type AssignmentInsertInput record {|
    # The `projectCode` field of `Assignment`
    string projectCode;
    # The `seq` field of `Assignment`
    int seq;
    # The `hours` field of `Assignment`
    decimal hours;
    # The key of the related 'Employee'.
    int employeeId;
|};

# The fields to change in an assignment; a field left out is not changed.
public type AssignmentUpdateInput record {|
    # The `hours` field of `Assignment`
    decimal? hours?;
    # The key of the related 'Employee'.
    int? employeeId?;
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

isolated function transformInputToAssignmentInsert(AssignmentInsertInput value) returns AssignmentInsert {
    return {projectCode: value.projectCode, seq: value.seq, hours: value.hours, employeeId: value.employeeId};
}

isolated function transformInputToAssignmentUpdate(AssignmentUpdateInput value) returns AssignmentUpdate|dsGraphql:Error {
    AssignmentUpdate update = {};
    if value.hasKey("hours") {
        decimal? hours = value?.hours;
        if hours is () {
            return dsGraphql:toNullError("Assignment", "hours");
        }
        update.hours = hours;
    }
    if value.hasKey("employeeId") {
        int? employeeId = value?.employeeId;
        if employeeId is () {
            return dsGraphql:toNullError("Assignment", "employeeId");
        }
        update.employeeId = employeeId;
    }
    return update;
}
