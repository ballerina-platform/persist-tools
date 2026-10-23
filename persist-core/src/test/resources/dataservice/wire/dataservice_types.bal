// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/time;

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
public type DocumentPayload record {|
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
public type DocumentInsertPayload DocumentPayload;

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
public type DocumentUpdatePayload record {|
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
    DocumentPayload[] items;
|};

# The response to creating one document.
#
# + body - The created document
public type DocumentCreated record {|
    *http:Created;
    DocumentPayload body;
|};

# The response to creating several documents.
#
# + body - The created documents
public type DocumentListCreated record {|
    *http:Created;
    DocumentList body;
|};

# Converts a `Document` to its JSON form.
#
# + value - The `Document` record
# + return - The JSON form
isolated function transformDocumentToPayload(Document value) returns DocumentPayload => {
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
# + payload - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function transformPayloadToDocumentInsert(DocumentInsertPayload payload) returns DocumentInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(payload.issuedOn);
    if issuedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(issuedOn, "Document", "issuedOn");
    }
    time:Date?|dataservice:ConversionError expiresOn = let string? expiresOnText = payload.expiresOn in expiresOnText is () ? () : dataservice:dateFromString(expiresOnText);
    if expiresOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(expiresOn, "Document", "expiresOn");
    }
    time:TimeOfDay|dataservice:ConversionError opensAt = dataservice:timeOfDayFromString(payload.opensAt);
    if opensAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(opensAt, "Document", "opensAt");
    }
    time:Civil|dataservice:ConversionError createdAt = dataservice:civilFromString(payload.createdAt);
    if createdAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(createdAt, "Document", "createdAt");
    }
    time:Utc?|dataservice:ConversionError signedAt = let string? signedAtText = payload.signedAt in signedAtText is () ? () : dataservice:utcFromString(signedAtText);
    if signedAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(signedAt, "Document", "signedAt");
    }
    byte[]|dataservice:ConversionError content = dataservice:bytesFromString(payload.content);
    if content is dataservice:ConversionError {
        return dsHttp:toInvalidInput(content, "Document", "content");
    }
    byte[]?|dataservice:ConversionError thumbnail = let string? thumbnailText = payload.thumbnail in thumbnailText is () ? () : dataservice:bytesFromString(thumbnailText);
    if thumbnail is dataservice:ConversionError {
        return dsHttp:toInvalidInput(thumbnail, "Document", "thumbnail");
    }
    return {id: payload.id, title: payload.title, issuedOn, expiresOn, opensAt, createdAt, signedAt, content, thumbnail, status: payload.status};
}

# Converts the JSON form of a `DocumentUpdate` to the persist type.
#
# + payload - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function transformPayloadToDocumentUpdate(DocumentUpdatePayload payload) returns DocumentUpdate|dsHttp:InvalidInput {
    DocumentUpdate value = {};
    string? title = payload?.title;
    if title !is () {
        value.title = title;
    }
    string? issuedOnText = payload?.issuedOn;
    if issuedOnText is string {
        time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(issuedOnText);
        if issuedOn is dataservice:ConversionError {
            return dsHttp:toInvalidInput(issuedOn, "Document", "issuedOn");
        }
        value.issuedOn = issuedOn;
    }
    if payload.hasKey("expiresOn") {
        string? expiresOnText = payload?.expiresOn;
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
    string? opensAtText = payload?.opensAt;
    if opensAtText is string {
        time:TimeOfDay|dataservice:ConversionError opensAt = dataservice:timeOfDayFromString(opensAtText);
        if opensAt is dataservice:ConversionError {
            return dsHttp:toInvalidInput(opensAt, "Document", "opensAt");
        }
        value.opensAt = opensAt;
    }
    string? createdAtText = payload?.createdAt;
    if createdAtText is string {
        time:Civil|dataservice:ConversionError createdAt = dataservice:civilFromString(createdAtText);
        if createdAt is dataservice:ConversionError {
            return dsHttp:toInvalidInput(createdAt, "Document", "createdAt");
        }
        value.createdAt = createdAt;
    }
    if payload.hasKey("signedAt") {
        string? signedAtText = payload?.signedAt;
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
    string? contentText = payload?.content;
    if contentText is string {
        byte[]|dataservice:ConversionError content = dataservice:bytesFromString(contentText);
        if content is dataservice:ConversionError {
            return dsHttp:toInvalidInput(content, "Document", "content");
        }
        value.content = content;
    }
    if payload.hasKey("thumbnail") {
        string? thumbnailText = payload?.thumbnail;
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
    Status? status = payload?.status;
    if status !is () {
        value.status = status;
    }
    return value;
}

# The JSON form of `Holiday`.
#
# + id - The `id` field of `Holiday`
# + day - The `day` field of `Holiday`, as an ISO 8601 date
public type HolidayPayload record {|
    readonly int id;
    string day;
|};

# A list of holidays.
#
# + items - The holidays
public type HolidayList record {|
    HolidayPayload[] items;
|};

# Converts a `Holiday` to its JSON form.
#
# + value - The `Holiday` record
# + return - The JSON form
isolated function transformHolidayToPayload(Holiday value) returns HolidayPayload => {
    id: value.id,
    day: dataservice:dateToString(value.day)
};
