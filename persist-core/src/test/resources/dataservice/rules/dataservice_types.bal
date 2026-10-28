// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/time;

# The JSON form of `Order`.
#
# + id - The `id` field of `Order`
# + number - The `number` field of `Order`
# + status - The `status` field of `Order`
# + placedOn - The `placedOn` field of `Order`, as an ISO 8601 date
# + customerId - The `customerId` field of `Order`
# + warehouseId - The `warehouseId` field of `Order`
public type OrderPayload record {|
    readonly int id;
    string number;
    string status;
    string placedOn;
    int customerId;
    int warehouseId;
|};

# The JSON form of `OrderInsert`.
#
# + id - The `id` field of `OrderInsert`
# + number - The `number` field of `OrderInsert`
# + status - The `status` field of `OrderInsert`
# + placedOn - The `placedOn` field of `OrderInsert`, as an ISO 8601 date
# + customerId - The `customerId` field of `OrderInsert`
# + warehouseId - The `warehouseId` field of `OrderInsert`
public type OrderInsertPayload record {|
    readonly int id;
    string number;
    string status?;
    string placedOn;
    int customerId;
    int warehouseId?;
|};

# The JSON form of `OrderUpdate`.
#
# + status - The `status` field of `OrderUpdate`
# + placedOn - The `placedOn` field of `OrderUpdate`, as an ISO 8601 date
# + warehouseId - The `warehouseId` field of `OrderUpdate`
public type OrderUpdatePayload record {|
    string status?;
    string placedOn?;
    int warehouseId?;
|};

# A list of orders.
#
# + items - The orders
# + hasMore - Whether records exist after this page
public type OrderList record {|
    OrderPayload[] items;
    boolean hasMore;
|};

# The response to creating one order.
#
# + body - The created order
public type OrderCreated record {|
    *http:Created;
    OrderPayload body;
|};

# The response to creating several orders.
#
# + body - The created orders
public type OrderListCreated record {|
    *http:Created;
    OrderList body;
|};

# Converts an `Order` to its JSON form.
#
# + value - The `Order` record
# + return - The JSON form
isolated function transformOrderToPayload(Order value) returns OrderPayload => {
    id: value.id,
    number: value.number,
    status: value.status,
    placedOn: dataservice:dateToString(value.placedOn),
    customerId: value.customerId,
    warehouseId: value.warehouseId
};

# Converts the JSON form of an `OrderInsert` to the persist type.
#
# + payload - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function transformPayloadToOrderInsert(OrderInsertPayload payload) returns OrderInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError placedOn = dataservice:dateFromString(payload.placedOn);
    if placedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(placedOn, "Order", "placedOn");
    }
    return {id: payload.id, number: payload.number, status: "pending", internalNotes: (), tenantId: 1, placedOn, customerId: payload.customerId, warehouseId: 7};
}

# Converts the JSON form of an `OrderUpdate` to the persist type.
#
# + payload - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function transformPayloadToOrderUpdate(OrderUpdatePayload payload) returns OrderUpdate|dsHttp:InvalidInput {
    OrderUpdate value = {};
    string? placedOnText = payload?.placedOn;
    if placedOnText is string {
        time:Date|dataservice:ConversionError placedOn = dataservice:dateFromString(placedOnText);
        if placedOn is dataservice:ConversionError {
            return dsHttp:toInvalidInput(placedOn, "Order", "placedOn");
        }
        value.placedOn = placedOn;
    }
    return value;
}

# A list of customers.
#
# + items - The customers
# + hasMore - Whether records exist after this page
public type CustomerList record {|
    Customer[] items;
    boolean hasMore;
|};

# A list of warehouses.
#
# + items - The warehouses
# + hasMore - Whether records exist after this page
public type WarehouseList record {|
    Warehouse[] items;
    boolean hasMore;
|};
