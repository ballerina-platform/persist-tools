// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/time;

# The JSON form of `Order`.
public type OrderPayload record {|
    # The `id` field of `Order`
    readonly int id;
    # The `number` field of `Order`
    string number;
    # The `status` field of `Order`
    string status;
    # The `placedOn` field of `Order`, as an ISO 8601 date
    string placedOn;
    # The key of the related 'Customer'.
    int customerId;
    # The key of the related 'Warehouse'.
    int warehouseId;
|};

# The JSON form of `OrderInsert`.
public type OrderInsertPayload record {|
    # The `id` field of `OrderInsert`
    readonly int id;
    # The `number` field of `OrderInsert`
    string number;
    # The `status` field of `OrderInsert`
    string status?;
    # The `placedOn` field of `OrderInsert`, as an ISO 8601 date
    string placedOn;
    # The key of the related 'Customer'.
    int customerId;
    # The key of the related 'Warehouse'.
    int warehouseId?;
|};

# The JSON form of `OrderUpdate`.
public type OrderUpdatePayload record {|
    # The `status` field of `OrderUpdate`
    string status?;
    # The `placedOn` field of `OrderUpdate`, as an ISO 8601 date
    string placedOn?;
    # The key of the related 'Warehouse'.
    int warehouseId?;
|};

# A list of orders.
public type OrderList record {
    # The orders
    OrderPayload[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that orders match, as the `filter` query parameter holds them.
public type OrderFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `number` field
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
    # The conditions on the `status` field
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
    # The conditions on the `placedOn` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} placedOn?;
    # The conditions on the `customerId` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} customerId?;
    # The conditions on the `warehouseId` field
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
public type OrderPartial record {|
    # The `id` field of `Order`
    int id;
    # The `number` field of `Order`
    string number?;
    # The `status` field of `Order`
    string status?;
    # The `placedOn` field of `Order`, as an ISO 8601 date
    string placedOn?;
    # The key of the related 'Customer'.
    int customerId?;
    # The key of the related 'Warehouse'.
    int warehouseId?;
    # The related customer, when `fields` names it
    CustomerRelatedPartial? customer?;
|};

# A list of orders with the fields named in the `fields` query parameter.
public type OrderPartialList record {
    # The orders
    OrderPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# An order read through a relation, with the fields named in the `fields` query parameter.
public type OrderRelatedPartial record {|
    # The `id` field of `Order`
    int id;
    # The `number` field of `Order`
    string number?;
    # The `status` field of `Order`
    string status?;
    # The `placedOn` field of `Order`, as an ISO 8601 date
    string placedOn?;
    # The key of the related 'Customer'.
    int customerId?;
    # The key of the related 'Warehouse'.
    int warehouseId?;
|};

# The orders read through a relation, with the fields named in the `fields` query parameter.
public type OrderRelatedPartialList record {
    # The orders, at most `dataserviceConfig.pageSize`
    OrderRelatedPartial[] items;
    # Whether more orders exist
    boolean hasMore;
};

# The fields of `Order` that callers can see.
final readonly & dataservice:Field[] dataserviceOrderFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "number", column: "number", 'type: dataservice:STRING},
    {name: "status", column: "status", 'type: dataservice:STRING},
    {name: "placedOn", column: "placedOn", 'type: dataservice:DATE},
    {name: "customerId", column: "customerId", 'type: dataservice:INT},
    {name: "warehouseId", column: "warehouseId", 'type: dataservice:INT}
];

# The description of `Order` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceOrderEntity = {
    name: "Order",
    collection: "orders",
    'resource: "orders",
    datastore: dataservice:H2,
    fields: dataserviceOrderFields,
    relations: [
        {
            'field: "customer",
            many: false,
            fields: ["customerId"],
            targetFields: ["id"],
            target: {
                name: "Customer",
                collection: "customers",
                'resource: "customers",
                datastore: dataservice:H2,
                fields: dataserviceCustomerFields
            }
        }
    ]
};

# The response to creating one order.
public type OrderCreated record {|
    *http:Created;
    # The created order
    OrderPayload body;
|};

# The response to creating several orders.
public type OrderListCreated record {|
    *http:Created;
    # The created orders
    OrderList body;
|};

# Converts an `Order` to its JSON form.
#
# + value - The Order to convert
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
# + payload - The JSON form to convert
# + return - The persist type, or `InvalidInput` when a value cannot be converted
isolated function transformPayloadToOrderInsert(OrderInsertPayload payload)
        returns OrderInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError placedOn = dataservice:dateFromString(payload.placedOn);
    if placedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(placedOn, "Order", "placedOn");
    }
    return {id: payload.id, number: payload.number, status: "pending", internalNotes: (), tenantId: 1, placedOn, customerId: payload.customerId, warehouseId: 7};
}

# Converts the JSON form of an `OrderUpdate` to the persist type.
#
# + payload - The JSON form to convert
# + return - The persist type, or `InvalidInput` when a value cannot be converted
isolated function transformPayloadToOrderUpdate(OrderUpdatePayload payload)
        returns OrderUpdate|dsHttp:InvalidInput {
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
public type CustomerList record {
    # The customers
    Customer[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that customers match, as the `filter` query parameter holds them.
public type CustomerFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
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
public type CustomerPartial record {|
    # The `id` field of `Customer`
    int id;
    # The `name` field of `Customer`
    string name?;
|};

# A list of customers with the fields named in the `fields` query parameter.
public type CustomerPartialList record {
    # The customers
    CustomerPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# A customer read through a relation, with the fields named in the `fields` query parameter.
public type CustomerRelatedPartial record {|
    # The `id` field of `Customer`
    int id;
    # The `name` field of `Customer`
    string name?;
|};

# The fields of `Customer` that callers can see.
final readonly & dataservice:Field[] dataserviceCustomerFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `Customer` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCustomerEntity = {
    name: "Customer",
    collection: "customers",
    'resource: "customers",
    datastore: dataservice:H2,
    fields: dataserviceCustomerFields
};

# A list of warehouses.
public type WarehouseList record {
    # The warehouses
    Warehouse[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that warehouses match, as the `filter` query parameter holds them.
public type WarehouseFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `city` field
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
public type WarehousePartial record {|
    # The `id` field of `Warehouse`
    int id;
    # The `city` field of `Warehouse`
    string city?;
    # The related orders, when `fields` names them
    OrderRelatedPartialList orders?;
|};

# A list of warehouses with the fields named in the `fields` query parameter.
public type WarehousePartialList record {
    # The warehouses
    WarehousePartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `Warehouse` that callers can see.
final readonly & dataservice:Field[] dataserviceWarehouseFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "city", column: "city", 'type: dataservice:STRING}
];

# The description of `Warehouse` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceWarehouseEntity = {
    name: "Warehouse",
    collection: "warehouses",
    'resource: "warehouses",
    datastore: dataservice:H2,
    fields: dataserviceWarehouseFields,
    relations: [
        {
            'field: "orders",
            many: true,
            fields: ["id"],
            targetFields: ["warehouseId"],
            target: {
                name: "Order",
                collection: "orders",
                'resource: "orders",
                datastore: dataservice:H2,
                fields: dataserviceOrderFields
            }
        }
    ]
};
