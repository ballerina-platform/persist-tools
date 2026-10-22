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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read item by its key.
    #
    # + return - The read item, or the error response
    resource function get readitems/[int id]() returns ReadItem|dsHttp:ErrorResponse {
        ReadItem|persist:Error item = dataserviceClient->/readitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadItem", [id]);
        }
        return item;
    }

    # Creates one create item, or several from an array.
    #
    # + return - The created create item or create items, or the error response
    resource function post createitems(CreateItemInsert|CreateItemInsert[] payload)
            returns CreateItemCreated|CreateItemListCreated|dsHttp:ErrorResponse {
        CreateItemInsert[] values = payload is CreateItemInsert[] ? payload : [payload];
        CreateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "CreateItem");
            }
            foreach int id in keys {
                CreateItem|persist:Error item = dataserviceClient->/createitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "CreateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateItemInsert[] {
            return <CreateItemListCreated>{body: {items}};
        }
        CreateItem created = items[0];
        return <CreateItemCreated>{
            headers: {"Location": dsHttp:location("/api/createitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of an update item.
    #
    # + return - The updated update item, or the error response
    resource function patch updateitems/[int id](UpdateItemUpdate payload) returns UpdateItem|dsHttp:ErrorResponse {
        UpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/updateitems/[id];
        } else {
            item = dataserviceClient->/updateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "UpdateItem", [id]);
        }
        return item;
    }

    # Deletes a delete item and returns it.
    #
    # + return - The deleted delete item, or the error response
    resource function delete deleteitems/[int id]() returns DeleteItem|dsHttp:ErrorResponse {
        DeleteItem|persist:Error item = dataserviceClient->/deleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "DeleteItem", [id]);
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read create item by its key.
    #
    # + return - The read create item, or the error response
    resource function get readcreateitems/[int id]() returns ReadCreateItem|dsHttp:ErrorResponse {
        ReadCreateItem|persist:Error item = dataserviceClient->/readcreateitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateItem", [id]);
        }
        return item;
    }

    # Creates one read create item, or several from an array.
    #
    # + return - The created read create item or read create items, or the error response
    resource function post readcreateitems(ReadCreateItemInsert|ReadCreateItemInsert[] payload)
            returns ReadCreateItemCreated|ReadCreateItemListCreated|dsHttp:ErrorResponse {
        ReadCreateItemInsert[] values = payload is ReadCreateItemInsert[] ? payload : [payload];
        ReadCreateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreateitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "ReadCreateItem");
            }
            foreach int id in keys {
                ReadCreateItem|persist:Error item = dataserviceClient->/readcreateitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "ReadCreateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateItemInsert[] {
            return <ReadCreateItemListCreated>{body: {items}};
        }
        ReadCreateItem created = items[0];
        return <ReadCreateItemCreated>{
            headers: {"Location": dsHttp:location("/api/readcreateitems", [created.id])},
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read update item by its key.
    #
    # + return - The read update item, or the error response
    resource function get readupdateitems/[int id]() returns ReadUpdateItem|dsHttp:ErrorResponse {
        ReadUpdateItem|persist:Error item = dataserviceClient->/readupdateitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadUpdateItem", [id]);
        }
        return item;
    }

    # Updates the given fields of a read update item.
    #
    # + return - The updated read update item, or the error response
    resource function patch readupdateitems/[int id](ReadUpdateItemUpdate payload) returns ReadUpdateItem|dsHttp:ErrorResponse {
        ReadUpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readupdateitems/[id];
        } else {
            item = dataserviceClient->/readupdateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadUpdateItem", [id]);
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read delete item by its key.
    #
    # + return - The read delete item, or the error response
    resource function get readdeleteitems/[int id]() returns ReadDeleteItem|dsHttp:ErrorResponse {
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read delete item and returns it.
    #
    # + return - The deleted read delete item, or the error response
    resource function delete readdeleteitems/[int id]() returns ReadDeleteItem|dsHttp:ErrorResponse {
        ReadDeleteItem|persist:Error item = dataserviceClient->/readdeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadDeleteItem", [id]);
        }
        return item;
    }

    # Creates one create update item, or several from an array.
    #
    # + return - The created create update item or create update items, or the error response
    resource function post createupdateitems(CreateUpdateItemInsert|CreateUpdateItemInsert[] payload)
            returns CreateUpdateItemCreated|CreateUpdateItemListCreated|dsHttp:ErrorResponse {
        CreateUpdateItemInsert[] values = payload is CreateUpdateItemInsert[] ? payload : [payload];
        CreateUpdateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createupdateitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "CreateUpdateItem");
            }
            foreach int id in keys {
                CreateUpdateItem|persist:Error item = dataserviceClient->/createupdateitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "CreateUpdateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateUpdateItemInsert[] {
            return <CreateUpdateItemListCreated>{body: {items}};
        }
        CreateUpdateItem created = items[0];
        return <CreateUpdateItemCreated>{
            headers: {"Location": dsHttp:location("/api/createupdateitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a create update item.
    #
    # + return - The updated create update item, or the error response
    resource function patch createupdateitems/[int id](CreateUpdateItemUpdate payload) returns CreateUpdateItem|dsHttp:ErrorResponse {
        CreateUpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/createupdateitems/[id];
        } else {
            item = dataserviceClient->/createupdateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "CreateUpdateItem", [id]);
        }
        return item;
    }

    # Creates one create delete item, or several from an array.
    #
    # + return - The created create delete item or create delete items, or the error response
    resource function post createdeleteitems(CreateDeleteItemInsert|CreateDeleteItemInsert[] payload)
            returns CreateDeleteItemCreated|CreateDeleteItemListCreated|dsHttp:ErrorResponse {
        CreateDeleteItemInsert[] values = payload is CreateDeleteItemInsert[] ? payload : [payload];
        CreateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createdeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "CreateDeleteItem");
            }
            foreach int id in keys {
                CreateDeleteItem|persist:Error item = dataserviceClient->/createdeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "CreateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateDeleteItemInsert[] {
            return <CreateDeleteItemListCreated>{body: {items}};
        }
        CreateDeleteItem created = items[0];
        return <CreateDeleteItemCreated>{
            headers: {"Location": dsHttp:location("/api/createdeleteitems", [created.id])},
            body: created
        };
    }

    # Deletes a create delete item and returns it.
    #
    # + return - The deleted create delete item, or the error response
    resource function delete createdeleteitems/[int id]() returns CreateDeleteItem|dsHttp:ErrorResponse {
        CreateDeleteItem|persist:Error item = dataserviceClient->/createdeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "CreateDeleteItem", [id]);
        }
        return item;
    }

    # Updates the given fields of an update delete item.
    #
    # + return - The updated update delete item, or the error response
    resource function patch updatedeleteitems/[int id](UpdateDeleteItemUpdate payload) returns UpdateDeleteItem|dsHttp:ErrorResponse {
        UpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/updatedeleteitems/[id];
        } else {
            item = dataserviceClient->/updatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "UpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes an update delete item and returns it.
    #
    # + return - The deleted update delete item, or the error response
    resource function delete updatedeleteitems/[int id]() returns UpdateDeleteItem|dsHttp:ErrorResponse {
        UpdateDeleteItem|persist:Error item = dataserviceClient->/updatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "UpdateDeleteItem", [id]);
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read create update item by its key.
    #
    # + return - The read create update item, or the error response
    resource function get readcreateupdateitems/[int id]() returns ReadCreateUpdateItem|dsHttp:ErrorResponse {
        ReadCreateUpdateItem|persist:Error item = dataserviceClient->/readcreateupdateitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateUpdateItem", [id]);
        }
        return item;
    }

    # Creates one read create update item, or several from an array.
    #
    # + return - The created read create update item or read create update items, or the error response
    resource function post readcreateupdateitems(ReadCreateUpdateItemInsert|ReadCreateUpdateItemInsert[] payload)
            returns ReadCreateUpdateItemCreated|ReadCreateUpdateItemListCreated|dsHttp:ErrorResponse {
        ReadCreateUpdateItemInsert[] values = payload is ReadCreateUpdateItemInsert[] ? payload : [payload];
        ReadCreateUpdateItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreateupdateitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "ReadCreateUpdateItem");
            }
            foreach int id in keys {
                ReadCreateUpdateItem|persist:Error item = dataserviceClient->/readcreateupdateitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "ReadCreateUpdateItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateUpdateItemInsert[] {
            return <ReadCreateUpdateItemListCreated>{body: {items}};
        }
        ReadCreateUpdateItem created = items[0];
        return <ReadCreateUpdateItemCreated>{
            headers: {"Location": dsHttp:location("/api/readcreateupdateitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a read create update item.
    #
    # + return - The updated read create update item, or the error response
    resource function patch readcreateupdateitems/[int id](ReadCreateUpdateItemUpdate payload) returns ReadCreateUpdateItem|dsHttp:ErrorResponse {
        ReadCreateUpdateItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readcreateupdateitems/[id];
        } else {
            item = dataserviceClient->/readcreateupdateitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateUpdateItem", [id]);
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read create delete item by its key.
    #
    # + return - The read create delete item, or the error response
    resource function get readcreatedeleteitems/[int id]() returns ReadCreateDeleteItem|dsHttp:ErrorResponse {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one read create delete item, or several from an array.
    #
    # + return - The created read create delete item or read create delete items, or the error response
    resource function post readcreatedeleteitems(ReadCreateDeleteItemInsert|ReadCreateDeleteItemInsert[] payload)
            returns ReadCreateDeleteItemCreated|ReadCreateDeleteItemListCreated|dsHttp:ErrorResponse {
        ReadCreateDeleteItemInsert[] values = payload is ReadCreateDeleteItemInsert[] ? payload : [payload];
        ReadCreateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreatedeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "ReadCreateDeleteItem");
            }
            foreach int id in keys {
                ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "ReadCreateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateDeleteItemInsert[] {
            return <ReadCreateDeleteItemListCreated>{body: {items}};
        }
        ReadCreateDeleteItem created = items[0];
        return <ReadCreateDeleteItemCreated>{
            headers: {"Location": dsHttp:location("/api/readcreatedeleteitems", [created.id])},
            body: created
        };
    }

    # Deletes a read create delete item and returns it.
    #
    # + return - The deleted read create delete item, or the error response
    resource function delete readcreatedeleteitems/[int id]() returns ReadCreateDeleteItem|dsHttp:ErrorResponse {
        ReadCreateDeleteItem|persist:Error item = dataserviceClient->/readcreatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateDeleteItem", [id]);
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read update delete item by its key.
    #
    # + return - The read update delete item, or the error response
    resource function get readupdatedeleteitems/[int id]() returns ReadUpdateDeleteItem|dsHttp:ErrorResponse {
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Updates the given fields of a read update delete item.
    #
    # + return - The updated read update delete item, or the error response
    resource function patch readupdatedeleteitems/[int id](ReadUpdateDeleteItemUpdate payload) returns ReadUpdateDeleteItem|dsHttp:ErrorResponse {
        ReadUpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readupdatedeleteitems/[id];
        } else {
            item = dataserviceClient->/readupdatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read update delete item and returns it.
    #
    # + return - The deleted read update delete item, or the error response
    resource function delete readupdatedeleteitems/[int id]() returns ReadUpdateDeleteItem|dsHttp:ErrorResponse {
        ReadUpdateDeleteItem|persist:Error item = dataserviceClient->/readupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one create update delete item, or several from an array.
    #
    # + return - The created create update delete item or create update delete items, or the error response
    resource function post createupdatedeleteitems(CreateUpdateDeleteItemInsert|CreateUpdateDeleteItemInsert[] payload)
            returns CreateUpdateDeleteItemCreated|CreateUpdateDeleteItemListCreated|dsHttp:ErrorResponse {
        CreateUpdateDeleteItemInsert[] values = payload is CreateUpdateDeleteItemInsert[] ? payload : [payload];
        CreateUpdateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/createupdatedeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "CreateUpdateDeleteItem");
            }
            foreach int id in keys {
                CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "CreateUpdateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is CreateUpdateDeleteItemInsert[] {
            return <CreateUpdateDeleteItemListCreated>{body: {items}};
        }
        CreateUpdateDeleteItem created = items[0];
        return <CreateUpdateDeleteItemCreated>{
            headers: {"Location": dsHttp:location("/api/createupdatedeleteitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a create update delete item.
    #
    # + return - The updated create update delete item, or the error response
    resource function patch createupdatedeleteitems/[int id](CreateUpdateDeleteItemUpdate payload) returns CreateUpdateDeleteItem|dsHttp:ErrorResponse {
        CreateUpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/createupdatedeleteitems/[id];
        } else {
            item = dataserviceClient->/createupdatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "CreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a create update delete item and returns it.
    #
    # + return - The deleted create update delete item, or the error response
    resource function delete createupdatedeleteitems/[int id]() returns CreateUpdateDeleteItem|dsHttp:ErrorResponse {
        CreateUpdateDeleteItem|persist:Error item = dataserviceClient->/createupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "CreateUpdateDeleteItem", [id]);
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
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a read create update delete item by its key.
    #
    # + return - The read create update delete item, or the error response
    resource function get readcreateupdatedeleteitems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:ErrorResponse {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Creates one read create update delete item, or several from an array.
    #
    # + return - The created read create update delete item or read create update delete items, or the error response
    resource function post readcreateupdatedeleteitems(ReadCreateUpdateDeleteItemInsert|ReadCreateUpdateDeleteItemInsert[] payload)
            returns ReadCreateUpdateDeleteItemCreated|ReadCreateUpdateDeleteItemListCreated|dsHttp:ErrorResponse {
        ReadCreateUpdateDeleteItemInsert[] values = payload is ReadCreateUpdateDeleteItemInsert[] ? payload : [payload];
        ReadCreateUpdateDeleteItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/readcreateupdatedeleteitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "ReadCreateUpdateDeleteItem");
            }
            foreach int id in keys {
                ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "ReadCreateUpdateDeleteItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is ReadCreateUpdateDeleteItemInsert[] {
            return <ReadCreateUpdateDeleteItemListCreated>{body: {items}};
        }
        ReadCreateUpdateDeleteItem created = items[0];
        return <ReadCreateUpdateDeleteItemCreated>{
            headers: {"Location": dsHttp:location("/api/readcreateupdatedeleteitems", [created.id])},
            body: created
        };
    }

    # Updates the given fields of a read create update delete item.
    #
    # + return - The updated read create update delete item, or the error response
    resource function patch readcreateupdatedeleteitems/[int id](ReadCreateUpdateDeleteItemUpdate payload) returns ReadCreateUpdateDeleteItem|dsHttp:ErrorResponse {
        ReadCreateUpdateDeleteItem|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/readcreateupdatedeleteitems/[id];
        } else {
            item = dataserviceClient->/readcreateupdatedeleteitems/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }

    # Deletes a read create update delete item and returns it.
    #
    # + return - The deleted read create update delete item, or the error response
    resource function delete readcreateupdatedeleteitems/[int id]() returns ReadCreateUpdateDeleteItem|dsHttp:ErrorResponse {
        ReadCreateUpdateDeleteItem|persist:Error item = dataserviceClient->/readcreateupdatedeleteitems/[id].delete();
        if item is persist:Error {
            return dsHttp:errorResponse(item, "ReadCreateUpdateDeleteItem", [id]);
        }
        return item;
    }
}

# A list of read items.
#
# + items - The read items
public type ReadItemList record {|
    ReadItem[] items;
|};

# A list of create items.
#
# + items - The create items
public type CreateItemList record {|
    CreateItem[] items;
|};

# The response to creating one create item.
#
# + body - The created create item
public type CreateItemCreated record {|
    *http:Created;
    CreateItem body;
|};

# The response to creating several create items.
#
# + body - The created create items
public type CreateItemListCreated record {|
    *http:Created;
    CreateItemList body;
|};

# A list of read create items.
#
# + items - The read create items
public type ReadCreateItemList record {|
    ReadCreateItem[] items;
|};

# The response to creating one read create item.
#
# + body - The created read create item
public type ReadCreateItemCreated record {|
    *http:Created;
    ReadCreateItem body;
|};

# The response to creating several read create items.
#
# + body - The created read create items
public type ReadCreateItemListCreated record {|
    *http:Created;
    ReadCreateItemList body;
|};

# A list of read update items.
#
# + items - The read update items
public type ReadUpdateItemList record {|
    ReadUpdateItem[] items;
|};

# A list of read delete items.
#
# + items - The read delete items
public type ReadDeleteItemList record {|
    ReadDeleteItem[] items;
|};

# A list of create update items.
#
# + items - The create update items
public type CreateUpdateItemList record {|
    CreateUpdateItem[] items;
|};

# The response to creating one create update item.
#
# + body - The created create update item
public type CreateUpdateItemCreated record {|
    *http:Created;
    CreateUpdateItem body;
|};

# The response to creating several create update items.
#
# + body - The created create update items
public type CreateUpdateItemListCreated record {|
    *http:Created;
    CreateUpdateItemList body;
|};

# A list of create delete items.
#
# + items - The create delete items
public type CreateDeleteItemList record {|
    CreateDeleteItem[] items;
|};

# The response to creating one create delete item.
#
# + body - The created create delete item
public type CreateDeleteItemCreated record {|
    *http:Created;
    CreateDeleteItem body;
|};

# The response to creating several create delete items.
#
# + body - The created create delete items
public type CreateDeleteItemListCreated record {|
    *http:Created;
    CreateDeleteItemList body;
|};

# A list of read create update items.
#
# + items - The read create update items
public type ReadCreateUpdateItemList record {|
    ReadCreateUpdateItem[] items;
|};

# The response to creating one read create update item.
#
# + body - The created read create update item
public type ReadCreateUpdateItemCreated record {|
    *http:Created;
    ReadCreateUpdateItem body;
|};

# The response to creating several read create update items.
#
# + body - The created read create update items
public type ReadCreateUpdateItemListCreated record {|
    *http:Created;
    ReadCreateUpdateItemList body;
|};

# A list of read create delete items.
#
# + items - The read create delete items
public type ReadCreateDeleteItemList record {|
    ReadCreateDeleteItem[] items;
|};

# The response to creating one read create delete item.
#
# + body - The created read create delete item
public type ReadCreateDeleteItemCreated record {|
    *http:Created;
    ReadCreateDeleteItem body;
|};

# The response to creating several read create delete items.
#
# + body - The created read create delete items
public type ReadCreateDeleteItemListCreated record {|
    *http:Created;
    ReadCreateDeleteItemList body;
|};

# A list of read update delete items.
#
# + items - The read update delete items
public type ReadUpdateDeleteItemList record {|
    ReadUpdateDeleteItem[] items;
|};

# A list of create update delete items.
#
# + items - The create update delete items
public type CreateUpdateDeleteItemList record {|
    CreateUpdateDeleteItem[] items;
|};

# The response to creating one create update delete item.
#
# + body - The created create update delete item
public type CreateUpdateDeleteItemCreated record {|
    *http:Created;
    CreateUpdateDeleteItem body;
|};

# The response to creating several create update delete items.
#
# + body - The created create update delete items
public type CreateUpdateDeleteItemListCreated record {|
    *http:Created;
    CreateUpdateDeleteItemList body;
|};

# A list of read create update delete items.
#
# + items - The read create update delete items
public type ReadCreateUpdateDeleteItemList record {|
    ReadCreateUpdateDeleteItem[] items;
|};

# The response to creating one read create update delete item.
#
# + body - The created read create update delete item
public type ReadCreateUpdateDeleteItemCreated record {|
    *http:Created;
    ReadCreateUpdateDeleteItem body;
|};

# The response to creating several read create update delete items.
#
# + body - The created read create update delete items
public type ReadCreateUpdateDeleteItemListCreated record {|
    *http:Created;
    ReadCreateUpdateDeleteItemList body;
|};
