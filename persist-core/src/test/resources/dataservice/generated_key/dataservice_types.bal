// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/time;

# The JSON form of `Ticket`.
public type TicketPayload record {|
    # The `id` field of `Ticket`
    readonly int id;
    # The `subject` field of `Ticket`
    string subject;
    # The `openedOn` field of `Ticket`, as an ISO 8601 date
    string openedOn;
|};

# The JSON form of `TicketInsert`.
public type TicketInsertPayload record {|
    # The `subject` field of `TicketInsert`
    string subject;
    # The `openedOn` field of `TicketInsert`, as an ISO 8601 date
    string openedOn;
|};

# A list of tickets.
public type TicketList record {
    # The tickets
    TicketPayload[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that tickets match, as the `filter` query parameter holds them.
public type TicketFilter record {|
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
    # The conditions on the `subject` field
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
    |} subject?;
    # The conditions on the `openedOn` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
    |} openedOn?;
|};

# A ticket with the fields named in the `fields` query parameter.
public type TicketPartial record {|
    # The `id` field of `Ticket`
    int id;
    # The `subject` field of `Ticket`
    string subject?;
    # The `openedOn` field of `Ticket`, as an ISO 8601 date
    string openedOn?;
|};

# A list of tickets with the fields named in the `fields` query parameter.
public type TicketPartialList record {
    # The tickets
    TicketPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `Ticket` that callers can see.
final readonly & dataservice:Field[] dataserviceTicketFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true, generated: true},
    {name: "subject", column: "subject", 'type: dataservice:STRING},
    {name: "openedOn", column: "openedOn", 'type: dataservice:DATE}
];

# The description of `Ticket` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceTicketEntity = {
    name: "Ticket",
    collection: "tickets",
    'resource: "tickets",
    datastore: dataservice:H2,
    fields: dataserviceTicketFields
};

# The response to creating one ticket.
public type TicketCreated record {|
    *http:Created;
    # The created ticket
    TicketPayload body;
|};

# The response to creating several tickets.
public type TicketListCreated record {|
    *http:Created;
    # The created tickets
    TicketList body;
|};

# Converts a `Ticket` to its JSON form.
#
# + value - The Ticket to convert
# + return - The JSON form
isolated function transformTicketToPayload(Ticket value) returns TicketPayload => {
    id: value.id,
    subject: value.subject,
    openedOn: dataservice:dateToString(value.openedOn)
};

# Converts the JSON form of a `TicketInsert` to the persist type.
#
# + payload - The JSON form to convert
# + return - The persist type, or `InvalidInput` when a value cannot be converted
isolated function transformPayloadToTicketInsert(TicketInsertPayload payload)
        returns TicketInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError openedOn = dataservice:dateFromString(payload.openedOn);
    if openedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(openedOn, "Ticket", "openedOn");
    }
    return {subject: payload.subject, openedOn};
}

# A list of order items.
public type OrderItemList record {
    # The order items
    OrderItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `OrderItem` that callers can see.
final readonly & dataservice:Field[] dataserviceOrderItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true, generated: true},
    {name: "sku", column: "sku", 'type: dataservice:STRING}
];

# The description of `OrderItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceOrderItemEntity = {
    name: "OrderItem",
    collection: "orderItems",
    'resource: "orderitems",
    datastore: dataservice:H2,
    fields: dataserviceOrderItemFields
};

# The response to creating one order item.
public type OrderItemCreated record {|
    *http:Created;
    # The created order item
    OrderItem body;
|};

# The response to creating several order items.
public type OrderItemListCreated record {|
    *http:Created;
    # The created order items
    OrderItemList body;
|};
