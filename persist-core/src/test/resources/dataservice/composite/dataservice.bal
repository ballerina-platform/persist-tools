// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/openapi;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@openapi:ServiceInfo {embed: true}
service /api on dataserviceListener {

    # Gets a page of assignments.
    #
    # + filter - The conditions that the assignments match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of assignments in the page
    # + offset - The number of assignments to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of assignments, or the error response
    resource function get assignments(AssignmentFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns AssignmentList|AssignmentPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceAssignmentEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Assignment");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceAssignmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Assignment");
        }
        stream<Assignment, persist:Error?> rows = dataserviceClient->/assignments(Assignment,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Assignment[]|persist:Error items = from Assignment item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            Assignment[] sources = items;
            map<anydata>[] partials = from Assignment item in sources
                select dataservice:narrow(item, dataserviceAssignmentEntity, fields);
            AssignmentPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <AssignmentPartialList>{items: converted, hasMore};
        }
        return <AssignmentList>{items, hasMore};
    }

    # Gets an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + fields - The fields to return; the key fields are always returned
    # + return - The assignment, or the error response
    resource function get assignments/[string projectCode]/[int seq](string[]? fields = ()) returns Assignment|AssignmentPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceAssignmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Assignment");
        }
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Assignment", [projectCode, seq]);
        }
        if fields is string[] {
            Assignment[] sources = [item];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceAssignmentEntity, fields)];
            AssignmentPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Assignment", [projectCode, seq]);
            }
            return partial;
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
                    dataservice:KeyValue[][] keyValues = keys;
                    dataservice:Query query = dataservice:keysQuery(dataserviceAssignmentEntity, keyValues);
                    stream<Assignment, persist:Error?> stored = dataserviceClient->/assignments(Assignment,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Assignment[] rows = check from Assignment item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceAssignmentEntity, keyValues);
                    items = rows;
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
    # + filter - The conditions that the rates match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of rates in the page
    # + offset - The number of rates to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of rates, or the error response
    resource function get rates(RateFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns RateList|RatePartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceRateEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Rate");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceRateEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Rate");
        }
        stream<Rate, persist:Error?> rows = dataserviceClient->/rates(Rate,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Rate[]|persist:Error items = from Rate item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            Rate[] sources = items;
            map<anydata>[] partials = from Rate item in sources
                select dataservice:narrow(item, dataserviceRateEntity, fields);
            RatePartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <RatePartialList>{items: converted, hasMore};
        }
        return <RateList>{items, hasMore};
    }

    # Gets a rate by its key.
    #
    # + amount - The amount of the rate
    # + factor - The factor of the rate
    # + active - The active of the rate
    # + fields - The fields to return; the key fields are always returned
    # + return - The rate, or the error response
    resource function get rates/[decimal amount]/[float factor]/[boolean active](string[]? fields = ()) returns Rate|RatePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey("Rate", [amount, factor, active]);
        if invalidKey is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: invalidKey};
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceRateEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Rate");
        }
        Rate|persist:Error item = dataserviceClient->/rates/[amount]/[factor]/[active];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Rate", [amount, factor, active]);
        }
        if fields is string[] {
            Rate[] sources = [item];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceRateEntity, fields)];
            RatePartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Rate", [amount, factor, active]);
            }
            return partial;
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
