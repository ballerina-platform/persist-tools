// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/constraint;
import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/time;

# The JSON form of `Document`.
public type DocumentPayload record {|
    # The `id` field of `Document`
    readonly int id;
    # The `title` field of `Document`
    @constraint:String {maxLength: 40, pattern: re `[A-Z].*`}
    string title;
    # The `issuedOn` field of `Document`, as an ISO 8601 date
    string issuedOn;
    # The `expiresOn` field of `Document`, as an ISO 8601 date
    string? expiresOn;
    # The `opensAt` field of `Document`, as an ISO 8601 time
    string opensAt;
    # The `createdAt` field of `Document`, as an ISO 8601 date-time without an offset
    string createdAt;
    # The `signedAt` field of `Document`, as an ISO 8601 date-time in UTC
    string? signedAt;
    # The `content` field of `Document`, as a base64 string
    string content;
    # The `thumbnail` field of `Document`, as a base64 string
    string? thumbnail;
    # The `status` field of `Document`
    Status status;
|};

# The JSON form of `DocumentInsert`.
public type DocumentInsertPayload DocumentPayload;

# The JSON form of `DocumentUpdate`.
public type DocumentUpdatePayload record {|
    # The `title` field of `DocumentUpdate`
    @constraint:String {maxLength: 40, pattern: re `[A-Z].*`}
    string title?;
    # The `issuedOn` field of `DocumentUpdate`, as an ISO 8601 date
    string issuedOn?;
    # The `expiresOn` field of `DocumentUpdate`, as an ISO 8601 date
    string? expiresOn?;
    # The `opensAt` field of `DocumentUpdate`, as an ISO 8601 time
    string opensAt?;
    # The `createdAt` field of `DocumentUpdate`, as an ISO 8601 date-time without an offset
    string createdAt?;
    # The `signedAt` field of `DocumentUpdate`, as an ISO 8601 date-time in UTC
    string? signedAt?;
    # The `content` field of `DocumentUpdate`, as a base64 string
    string content?;
    # The `thumbnail` field of `DocumentUpdate`, as a base64 string
    string? thumbnail?;
    # The `status` field of `DocumentUpdate`
    Status status?;
|};

# A list of documents.
public type DocumentList record {
    # The documents
    DocumentPayload[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that documents match, as the `filter` query parameter holds them.
public type DocumentFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `title` field
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
    # The conditions on the `issuedOn` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} issuedOn?;
    # The conditions on the `expiresOn` field
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
    # The conditions on the `opensAt` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} opensAt?;
    # The conditions on the `createdAt` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} createdAt?;
    # The conditions on the `signedAt` field
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
    # The conditions on the `status` field
    record {|
        Status eq?;
        Status ne?;
        Status[] anyOf?;
    |} status?;
|};

# A document with the fields named in the `fields` query parameter.
public type DocumentPartial record {|
    # The `id` field of `Document`
    int id;
    # The `title` field of `Document`
    string title?;
    # The `issuedOn` field of `Document`, as an ISO 8601 date
    string issuedOn?;
    # The `expiresOn` field of `Document`, as an ISO 8601 date
    string? expiresOn?;
    # The `opensAt` field of `Document`, as an ISO 8601 time
    string opensAt?;
    # The `createdAt` field of `Document`, as an ISO 8601 date-time without an offset
    string createdAt?;
    # The `signedAt` field of `Document`, as an ISO 8601 date-time in UTC
    string? signedAt?;
    # The `content` field of `Document`, as a base64 string
    string content?;
    # The `thumbnail` field of `Document`, as a base64 string
    string? thumbnail?;
    # The `status` field of `Document`
    Status status?;
|};

# A list of documents with the fields named in the `fields` query parameter.
public type DocumentPartialList record {
    # The documents
    DocumentPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `Document` that callers can see.
final readonly & dataservice:Field[] dataserviceDocumentFields = [
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
];

# The description of `Document` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceDocumentEntity = {
    name: "Document",
    collection: "documents",
    'resource: "documents",
    datastore: dataservice:H2,
    fields: dataserviceDocumentFields
};

# The response to creating one document.
public type DocumentCreated record {|
    *http:Created;
    # The created document
    DocumentPayload body;
|};

# The response to creating several documents.
public type DocumentListCreated record {|
    *http:Created;
    # The created documents
    DocumentList body;
|};

# Converts a `Document` to its JSON form.
#
# + value - The Document to convert
# + return - The JSON form
isolated function transformDocumentToPayload(Document value) returns DocumentPayload {
    time:Date? expiresOn = value.expiresOn;
    string? expiresOnText = ();
    if expiresOn !is () {
        expiresOnText = dataservice:dateToString(expiresOn);
    }
    time:Utc? signedAt = value.signedAt;
    string? signedAtText = ();
    if signedAt !is () {
        signedAtText = dataservice:utcToString(signedAt);
    }
    byte[]? thumbnail = value.thumbnail;
    string? thumbnailText = ();
    if thumbnail !is () {
        thumbnailText = dataservice:bytesToString(thumbnail);
    }
    return {
        id: value.id,
        title: value.title,
        issuedOn: dataservice:dateToString(value.issuedOn),
        expiresOn: expiresOnText,
        opensAt: dataservice:timeOfDayToString(value.opensAt),
        createdAt: dataservice:civilToString(value.createdAt),
        signedAt: signedAtText,
        content: dataservice:bytesToString(value.content),
        thumbnail: thumbnailText,
        status: value.status
    };
}

# Converts the JSON form of a `DocumentInsert` to the persist type.
#
# + payload - The JSON form to convert
# + return - The persist type, or `InvalidInput` when a value cannot be converted
isolated function transformPayloadToDocumentInsert(DocumentInsertPayload payload)
        returns DocumentInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(payload.issuedOn);
    if issuedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(issuedOn, "Document", "issuedOn");
    }
    time:Date?|dataservice:ConversionError expiresOn = ();
    string? expiresOnText = payload.expiresOn;
    if expiresOnText !is () {
        expiresOn = dataservice:dateFromString(expiresOnText);
    }
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
    time:Utc?|dataservice:ConversionError signedAt = ();
    string? signedAtText = payload.signedAt;
    if signedAtText !is () {
        signedAt = dataservice:utcFromString(signedAtText);
    }
    if signedAt is dataservice:ConversionError {
        return dsHttp:toInvalidInput(signedAt, "Document", "signedAt");
    }
    byte[]|dataservice:ConversionError content = dataservice:bytesFromString(payload.content);
    if content is dataservice:ConversionError {
        return dsHttp:toInvalidInput(content, "Document", "content");
    }
    byte[]?|dataservice:ConversionError thumbnail = ();
    string? thumbnailText = payload.thumbnail;
    if thumbnailText !is () {
        thumbnail = dataservice:bytesFromString(thumbnailText);
    }
    if thumbnail is dataservice:ConversionError {
        return dsHttp:toInvalidInput(thumbnail, "Document", "thumbnail");
    }
    return {id: payload.id, title: payload.title, issuedOn, expiresOn, opensAt, createdAt, signedAt, content, thumbnail, status: payload.status};
}

# Converts the JSON form of a `DocumentUpdate` to the persist type.
#
# + payload - The JSON form to convert
# + return - The persist type, or `InvalidInput` when a value cannot be converted
isolated function transformPayloadToDocumentUpdate(DocumentUpdatePayload payload)
        returns DocumentUpdate|dsHttp:InvalidInput {
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
public type HolidayPayload record {|
    # The `id` field of `Holiday`
    readonly int id;
    # The `day` field of `Holiday`, as an ISO 8601 date
    string day;
|};

# A list of holidays.
public type HolidayList record {
    # The holidays
    HolidayPayload[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that holidays match, as the `filter` query parameter holds them.
public type HolidayFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `day` field
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
public type HolidayPartial record {|
    # The `id` field of `Holiday`
    int id;
    # The `day` field of `Holiday`, as an ISO 8601 date
    string day?;
|};

# A list of holidays with the fields named in the `fields` query parameter.
public type HolidayPartialList record {
    # The holidays
    HolidayPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `Holiday` that callers can see.
final readonly & dataservice:Field[] dataserviceHolidayFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "day", column: "day", 'type: dataservice:DATE}
];

# The description of `Holiday` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceHolidayEntity = {
    name: "Holiday",
    collection: "holidays",
    'resource: "holidays",
    datastore: dataservice:H2,
    fields: dataserviceHolidayFields
};

# Converts a `Holiday` to its JSON form.
#
# + value - The Holiday to convert
# + return - The JSON form
isolated function transformHolidayToPayload(Holiday value) returns HolidayPayload => {
    id: value.id,
    day: dataservice:dateToString(value.day)
};
