// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;
import ballerina/log;
import ballerina/persist;

final Client dataserviceClient = check new ();

listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /hr\-api/v1\.0 on dataserviceListener {

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
}

# A list of `Department` records.
#
# + items - The records
public type DepartmentList record {|
    Department[] items;
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
