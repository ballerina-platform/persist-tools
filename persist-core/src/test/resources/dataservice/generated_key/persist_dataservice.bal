// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;
import ballerina/time;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /api on dataserviceListener {

    # Gets the tickets.
    #
    # + return - The tickets, or an internal error
    resource function get tickets() returns TicketList|dsHttp:InternalError {
        TicketRecord[]|persist:Error items = from Ticket item in dataserviceClient->/tickets(Ticket)
            select dataserviceToTicketRecord(item);
        if items is persist:Error {
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a ticket by its key.
    #
    # + return - The ticket, or the error response
    resource function get tickets/[int id]() returns TicketRecord|dsHttp:ErrorResponse {
        Ticket|persist:Error item = dataserviceClient->/tickets/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Ticket", [id]);
        }
        return dataserviceToTicketRecord(item);
    }

    # Creates one ticket, or several from an array.
    #
    # + return - The created ticket or tickets, or the error response
    resource function post tickets(TicketInsertRecord|TicketInsertRecord[] payload)
            returns TicketCreated|TicketListCreated|dsHttp:ErrorResponse {
        TicketInsertRecord[] entries = payload is TicketInsertRecord[] ? payload : [payload];
        TicketInsert[] values = [];
        foreach TicketInsertRecord entry in entries {
            TicketInsert|dsHttp:InvalidInput value = dataserviceFromTicketInsertRecord(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        TicketRecord[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/tickets.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "Ticket");
            }
            foreach int id in keys {
                Ticket|persist:Error item = dataserviceClient->/tickets/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "Ticket", [id]);
                }
                items.push(dataserviceToTicketRecord(item));
            }
        }
        if payload is TicketInsertRecord[] {
            return <TicketListCreated>{body: {items}};
        }
        TicketRecord created = items[0];
        return <TicketCreated>{
            headers: {"Location": dsHttp:location("/api/tickets", [created.id])},
            body: created
        };
    }

    # Creates one order item, or several from an array.
    #
    # + return - The created order item or order items, or the error response
    resource function post orderitems(OrderItemInsert|OrderItemInsert[] payload)
            returns OrderItemCreated|OrderItemListCreated|dsHttp:ErrorResponse {
        OrderItemInsert[] values = payload is OrderItemInsert[] ? payload : [payload];
        OrderItem[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/orderitems.post(values);
            if keys is persist:Error {
                return dsHttp:errorResponse(keys, "OrderItem");
            }
            foreach int id in keys {
                OrderItem|persist:Error item = dataserviceClient->/orderitems/[id];
                if item is persist:Error {
                    return dsHttp:errorResponse(item, "OrderItem", [id]);
                }
                items.push(item);
            }
        }
        if payload is OrderItemInsert[] {
            return <OrderItemListCreated>{body: {items}};
        }
        OrderItem created = items[0];
        return <OrderItemCreated>{
            headers: {"Location": dsHttp:location("/api/orderitems", [created.id])},
            body: created
        };
    }
}

# The JSON form of `Ticket`.
#
# + id - The `id` field of `Ticket`
# + subject - The `subject` field of `Ticket`
# + openedOn - The `openedOn` field of `Ticket`, as an ISO 8601 date
public type TicketRecord record {|
    readonly int id;
    string subject;
    string openedOn;
|};

# The JSON form of `TicketInsert`.
#
# + subject - The `subject` field of `TicketInsert`
# + openedOn - The `openedOn` field of `TicketInsert`, as an ISO 8601 date
public type TicketInsertRecord record {|
    string subject;
    string openedOn;
|};

# A list of tickets.
#
# + items - The tickets
public type TicketList record {|
    TicketRecord[] items;
|};

# The response to creating one ticket.
#
# + body - The created ticket
public type TicketCreated record {|
    *http:Created;
    TicketRecord body;
|};

# The response to creating several tickets.
#
# + body - The created tickets
public type TicketListCreated record {|
    *http:Created;
    TicketList body;
|};

# A list of order items.
#
# + items - The order items
public type OrderItemList record {|
    OrderItem[] items;
|};

# The response to creating one order item.
#
# + body - The created order item
public type OrderItemCreated record {|
    *http:Created;
    OrderItem body;
|};

# The response to creating several order items.
#
# + body - The created order items
public type OrderItemListCreated record {|
    *http:Created;
    OrderItemList body;
|};

# Converts a `Ticket` to its JSON form.
#
# + value - The `Ticket` record
# + return - The JSON form
isolated function dataserviceToTicketRecord(Ticket value) returns TicketRecord => {
    id: value.id,
    subject: value.subject,
    openedOn: dataservice:dateToString(value.openedOn)
};

# Converts the JSON form of a `TicketInsert` to the persist type.
#
# + wire - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function dataserviceFromTicketInsertRecord(TicketInsertRecord wire) returns TicketInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError openedOn = dataservice:dateFromString(wire.openedOn);
    if openedOn is dataservice:ConversionError {
        return dsHttp:invalidInput(openedOn, "Ticket", "openedOn");
    }
    return {subject: wire.subject, openedOn};
}
