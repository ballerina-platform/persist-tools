// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;
import ballerina/log;
import ballerina/persist;
import ballerina/url;

final Client dataserviceClient = check new ();

listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /api on dataserviceListener {

    resource function get departments() returns DepartmentList|DataserviceInternalError {
        Department[]|persist:Error items = from Department item in dataserviceClient->/departments(Department)
            select item;
        if items is persist:Error {
            return dataserviceInternalError(items);
        }
        return {items};
    }

    resource function get departments/[int id]() returns Department|DataserviceErrorResponse {
        Department|persist:Error item = dataserviceClient->/departments/[id];
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Department", string `${id}`);
        }
        return item;
    }

    resource function post departments(DepartmentInsert|DepartmentInsert[] payload)
            returns DepartmentCreated|DepartmentListCreated|DataserviceErrorResponse {
        DepartmentInsert[] values = payload is DepartmentInsert[] ? payload : [payload];
        int[]|persist:Error keys = dataserviceClient->/departments.post(values);
        if keys is persist:Error {
            return dataserviceErrorResponse(keys, "Department");
        }
        Department[] items = [];
        foreach int id in keys {
            Department|persist:Error item = dataserviceClient->/departments/[id];
            if item is persist:Error {
                return dataserviceErrorResponse(item, "Department", string `${id}`);
            }
            items.push(item);
        }
        if payload is DepartmentInsert[] {
            return {body: {items}};
        }
        int id = keys[0];
        return {headers: {"Location": string `/api/departments/${id}`}, body: items[0]};
    }

    resource function patch departments/[int id](DepartmentUpdate payload)
            returns Department|DataserviceErrorResponse {
        Department|persist:Error item = dataserviceClient->/departments/[id].put(payload);
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Department", string `${id}`);
        }
        return item;
    }

    resource function delete departments/[int id]() returns Department|DataserviceErrorResponse {
        Department|persist:Error item = dataserviceClient->/departments/[id].delete();
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Department", string `${id}`);
        }
        return item;
    }

    resource function get employees() returns EmployeeList|DataserviceInternalError {
        Employee[]|persist:Error items = from Employee item in dataserviceClient->/employees(Employee)
            select item;
        if items is persist:Error {
            return dataserviceInternalError(items);
        }
        return {items};
    }

    resource function get employees/[int id]() returns Employee|DataserviceErrorResponse {
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Employee", string `${id}`);
        }
        return item;
    }

    resource function post employees(EmployeeInsert|EmployeeInsert[] payload)
            returns EmployeeCreated|EmployeeListCreated|DataserviceErrorResponse {
        EmployeeInsert[] values = payload is EmployeeInsert[] ? payload : [payload];
        int[]|persist:Error keys = dataserviceClient->/employees.post(values);
        if keys is persist:Error {
            return dataserviceErrorResponse(keys, "Employee");
        }
        Employee[] items = [];
        foreach int id in keys {
            Employee|persist:Error item = dataserviceClient->/employees/[id];
            if item is persist:Error {
                return dataserviceErrorResponse(item, "Employee", string `${id}`);
            }
            items.push(item);
        }
        if payload is EmployeeInsert[] {
            return {body: {items}};
        }
        int id = keys[0];
        return {headers: {"Location": string `/api/employees/${id}`}, body: items[0]};
    }

    resource function patch employees/[int id](EmployeeUpdate payload)
            returns Employee|DataserviceErrorResponse {
        Employee|persist:Error item = dataserviceClient->/employees/[id].put(payload);
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Employee", string `${id}`);
        }
        return item;
    }

    resource function delete employees/[int id]() returns Employee|DataserviceErrorResponse {
        Employee|persist:Error item = dataserviceClient->/employees/[id].delete();
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Employee", string `${id}`);
        }
        return item;
    }

    resource function get assignments() returns AssignmentList|DataserviceInternalError {
        Assignment[]|persist:Error items = from Assignment item in dataserviceClient->/assignments(Assignment)
            select item;
        if items is persist:Error {
            return dataserviceInternalError(items);
        }
        return {items};
    }

    resource function get assignments/[string projectCode]/[int seq]() returns Assignment|DataserviceErrorResponse {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Assignment", string `${projectCode}/${seq}`);
        }
        return item;
    }

    resource function post assignments(AssignmentInsert|AssignmentInsert[] payload)
            returns AssignmentCreated|AssignmentListCreated|DataserviceErrorResponse {
        AssignmentInsert[] values = payload is AssignmentInsert[] ? payload : [payload];
        [string, int][]|persist:Error keys = dataserviceClient->/assignments.post(values);
        if keys is persist:Error {
            return dataserviceErrorResponse(keys, "Assignment");
        }
        Assignment[] items = [];
        foreach [string, int] [projectCode, seq] in keys {
            Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
            if item is persist:Error {
                return dataserviceErrorResponse(item, "Assignment", string `${projectCode}/${seq}`);
            }
            items.push(item);
        }
        if payload is AssignmentInsert[] {
            return {body: {items}};
        }
        [string, int] [projectCode, seq] = keys[0];
        return {headers: {"Location": string `/api/assignments/${dataserviceEncodePathSegment(projectCode)}/${seq}`}, body: items[0]};
    }

    resource function patch assignments/[string projectCode]/[int seq](AssignmentUpdate payload)
            returns Assignment|DataserviceErrorResponse {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq].put(payload);
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Assignment", string `${projectCode}/${seq}`);
        }
        return item;
    }

    resource function delete assignments/[string projectCode]/[int seq]() returns Assignment|DataserviceErrorResponse {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq].delete();
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Assignment", string `${projectCode}/${seq}`);
        }
        return item;
    }
}

# A list of `Department` records.
#
# + items - The records
public type DepartmentList record {|
    Department[] items;
|};

# The response for a created `Department` record.
#
# + body - The created record
public type DepartmentCreated record {|
    *http:Created;
    Department body;
|};

# The response for created `Department` records.
#
# + body - The created records
public type DepartmentListCreated record {|
    *http:Created;
    DepartmentList body;
|};

# A list of `Employee` records.
#
# + items - The records
public type EmployeeList record {|
    Employee[] items;
|};

# The response for a created `Employee` record.
#
# + body - The created record
public type EmployeeCreated record {|
    *http:Created;
    Employee body;
|};

# The response for created `Employee` records.
#
# + body - The created records
public type EmployeeListCreated record {|
    *http:Created;
    EmployeeList body;
|};

# A list of `Assignment` records.
#
# + items - The records
public type AssignmentList record {|
    Assignment[] items;
|};

# The response for a created `Assignment` record.
#
# + body - The created record
public type AssignmentCreated record {|
    *http:Created;
    Assignment body;
|};

# The response for created `Assignment` records.
#
# + body - The created records
public type AssignmentListCreated record {|
    *http:Created;
    AssignmentList body;
|};

# The response when no record has the given key.
#
# + body - The error
public type DataserviceNotFound record {|
    *http:NotFound;
    dataservice:DataserviceError body;
|};

# The response when a record with the key or a unique value already exists.
#
# + body - The error
public type DataserviceConflict record {|
    *http:Conflict;
    dataservice:DataserviceError body;
|};

# The response when a write violates a constraint.
#
# + body - The error
public type DataserviceConstraintViolation record {|
    *http:Conflict;
    dataservice:DataserviceError body;
|};

# The response for any other failure.
#
# + body - The error
public type DataserviceInternalError record {|
    *http:InternalServerError;
    dataservice:DataserviceError body;
|};

# An error response of a data service operation.
public type DataserviceErrorResponse DataserviceNotFound|DataserviceConflict
    |DataserviceConstraintViolation|DataserviceInternalError;

function dataserviceErrorResponse(persist:Error err, string entity, string key = "")
        returns DataserviceErrorResponse {
    if err is persist:NotFoundError {
        return <DataserviceNotFound>{
            body: {
                code: dataservice:NOT_FOUND,
                message: string `A record with the key '${key}' does not exist for the entity '${entity}'.`
            }
        };
    }
    if err is persist:AlreadyExistsError {
        return <DataserviceConflict>{
            body: {
                code: dataservice:CONFLICT,
                message: string `A record that conflicts with an existing record of the entity '${entity}' already exists.`
            }
        };
    }
    if err is persist:ConstraintViolationError {
        log:printError(string `Constraint violation on the entity '${entity}'.`, err);
        return <DataserviceConstraintViolation>{
            body: {
                code: dataservice:CONSTRAINT_VIOLATION,
                message: string `The operation violates a constraint of the entity '${entity}'.`
            }
        };
    }
    return dataserviceInternalError(err);
}

function dataserviceInternalError(persist:Error err) returns DataserviceInternalError {
    log:printError("Data service operation failed.", err);
    return {body: {code: dataservice:INTERNAL, message: "An internal error occurred."}};
}

function dataserviceEncodePathSegment(string value) returns string {
    string|url:Error encoded = url:encode(value, "UTF-8");
    return encoded is string ? re `\+`.replaceAll(encoded, "%20") : value;
}
