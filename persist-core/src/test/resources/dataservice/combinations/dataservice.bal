// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /api on dataserviceListener {

    # Gets the read items.
    #
    # + return - The read items, or an internal error
    resource function get readitems() returns ReadItemList|dsHttp:InternalError {
        ReadItem[]|persist:Error items = from ReadItem item in dataserviceClient->/readitems(ReadItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read item by its key.
    #
    # + return - The read item, or the error response
    resource function get readitems/[int id]() returns ReadItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadItem|persist:Error item = dataserviceClient->/readitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadItem", [id]);
        }
        return item;
    }

    # Creates one create item, or several from an array.
    #
    # + return - The created create item or create items, or the error response
    resource function post createitems(CreateItemInsert|CreateItemInsert[] payload)
            returns CreateItemCreated|CreateItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateItemInsert[] values = payload is CreateItemInsert[] ? payload : [payload];
        CreateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "CreateItem");
            }
            foreach int id in keys {
                CreateItem|persist:Error item = dataserviceClient->/createitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "CreateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateItemInsert[] {
            return <CreateItemListCreated>{body: {items}};
        }
        CreateItem created = items[0];
        return <CreateItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/createitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of an update item.
    #
    # + return - The updated update item, or the error response
    resource function patch updateitems/[int id](UpdateItemUpdate payload)
            returns UpdateItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        UpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/updateitems/[id];
        } else {
            item = dataserviceClient->/updateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "UpdateItem", [id]);
        }
        return item;
    }

    # Deletes a delete item and returns it.
    #
    # + return - The deleted delete item, or the error response
    resource function delete deleteitems/[int id]() returns DeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DeleteItem|persist:Error item = dataserviceClient->/deleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "DeleteItem", [id]);
        }
        return item;
    }

    # Gets the read create items.
    #
    # + return - The read create items, or an internal error
    resource function get readcreateitems() returns ReadCreateItemList|dsHttp:InternalError {
        ReadCreateItem[]|persist:Error items = from ReadCreateItem item in dataserviceClient->/readcreateitems(ReadCreateItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read create item by its key.
    #
    # + return - The read create item, or the error response
    resource function get readcreateitems/[int id]() returns ReadCreateItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadCreateItem|persist:Error item = dataserviceClient->/readcreateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateItem", [id]);
        }
        return item;
    }

    # Creates one read create item, or several from an array.
    #
    # + return - The created read create item or read create items, or the error response
    resource function post readcreateitems(ReadCreateItemInsert|ReadCreateItemInsert[] payload)
            returns ReadCreateItemCreated|ReadCreateItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateItemInsert[] values = payload is ReadCreateItemInsert[] ? payload : [payload];
        ReadCreateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreateitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "ReadCreateItem");
            }
            foreach int id in keys {
                ReadCreateItem|persist:Error item = dataserviceClient->/readcreateitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "ReadCreateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateItemInsert[] {
            return <ReadCreateItemListCreated>{body: {items}};
        }
        ReadCreateItem created = items[0];
        return <ReadCreateItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/readcreateitems", [created.id])},
            body: created
        };
    }

    # Gets the read update items.
    #
    # + return - The read update items, or an internal error
    resource function get readupdateitems() returns ReadUpdateItemList|dsHttp:InternalError {
        ReadUpdateItem[]|persist:Error items = from ReadUpdateItem item in dataserviceClient->/readupdateitems(ReadUpdateItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read update item by its key.
    #
    # + return - The read update item, or the error response
    resource function get readupdateitems/[int id]() returns ReadUpdateItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadUpdateItem|persist:Error item = dataserviceClient->/readupdateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadUpdateItem", [id]);
        }
        return item;
    }

    # Updates the given fields of a read update item.
    #
    # + return - The updated read update item, or the error response
    resource function patch readupdateitems/[int id](ReadUpdateItemUpdate payload)
            returns ReadUpdateItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
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

    # Gets the read delete items.
    #
    # + return - The read delete items, or an internal error
    resource function get readdeleteitems() returns ReadDeleteItemList|dsHttp:InternalError {
        ReadDeleteItem[]|persist:Error items = from ReadDeleteItem item in dataserviceClient->/readdeleteitems(ReadDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read delete item by its key.
    #
    # + return - The read delete item, or the error response
    resource function get readdeleteitems/[int id]() returns ReadDeleteItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read delete item and returns it.
    #
    # + return - The deleted read delete item, or the error response
    resource function delete readdeleteitems/[int id]() returns ReadDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadDeleteItem", [id]);
        }
        return item;
    }

    # Creates one create update item, or several from an array.
    #
    # + return - The created create update item or create update items, or the error response
    resource function post createupdateitems(CreateUpdateItemInsert|CreateUpdateItemInsert[] payload)
            returns CreateUpdateItemCreated|CreateUpdateItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateItemInsert[] values = payload is CreateUpdateItemInsert[] ? payload : [payload];
        CreateUpdateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createupdateitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "CreateUpdateItem");
            }
            foreach int id in keys {
                CreateUpdateItem|persist:Error item = dataserviceClient->/createupdateitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "CreateUpdateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateUpdateItemInsert[] {
            return <CreateUpdateItemListCreated>{body: {items}};
        }
        CreateUpdateItem created = items[0];
        return <CreateUpdateItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/createupdateitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a create update item.
    #
    # + return - The updated create update item, or the error response
    resource function patch createupdateitems/[int id](CreateUpdateItemUpdate payload)
            returns CreateUpdateItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/createupdateitems/[id];
        } else {
            item = dataserviceClient->/createupdateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "CreateUpdateItem", [id]);
        }
        return item;
    }

    # Creates one create delete item, or several from an array.
    #
    # + return - The created create delete item or create delete items, or the error response
    resource function post createdeleteitems(CreateDeleteItemInsert|CreateDeleteItemInsert[] payload)
            returns CreateDeleteItemCreated|CreateDeleteItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateDeleteItemInsert[] values = payload is CreateDeleteItemInsert[] ? payload : [payload];
        CreateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createdeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "CreateDeleteItem");
            }
            foreach int id in keys {
                CreateDeleteItem|persist:Error item = dataserviceClient->/createdeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "CreateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateDeleteItemInsert[] {
            return <CreateDeleteItemListCreated>{body: {items}};
        }
        CreateDeleteItem created = items[0];
        return <CreateDeleteItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/createdeleteitems", [created.id])},
            body: created
        };
    }

    # Deletes a create delete item and returns it.
    #
    # + return - The deleted create delete item, or the error response
    resource function delete createdeleteitems/[int id]() returns CreateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateDeleteItem|persist:Error item = dataserviceClient->/createdeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "CreateDeleteItem", [id]);
        }
        return item;
    }

    # Updates the given fields of an update delete item.
    #
    # + return - The updated update delete item, or the error response
    resource function patch updatedeleteitems/[int id](UpdateDeleteItemUpdate payload)
            returns UpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        UpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/updatedeleteitems/[id];
        } else {
            item = dataserviceClient->/updatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "UpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes an update delete item and returns it.
    #
    # + return - The deleted update delete item, or the error response
    resource function delete updatedeleteitems/[int id]() returns UpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        UpdateDeleteItem|persist:Error item = dataserviceClient->/updatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "UpdateDeleteItem", [id]);
        }
        return item;
    }

    # Gets the read create update items.
    #
    # + return - The read create update items, or an internal error
    resource function get readcreateupdateitems() returns ReadCreateUpdateItemList|dsHttp:InternalError {
        ReadCreateUpdateItem[]|persist:Error items = from ReadCreateUpdateItem item in dataserviceClient->/readcreateupdateitems(ReadCreateUpdateItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read create update item by its key.
    #
    # + return - The read create update item, or the error response
    resource function get readcreateupdateitems/[int id]() returns ReadCreateUpdateItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadCreateUpdateItem|persist:Error item = dataserviceClient->/readcreateupdateitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateUpdateItem", [id]);
        }
        return item;
    }

    # Creates one read create update item, or several from an array.
    #
    # + return - The created read create update item or read create update items, or the error response
    resource function post readcreateupdateitems(ReadCreateUpdateItemInsert|ReadCreateUpdateItemInsert[] payload)
            returns ReadCreateUpdateItemCreated|ReadCreateUpdateItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateItemInsert[] values = payload is ReadCreateUpdateItemInsert[] ? payload : [payload];
        ReadCreateUpdateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreateupdateitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "ReadCreateUpdateItem");
            }
            foreach int id in keys {
                ReadCreateUpdateItem|persist:Error item = dataserviceClient->/readcreateupdateitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "ReadCreateUpdateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateUpdateItemInsert[] {
            return <ReadCreateUpdateItemListCreated>{body: {items}};
        }
        ReadCreateUpdateItem created = items[0];
        return <ReadCreateUpdateItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/readcreateupdateitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a read create update item.
    #
    # + return - The updated read create update item, or the error response
    resource function patch readcreateupdateitems/[int id](ReadCreateUpdateItemUpdate payload)
            returns ReadCreateUpdateItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
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

    # Gets the read create delete items.
    #
    # + return - The read create delete items, or an internal error
    resource function get readcreatedeleteitems() returns ReadCreateDeleteItemList|dsHttp:InternalError {
        ReadCreateDeleteItem[]|persist:Error items = from ReadCreateDeleteItem item in dataserviceClient->/readcreatedeleteitems(ReadCreateDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read create delete item by its key.
    #
    # + return - The read create delete item, or the error response
    resource function get readcreatedeleteitems/[int id]() returns ReadCreateDeleteItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one read create delete item, or several from an array.
    #
    # + return - The created read create delete item or read create delete items, or the error response
    resource function post readcreatedeleteitems(ReadCreateDeleteItemInsert|ReadCreateDeleteItemInsert[] payload)
            returns ReadCreateDeleteItemCreated|ReadCreateDeleteItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateDeleteItemInsert[] values = payload is ReadCreateDeleteItemInsert[] ? payload : [payload];
        ReadCreateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreatedeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "ReadCreateDeleteItem");
            }
            foreach int id in keys {
                ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "ReadCreateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateDeleteItemInsert[] {
            return <ReadCreateDeleteItemListCreated>{body: {items}};
        }
        ReadCreateDeleteItem created = items[0];
        return <ReadCreateDeleteItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/readcreatedeleteitems", [created.id])},
            body: created
        };
    }

    # Deletes a read create delete item and returns it.
    #
    # + return - The deleted read create delete item, or the error response
    resource function delete readcreatedeleteitems/[int id]() returns ReadCreateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadCreateDeleteItem", [id]);
        }
        return item;
    }

    # Gets the read update delete items.
    #
    # + return - The read update delete items, or an internal error
    resource function get readupdatedeleteitems() returns ReadUpdateDeleteItemList|dsHttp:InternalError {
        ReadUpdateDeleteItem[]|persist:Error items = from ReadUpdateDeleteItem item in dataserviceClient->/readupdatedeleteitems(ReadUpdateDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read update delete item by its key.
    #
    # + return - The read update delete item, or the error response
    resource function get readupdatedeleteitems/[int id]() returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Updates the given fields of a read update delete item.
    #
    # + return - The updated read update delete item, or the error response
    resource function patch readupdatedeleteitems/[int id](ReadUpdateDeleteItemUpdate payload)
            returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
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

    # Deletes a read update delete item and returns it.
    #
    # + return - The deleted read update delete item, or the error response
    resource function delete readupdatedeleteitems/[int id]() returns ReadUpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one create update delete item, or several from an array.
    #
    # + return - The created create update delete item or create update delete items, or the error response
    resource function post createupdatedeleteitems(CreateUpdateDeleteItemInsert|CreateUpdateDeleteItemInsert[] payload)
            returns CreateUpdateDeleteItemCreated|CreateUpdateDeleteItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateDeleteItemInsert[] values = payload is CreateUpdateDeleteItemInsert[] ? payload : [payload];
        CreateUpdateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createupdatedeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "CreateUpdateDeleteItem");
            }
            foreach int id in keys {
                CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "CreateUpdateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateUpdateDeleteItemInsert[] {
            return <CreateUpdateDeleteItemListCreated>{body: {items}};
        }
        CreateUpdateDeleteItem created = items[0];
        return <CreateUpdateDeleteItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/createupdatedeleteitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a create update delete item.
    #
    # + return - The updated create update delete item, or the error response
    resource function patch createupdatedeleteitems/[int id](CreateUpdateDeleteItemUpdate payload)
            returns CreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/createupdatedeleteitems/[id];
        } else {
            item = dataserviceClient->/createupdatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "CreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a create update delete item and returns it.
    #
    # + return - The deleted create update delete item, or the error response
    resource function delete createupdatedeleteitems/[int id]() returns CreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "CreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Gets the read create update delete items.
    #
    # + return - The read create update delete items, or an internal error
    resource function get readcreateupdatedeleteitems() returns ReadCreateUpdateDeleteItemList|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem[]|persist:Error items = from ReadCreateUpdateDeleteItem item in dataserviceClient->/readcreateupdatedeleteitems(ReadCreateUpdateDeleteItem)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a read create update delete item by its key.
    #
    # + return - The read create update delete item, or the error response
    resource function get readcreateupdatedeleteitems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one read create update delete item, or several from an array.
    #
    # + return - The created read create update delete item or read create update delete items, or the error response
    resource function post readcreateupdatedeleteitems(ReadCreateUpdateDeleteItemInsert|ReadCreateUpdateDeleteItemInsert[] payload)
            returns ReadCreateUpdateDeleteItemCreated|ReadCreateUpdateDeleteItemListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateDeleteItemInsert[] values = payload is ReadCreateUpdateDeleteItemInsert[] ? payload : [payload];
        ReadCreateUpdateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreateupdatedeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "ReadCreateUpdateDeleteItem");
            }
            foreach int id in keys {
                ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "ReadCreateUpdateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateUpdateDeleteItemInsert[] {
            return <ReadCreateUpdateDeleteItemListCreated>{body: {items}};
        }
        ReadCreateUpdateDeleteItem created = items[0];
        return <ReadCreateUpdateDeleteItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/readcreateupdatedeleteitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a read create update delete item.
    #
    # + return - The updated read create update delete item, or the error response
    resource function patch readcreateupdatedeleteitems/[int id](ReadCreateUpdateDeleteItemUpdate payload)
            returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
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

    # Deletes a read create update delete item and returns it.
    #
    # + return - The deleted read create update delete item, or the error response
    resource function delete readcreateupdatedeleteitems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }
}
