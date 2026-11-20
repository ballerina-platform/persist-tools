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

    # Gets a page of tickets.
    #
    # + filter - The conditions that the tickets match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of tickets in the page
    # + offset - The number of tickets to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of tickets, or the error response
    resource function get tickets(TicketFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns TicketList|TicketPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceTicketEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Ticket");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceTicketEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Ticket");
        }
        stream<Ticket, persist:Error?> rows = self.persistClient->/tickets(Ticket,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        TicketPayload[]|persist:Error items = from Ticket item in rows
            select transformTicketToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items, "Ticket");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            TicketPayload[] sources = items;
            map<anydata>[] partials = from TicketPayload item in sources
                select dataservice:selectFields(item, dataserviceTicketEntity, fields);
            TicketPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Ticket");
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
    resource function get tickets/[int id](string[]? fields = ())
            returns TicketPayload|TicketPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceTicketEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Ticket");
        }
        Ticket|persist:Error item = self.persistClient->/tickets/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Ticket", [id]);
        }
        if fields is string[] {
            TicketPayload itemPayload = transformTicketToPayload(item);
            map<anydata> partial = dataservice:selectFields(itemPayload, dataserviceTicketEntity, fields);
            TicketPartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Ticket", [id]);
            }
            return converted;
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
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(entries.length(), self.pageConfig);
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
                    int[] keys = check self.persistClient->/tickets.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceTicketEntity, keyValues);
                    stream<Ticket, persist:Error?> stored = self.persistClient->/tickets(Ticket,
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
            headers: {"Location": dsHttp:toLocation("/api/tickets", [created.id])},
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
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        OrderItem[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check self.persistClient->/orderitems.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceOrderItemEntity, keyValues);
                    stream<OrderItem, persist:Error?> stored = self.persistClient->/orderitems(OrderItem,
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
            headers: {"Location": dsHttp:toLocation("/api/orderItems", [created.id])},
            body: created
        };
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
