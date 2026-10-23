// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/http;

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
