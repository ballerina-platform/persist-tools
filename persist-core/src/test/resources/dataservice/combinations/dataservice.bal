// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/openapi;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@openapi:ServiceInfo {embed: true}
@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}
service http:InterceptableService /api on dataserviceListener {

    # Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
    #
    # + return - The error interceptor
    public function createInterceptors() returns dsHttp:ErrorInterceptor => new;

    # Gets a page of read items.
    #
    # + filter - The conditions that the read items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read items in the page
    # + offset - The number of read items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read items, or the error response
    resource function get readItems(ReadItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadItemList|ReadItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadItem");
        }
        stream<ReadItem, persist:Error?> rows = dataserviceClient->/readitems(ReadItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadItem[]|persist:Error items = from ReadItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadItem[] sources = items;
            map<anydata>[] partials = from ReadItem item in sources
                select dataservice:selectFields(item, dataserviceReadItemEntity, fields);
            ReadItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadItemPartialList>{items: converted, hasMore};
        }
        return <ReadItemList>{items, hasMore};
    }

    # Gets a read item by its key.
    #
    # + id - The id of the read item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read item, or the error response
    resource function get readItems/[int id](string[]? fields = ()) returns ReadItem|ReadItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadItem");
        }
        ReadItem|persist:Error item = dataserviceClient->/readitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadItem", [id]);
        }
        if fields is string[] {
            ReadItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadItemEntity, fields)];
            ReadItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Creates one or more create items.
    #
    # + payload - The create item, or the create items, to create
    # + return - The created create item or create items, or the error response
    resource function post createItems(CreateItemInsert|CreateItemInsert[] payload)
            returns CreateItemCreated|CreateItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateItemInsert[] values = payload is CreateItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        CreateItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/createitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceCreateItemEntity, keyValues);
                    stream<CreateItem, persist:Error?> stored = dataserviceClient->/createitems(CreateItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    CreateItem[] rows = check from CreateItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceCreateItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "CreateItem");
            }
        }
        if payload is CreateItemInsert[] {
            return <CreateItemListCreated>{body: {items, hasMore: false}};
        }
        CreateItem created = items[0];
        return <CreateItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/createItems", [created.id])},
            body: created
        };
    }

    # Updates an update item by its key.
    #
    # + id - The id of the update item
    # + payload - The fields of the update item to update
    # + return - No content, or the error response
    resource function patch updateItems/[int id](UpdateItemUpdate payload)
            returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        if payload.length() == 0 {
            return http:NO_CONTENT;
        }
        UpdateItem|persist:Error item = dataserviceClient->/updateitems/[id].put(payload);
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "UpdateItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Deletes a delete item by its key.
    #
    # + id - The id of the delete item
    # + return - No content, or the error response
    resource function delete deleteItems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DeleteItem|persist:Error item = dataserviceClient->/deleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "DeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Gets a page of read create items.
    #
    # + filter - The conditions that the read create items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read create items in the page
    # + offset - The number of read create items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read create items, or the error response
    resource function get readCreateItems(ReadCreateItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadCreateItemList|ReadCreateItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadCreateItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadCreateItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateItem");
        }
        stream<ReadCreateItem, persist:Error?> rows = dataserviceClient->/readcreateitems(ReadCreateItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadCreateItem[]|persist:Error items = from ReadCreateItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadCreateItem[] sources = items;
            map<anydata>[] partials = from ReadCreateItem item in sources
                select dataservice:selectFields(item, dataserviceReadCreateItemEntity, fields);
            ReadCreateItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadCreateItemPartialList>{items: converted, hasMore};
        }
        return <ReadCreateItemList>{items, hasMore};
    }

    # Gets a read create item by its key.
    #
    # + id - The id of the read create item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read create item, or the error response
    resource function get readCreateItems/[int id](string[]? fields = ()) returns ReadCreateItem|ReadCreateItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateItem");
        }
        ReadCreateItem|persist:Error item = dataserviceClient->/readcreateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateItem", [id]);
        }
        if fields is string[] {
            ReadCreateItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadCreateItemEntity, fields)];
            ReadCreateItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadCreateItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Creates one or more read create items.
    #
    # + payload - The read create item, or the read create items, to create
    # + return - The created read create item or read create items, or the error response
    resource function post readCreateItems(ReadCreateItemInsert|ReadCreateItemInsert[] payload)
            returns ReadCreateItemCreated|ReadCreateItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateItemInsert[] values = payload is ReadCreateItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        ReadCreateItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/readcreateitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceReadCreateItemEntity, keyValues);
                    stream<ReadCreateItem, persist:Error?> stored = dataserviceClient->/readcreateitems(ReadCreateItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    ReadCreateItem[] rows = check from ReadCreateItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceReadCreateItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "ReadCreateItem");
            }
        }
        if payload is ReadCreateItemInsert[] {
            return <ReadCreateItemListCreated>{body: {items, hasMore: false}};
        }
        ReadCreateItem created = items[0];
        return <ReadCreateItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/readCreateItems", [created.id])},
            body: created
        };
    }

    # Gets a page of read update items.
    #
    # + filter - The conditions that the read update items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read update items in the page
    # + offset - The number of read update items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read update items, or the error response
    resource function get readUpdateItems(ReadUpdateItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadUpdateItemList|ReadUpdateItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadUpdateItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadUpdateItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadUpdateItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadUpdateItem");
        }
        stream<ReadUpdateItem, persist:Error?> rows = dataserviceClient->/readupdateitems(ReadUpdateItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadUpdateItem[]|persist:Error items = from ReadUpdateItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadUpdateItem[] sources = items;
            map<anydata>[] partials = from ReadUpdateItem item in sources
                select dataservice:selectFields(item, dataserviceReadUpdateItemEntity, fields);
            ReadUpdateItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadUpdateItemPartialList>{items: converted, hasMore};
        }
        return <ReadUpdateItemList>{items, hasMore};
    }

    # Gets a read update item by its key.
    #
    # + id - The id of the read update item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read update item, or the error response
    resource function get readUpdateItems/[int id](string[]? fields = ()) returns ReadUpdateItem|ReadUpdateItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadUpdateItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadUpdateItem");
        }
        ReadUpdateItem|persist:Error item = dataserviceClient->/readupdateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadUpdateItem", [id]);
        }
        if fields is string[] {
            ReadUpdateItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadUpdateItemEntity, fields)];
            ReadUpdateItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadUpdateItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Updates a read update item by its key.
    #
    # + id - The id of the read update item
    # + payload - The fields of the read update item to update
    # + return - The updated read update item, or no content when it cannot be read back, or the error response
    resource function patch readUpdateItems/[int id](ReadUpdateItemUpdate payload)
            returns ReadUpdateItem|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadUpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readupdateitems/[id];
        } else {
            item = dataserviceClient->/readupdateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "ReadUpdateItem", [id]);
        }
        return item;
    }

    # Gets a page of read delete items.
    #
    # + filter - The conditions that the read delete items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read delete items in the page
    # + offset - The number of read delete items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read delete items, or the error response
    resource function get readDeleteItems(ReadDeleteItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadDeleteItemList|ReadDeleteItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadDeleteItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadDeleteItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadDeleteItem");
        }
        stream<ReadDeleteItem, persist:Error?> rows = dataserviceClient->/readdeleteitems(ReadDeleteItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadDeleteItem[]|persist:Error items = from ReadDeleteItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadDeleteItem[] sources = items;
            map<anydata>[] partials = from ReadDeleteItem item in sources
                select dataservice:selectFields(item, dataserviceReadDeleteItemEntity, fields);
            ReadDeleteItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadDeleteItemPartialList>{items: converted, hasMore};
        }
        return <ReadDeleteItemList>{items, hasMore};
    }

    # Gets a read delete item by its key.
    #
    # + id - The id of the read delete item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read delete item, or the error response
    resource function get readDeleteItems/[int id](string[]? fields = ()) returns ReadDeleteItem|ReadDeleteItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadDeleteItem");
        }
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadDeleteItem", [id]);
        }
        if fields is string[] {
            ReadDeleteItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadDeleteItemEntity, fields)];
            ReadDeleteItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadDeleteItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Deletes a read delete item by its key.
    #
    # + id - The id of the read delete item
    # + return - The deleted read delete item, or the error response
    resource function delete readDeleteItems/[int id]() returns ReadDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadDeleteItem", [id]);
        }
        return item;
    }

    # Creates one or more create update items.
    #
    # + payload - The create update item, or the create update items, to create
    # + return - The created create update item or create update items, or the error response
    resource function post createUpdateItems(CreateUpdateItemInsert|CreateUpdateItemInsert[] payload)
            returns CreateUpdateItemCreated|CreateUpdateItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateItemInsert[] values = payload is CreateUpdateItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        CreateUpdateItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/createupdateitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceCreateUpdateItemEntity, keyValues);
                    stream<CreateUpdateItem, persist:Error?> stored = dataserviceClient->/createupdateitems(CreateUpdateItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    CreateUpdateItem[] rows = check from CreateUpdateItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceCreateUpdateItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "CreateUpdateItem");
            }
        }
        if payload is CreateUpdateItemInsert[] {
            return <CreateUpdateItemListCreated>{body: {items, hasMore: false}};
        }
        CreateUpdateItem created = items[0];
        return <CreateUpdateItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/createUpdateItems", [created.id])},
            body: created
        };
    }

    # Updates a create update item by its key.
    #
    # + id - The id of the create update item
    # + payload - The fields of the create update item to update
    # + return - No content, or the error response
    resource function patch createUpdateItems/[int id](CreateUpdateItemUpdate payload)
            returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        if payload.length() == 0 {
            return http:NO_CONTENT;
        }
        CreateUpdateItem|persist:Error item = dataserviceClient->/createupdateitems/[id].put(payload);
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "CreateUpdateItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Creates one or more create delete items.
    #
    # + payload - The create delete item, or the create delete items, to create
    # + return - The created create delete item or create delete items, or the error response
    resource function post createDeleteItems(CreateDeleteItemInsert|CreateDeleteItemInsert[] payload)
            returns CreateDeleteItemCreated|CreateDeleteItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateDeleteItemInsert[] values = payload is CreateDeleteItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        CreateDeleteItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/createdeleteitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceCreateDeleteItemEntity, keyValues);
                    stream<CreateDeleteItem, persist:Error?> stored = dataserviceClient->/createdeleteitems(CreateDeleteItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    CreateDeleteItem[] rows = check from CreateDeleteItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceCreateDeleteItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "CreateDeleteItem");
            }
        }
        if payload is CreateDeleteItemInsert[] {
            return <CreateDeleteItemListCreated>{body: {items, hasMore: false}};
        }
        CreateDeleteItem created = items[0];
        return <CreateDeleteItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/createDeleteItems", [created.id])},
            body: created
        };
    }

    # Deletes a create delete item by its key.
    #
    # + id - The id of the create delete item
    # + return - No content, or the error response
    resource function delete createDeleteItems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateDeleteItem|persist:Error item = dataserviceClient->/createdeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "CreateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Updates an update delete item by its key.
    #
    # + id - The id of the update delete item
    # + payload - The fields of the update delete item to update
    # + return - No content, or the error response
    resource function patch updateDeleteItems/[int id](UpdateDeleteItemUpdate payload)
            returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        if payload.length() == 0 {
            return http:NO_CONTENT;
        }
        UpdateDeleteItem|persist:Error item = dataserviceClient->/updatedeleteitems/[id].put(payload);
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "UpdateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Deletes an update delete item by its key.
    #
    # + id - The id of the update delete item
    # + return - No content, or the error response
    resource function delete updateDeleteItems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        UpdateDeleteItem|persist:Error item = dataserviceClient->/updatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "UpdateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Gets a page of read create update items.
    #
    # + filter - The conditions that the read create update items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read create update items in the page
    # + offset - The number of read create update items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read create update items, or the error response
    resource function get readCreateUpdateItems(ReadCreateUpdateItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadCreateUpdateItemList|ReadCreateUpdateItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadCreateUpdateItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadCreateUpdateItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateUpdateItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateUpdateItem");
        }
        stream<ReadCreateUpdateItem, persist:Error?> rows = dataserviceClient->/readcreateupdateitems(ReadCreateUpdateItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadCreateUpdateItem[]|persist:Error items = from ReadCreateUpdateItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadCreateUpdateItem[] sources = items;
            map<anydata>[] partials = from ReadCreateUpdateItem item in sources
                select dataservice:selectFields(item, dataserviceReadCreateUpdateItemEntity, fields);
            ReadCreateUpdateItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadCreateUpdateItemPartialList>{items: converted, hasMore};
        }
        return <ReadCreateUpdateItemList>{items, hasMore};
    }

    # Gets a read create update item by its key.
    #
    # + id - The id of the read create update item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read create update item, or the error response
    resource function get readCreateUpdateItems/[int id](string[]? fields = ()) returns ReadCreateUpdateItem|ReadCreateUpdateItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateUpdateItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateUpdateItem");
        }
        ReadCreateUpdateItem|persist:Error item = dataserviceClient->/readcreateupdateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateUpdateItem", [id]);
        }
        if fields is string[] {
            ReadCreateUpdateItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadCreateUpdateItemEntity, fields)];
            ReadCreateUpdateItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadCreateUpdateItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Creates one or more read create update items.
    #
    # + payload - The read create update item, or the read create update items, to create
    # + return - The created read create update item or read create update items, or the error response
    resource function post readCreateUpdateItems(ReadCreateUpdateItemInsert|ReadCreateUpdateItemInsert[] payload)
            returns ReadCreateUpdateItemCreated|ReadCreateUpdateItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateItemInsert[] values = payload is ReadCreateUpdateItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        ReadCreateUpdateItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/readcreateupdateitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceReadCreateUpdateItemEntity, keyValues);
                    stream<ReadCreateUpdateItem, persist:Error?> stored = dataserviceClient->/readcreateupdateitems(ReadCreateUpdateItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    ReadCreateUpdateItem[] rows = check from ReadCreateUpdateItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceReadCreateUpdateItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "ReadCreateUpdateItem");
            }
        }
        if payload is ReadCreateUpdateItemInsert[] {
            return <ReadCreateUpdateItemListCreated>{body: {items, hasMore: false}};
        }
        ReadCreateUpdateItem created = items[0];
        return <ReadCreateUpdateItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/readCreateUpdateItems", [created.id])},
            body: created
        };
    }

    # Updates a read create update item by its key.
    #
    # + id - The id of the read create update item
    # + payload - The fields of the read create update item to update
    # + return - The updated read create update item, or no content when it cannot be read back, or the error response
    resource function patch readCreateUpdateItems/[int id](ReadCreateUpdateItemUpdate payload)
            returns ReadCreateUpdateItem|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readcreateupdateitems/[id];
        } else {
            item = dataserviceClient->/readcreateupdateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "ReadCreateUpdateItem", [id]);
        }
        return item;
    }

    # Gets a page of read create delete items.
    #
    # + filter - The conditions that the read create delete items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read create delete items in the page
    # + offset - The number of read create delete items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read create delete items, or the error response
    resource function get readCreateDeleteItems(ReadCreateDeleteItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadCreateDeleteItemList|ReadCreateDeleteItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadCreateDeleteItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadCreateDeleteItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateDeleteItem");
        }
        stream<ReadCreateDeleteItem, persist:Error?> rows = dataserviceClient->/readcreatedeleteitems(ReadCreateDeleteItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadCreateDeleteItem[]|persist:Error items = from ReadCreateDeleteItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadCreateDeleteItem[] sources = items;
            map<anydata>[] partials = from ReadCreateDeleteItem item in sources
                select dataservice:selectFields(item, dataserviceReadCreateDeleteItemEntity, fields);
            ReadCreateDeleteItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadCreateDeleteItemPartialList>{items: converted, hasMore};
        }
        return <ReadCreateDeleteItemList>{items, hasMore};
    }

    # Gets a read create delete item by its key.
    #
    # + id - The id of the read create delete item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read create delete item, or the error response
    resource function get readCreateDeleteItems/[int id](string[]? fields = ()) returns ReadCreateDeleteItem|ReadCreateDeleteItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateDeleteItem");
        }
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateDeleteItem", [id]);
        }
        if fields is string[] {
            ReadCreateDeleteItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadCreateDeleteItemEntity, fields)];
            ReadCreateDeleteItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadCreateDeleteItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Creates one or more read create delete items.
    #
    # + payload - The read create delete item, or the read create delete items, to create
    # + return - The created read create delete item or read create delete items, or the error response
    resource function post readCreateDeleteItems(ReadCreateDeleteItemInsert|ReadCreateDeleteItemInsert[] payload)
            returns ReadCreateDeleteItemCreated|ReadCreateDeleteItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateDeleteItemInsert[] values = payload is ReadCreateDeleteItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        ReadCreateDeleteItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/readcreatedeleteitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceReadCreateDeleteItemEntity, keyValues);
                    stream<ReadCreateDeleteItem, persist:Error?> stored = dataserviceClient->/readcreatedeleteitems(ReadCreateDeleteItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    ReadCreateDeleteItem[] rows = check from ReadCreateDeleteItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceReadCreateDeleteItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "ReadCreateDeleteItem");
            }
        }
        if payload is ReadCreateDeleteItemInsert[] {
            return <ReadCreateDeleteItemListCreated>{body: {items, hasMore: false}};
        }
        ReadCreateDeleteItem created = items[0];
        return <ReadCreateDeleteItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/readCreateDeleteItems", [created.id])},
            body: created
        };
    }

    # Deletes a read create delete item by its key.
    #
    # + id - The id of the read create delete item
    # + return - The deleted read create delete item, or the error response
    resource function delete readCreateDeleteItems/[int id]() returns ReadCreateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadCreateDeleteItem", [id]);
        }
        return item;
    }

    # Gets a page of read update delete items.
    #
    # + filter - The conditions that the read update delete items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read update delete items in the page
    # + offset - The number of read update delete items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read update delete items, or the error response
    resource function get readUpdateDeleteItems(ReadUpdateDeleteItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadUpdateDeleteItemList|ReadUpdateDeleteItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadUpdateDeleteItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadUpdateDeleteItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadUpdateDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadUpdateDeleteItem");
        }
        stream<ReadUpdateDeleteItem, persist:Error?> rows = dataserviceClient->/readupdatedeleteitems(ReadUpdateDeleteItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadUpdateDeleteItem[]|persist:Error items = from ReadUpdateDeleteItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadUpdateDeleteItem[] sources = items;
            map<anydata>[] partials = from ReadUpdateDeleteItem item in sources
                select dataservice:selectFields(item, dataserviceReadUpdateDeleteItemEntity, fields);
            ReadUpdateDeleteItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadUpdateDeleteItemPartialList>{items: converted, hasMore};
        }
        return <ReadUpdateDeleteItemList>{items, hasMore};
    }

    # Gets a read update delete item by its key.
    #
    # + id - The id of the read update delete item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read update delete item, or the error response
    resource function get readUpdateDeleteItems/[int id](string[]? fields = ()) returns ReadUpdateDeleteItem|ReadUpdateDeleteItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadUpdateDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadUpdateDeleteItem");
        }
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadUpdateDeleteItem", [id]);
        }
        if fields is string[] {
            ReadUpdateDeleteItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadUpdateDeleteItemEntity, fields)];
            ReadUpdateDeleteItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadUpdateDeleteItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Updates a read update delete item by its key.
    #
    # + id - The id of the read update delete item
    # + payload - The fields of the read update delete item to update
    # + return - The updated read update delete item, or no content when it cannot be read back, or the error response
    resource function patch readUpdateDeleteItems/[int id](ReadUpdateDeleteItemUpdate payload)
            returns ReadUpdateDeleteItem|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadUpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readupdatedeleteitems/[id];
        } else {
            item = dataserviceClient->/readupdatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read update delete item by its key.
    #
    # + id - The id of the read update delete item
    # + return - The deleted read update delete item, or the error response
    resource function delete readUpdateDeleteItems/[int id]() returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one or more create update delete items.
    #
    # + payload - The create update delete item, or the create update delete items, to create
    # + return - The created create update delete item or create update delete items, or the error response
    resource function post createUpdateDeleteItems(CreateUpdateDeleteItemInsert|CreateUpdateDeleteItemInsert[] payload)
            returns CreateUpdateDeleteItemCreated|CreateUpdateDeleteItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateDeleteItemInsert[] values = payload is CreateUpdateDeleteItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        CreateUpdateDeleteItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/createupdatedeleteitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceCreateUpdateDeleteItemEntity, keyValues);
                    stream<CreateUpdateDeleteItem, persist:Error?> stored = dataserviceClient->/createupdatedeleteitems(CreateUpdateDeleteItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    CreateUpdateDeleteItem[] rows = check from CreateUpdateDeleteItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceCreateUpdateDeleteItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "CreateUpdateDeleteItem");
            }
        }
        if payload is CreateUpdateDeleteItemInsert[] {
            return <CreateUpdateDeleteItemListCreated>{body: {items, hasMore: false}};
        }
        CreateUpdateDeleteItem created = items[0];
        return <CreateUpdateDeleteItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/createUpdateDeleteItems", [created.id])},
            body: created
        };
    }

    # Updates a create update delete item by its key.
    #
    # + id - The id of the create update delete item
    # + payload - The fields of the create update delete item to update
    # + return - No content, or the error response
    resource function patch createUpdateDeleteItems/[int id](CreateUpdateDeleteItemUpdate payload)
            returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        if payload.length() == 0 {
            return http:NO_CONTENT;
        }
        CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id].put(payload);
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "CreateUpdateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Deletes a create update delete item by its key.
    #
    # + id - The id of the create update delete item
    # + return - No content, or the error response
    resource function delete createUpdateDeleteItems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "CreateUpdateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Gets a page of read create update delete items.
    #
    # + filter - The conditions that the read create update delete items match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of read create update delete items in the page
    # + offset - The number of read create update delete items to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of read create update delete items, or the error response
    resource function get readCreateUpdateDeleteItems(ReadCreateUpdateDeleteItemFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns ReadCreateUpdateDeleteItemList|ReadCreateUpdateDeleteItemPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceReadCreateUpdateDeleteItemEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ReadCreateUpdateDeleteItem");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateUpdateDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateUpdateDeleteItem");
        }
        stream<ReadCreateUpdateDeleteItem, persist:Error?> rows = dataserviceClient->/readcreateupdatedeleteitems(ReadCreateUpdateDeleteItem,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ReadCreateUpdateDeleteItem[]|persist:Error items = from ReadCreateUpdateDeleteItem item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ReadCreateUpdateDeleteItem[] sources = items;
            map<anydata>[] partials = from ReadCreateUpdateDeleteItem item in sources
                select dataservice:selectFields(item, dataserviceReadCreateUpdateDeleteItemEntity, fields);
            ReadCreateUpdateDeleteItemPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <ReadCreateUpdateDeleteItemPartialList>{items: converted, hasMore};
        }
        return <ReadCreateUpdateDeleteItemList>{items, hasMore};
    }

    # Gets a read create update delete item by its key.
    #
    # + id - The id of the read create update delete item
    # + fields - The fields to return; the key fields are always returned
    # + return - The read create update delete item, or the error response
    resource function get readCreateUpdateDeleteItems/[int id](string[]? fields = ()) returns ReadCreateUpdateDeleteItem|ReadCreateUpdateDeleteItemPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceReadCreateUpdateDeleteItemEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ReadCreateUpdateDeleteItem");
        }
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        if fields is string[] {
            ReadCreateUpdateDeleteItem[] sources = [item];
            map<anydata>[] partials = [dataservice:selectFields(sources[0], dataserviceReadCreateUpdateDeleteItemEntity, fields)];
            ReadCreateUpdateDeleteItemPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "ReadCreateUpdateDeleteItem", [id]);
            }
            return partial;
        }
        return item;
    }

    # Creates one or more read create update delete items.
    #
    # + payload - The read create update delete item, or the read create update delete items, to create
    # + return - The created read create update delete item or read create update delete items, or the error response
    resource function post readCreateUpdateDeleteItems(ReadCreateUpdateDeleteItemInsert|ReadCreateUpdateDeleteItemInsert[] payload)
            returns ReadCreateUpdateDeleteItemCreated|ReadCreateUpdateDeleteItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateDeleteItemInsert[] values = payload is ReadCreateUpdateDeleteItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        ReadCreateUpdateDeleteItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/readcreateupdatedeleteitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceReadCreateUpdateDeleteItemEntity, keyValues);
                    stream<ReadCreateUpdateDeleteItem, persist:Error?> stored = dataserviceClient->/readcreateupdatedeleteitems(ReadCreateUpdateDeleteItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    ReadCreateUpdateDeleteItem[] rows = check from ReadCreateUpdateDeleteItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceReadCreateUpdateDeleteItemEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "ReadCreateUpdateDeleteItem");
            }
        }
        if payload is ReadCreateUpdateDeleteItemInsert[] {
            return <ReadCreateUpdateDeleteItemListCreated>{body: {items, hasMore: false}};
        }
        ReadCreateUpdateDeleteItem created = items[0];
        return <ReadCreateUpdateDeleteItemCreated>{
            headers: {"Location": dsHttp:toLocation("/api/readCreateUpdateDeleteItems", [created.id])},
            body: created
        };
    }

    # Updates a read create update delete item by its key.
    #
    # + id - The id of the read create update delete item
    # + payload - The fields of the read create update delete item to update
    # + return - The updated read create update delete item, or no content when it cannot be read back, or the error response
    resource function patch readCreateUpdateDeleteItems/[int id](ReadCreateUpdateDeleteItemUpdate payload)
            returns ReadCreateUpdateDeleteItem|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readcreateupdatedeleteitems/[id];
        } else {
            item = dataserviceClient->/readcreateupdatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read create update delete item by its key.
    #
    # + id - The id of the read create update delete item
    # + return - The deleted read create update delete item, or the error response
    resource function delete readCreateUpdateDeleteItems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }
}
