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
# + customerId - The key of the related 'Customer'.
# + warehouseId - The key of the related 'Warehouse'.
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
# + customerId - The key of the related 'Customer'.
# + warehouseId - The key of the related 'Warehouse'.
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
# + warehouseId - The key of the related 'Warehouse'.
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

# The conditions that orders match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + number - The conditions on the `number` field
# + status - The conditions on the `status` field
# + placedOn - The conditions on the `placedOn` field
# + customerId - The conditions on the `customerId` field
# + warehouseId - The conditions on the `warehouseId` field
public type OrderFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} number?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} status?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} placedOn?;
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} customerId?;
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} warehouseId?;
|};

# An order with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Order`
# + number - The `number` field of `Order`
# + status - The `status` field of `Order`
# + placedOn - The `placedOn` field of `Order`, as an ISO 8601 date
# + customerId - The key of the related 'Customer'.
# + warehouseId - The key of the related 'Warehouse'.
# + customer - The related customer, when `fields` names it
public type OrderPartial record {|
    int id;
    string number?;
    string status?;
    string placedOn?;
    int customerId?;
    int warehouseId?;
    CustomerRelatedPartial? customer?;
|};

# A list of orders with the fields named in the `fields` query parameter.
#
# + items - The orders
# + hasMore - Whether records exist after this page
public type OrderPartialList record {|
    OrderPartial[] items;
    boolean hasMore;
|};

# An order included in a related record by the `fields` query parameter.
#
# + id - The `id` field of `Order`
# + number - The `number` field of `Order`
# + status - The `status` field of `Order`
# + placedOn - The `placedOn` field of `Order`, as an ISO 8601 date
# + customerId - The key of the related 'Customer'.
# + warehouseId - The key of the related 'Warehouse'.
public type OrderRelatedPartial record {|
    int id;
    string number?;
    string status?;
    string placedOn?;
    int customerId?;
    int warehouseId?;
|};

# The orders included in a related record by the `fields` query parameter.
#
# + items - The orders, at most `dataservicePageSize`
# + hasMore - Whether more orders exist
public type OrderRelatedPartialList record {|
    OrderRelatedPartial[] items;
    boolean hasMore;
|};

# The description of `Order` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceOrderEntity = {
    name: "Order",
    'resource: "orders",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "number", column: "number", 'type: dataservice:STRING},
        {name: "status", column: "status", 'type: dataservice:STRING},
        {name: "placedOn", column: "placedOn", 'type: dataservice:DATE},
        {name: "customerId", column: "customerId", 'type: dataservice:INT},
        {name: "warehouseId", column: "warehouseId", 'type: dataservice:INT}
    ],
    relations: [
        {
            'field: "customer",
            many: false,
            fields: ["customerId"],
            targetFields: ["id"],
            target: {
                name: "Customer",
                'resource: "customers",
                datastore: dataservice:H2,
                fields: [
                    {name: "id", column: "id", 'type: dataservice:INT, key: true},
                    {name: "name", column: "name", 'type: dataservice:STRING}
                ]
            }
        }
    ]
};

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

# The conditions that customers match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type CustomerFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A customer with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Customer`
# + name - The `name` field of `Customer`
public type CustomerPartial record {|
    int id;
    string name?;
|};

# A list of customers with the fields named in the `fields` query parameter.
#
# + items - The customers
# + hasMore - Whether records exist after this page
public type CustomerPartialList record {|
    CustomerPartial[] items;
    boolean hasMore;
|};

# A customer included in a related record by the `fields` query parameter.
#
# + id - The `id` field of `Customer`
# + name - The `name` field of `Customer`
public type CustomerRelatedPartial record {|
    int id;
    string name?;
|};

# The description of `Customer` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCustomerEntity = {
    name: "Customer",
    'resource: "customers",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# A list of warehouses.
#
# + items - The warehouses
# + hasMore - Whether records exist after this page
public type WarehouseList record {|
    Warehouse[] items;
    boolean hasMore;
|};

# The conditions that warehouses match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + city - The conditions on the `city` field
public type WarehouseFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} city?;
|};

# A warehouse with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Warehouse`
# + city - The `city` field of `Warehouse`
# + orders - The related orders, when `fields` names them
public type WarehousePartial record {|
    int id;
    string city?;
    OrderRelatedPartialList orders?;
|};

# A list of warehouses with the fields named in the `fields` query parameter.
#
# + items - The warehouses
# + hasMore - Whether records exist after this page
public type WarehousePartialList record {|
    WarehousePartial[] items;
    boolean hasMore;
|};

# The description of `Warehouse` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceWarehouseEntity = {
    name: "Warehouse",
    'resource: "warehouses",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "city", column: "city", 'type: dataservice:STRING}
    ],
    relations: [
        {
            'field: "orders",
            many: true,
            fields: ["id"],
            targetFields: ["warehouseId"],
            target: {
                name: "Order",
                'resource: "orders",
                datastore: dataservice:H2,
                fields: [
                    {name: "id", column: "id", 'type: dataservice:INT, key: true},
                    {name: "number", column: "number", 'type: dataservice:STRING},
                    {name: "status", column: "status", 'type: dataservice:STRING},
                    {name: "placedOn", column: "placedOn", 'type: dataservice:DATE},
                    {name: "customerId", column: "customerId", 'type: dataservice:INT},
                    {name: "warehouseId", column: "warehouseId", 'type: dataservice:INT}
                ]
            }
        }
    ]
};
