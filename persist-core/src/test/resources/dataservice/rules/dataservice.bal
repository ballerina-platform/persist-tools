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

    # Gets a page of orders.
    #
    # + return - A page of orders, or the error response
    resource function get orders() returns OrderList|dsHttp:InvalidInput|dsHttp:InternalError {
        OrderPayload[]|persist:Error items = from Order item in dataserviceClient->/orders(Order)
            select transformOrderToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets an order by its key.
    #
    # + id - The id of the order
    # + return - The order, or the error response
    resource function get orders/[int id]() returns OrderPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Order|persist:Error item = dataserviceClient->/orders/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Order", [id]);
        }
        return transformOrderToPayload(item);
    }

    # Creates one or more orders.
    #
    # + payload - The order, or the orders, to create
    # + return - The created order or orders, or the error response
    resource function post orders(OrderInsertPayload|OrderInsertPayload[] payload)
            returns OrderCreated|OrderListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        OrderInsertPayload[] entries = payload is OrderInsertPayload[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(entries.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        OrderInsert[] values = [];
        foreach OrderInsertPayload entry in entries {
            OrderInsert|dsHttp:InvalidInput value = transformPayloadToOrderInsert(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        OrderPayload[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/orders.post(values);
                    foreach int id in keys {
                        Order item = check dataserviceClient->/orders/[id];
                        items.push(transformOrderToPayload(item));
                    }
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Order");
            }
        }
        if payload is OrderInsertPayload[] {
            return <OrderListCreated>{body: {items, hasMore: false}};
        }
        OrderPayload created = items[0];
        return <OrderCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/orders", [created.id])},
            body: created
        };
    }

    # Updates an order by its key.
    #
    # + id - The id of the order
    # + payload - The fields of the order to update
    # + return - The updated order, or the error response
    resource function patch orders/[int id](OrderUpdatePayload payload)
            returns OrderPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        OrderUpdate|dsHttp:InvalidInput value = transformPayloadToOrderUpdate(payload);
        if value is dsHttp:InvalidInput {
            return value;
        }
        Order|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/orders/[id];
        } else {
            item = dataserviceClient->/orders/[id].put(value);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Order", [id]);
        }
        return transformOrderToPayload(item);
    }

    # Deletes an order by its key.
    #
    # + id - The id of the order
    # + return - The deleted order, or the error response
    resource function delete orders/[int id]() returns OrderPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Order|persist:Error item = dataserviceClient->/orders/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Order", [id]);
        }
        return transformOrderToPayload(item);
    }

    # Gets a page of customers.
    #
    # + return - A page of customers, or the error response
    resource function get customers() returns CustomerList|dsHttp:InvalidInput|dsHttp:InternalError {
        Customer[]|persist:Error items = from Customer item in dataserviceClient->/customers(Customer)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a customer by its key.
    #
    # + id - The id of the customer
    # + return - The customer, or the error response
    resource function get customers/[int id]() returns Customer|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Customer|persist:Error item = dataserviceClient->/customers/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Customer", [id]);
        }
        return item;
    }

    # Gets a page of warehouses.
    #
    # + return - A page of warehouses, or the error response
    resource function get warehouses() returns WarehouseList|dsHttp:InvalidInput|dsHttp:InternalError {
        Warehouse[]|persist:Error items = from Warehouse item in dataserviceClient->/warehouses(Warehouse)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a warehouse by its key.
    #
    # + id - The id of the warehouse
    # + return - The warehouse, or the error response
    resource function get warehouses/[int id]() returns Warehouse|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Warehouse|persist:Error item = dataserviceClient->/warehouses/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Warehouse", [id]);
        }
        return item;
    }
}
