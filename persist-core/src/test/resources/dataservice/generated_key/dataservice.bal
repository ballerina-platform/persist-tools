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
    # + filter - The conditions that the tickets match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of tickets in the page
    # + offset - The number of tickets to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of tickets, or the error response
    resource function get tickets(TicketFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns TicketList|TicketPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceTicketEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Ticket");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceTicketEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Ticket");
        }
        stream<Ticket, persist:Error?> rows = dataserviceClient->/tickets(Ticket,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        TicketPayload[]|persist:Error items = from Ticket item in rows
            select transformTicketToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            TicketPayload[] sources = items;
            map<anydata>[] partials = from TicketPayload item in sources
                select dataservice:narrow(item, dataserviceTicketEntity, fields);
            TicketPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <TicketPartialList>{items: converted, hasMore};
        }
        return <TicketList>{items, hasMore};
    }

    # Gets a ticket by its key.
    #
    # + id - The id of the ticket
    # + fields - The fields to return; the key fields are always returned
    # + return - The ticket, or the error response
    resource function get tickets/[int id](string[]? fields = ()) returns TicketPayload|TicketPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceTicketEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Ticket");
        }
        Ticket|persist:Error item = dataserviceClient->/tickets/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Ticket", [id]);
        }
        if fields is string[] {
            TicketPayload[] sources = [transformTicketToPayload(item)];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceTicketEntity, fields)];
            TicketPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Ticket", [id]);
            }
            return partial;
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
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:keysQuery(dataserviceTicketEntity, keyValues);
                    stream<Ticket, persist:Error?> stored = dataserviceClient->/tickets(Ticket,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Ticket[] rows = check from Ticket item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceTicketEntity, keyValues);
                    items = from Ticket item in rows
                        select transformTicketToPayload(item);
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
    resource function post orderItems(OrderItemInsert|OrderItemInsert[] payload)
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
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:keysQuery(dataserviceOrderItemEntity, keyValues);
                    stream<OrderItem, persist:Error?> stored = dataserviceClient->/orderitems(OrderItem,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    OrderItem[] rows = check from OrderItem item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceOrderItemEntity, keyValues);
                    items = rows;
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
            headers: {"Location": dsHttp:buildLocation("/api/orderItems", [created.id])},
            body: created
        };
    }
}
