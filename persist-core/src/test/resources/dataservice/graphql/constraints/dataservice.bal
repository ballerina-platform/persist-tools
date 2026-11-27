// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/persist;

# The persist client of the data service.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener graphql:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@graphql:ServiceConfig {
    graphiql: {enabled: true},
    cors: dataserviceConfig.serviceConfig.cors,
    maxQueryDepth: dataserviceConfig.serviceConfig.maxQueryDepth,
    queryComplexityConfig: {maxComplexity: dataserviceConfig.serviceConfig.maxQueryComplexity},
    validation: false
}
service graphql:Service /graphql on dataserviceListener {

    # Checks the query limits, so that a value out of range stops the program.
    #
    # + return - An error naming the limit that is out of range
    function init() returns error? {
        check dsGraphql:checkLimits(dataserviceConfig.serviceConfig);
    }

    # Gets a page of documents.
    #
    # + filter - The conditions that the documents match
    # + sort - The order of the documents; each entry names one field
    # + size - The number of documents in the page
    # + offset - The number of documents to skip
    # + return - A page of documents, or the error
    resource function get documents(DocumentFilter? filter = (), DocumentSort[]? sort = (),
            int? size = (), int? offset = ()) returns DocumentList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceDocumentEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Document, persist:Error?> rows = dataserviceClient->/documents(Document,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Document[]|persist:Error items = from Document item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Document");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Document item in items
                select new DocumentObject(item), hasMore};
    }

    # Gets a document by its key.
    #
    # + id - The id of the document
    # + return - The document, or the error
    resource function get document(int id) returns DocumentObject?|error {
        Document|persist:Error item = dataserviceClient->/documents/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Document", [id]);
        }
        return new (item);
    }

    # Creates a document.
    #
    # + input - The document to create
    # + return - The created document, or the error
    remote function createDocument(DocumentInsertInput input) returns DocumentObject?|error =>
        (check dataserviceCreateDocument([input]))[0];

    # Creates documents.
    #
    # + input - The documents to create
    # + return - The created documents, or the error
    remote function createDocuments(DocumentInsertInput[] input) returns DocumentList?|error =>
        {items: check dataserviceCreateDocument(input), hasMore: false};

    # Updates a document by its key.
    #
    # + id - The id of the document
    # + input - The fields to change
    # + return - The updated document, `()` when it cannot be returned, or the error
    remote function updateDocument(int id, DocumentUpdateInput input)
            returns DocumentObject?|error {
        DocumentUpdate value = check transformInputToDocumentUpdate(input);
        dataservice:DataserviceError? broken = dataservice:checkConstraints(value, "Document");
        if broken is dataservice:DataserviceError {
            return dsGraphql:toError(broken);
        }
        Document|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/documents/[id];
        } else {
            item = dataserviceClient->/documents/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Document", [id]);
        }
        return new (item);
    }

    # Deletes a document by its key.
    #
    # + id - The id of the document
    # + return - The deleted document, or the error
    remote function deleteDocument(int id) returns DocumentObject?|error {
        Document|persist:Error item = dataserviceClient->/documents/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Document", [id]);
        }
        return new (item);
    }

    # Gets a page of notes.
    #
    # + filter - The conditions that the notes match
    # + sort - The order of the notes; each entry names one field
    # + size - The number of notes in the page
    # + offset - The number of notes to skip
    # + return - A page of notes, or the error
    resource function get notes(NoteFilter? filter = (), NoteSort[]? sort = (),
            int? size = (), int? offset = ()) returns NoteList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceNoteEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Note, persist:Error?> rows = dataserviceClient->/notes(Note,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Note[]|persist:Error items = from Note item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Note");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Note item in items
                select new NoteObject(item), hasMore};
    }

    # Gets a note by its key.
    #
    # + id - The id of the note
    # + return - The note, or the error
    resource function get note(int id) returns NoteObject?|error {
        Note|persist:Error item = dataserviceClient->/notes/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Note", [id]);
        }
        return new (item);
    }

    # Creates a note.
    #
    # + input - The note to create
    # + return - The created note, or the error
    remote function createNote(NoteInsertInput input) returns NoteObject?|error =>
        (check dataserviceCreateNote([input]))[0];

    # Creates notes.
    #
    # + input - The notes to create
    # + return - The created notes, or the error
    remote function createNotes(NoteInsertInput[] input) returns NoteList?|error =>
        {items: check dataserviceCreateNote(input), hasMore: false};

    # Updates a note by its key.
    #
    # + id - The id of the note
    # + input - The fields to change
    # + return - The updated note, `()` when it cannot be returned, or the error
    remote function updateNote(int id, NoteUpdateInput input)
            returns NoteObject?|error {
        NoteUpdate value = check transformInputToNoteUpdate(input);
        Note|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/notes/[id];
        } else {
            item = dataserviceClient->/notes/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Note", [id]);
        }
        return new (item);
    }

    # Deletes a note by its key.
    #
    # + id - The id of the note
    # + return - The deleted note, or the error
    remote function deleteNote(int id) returns NoteObject?|error {
        Note|persist:Error item = dataserviceClient->/notes/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Note", [id]);
        }
        return new (item);
    }
}

isolated function dataserviceCreateDocument(DocumentInsertInput[] input) returns DocumentObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    DocumentInsert[] values = [];
    foreach DocumentInsertInput entry in input {
        DocumentInsert value = check transformInputToDocumentInsert(entry);
        dataservice:DataserviceError? broken = dataservice:checkConstraints(value, "Document");
        if broken is dataservice:DataserviceError {
            return dsGraphql:toError(broken);
        }
        values.push(value);
    }
    do {
        transaction {
            int[] keys = check dataserviceClient->/documents.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceDocumentEntity, keyValues);
            stream<Document, persist:Error?> stored = dataserviceClient->/documents(Document,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Document[] rows = check from Document item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceDocumentEntity, keyValues);
            check commit;
            return from Document item in rows
                select new DocumentObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Document");
    }
}

isolated function dataserviceCreateNote(NoteInsertInput[] input) returns NoteObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    NoteInsert[] values = from NoteInsertInput entry in input
        select transformInputToNoteInsert(entry);
    do {
        transaction {
            int[] keys = check dataserviceClient->/notes.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceNoteEntity, keyValues);
            stream<Note, persist:Error?> stored = dataserviceClient->/notes(Note,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Note[] rows = check from Note item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceNoteEntity, keyValues);
            check commit;
            return from Note item in rows
                select new NoteObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Note");
    }
}
