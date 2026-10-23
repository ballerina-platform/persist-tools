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

    # Gets the documents.
    #
    # + return - The documents, or an internal error
    resource function get documents() returns DocumentList|dsHttp:InternalError {
        DocumentPayload[]|persist:Error items = from Document item in dataserviceClient->/documents(Document)
            select dataserviceToDocumentPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a document by its key.
    #
    # + return - The document, or the error response
    resource function get documents/[int id]() returns DocumentPayload|dsHttp:NotFound|dsHttp:InternalError {
        Document|persist:Error item = dataserviceClient->/documents/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Document", [id]);
        }
        return dataserviceToDocumentPayload(item);
    }

    # Creates one document, or several from an array.
    #
    # + return - The created document or documents, or the error response
    resource function post documents(DocumentInsertPayload|DocumentInsertPayload[] payload)
            returns DocumentCreated|DocumentListCreated|dsHttp:InvalidInput|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DocumentInsertPayload[] entries = payload is DocumentInsertPayload[] ? payload : [payload];
        DocumentInsert[] values = [];
        foreach DocumentInsertPayload entry in entries {
            DocumentInsert|dsHttp:InvalidInput value = dataserviceFromDocumentInsertPayload(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        DocumentPayload[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/documents.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "Document");
            }
            foreach int id in keys {
                Document|persist:Error item = dataserviceClient->/documents/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "Document", [id]);
                }
                items.push(dataserviceToDocumentPayload(item));
            }
        }
        if payload is DocumentInsertPayload[] {
            return <DocumentListCreated>{body: {items}};
        }
        DocumentPayload created = items[0];
        return <DocumentCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/documents", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a document.
    #
    # + return - The updated document, or the error response
    resource function patch documents/[int id](DocumentUpdatePayload payload)
            returns DocumentPayload|dsHttp:InvalidInput|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DocumentUpdate|dsHttp:InvalidInput value = dataserviceFromDocumentUpdatePayload(payload);
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
        return dataserviceToDocumentPayload(item);
    }

    # Deletes a document and returns it.
    #
    # + return - The deleted document, or the error response
    resource function delete documents/[int id]() returns DocumentPayload|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Document|persist:Error item = dataserviceClient->/documents/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Document", [id]);
        }
        return dataserviceToDocumentPayload(item);
    }

    # Gets the holidays.
    #
    # + return - The holidays, or an internal error
    resource function get holidays() returns HolidayList|dsHttp:InternalError {
        HolidayPayload[]|persist:Error items = from Holiday item in dataserviceClient->/holidays(Holiday)
            select dataserviceToHolidayPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a holiday by its key.
    #
    # + return - The holiday, or the error response
    resource function get holidays/[int id]() returns HolidayPayload|dsHttp:NotFound|dsHttp:InternalError {
        Holiday|persist:Error item = dataserviceClient->/holidays/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Holiday", [id]);
        }
        return dataserviceToHolidayPayload(item);
    }
}
