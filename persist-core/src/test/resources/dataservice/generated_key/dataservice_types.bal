// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/time;

# The JSON form of `Ticket`.
#
# + id - The `id` field of `Ticket`
# + subject - The `subject` field of `Ticket`
# + openedOn - The `openedOn` field of `Ticket`, as an ISO 8601 date
public type TicketPayload record {|
    readonly int id;
    string subject;
    string openedOn;
|};

# The JSON form of `TicketInsert`.
#
# + subject - The `subject` field of `TicketInsert`
# + openedOn - The `openedOn` field of `TicketInsert`, as an ISO 8601 date
public type TicketInsertPayload record {|
    string subject;
    string openedOn;
|};

# A list of tickets.
#
# + items - The tickets
# + hasMore - Whether records exist after this page
public type TicketList record {|
    TicketPayload[] items;
    boolean hasMore;
|};

# The conditions that tickets match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + subject - The conditions on the `subject` field
# + openedOn - The conditions on the `openedOn` field
public type TicketFilter record {|
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
    |} subject?;
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
#
# + id - The `id` field of `Ticket`
# + subject - The `subject` field of `Ticket`
# + openedOn - The `openedOn` field of `Ticket`, as an ISO 8601 date
public type TicketPartial record {|
    int id;
    string subject?;
    string openedOn?;
|};

# A list of tickets with the fields named in the `fields` query parameter.
#
# + items - The tickets
# + hasMore - Whether records exist after this page
public type TicketPartialList record {|
    TicketPartial[] items;
    boolean hasMore;
|};

# The description of `Ticket` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceTicketEntity = {
    name: "Ticket",
    'resource: "tickets",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true, generated: true},
        {name: "subject", column: "subject", 'type: dataservice:STRING},
        {name: "openedOn", column: "openedOn", 'type: dataservice:DATE}
    ]
};

# The response to creating one ticket.
#
# + body - The created ticket
public type TicketCreated record {|
    *http:Created;
    TicketPayload body;
|};

# The response to creating several tickets.
#
# + body - The created tickets
public type TicketListCreated record {|
    *http:Created;
    TicketList body;
|};

# Converts a `Ticket` to its JSON form.
#
# + value - The `Ticket` record
# + return - The JSON form
isolated function transformTicketToPayload(Ticket value) returns TicketPayload => {
    id: value.id,
    subject: value.subject,
    openedOn: dataservice:dateToString(value.openedOn)
};

# Converts the JSON form of a `TicketInsert` to the persist type.
#
# + payload - The JSON form
# + return - The persist value, or the response for a value that is not in its format
isolated function transformPayloadToTicketInsert(TicketInsertPayload payload) returns TicketInsert|dsHttp:InvalidInput {
    time:Date|dataservice:ConversionError openedOn = dataservice:dateFromString(payload.openedOn);
    if openedOn is dataservice:ConversionError {
        return dsHttp:toInvalidInput(openedOn, "Ticket", "openedOn");
    }
    return {subject: payload.subject, openedOn};
}

# A list of order items.
#
# + items - The order items
# + hasMore - Whether records exist after this page
public type OrderItemList record {|
    OrderItem[] items;
    boolean hasMore;
|};

# The description of `OrderItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceOrderItemEntity = {
    name: "OrderItem",
    'resource: "orderitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true, generated: true},
        {name: "sku", column: "sku", 'type: dataservice:STRING}
    ]
};

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
