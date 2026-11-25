// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/time;

# A document.
public isolated service class DocumentObject {
    private final readonly & Document row;

    isolated function init(Document row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Document`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `title` field of `Document`
    # + return - The value
    isolated resource function get title() returns string => self.row.title;

    # The `issuedOn` field of `Document`, as an ISO 8601 date
    # + return - The value
    isolated resource function get issuedOn() returns string => dataservice:dateToString(self.row.issuedOn);

    # The `pages` field of `Document`
    # + return - The value
    isolated resource function get pages() returns int => self.row.pages;
}

# A page of documents.
#
# + items - The documents
# + hasMore - Whether records exist after this page
public type DocumentList record {
    DocumentObject[] items;
    boolean hasMore;
};

# The conditions that documents match; every condition set must hold.
public type DocumentFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `title`
    dsGraphql:StringFilter? title;
    # The conditions on `issuedOn`
    dsGraphql:DateFilter? issuedOn;
    # The conditions on `pages`
    dsGraphql:IntFilter? pages;
|};

# One entry of the order of documents; set exactly one field.
public type DocumentSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `title`
    dsGraphql:SortDirection? title;
    # Sorts by `issuedOn`
    dsGraphql:SortDirection? issuedOn;
    # Sorts by `pages`
    dsGraphql:SortDirection? pages;
|};

# The fields to create a document with.
public type DocumentInsertInput record {|
    # The `id` field of `Document`
    int id;
    # The `title` field of `Document`
    string title;
    # The `issuedOn` field of `Document`, as an ISO 8601 date
    string issuedOn;
    # The `pages` field of `Document`
    int pages;
|};

# The fields to change in a document; a field left out or set to `null` is not changed.
public type DocumentUpdateInput record {|
    # The `title` field of `Document`
    string? title;
    # The `issuedOn` field of `Document`, as an ISO 8601 date
    string? issuedOn;
    # The `pages` field of `Document`
    int? pages;
|};

# The fields of `Document` that callers can see.
final readonly & dataservice:Field[] dataserviceDocumentFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "title", column: "title", 'type: dataservice:STRING},
    {name: "issuedOn", column: "issuedOn", 'type: dataservice:DATE},
    {name: "pages", column: "pages", 'type: dataservice:INT}
];

# The description of `Document` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceDocumentEntity = {
    name: "Document",
    collection: "documents",
    'resource: "documents",
    datastore: dataservice:H2,
    fields: dataserviceDocumentFields
};

isolated function transformInputToDocumentInsert(DocumentInsertInput value) returns DocumentInsert|dsGraphql:Error {
    time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(value.issuedOn);
    if issuedOn is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(issuedOn, "Document", "issuedOn");
    }
    return {id: value.id, title: value.title, issuedOn, pages: value.pages};
}

isolated function transformInputToDocumentUpdate(DocumentUpdateInput value) returns DocumentUpdate|dsGraphql:Error {
    DocumentUpdate update = {};
    string? title = value.title;
    if title !is () {
        update.title = title;
    }
    string? issuedOnText = value.issuedOn;
    if issuedOnText !is () {
        time:Date|dataservice:ConversionError issuedOn = dataservice:dateFromString(issuedOnText);
        if issuedOn is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(issuedOn, "Document", "issuedOn");
        }
        update.issuedOn = issuedOn;
    }
    int? pages = value.pages;
    if pages !is () {
        update.pages = pages;
    }
    return update;
}

# A note.
public isolated service class NoteObject {
    private final readonly & Note row;

    isolated function init(Note row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Note`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `text` field of `Note`
    # + return - The value
    isolated resource function get text() returns string => self.row.text;
}

# A page of notes.
#
# + items - The notes
# + hasMore - Whether records exist after this page
public type NoteList record {
    NoteObject[] items;
    boolean hasMore;
};

# The conditions that notes match; every condition set must hold.
public type NoteFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `text`
    dsGraphql:StringFilter? text;
|};

# One entry of the order of notes; set exactly one field.
public type NoteSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `text`
    dsGraphql:SortDirection? text;
|};

# The fields to create a note with.
public type NoteInsertInput record {|
    # The `id` field of `Note`
    int id;
    # The `text` field of `Note`
    string text;
|};

# The fields to change in a note; a field left out or set to `null` is not changed.
public type NoteUpdateInput record {|
    # The `text` field of `Note`
    string? text;
|};

# The fields of `Note` that callers can see.
final readonly & dataservice:Field[] dataserviceNoteFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "text", column: "text", 'type: dataservice:STRING}
];

# The description of `Note` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceNoteEntity = {
    name: "Note",
    collection: "notes",
    'resource: "notes",
    datastore: dataservice:H2,
    fields: dataserviceNoteFields
};

isolated function transformInputToNoteInsert(NoteInsertInput value) returns NoteInsert {
    return {id: value.id, text: value.text};
}

isolated function transformInputToNoteUpdate(NoteUpdateInput value) returns NoteUpdate|dsGraphql:Error {
    NoteUpdate update = {};
    string? text = value.text;
    if text !is () {
        update.text = text;
    }
    return update;
}
