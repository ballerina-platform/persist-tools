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
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

@openapi:ServiceInfo {embed: true}
service /api on dataserviceListener {

    # Gets a page of tickets.
    #
    # + return - A page of tickets, or the error response
    resource function get tickets() returns TicketList|dsHttp:InvalidInput|dsHttp:InternalError {
        TicketPayload[]|persist:Error items = from Ticket item in dataserviceClient->/tickets(Ticket)
            select transformTicketToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a ticket by its key.
    #
    # + id - The id of the ticket
    # + return - The ticket, or the error response
    resource function get tickets/[int id]() returns TicketPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Ticket|persist:Error item = dataserviceClient->/tickets/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Ticket", [id]);
        }
        return transformTicketToPayload(item);
    }

    # Creates one or more tickets.
    #
    # + payload - The ticket, or the tickets, to create
    # + return - The created ticket or tickets, or the error response
    resource function post tickets(TicketInsertPayload|TicketInsertPayload[] payload)
            returns TicketCreated|TicketListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        TicketInsertPayload[] entries = payload is TicketInsertPayload[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(entries.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        TicketInsert[] values = [];
        foreach TicketInsertPayload entry in entries {
            TicketInsert|dsHttp:InvalidInput value = transformPayloadToTicketInsert(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        TicketPayload[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/tickets.post(values);
                    foreach int id in keys {
                        Ticket item = check dataserviceClient->/tickets/[id];
                        items.push(transformTicketToPayload(item));
                    }
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Ticket");
            }
        }
        if payload is TicketInsertPayload[] {
            return <TicketListCreated>{body: {items, hasMore: false}};
        }
        TicketPayload created = items[0];
        return <TicketCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/tickets", [created.id])},
            body: created
        };
    }

    # Creates one or more order items.
    #
    # + payload - The order item, or the order items, to create
    # + return - The created order item or order items, or the error response
    resource function post orderitems(OrderItemInsert|OrderItemInsert[] payload)
            returns OrderItemCreated|OrderItemListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        OrderItemInsert[] values = payload is OrderItemInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        OrderItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/orderitems.post(values);
                    foreach int id in keys {
                        OrderItem item = check dataserviceClient->/orderitems/[id];
                        items.push(item);
                    }
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "OrderItem");
            }
        }
        if payload is OrderItemInsert[] {
            return <OrderItemListCreated>{body: {items, hasMore: false}};
        }
        OrderItem created = items[0];
        return <OrderItemCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/orderitems", [created.id])},
            body: created
        };
    }
}
