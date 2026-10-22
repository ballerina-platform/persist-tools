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
        DocumentRecord[]|persist:Error items = from Document item in dataserviceClient->/documents(Document)
            select dataserviceToDocumentRecord(item);
        if items is persist:Error {
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a document by its key.
    #
    # + return - The document, or the error response
    resource function get documents/[int id]() returns DocumentRecord|dsHttp:ErrorResponse {
        Document|persist:Error item = dataserviceClient->/documents/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Document", [id]);
        }
        return dataserviceToDocumentRecord(item);
    }

    # Creates one document, or several from an array.
    #
    # + return - The created document or documents, or the error response
    resource function post documents(DocumentInsertRecord|DocumentInsertRecord[] payload)
            returns DocumentCreated|DocumentListCreated|dsHttp:ErrorResponse {
        DocumentInsertRecord[] entries = payload is DocumentInsertRecord[] ? payload : [payload];
        DocumentInsert[] values = [];
        foreach DocumentInsertRecord entry in entries {
            DocumentInsert|dsHttp:InvalidInput value = dataserviceFromDocumentInsertRecord(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        DocumentRecord[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/documents.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "Document");
            }
            foreach int id in keys {
                Document|persist:Error item = dataserviceClient->/documents/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "Document", [id]);
                }
                items.push(dataserviceToDocumentRecord(item));
            }
        }
        if payload is DocumentInsertRecord[] {
            return <DocumentListCreated>{body: {items}};
        }
        DocumentRecord created = items[0];
        return <DocumentCreated>{
            headers: {"Location": dsHttp:location("/api/documents", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a document.
    #
    # + return - The updated document, or the error response
    resource function patch documents/[int id](DocumentUpdateRecord payload) returns DocumentRecord|dsHttp:ErrorResponse {
        DocumentUpdate|dsHttp:InvalidInput value = dataserviceFromDocumentUpdateRecord(payload);
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
            return dsHttp:errorResponse(item, "Document", [id]);
        }
        return dataserviceToDocumentRecord(item);
    }

    # Deletes a document and returns it.
    #
    # + return - The deleted document, or the error response
    resource function delete documents/[int id]() returns DocumentRecord|dsHttp:ErrorResponse {
        Document|persist:Error item = dataserviceClient->/documents/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Document", [id]);
        }
        return dataserviceToDocumentRecord(item);
    }

    # Gets the holidays.
    #
    # + return - The holidays, or an internal error
    resource function get holidays() returns HolidayList|dsHttp:InternalError {
        HolidayRecord[]|persist:Error items = from Holiday item in dataserviceClient->/holidays(Holiday)
            select dataserviceToHolidayRecord(item);
        if items is persist:Error {
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a holiday by its key.
    #
    # + return - The holiday, or the error response
    resource function get holidays/[int id]() returns HolidayRecord|dsHttp:ErrorResponse {
        Holiday|persist:Error item = dataserviceClient->/holidays/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Holiday", [id]);
        }
        return dataserviceToHolidayRecord(item);
    }
}

# The JSON form of `Document`.
#
# + id - The `id` field of `Document`
# + title - The `title` field of `Document`
# + issuedOn - The `issuedOn` field of `Document`, as an ISO 8601 date
# + expiresOn - The `expiresOn` field of `Document`, as an ISO 8601 date
# + opensAt - The `opensAt` field of `Document`, as an ISO 8601 time
# + createdAt - The `createdAt` field of `Document`, as an ISO 8601 date-time without an offset
# + signedAt - The `signedAt` field of `Document`, as an ISO 8601 date-time in UTC
# + content - The `content` field of `Document`, as a base64 string
# + thumbnail - The `thumbnail` field of `Document`, as a base64 string
# + status - The `status` field of `Document`
public type DocumentRecord record {|
    readonly int id;
    string title;
    string issuedOn;
    string? expiresOn;
    string opensAt;
    string createdAt;
    string? signedAt;
    string content;
    string? thumbnail;
    Status status;
|};

# The JSON form of `DocumentInsert`.
public type DocumentInsertRecord DocumentRecord;

# The JSON form of `DocumentUpdate`.
#
# + title - The `title` field of `DocumentUpdate`
# + issuedOn - The `issuedOn` field of `DocumentUpdate`, as an ISO 8601 date
# + expiresOn - The `expiresOn` field of `DocumentUpdate`, as an ISO 8601 date
# + opensAt - The `opensAt` field of `DocumentUpdate`, as an ISO 8601 time
# + createdAt - The `createdAt` field of `DocumentUpdate`, as an ISO 8601 date-time without an offset
# + signedAt - The `signedAt` field of `DocumentUpdate`, as an ISO 8601 date-time in UTC
# + content - The `content` field of `DocumentUpdate`, as a base64 string
# + thumbnail - The `thumbnail` field of `DocumentUpdate`, as a base64 string
# + status - The `status` field of `DocumentUpdate`
public type DocumentUpdateRecord record {|
    string title?;
    string issuedOn?;
    string? expiresOn?;
    string opensAt?;
    string createdAt?;
    string? signedAt?;
    string content?;
    string? thumbnail?;
    Status status?;
|};

# A list of documents.
#
# + items - The documents
public type DocumentList record {|
    DocumentRecord[] items;
|};

# The response to creating one document.
#
# + body - The created document
public type DocumentCreated record {|
    *http:Created;
    DocumentRecord body;
|};

# The response to creating several documents.
#
# + body - The created documents
public type DocumentListCreated record {|
    *http:Created;
    DocumentList body;
|};

# The JSON form of `Holiday`.
#
# + id - The `id` field of `Holiday`
# + day - The `day` field of `Holiday`, as an ISO 8601 date
public type HolidayRecord record {|
    readonly int id;
    string day;
|};

# A list of holidays.
#
# + items - The holidays
public type HolidayList record {|
    HolidayRecord[] items;
|};

# Converts a `Document` to its JSON form.
#
# + value - The `Document` record
# + return - The JSON form
isolated function dataserviceToDocumentRecord(Document value) returns DocumentRecord => {
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
isolated function dataserviceFromDocumentInsertRecord(DocumentInsertRecord wire) returns DocumentInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(wire.issuedOn);
    if issuedOn is dataservice:ConversionError {
        return dsHttp:invalidInput(issuedOn, "Document", "issuedOn");
    }
    time:Date?|dataservice:ConversionError expiresOn = let string? expiresOnText = wire.expiresOn in expiresOnText is () ? () : dataservice:dateFromString(expiresOnText);
    if expiresOn is dataservice:ConversionError {
        return dsHttp:invalidInput(expiresOn, "Document", "expiresOn");
    }
    time:TimeOfDay|dataservice:ConversionError opensAt = dataservice:timeOfDayFromString(wire.opensAt);
    if opensAt is dataservice:ConversionError {
        return dsHttp:invalidInput(opensAt, "Document", "opensAt");
    }
    time:Civil|dataservice:ConversionError createdAt = dataservice:civilFromString(wire.createdAt);
    if createdAt is dataservice:ConversionError {
        return dsHttp:invalidInput(createdAt, "Document", "createdAt");
    }
    time:Utc?|dataservice:ConversionError signedAt = let string? signedAtText = wire.signedAt in signedAtText is () ? () : dataservice:utcFromString(signedAtText);
    if signedAt is dataservice:ConversionError {
        return dsHttp:invalidInput(signedAt, "Document", "signedAt");
    }
    byte[]|dataservice:ConversionError content = dataservice:bytesFromString(wire.content);
    if content is dataservice:ConversionError {
        return dsHttp:invalidInput(content, "Document", "content");
    }
    byte[]?|dataservice:ConversionError thumbnail = let string? thumbnailText = wire.thumbnail in thumbnailText is () ? () : dataservice:bytesFromString(thumbnailText);
    if thumbnail is dataservice:ConversionError {
        return dsHttp:invalidInput(thumbnail, "Document", "thumbnail");
    }
    return {id: wire.id, title: wire.title, issuedOn, expiresOn, opensAt, createdAt, signedAt, content, thumbnail, status: wire.status};
}

# Converts the JSON form of a `DocumentUpdate` to the persist type.
#
# + wire - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function dataserviceFromDocumentUpdateRecord(DocumentUpdateRecord wire) returns DocumentUpdate|dsHttp:InvalidInput {
    DocumentUpdate value = {};
    string? title = wire?.title;
    if title !is () {
        value.title = title;
    }
    string? issuedOnText = wire?.issuedOn;
    if issuedOnText is string {
        time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(issuedOnText);
        if issuedOn is dataservice:ConversionError {
            return dsHttp:invalidInput(issuedOn, "Document", "issuedOn");
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
                return dsHttp:invalidInput(expiresOn, "Document", "expiresOn");
            }
            value.expiresOn = expiresOn;
        }
    }
    string? opensAtText = wire?.opensAt;
    if opensAtText is string {
        time:TimeOfDay|dataservice:ConversionError opensAt = dataservice:timeOfDayFromString(opensAtText);
        if opensAt is dataservice:ConversionError {
            return dsHttp:invalidInput(opensAt, "Document", "opensAt");
        }
        value.opensAt = opensAt;
    }
    string? createdAtText = wire?.createdAt;
    if createdAtText is string {
        time:Civil|dataservice:ConversionError createdAt = dataservice:civilFromString(createdAtText);
        if createdAt is dataservice:ConversionError {
            return dsHttp:invalidInput(createdAt, "Document", "createdAt");
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
                return dsHttp:invalidInput(signedAt, "Document", "signedAt");
            }
            value.signedAt = signedAt;
        }
    }
    string? contentText = wire?.content;
    if contentText is string {
        byte[]|dataservice:ConversionError content = dataservice:bytesFromString(contentText);
        if content is dataservice:ConversionError {
            return dsHttp:invalidInput(content, "Document", "content");
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
                return dsHttp:invalidInput(thumbnail, "Document", "thumbnail");
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
isolated function dataserviceToHolidayRecord(Holiday value) returns HolidayRecord => {
    id: value.id,
    day: dataservice:dateToString(value.day)
};
