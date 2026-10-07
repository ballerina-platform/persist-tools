// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;
import ballerina/log;
import ballerina/persist;

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

    resource function get projects() returns ProjectList|DataserviceInternalError {
        Project[]|persist:Error items = from Project item in dataserviceClient->/projects(Project)
            select item;
        if items is persist:Error {
            return dataserviceInternalError(items);
        }
        return {items};
    }

    resource function get projects/[string code]() returns Project|DataserviceErrorResponse {
        Project|persist:Error item = dataserviceClient->/projects/[code];
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Project", string `${code}`);
        }
        return item;
    }

    resource function patch projects/[string code](ProjectUpdate payload)
            returns Project|DataserviceErrorResponse {
        Project|persist:Error item = dataserviceClient->/projects/[code].put(payload);
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Project", string `${code}`);
        }
        return item;
    }

    resource function delete projects/[string code]() returns Project|DataserviceErrorResponse {
        Project|persist:Error item = dataserviceClient->/projects/[code].delete();
        if item is persist:Error {
            return dataserviceErrorResponse(item, "Project", string `${code}`);
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

# A list of `Employee` records.
#
# + items - The records
public type EmployeeList record {|
    Employee[] items;
|};

# A list of `Project` records.
#
# + items - The records
public type ProjectList record {|
    Project[] items;
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
