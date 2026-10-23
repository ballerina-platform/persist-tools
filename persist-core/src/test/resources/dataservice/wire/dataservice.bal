// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;
import ballerina/time;

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

# Converts a `Document` to its JSON form.
#
# + value - The `Document` record
# + return - The JSON form
isolated function dataserviceToDocumentPayload(Document value) returns DocumentPayload => {
    id: value.id,
    title: value.title,
    issuedOn: dataservice:dateToString(value.issuedOn),
    expiresOn: let time:Date? expiresOn = value.expiresOn in expiresOn is () ? () : dataservice:dateToString(expiresOn),
    opensAt: dataservice:timeOfDayToString(value.opensAt),
    createdAt: dataservice:civilToString(value.createdAt),
    signedAt: let time:Utc? signedAt = value.signedAt in signedAt is () ? () : dataservice:utcToString(signedAt),
    content: dataservice:bytesToString(value.content),
    thumbnail: let byte[]? thumbnail = value.thumbnail in thumbnail is () ? () : dataservice:bytesToString(thumbnail),
    status: value.status
};

# Converts the JSON form of a `DocumentInsert` to the persist type.
#
# + wire - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function dataserviceFromDocumentInsertPayload(DocumentInsertPayload wire) returns DocumentInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(wire.issuedOn);
    if issuedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(issuedOn, "Document", "issuedOn");
    }
    time:Date?|dataservice:ConversionError expiresOn = let string? expiresOnText = wire.expiresOn in expiresOnText is () ? () : dataservice:dateFromString(expiresOnText);
    if expiresOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(expiresOn, "Document", "expiresOn");
    }
    time:TimeOfDay|dataservice:ConversionError opensAt = dataservice:timeOfDayFromString(wire.opensAt);
    if opensAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(opensAt, "Document", "opensAt");
    }
    time:Civil|dataservice:ConversionError createdAt = dataservice:civilFromString(wire.createdAt);
    if createdAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(createdAt, "Document", "createdAt");
    }
    time:Utc?|dataservice:ConversionError signedAt = let string? signedAtText = wire.signedAt in signedAtText is () ? () : dataservice:utcFromString(signedAtText);
    if signedAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(signedAt, "Document", "signedAt");
    }
    byte[]|dataservice:ConversionError content = dataservice:bytesFromString(wire.content);
    if content is dataservice:ConversionError {
        return dsHttp:toInvalidInput(content, "Document", "content");
    }
    byte[]?|dataservice:ConversionError thumbnail = let string? thumbnailText = wire.thumbnail in thumbnailText is () ? () : dataservice:bytesFromString(thumbnailText);
    if thumbnail is dataservice:ConversionError {
        return dsHttp:toInvalidInput(thumbnail, "Document", "thumbnail");
    }
    return {id: wire.id, title: wire.title, issuedOn, expiresOn, opensAt, createdAt, signedAt, content, thumbnail, status: wire.status};
}

# Converts the JSON form of a `DocumentUpdate` to the persist type.
#
# + wire - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function dataserviceFromDocumentUpdatePayload(DocumentUpdatePayload wire) returns DocumentUpdate|dsHttp:InvalidInput {
    DocumentUpdate value = {};
    string? title = wire?.title;
    if title !is () {
        value.title = title;
    }
    string? issuedOnText = wire?.issuedOn;
    if issuedOnText is string {
        time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(issuedOnText);
        if issuedOn is dataservice:ConversionError {
            return dsHttp:toInvalidInput(issuedOn, "Document", "issuedOn");
        }
        value.issuedOn = issuedOn;
    }
    if wire.hasKey("expiresOn") {
        string? expiresOnText = wire?.expiresOn;
        if expiresOnText is () {
            value.expiresOn = ();
        } else {
            time:Date|dataservice:ConversionError expiresOn = dataservice:dateFromString(expiresOnText);
            if expiresOn is dataservice:ConversionError {
                return dsHttp:toInvalidInput(expiresOn, "Document", "expiresOn");
            }
            value.expiresOn = expiresOn;
        }
    }
    string? opensAtText = wire?.opensAt;
    if opensAtText is string {
        time:TimeOfDay|dataservice:ConversionError opensAt = dataservice:timeOfDayFromString(opensAtText);
        if opensAt is dataservice:ConversionError {
            return dsHttp:toInvalidInput(opensAt, "Document", "opensAt");
        }
        value.opensAt = opensAt;
    }
    string? createdAtText = wire?.createdAt;
    if createdAtText is string {
        time:Civil|dataservice:ConversionError createdAt = dataservice:civilFromString(createdAtText);
        if createdAt is dataservice:ConversionError {
            return dsHttp:toInvalidInput(createdAt, "Document", "createdAt");
        }
        value.createdAt = createdAt;
    }
    if wire.hasKey("signedAt") {
        string? signedAtText = wire?.signedAt;
        if signedAtText is () {
            value.signedAt = ();
        } else {
            time:Utc|dataservice:ConversionError signedAt = dataservice:utcFromString(signedAtText);
            if signedAt is dataservice:ConversionError {
                return dsHttp:toInvalidInput(signedAt, "Document", "signedAt");
            }
            value.signedAt = signedAt;
        }
    }
    string? contentText = wire?.content;
    if contentText is string {
        byte[]|dataservice:ConversionError content = dataservice:bytesFromString(contentText);
        if content is dataservice:ConversionError {
            return dsHttp:toInvalidInput(content, "Document", "content");
        }
        value.content = content;
    }
    if wire.hasKey("thumbnail") {
        string? thumbnailText = wire?.thumbnail;
        if thumbnailText is () {
            value.thumbnail = ();
        } else {
            byte[]|dataservice:ConversionError thumbnail = dataservice:bytesFromString(thumbnailText);
            if thumbnail is dataservice:ConversionError {
                return dsHttp:toInvalidInput(thumbnail, "Document", "thumbnail");
            }
            value.thumbnail = thumbnail;
        }
    }
    Status? status = wire?.status;
    if status !is () {
        value.status = status;
    }
    return value;
}

# Converts a `Holiday` to its JSON form.
#
# + value - The `Holiday` record
# + return - The JSON form
isolated function dataserviceToHolidayPayload(Holiday value) returns HolidayPayload => {
    id: value.id,
    day: dataservice:dateToString(value.day)
};
