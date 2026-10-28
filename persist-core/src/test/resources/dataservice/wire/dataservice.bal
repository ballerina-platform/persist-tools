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
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

@openapi:ServiceInfo {embed: true}
service /api on dataserviceListener {

    # Gets a page of documents.
    #
    # + return - A page of documents, or the error response
    resource function get documents() returns DocumentList|dsHttp:InvalidInput|dsHttp:InternalError {
        DocumentPayload[]|persist:Error items = from Document item in dataserviceClient->/documents(Document)
            select transformDocumentToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a document by its key.
    #
    # + id - The id of the document
    # + return - The document, or the error response
    resource function get documents/[int id]() returns DocumentPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Document|persist:Error item = dataserviceClient->/documents/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Document", [id]);
        }
        return transformDocumentToPayload(item);
    }

    # Creates one or more documents.
    #
    # + payload - The document, or the documents, to create
    # + return - The created document or documents, or the error response
    resource function post documents(DocumentInsertPayload|DocumentInsertPayload[] payload)
            returns DocumentCreated|DocumentListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DocumentInsertPayload[] entries = payload is DocumentInsertPayload[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(entries.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        DocumentInsert[] values = [];
        foreach DocumentInsertPayload entry in entries {
            DocumentInsert|dsHttp:InvalidInput value = transformPayloadToDocumentInsert(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        DocumentPayload[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/documents.post(values);
                    foreach int id in keys {
                        Document item = check dataserviceClient->/documents/[id];
                        items.push(transformDocumentToPayload(item));
                    }
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Document");
            }
        }
        if payload is DocumentInsertPayload[] {
            return <DocumentListCreated>{body: {items, hasMore: false}};
        }
        DocumentPayload created = items[0];
        return <DocumentCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/documents", [created.id])},
            body: created
        };
    }

    # Updates a document by its key.
    #
    # + id - The id of the document
    # + payload - The fields of the document to update
    # + return - The updated document, or the error response
    resource function patch documents/[int id](DocumentUpdatePayload payload)
            returns DocumentPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DocumentUpdate|dsHttp:InvalidInput value = transformPayloadToDocumentUpdate(payload);
        if value is dsHttp:InvalidInput {
            return value;
        }
        Document|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/documents/[id];
        } else {
            item = dataserviceClient->/documents/[id].put(value);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Document", [id]);
        }
        return transformDocumentToPayload(item);
    }

    # Deletes a document by its key.
    #
    # + id - The id of the document
    # + return - The deleted document, or the error response
    resource function delete documents/[int id]() returns DocumentPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Document|persist:Error item = dataserviceClient->/documents/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Document", [id]);
        }
        return transformDocumentToPayload(item);
    }

    # Gets a page of holidays.
    #
    # + return - A page of holidays, or the error response
    resource function get holidays() returns HolidayList|dsHttp:InvalidInput|dsHttp:InternalError {
        HolidayPayload[]|persist:Error items = from Holiday item in dataserviceClient->/holidays(Holiday)
            select transformHolidayToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a holiday by its key.
    #
    # + id - The id of the holiday
    # + return - The holiday, or the error response
    resource function get holidays/[int id]() returns HolidayPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Holiday|persist:Error item = dataserviceClient->/holidays/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Holiday", [id]);
        }
        return transformHolidayToPayload(item);
    }
}
