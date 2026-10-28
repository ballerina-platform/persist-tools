// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /api on dataserviceListener {

    # Gets a page of assignments.
    #
    # + return - A page of assignments, or the error response
    resource function get assignments() returns AssignmentList|dsHttp:InvalidInput|dsHttp:InternalError {
        Assignment[]|persist:Error items = from Assignment item in dataserviceClient->/assignments(Assignment)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + return - The assignment, or the error response
    resource function get assignments/[string projectCode]/[int seq]() returns Assignment|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Assignment", [projectCode, seq]);
        }
        return item;
    }

    # Creates one or more assignments.
    #
    # + payload - The assignment, or the assignments, to create
    # + return - The created assignment or assignments, or the error response
    resource function post assignments(AssignmentInsert|AssignmentInsert[] payload)
            returns AssignmentCreated|AssignmentListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        AssignmentInsert[] values = payload is AssignmentInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Assignment[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    [string, int][] keys = check dataserviceClient->/assignments.post(values);
                    foreach [string, int] [projectCode, seq] in keys {
                        Assignment item = check dataserviceClient->/assignments/[projectCode]/[seq];
                        items.push(item);
                    }
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Assignment");
            }
        }
        if payload is AssignmentInsert[] {
            return <AssignmentListCreated>{body: {items, hasMore: false}};
        }
        Assignment created = items[0];
        return <AssignmentCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/assignments", [created.projectCode, created.seq])},
            body: created
        };
    }

    # Updates an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + payload - The fields of the assignment to update
    # + return - The updated assignment, or the error response
    resource function patch assignments/[string projectCode]/[int seq](AssignmentUpdate payload)
            returns Assignment|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Assignment|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/assignments/[projectCode]/[seq];
        } else {
            item = dataserviceClient->/assignments/[projectCode]/[seq].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Assignment", [projectCode, seq]);
        }
        return item;
    }

    # Deletes an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + return - The deleted assignment, or the error response
    resource function delete assignments/[string projectCode]/[int seq]() returns Assignment|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Assignment", [projectCode, seq]);
        }
        return item;
    }

    # Gets a page of rates.
    #
    # + return - A page of rates, or the error response
    resource function get rates() returns RateList|dsHttp:InvalidInput|dsHttp:InternalError {
        Rate[]|persist:Error items = from Rate item in dataserviceClient->/rates(Rate)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a rate by its key.
    #
    # + amount - The amount of the rate
    # + factor - The factor of the rate
    # + active - The active of the rate
    # + return - The rate, or the error response
    resource function get rates/[decimal amount]/[float factor]/[boolean active]() returns Rate|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey("Rate", [amount, factor, active]);
        if invalidKey is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: invalidKey};
        }
        Rate|persist:Error item = dataserviceClient->/rates/[amount]/[factor]/[active];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Rate", [amount, factor, active]);
        }
        return item;
    }

    # Deletes a rate by its key.
    #
    # + amount - The amount of the rate
    # + factor - The factor of the rate
    # + active - The active of the rate
    # + return - The deleted rate, or the error response
    resource function delete rates/[decimal amount]/[float factor]/[boolean active]() returns Rate|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey("Rate", [amount, factor, active]);
        if invalidKey is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: invalidKey};
        }
        Rate|persist:Error item = dataserviceClient->/rates/[amount]/[factor]/[active].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Rate", [amount, factor, active]);
        }
        return item;
    }
}
