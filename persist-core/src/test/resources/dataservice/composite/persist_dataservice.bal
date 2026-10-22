// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /api on dataserviceListener {

    # Gets the assignments.
    #
    # + return - The assignments, or an internal error
    resource function get assignments() returns AssignmentList|dsHttp:InternalError {
        Assignment[]|persist:Error items = from Assignment item in dataserviceClient->/assignments(Assignment)
            select item;
        if items is persist:Error {
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets an assignment by its key.
    #
    # + return - The assignment, or the error response
    resource function get assignments/[string projectCode]/[int seq]() returns Assignment|dsHttp:ErrorResponse {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Assignment", [projectCode, seq]);
        }
        return item;
    }

    # Creates one assignment, or several from an array.
    #
    # + return - The created assignment or assignments, or the error response
    resource function post assignments(AssignmentInsert|AssignmentInsert[] payload)
            returns AssignmentCreated|AssignmentListCreated|dsHttp:ErrorResponse {
        AssignmentInsert[] values = payload is AssignmentInsert[] ? payload : [payload];
        Assignment[] items = [];
        if values.length() > 0 {
            [string, int][]|persist:Error keys = dataserviceClient->/assignments.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "Assignment");
            }
            foreach [string, int] [projectCode, seq] in keys {
                Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "Assignment", [projectCode, seq]);
                }
                items.push(item);
            }
        }
        if payload is AssignmentInsert[] {
            return <AssignmentListCreated>{body: {items}};
        }
        Assignment created = items[0];
        return <AssignmentCreated>{
            headers: {"Location": dsHttp:location("/api/assignments", [created.projectCode, created.seq])},
            body: created
        };
    }

    # Updates the given fields of an assignment.
    #
    # + return - The updated assignment, or the error response
    resource function patch assignments/[string projectCode]/[int seq](AssignmentUpdate payload) returns Assignment|dsHttp:ErrorResponse {
        Assignment|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/assignments/[projectCode]/[seq];
        } else {
            item = dataserviceClient->/assignments/[projectCode]/[seq].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Assignment", [projectCode, seq]);
        }
        return item;
    }

    # Deletes an assignment and returns it.
    #
    # + return - The deleted assignment, or the error response
    resource function delete assignments/[string projectCode]/[int seq]() returns Assignment|dsHttp:ErrorResponse {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Assignment", [projectCode, seq]);
        }
        return item;
    }

    # Gets the rates.
    #
    # + return - The rates, or an internal error
    resource function get rates() returns RateList|dsHttp:InternalError {
        Rate[]|persist:Error items = from Rate item in dataserviceClient->/rates(Rate)
            select item;
        if items is persist:Error {
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a rate by its key.
    #
    # + return - The rate, or the error response
    resource function get rates/[decimal amount]/[float factor]/[boolean active]() returns Rate|dsHttp:ErrorResponse {
        Rate|persist:Error item = dataserviceClient->/rates/[amount]/[factor]/[active];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Rate", [amount, factor, active]);
        }
        return item;
    }

    # Deletes a rate and returns it.
    #
    # + return - The deleted rate, or the error response
    resource function delete rates/[decimal amount]/[float factor]/[boolean active]() returns Rate|dsHttp:ErrorResponse {
        Rate|persist:Error item = dataserviceClient->/rates/[amount]/[factor]/[active].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Rate", [amount, factor, active]);
        }
        return item;
    }
}

# A list of assignments.
#
# + items - The assignments
public type AssignmentList record {|
    Assignment[] items;
|};

# The response to creating one assignment.
#
# + body - The created assignment
public type AssignmentCreated record {|
    *http:Created;
    Assignment body;
|};

# The response to creating several assignments.
#
# + body - The created assignments
public type AssignmentListCreated record {|
    *http:Created;
    AssignmentList body;
|};

# A list of rates.
#
# + items - The rates
public type RateList record {|
    Rate[] items;
|};
