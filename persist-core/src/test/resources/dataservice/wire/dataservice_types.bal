// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/constraint;
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
    @constraint:String {maxLength: 40, pattern: re `[A-Z].*`}
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
    @constraint:String {maxLength: 40, pattern: re `[A-Z].*`}
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
# + hasMore - Whether records exist after this page
public type DocumentList record {
    DocumentPayload[] items;
    boolean hasMore;
};

# The conditions that documents match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + title - The conditions on the `title` field
# + issuedOn - The conditions on the `issuedOn` field
# + expiresOn - The conditions on the `expiresOn` field
# + opensAt - The conditions on the `opensAt` field
# + createdAt - The conditions on the `createdAt` field
# + signedAt - The conditions on the `signedAt` field
# + status - The conditions on the `status` field
public type DocumentFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} title?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} issuedOn?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        boolean isNull?;
    |} expiresOn?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} opensAt?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} createdAt?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        boolean isNull?;
    |} signedAt?;
    record {|
        Status eq?;
        Status ne?;
        Status[] anyOf?;
    |} status?;
|};

# A document with the fields named in the `fields` query parameter.
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
public type DocumentPartial record {|
    int id;
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

# A list of documents with the fields named in the `fields` query parameter.
#
# + items - The documents
# + hasMore - Whether records exist after this page
public type DocumentPartialList record {
    DocumentPartial[] items;
    boolean hasMore;
};

# The description of `Document` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceDocumentEntity = {
    name: "Document",
    collection: "documents",
    'resource: "documents",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "title", column: "title", 'type: dataservice:STRING},
        {name: "issuedOn", column: "issuedOn", 'type: dataservice:DATE},
        {name: "expiresOn", column: "expiresOn", 'type: dataservice:DATE, nullable: true},
        {name: "opensAt", column: "opensAt", 'type: dataservice:TIME_OF_DAY},
        {name: "createdAt", column: "createdAt", 'type: dataservice:CIVIL},
        {name: "signedAt", column: "signedAt", 'type: dataservice:UTC, nullable: true},
        {name: "content", column: "content", 'type: dataservice:BYTES},
        {name: "thumbnail", column: "thumbnail", 'type: dataservice:BYTES, nullable: true},
        {name: "status", column: "status", 'type: dataservice:ENUM}
    ]
};

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
# + hasMore - Whether records exist after this page
public type HolidayList record {
    HolidayPayload[] items;
    boolean hasMore;
};

# The conditions that holidays match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + day - The conditions on the `day` field
public type HolidayFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} day?;
|};

# A holiday with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Holiday`
# + day - The `day` field of `Holiday`, as an ISO 8601 date
public type HolidayPartial record {|
    int id;
    string day?;
|};

# A list of holidays with the fields named in the `fields` query parameter.
#
# + items - The holidays
# + hasMore - Whether records exist after this page
public type HolidayPartialList record {
    HolidayPartial[] items;
    boolean hasMore;
};

# The description of `Holiday` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceHolidayEntity = {
    name: "Holiday",
    collection: "holidays",
    'resource: "holidays",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "day", column: "day", 'type: dataservice:DATE}
    ]
};

# Converts a `Holiday` to its JSON form.
#
# + value - The `Holiday` record
# + return - The JSON form
isolated function transformHolidayToPayload(Holiday value) returns HolidayPayload => {
    id: value.id,
    day: dataservice:dateToString(value.day)
};
