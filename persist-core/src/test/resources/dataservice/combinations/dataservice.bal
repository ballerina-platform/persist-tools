// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /api on dataserviceListener {

    # Gets a page of read items.
    #
    # + return - A page of read items, or the error response
    resource function get readitems() returns ReadItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadItem[]|persist:Error items = from ReadItem item in dataserviceClient->/readitems(ReadItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read item by its key.
    #
    # + id - The id of the read item
    # + return - The read item, or the error response
    resource function get readitems/[int id]() returns ReadItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadItem|persist:Error item = dataserviceClient->/readitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadItem", [id]);
        }
        return item;
    }

    # Creates one or more create items.
    #
    # + payload - The create item, or the create items, to create
    # + return - The created create item or create items, or the error response
    resource function post createitems(CreateItemInsert|CreateItemInsert[] payload)
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
                    foreach int id in keys {
                        CreateItem item = check dataserviceClient->/createitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/createitems", [created.id])},
            body: created
        };
    }

    # Updates an update item by its key.
    #
    # + id - The id of the update item
    # + payload - The fields of the update item to update
    # + return - No content, or the error response
    resource function patch updateitems/[int id](UpdateItemUpdate payload)
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
    resource function delete deleteitems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DeleteItem|persist:Error item = dataserviceClient->/deleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "DeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Gets a page of read create items.
    #
    # + return - A page of read create items, or the error response
    resource function get readcreateitems() returns ReadCreateItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateItem[]|persist:Error items = from ReadCreateItem item in dataserviceClient->/readcreateitems(ReadCreateItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read create item by its key.
    #
    # + id - The id of the read create item
    # + return - The read create item, or the error response
    resource function get readcreateitems/[int id]() returns ReadCreateItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateItem|persist:Error item = dataserviceClient->/readcreateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateItem", [id]);
        }
        return item;
    }

    # Creates one or more read create items.
    #
    # + payload - The read create item, or the read create items, to create
    # + return - The created read create item or read create items, or the error response
    resource function post readcreateitems(ReadCreateItemInsert|ReadCreateItemInsert[] payload)
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
                    foreach int id in keys {
                        ReadCreateItem item = check dataserviceClient->/readcreateitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/readcreateitems", [created.id])},
            body: created
        };
    }

    # Gets a page of read update items.
    #
    # + return - A page of read update items, or the error response
    resource function get readupdateitems() returns ReadUpdateItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadUpdateItem[]|persist:Error items = from ReadUpdateItem item in dataserviceClient->/readupdateitems(ReadUpdateItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read update item by its key.
    #
    # + id - The id of the read update item
    # + return - The read update item, or the error response
    resource function get readupdateitems/[int id]() returns ReadUpdateItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadUpdateItem|persist:Error item = dataserviceClient->/readupdateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadUpdateItem", [id]);
        }
        return item;
    }

    # Updates a read update item by its key.
    #
    # + id - The id of the read update item
    # + payload - The fields of the read update item to update
    # + return - The updated read update item, or the error response
    resource function patch readupdateitems/[int id](ReadUpdateItemUpdate payload)
            returns ReadUpdateItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    # + return - A page of read delete items, or the error response
    resource function get readdeleteitems() returns ReadDeleteItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadDeleteItem[]|persist:Error items = from ReadDeleteItem item in dataserviceClient->/readdeleteitems(ReadDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read delete item by its key.
    #
    # + id - The id of the read delete item
    # + return - The read delete item, or the error response
    resource function get readdeleteitems/[int id]() returns ReadDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read delete item by its key.
    #
    # + id - The id of the read delete item
    # + return - The deleted read delete item, or the error response
    resource function delete readdeleteitems/[int id]() returns ReadDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    resource function post createupdateitems(CreateUpdateItemInsert|CreateUpdateItemInsert[] payload)
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
                    foreach int id in keys {
                        CreateUpdateItem item = check dataserviceClient->/createupdateitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/createupdateitems", [created.id])},
            body: created
        };
    }

    # Updates a create update item by its key.
    #
    # + id - The id of the create update item
    # + payload - The fields of the create update item to update
    # + return - No content, or the error response
    resource function patch createupdateitems/[int id](CreateUpdateItemUpdate payload)
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
    resource function post createdeleteitems(CreateDeleteItemInsert|CreateDeleteItemInsert[] payload)
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
                    foreach int id in keys {
                        CreateDeleteItem item = check dataserviceClient->/createdeleteitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/createdeleteitems", [created.id])},
            body: created
        };
    }

    # Deletes a create delete item by its key.
    #
    # + id - The id of the create delete item
    # + return - No content, or the error response
    resource function delete createdeleteitems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    resource function patch updatedeleteitems/[int id](UpdateDeleteItemUpdate payload)
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
    resource function delete updatedeleteitems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        UpdateDeleteItem|persist:Error item = dataserviceClient->/updatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "UpdateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Gets a page of read create update items.
    #
    # + return - A page of read create update items, or the error response
    resource function get readcreateupdateitems() returns ReadCreateUpdateItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateUpdateItem[]|persist:Error items = from ReadCreateUpdateItem item in dataserviceClient->/readcreateupdateitems(ReadCreateUpdateItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read create update item by its key.
    #
    # + id - The id of the read create update item
    # + return - The read create update item, or the error response
    resource function get readcreateupdateitems/[int id]() returns ReadCreateUpdateItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateUpdateItem|persist:Error item = dataserviceClient->/readcreateupdateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateUpdateItem", [id]);
        }
        return item;
    }

    # Creates one or more read create update items.
    #
    # + payload - The read create update item, or the read create update items, to create
    # + return - The created read create update item or read create update items, or the error response
    resource function post readcreateupdateitems(ReadCreateUpdateItemInsert|ReadCreateUpdateItemInsert[] payload)
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
                    foreach int id in keys {
                        ReadCreateUpdateItem item = check dataserviceClient->/readcreateupdateitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/readcreateupdateitems", [created.id])},
            body: created
        };
    }

    # Updates a read create update item by its key.
    #
    # + id - The id of the read create update item
    # + payload - The fields of the read create update item to update
    # + return - The updated read create update item, or the error response
    resource function patch readcreateupdateitems/[int id](ReadCreateUpdateItemUpdate payload)
            returns ReadCreateUpdateItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    # + return - A page of read create delete items, or the error response
    resource function get readcreatedeleteitems() returns ReadCreateDeleteItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateDeleteItem[]|persist:Error items = from ReadCreateDeleteItem item in dataserviceClient->/readcreatedeleteitems(ReadCreateDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read create delete item by its key.
    #
    # + id - The id of the read create delete item
    # + return - The read create delete item, or the error response
    resource function get readcreatedeleteitems/[int id]() returns ReadCreateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one or more read create delete items.
    #
    # + payload - The read create delete item, or the read create delete items, to create
    # + return - The created read create delete item or read create delete items, or the error response
    resource function post readcreatedeleteitems(ReadCreateDeleteItemInsert|ReadCreateDeleteItemInsert[] payload)
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
                    foreach int id in keys {
                        ReadCreateDeleteItem item = check dataserviceClient->/readcreatedeleteitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/readcreatedeleteitems", [created.id])},
            body: created
        };
    }

    # Deletes a read create delete item by its key.
    #
    # + id - The id of the read create delete item
    # + return - The deleted read create delete item, or the error response
    resource function delete readcreatedeleteitems/[int id]() returns ReadCreateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadCreateDeleteItem", [id]);
        }
        return item;
    }

    # Gets a page of read update delete items.
    #
    # + return - A page of read update delete items, or the error response
    resource function get readupdatedeleteitems() returns ReadUpdateDeleteItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadUpdateDeleteItem[]|persist:Error items = from ReadUpdateDeleteItem item in dataserviceClient->/readupdatedeleteitems(ReadUpdateDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read update delete item by its key.
    #
    # + id - The id of the read update delete item
    # + return - The read update delete item, or the error response
    resource function get readupdatedeleteitems/[int id]() returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Updates a read update delete item by its key.
    #
    # + id - The id of the read update delete item
    # + payload - The fields of the read update delete item to update
    # + return - The updated read update delete item, or the error response
    resource function patch readupdatedeleteitems/[int id](ReadUpdateDeleteItemUpdate payload)
            returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    resource function delete readupdatedeleteitems/[int id]() returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    resource function post createupdatedeleteitems(CreateUpdateDeleteItemInsert|CreateUpdateDeleteItemInsert[] payload)
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
                    foreach int id in keys {
                        CreateUpdateDeleteItem item = check dataserviceClient->/createupdatedeleteitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/createupdatedeleteitems", [created.id])},
            body: created
        };
    }

    # Updates a create update delete item by its key.
    #
    # + id - The id of the create update delete item
    # + payload - The fields of the create update delete item to update
    # + return - No content, or the error response
    resource function patch createupdatedeleteitems/[int id](CreateUpdateDeleteItemUpdate payload)
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
    resource function delete createupdatedeleteitems/[int id]() returns http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "CreateUpdateDeleteItem", [id]);
        }
        return http:NO_CONTENT;
    }

    # Gets a page of read create update delete items.
    #
    # + return - A page of read create update delete items, or the error response
    resource function get readcreateupdatedeleteitems() returns ReadCreateUpdateDeleteItemList|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem[]|persist:Error items = from ReadCreateUpdateDeleteItem item in dataserviceClient->/readcreateupdatedeleteitems(ReadCreateUpdateDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a read create update delete item by its key.
    #
    # + id - The id of the read create update delete item
    # + return - The read create update delete item, or the error response
    resource function get readcreateupdatedeleteitems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one or more read create update delete items.
    #
    # + payload - The read create update delete item, or the read create update delete items, to create
    # + return - The created read create update delete item or read create update delete items, or the error response
    resource function post readcreateupdatedeleteitems(ReadCreateUpdateDeleteItemInsert|ReadCreateUpdateDeleteItemInsert[] payload)
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
                    foreach int id in keys {
                        ReadCreateUpdateDeleteItem item = check dataserviceClient->/readcreateupdatedeleteitems/[id];
                        items.push(item);
                    }
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
            headers: {"Location": dsHttp:buildLocation("/api/readcreateupdatedeleteitems", [created.id])},
            body: created
        };
    }

    # Updates a read create update delete item by its key.
    #
    # + id - The id of the read create update delete item
    # + payload - The fields of the read create update delete item to update
    # + return - The updated read create update delete item, or the error response
    resource function patch readcreateupdatedeleteitems/[int id](ReadCreateUpdateDeleteItemUpdate payload)
            returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
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
    resource function delete readcreateupdatedeleteitems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }
}
