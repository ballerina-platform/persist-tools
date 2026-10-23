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
        TicketPayload[]|persist:Error items = from Ticket item in dataserviceClient->/tickets(Ticket)
            select dataserviceToTicketPayload(item);
        if items is persist:Error {
            return dsHttp:internalError(items);
        }
        return {items};
    }

    # Gets a ticket by its key.
    #
    # + return - The ticket, or the error response
    resource function get tickets/[int id]() returns TicketPayload|dsHttp:ErrorResponse {
        Ticket|persist:Error item = dataserviceClient->/tickets/[id];
        if item is persist:Error {
            return dsHttp:errorResponse(item, "Ticket", [id]);
        }
        return dataserviceToTicketPayload(item);
    }

    # Creates one ticket, or several from an array.
    #
    # + return - The created ticket or tickets, or the error response
    resource function post tickets(TicketInsertPayload|TicketInsertPayload[] payload)
            returns TicketCreated|TicketListCreated|dsHttp:ErrorResponse {
        TicketInsertPayload[] entries = payload is TicketInsertPayload[] ? payload : [payload];
        TicketInsert[] values = [];
        foreach TicketInsertPayload entry in entries {
            TicketInsert|dsHttp:InvalidInput value = dataserviceFromTicketInsertPayload(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        TicketPayload[] items = [];
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
                items.push(dataserviceToTicketPayload(item));
            }
        }
        if payload is TicketInsertPayload[] {
            return <TicketListCreated>{body: {items}};
        }
        TicketPayload created = items[0];
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

# Converts a `Ticket` to its JSON form.
#
# + value - The `Ticket` record
# + return - The JSON form
isolated function dataserviceToTicketPayload(Ticket value) returns TicketPayload => {
    id: value.id,
    subject: value.subject,
    openedOn: dataservice:dateToString(value.openedOn)
};

# Converts the JSON form of a `TicketInsert` to the persist type.
#
# + wire - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function dataserviceFromTicketInsertPayload(TicketInsertPayload wire) returns TicketInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError openedOn = dataservice:dateFromString(wire.openedOn);
    if openedOn is dataservice:ConversionError {
        return dsHttp:invalidInput(openedOn, "Ticket", "openedOn");
    }
    return {subject: wire.subject, openedOn};
}
