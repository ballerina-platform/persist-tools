// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/openapi;
import ballerina/persist;

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@openapi:ServiceInfo {embed: true, title: "hr"}
@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}
service http:InterceptableService /api on dataserviceListener {

    private final Client persistClient;
    private final dataservice:PageConfig pageConfig;

    # Creates the persist client, and checks the page sizes.
    #
    # + return - An error when the client cannot be created or a setting is invalid
    function init() returns error? {
        self.persistClient = check new ();
        self.pageConfig = check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
    }

    # Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
    #
    # + return - The error interceptor
    public function createInterceptors() returns DataserviceErrorInterceptor => new;

    # Gets a page of documents.
    #
    # + filter - The conditions that the documents match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of documents in the page
    # + offset - The number of documents to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of documents, or the error response
    resource function get documents(DocumentFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns DocumentList|DocumentPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceDocumentEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Document");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceDocumentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Document");
        }
        stream<Document, persist:Error?> rows = self.persistClient->/documents(Document,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        DocumentPayload[]|persist:Error items = from Document item in rows
            select transformDocumentToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items, "Document");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            DocumentPayload[] sources = items;
            map<anydata>[] partials = from DocumentPayload item in sources
                select dataservice:selectFields(item, dataserviceDocumentEntity, fields);
            DocumentPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Document");
            }
            return <DocumentPartialList>{items: converted, hasMore};
        }
        return <DocumentList>{items, hasMore};
    }

    # Gets a document by its key.
    #
    # + id - The id of the document
    # + fields - The fields to return; the key fields are always returned
    # + return - The document, or the error response
    resource function get documents/[int id](string[]? fields = ())
            returns DocumentPayload|DocumentPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceDocumentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Document");
        }
        Document|persist:Error item = self.persistClient->/documents/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Document", [id]);
        }
        if fields is string[] {
            DocumentPayload itemPayload = transformDocumentToPayload(item);
            map<anydata> partial = dataservice:selectFields(itemPayload, dataserviceDocumentEntity, fields);
            DocumentPartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Document", [id]);
            }
            return converted;
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
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(entries.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        DocumentInsert[] values = [];
        foreach DocumentInsertPayload entry in entries {
            DocumentInsert|dsHttp:InvalidInput value = transformPayloadToDocumentInsert(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            dataservice:DataserviceError? broken = dataservice:checkConstraints(value, "Document");
            if broken is dataservice:DataserviceError {
                return <dsHttp:InvalidInput>{body: broken};
            }
            values.push(value);
        }
        DocumentPayload[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check self.persistClient->/documents.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceDocumentEntity, keyValues);
                    stream<Document, persist:Error?> stored = self.persistClient->/documents(Document,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Document[] rows = check from Document item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceDocumentEntity, keyValues);
                    items = from Document item in rows
                        select transformDocumentToPayload(item);
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
            headers: {"Location": dsHttp:toLocation("/api/documents", [created.id])},
            body: created
        };
    }

    # Updates a document by its key.
    #
    # + id - The id of the document
    # + payload - The fields of the document to update
    # + return - The updated document, or no content when it cannot be read back, or the error response
    resource function patch documents/[int id](DocumentUpdatePayload payload)
            returns DocumentPayload|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DocumentUpdate|dsHttp:InvalidInput value = transformPayloadToDocumentUpdate(payload);
        if value is dsHttp:InvalidInput {
            return value;
        }
        dataservice:DataserviceError? broken = dataservice:checkConstraints(value, "Document");
        if broken is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: broken};
        }
        Document|persist:Error item;
        if value.length() == 0 {
            item = self.persistClient->/documents/[id];
        } else {
            item = self.persistClient->/documents/[id].put(value);
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
    resource function delete documents/[int id]()
            returns DocumentPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Document|persist:Error item = self.persistClient->/documents/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Document", [id]);
        }
        return transformDocumentToPayload(item);
    }

    # Gets a page of holidays.
    #
    # + filter - The conditions that the holidays match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of holidays in the page
    # + offset - The number of holidays to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of holidays, or the error response
    resource function get holidays(HolidayFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns HolidayList|HolidayPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceHolidayEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Holiday");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceHolidayEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Holiday");
        }
        stream<Holiday, persist:Error?> rows = self.persistClient->/holidays(Holiday,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        HolidayPayload[]|persist:Error items = from Holiday item in rows
            select transformHolidayToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items, "Holiday");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            HolidayPayload[] sources = items;
            map<anydata>[] partials = from HolidayPayload item in sources
                select dataservice:selectFields(item, dataserviceHolidayEntity, fields);
            HolidayPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Holiday");
            }
            return <HolidayPartialList>{items: converted, hasMore};
        }
        return <HolidayList>{items, hasMore};
    }

    # Gets a holiday by its key.
    #
    # + id - The id of the holiday
    # + fields - The fields to return; the key fields are always returned
    # + return - The holiday, or the error response
    resource function get holidays/[int id](string[]? fields = ())
            returns HolidayPayload|HolidayPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceHolidayEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Holiday");
        }
        Holiday|persist:Error item = self.persistClient->/holidays/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Holiday", [id]);
        }
        if fields is string[] {
            HolidayPayload itemPayload = transformHolidayToPayload(item);
            map<anydata> partial = dataservice:selectFields(itemPayload, dataserviceHolidayEntity, fields);
            HolidayPartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Holiday", [id]);
            }
            return converted;
        }
        return transformHolidayToPayload(item);
    }
}

# Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
isolated service class DataserviceErrorInterceptor {
    *http:ResponseErrorInterceptor;

    isolated remote function interceptResponseError(error err, http:RequestContext ctx, http:Response response)
            returns http:NextService? {
        dsHttp:setErrorBody(response, err);
        // The response is changed in place; an error return would add a 500 response to the OpenAPI definition.
        http:NextService|error? next = ctx.next();
        return next is http:NextService ? next : ();
    }
}
