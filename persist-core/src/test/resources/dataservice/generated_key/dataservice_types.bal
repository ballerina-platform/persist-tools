// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/http;

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
public type TicketList record {|
    TicketPayload[] items;
|};

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

# A list of order items.
#
# + items - The order items
public type OrderItemList record {|
    OrderItem[] items;
|};

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
