// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/persist;
import ballerina/time;

# A purchase.
public isolated service class PurchaseObject {
    private final readonly & Purchase row;

    isolated function init(Purchase row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Purchase`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `number` field of `Purchase`
    # + return - The value
    isolated resource function get number() returns string => self.row.number;

    # The `status` field of `Purchase`
    # + return - The value
    isolated resource function get status() returns string => self.row.status;

    # The `placedOn` field of `Purchase`, as an ISO 8601 date
    # + return - The value
    isolated resource function get placedOn() returns string => dataservice:dateToString(self.row.placedOn);

    # The `shippedAt` field of `Purchase`, as an ISO 8601 date-time without an offset
    # + return - The value
    isolated resource function get shippedAt() returns string? {
        time:Civil? value = self.row.shippedAt;
        return value is () ? () : dataservice:civilToString(value);
    }

    # The `note` field of `Purchase`
    # + return - The value
    isolated resource function get note() returns string? => self.row.note;

    # The `total` field of `Purchase`
    # + return - The value
    isolated resource function get total() returns decimal => self.row.total;

    # The key of the related 'Customer'.
    # + return - The value
    isolated resource function get customerId() returns int => self.row.customerId;

    isolated function prefetchCustomer(graphql:Context ctx) {
        ctx.getDataLoader("Purchase.customer").add({"customerId": self.row.customerId});
    }

    # Gets the customer of a purchase.
    #
    # + ctx - The request context, which holds the dataloader of the relation
    # + return - The customer, `()` when there is none, or the error
    @graphql:ResourceConfig {prefetchMethodName: "prefetchCustomer"}
    isolated resource function get customer(graphql:Context ctx) returns CustomerRelatedObject?|error {
        Customer? row = check ctx.getDataLoader("Purchase.customer").get({"customerId": self.row.customerId});
        return row is Customer ? new (row) : ();
    }
}

# A page of purchases.
#
# + items - The purchases
# + hasMore - Whether records exist after this page
public type PurchaseList record {
    PurchaseObject[] items;
    boolean hasMore;
};

# A purchase, as a related record: its own fields and no relation fields.
public isolated service class PurchaseRelatedObject {
    private final readonly & Purchase row;

    isolated function init(Purchase row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Purchase`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `number` field of `Purchase`
    # + return - The value
    isolated resource function get number() returns string => self.row.number;

    # The `status` field of `Purchase`
    # + return - The value
    isolated resource function get status() returns string => self.row.status;

    # The `placedOn` field of `Purchase`, as an ISO 8601 date
    # + return - The value
    isolated resource function get placedOn() returns string => dataservice:dateToString(self.row.placedOn);

    # The `shippedAt` field of `Purchase`, as an ISO 8601 date-time without an offset
    # + return - The value
    isolated resource function get shippedAt() returns string? {
        time:Civil? value = self.row.shippedAt;
        return value is () ? () : dataservice:civilToString(value);
    }

    # The `note` field of `Purchase`
    # + return - The value
    isolated resource function get note() returns string? => self.row.note;

    # The `total` field of `Purchase`
    # + return - The value
    isolated resource function get total() returns decimal => self.row.total;

    # The key of the related 'Customer'.
    # + return - The value
    isolated resource function get customerId() returns int => self.row.customerId;
}

# A page of related purchases.
#
# + items - The purchases
# + hasMore - Whether records exist after this page; read them with `offset`
public type PurchaseRelatedList record {
    PurchaseRelatedObject[] items;
    boolean hasMore;
};

# The conditions that purchases match; every condition set must hold.
public type PurchaseFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `number`
    dsGraphql:StringFilter? number;
    # The conditions on `status`
    dsGraphql:StringFilter? status;
    # The conditions on `placedOn`
    dsGraphql:DateFilter? placedOn;
    # The conditions on `shippedAt`
    dsGraphql:CivilNullableFilter? shippedAt;
    # The conditions on `note`
    dsGraphql:StringNullableFilter? note;
    # The conditions on `total`
    dsGraphql:DecimalFilter? total;
    # The conditions on `customerId`
    dsGraphql:IntFilter? customerId;
|};

# One entry of the order of purchases; set exactly one field.
public type PurchaseSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `number`
    dsGraphql:SortDirection? number;
    # Sorts by `status`
    dsGraphql:SortDirection? status;
    # Sorts by `placedOn`
    dsGraphql:SortDirection? placedOn;
    # Sorts by `shippedAt`
    dsGraphql:SortDirection? shippedAt;
    # Sorts by `note`
    dsGraphql:SortDirection? note;
    # Sorts by `total`
    dsGraphql:SortDirection? total;
    # Sorts by `customerId`
    dsGraphql:SortDirection? customerId;
|};

# The fields to create a purchase with.
public type PurchaseInsertInput record {|
    # The `id` field of `Purchase`
    int id;
    # The `number` field of `Purchase`
    string number;
    # The `placedOn` field of `Purchase`, as an ISO 8601 date
    string placedOn;
    # The `shippedAt` field of `Purchase`, as an ISO 8601 date-time without an offset
    string? shippedAt;
    # The `note` field of `Purchase`
    string? note;
    # The `total` field of `Purchase`
    decimal total;
    # The key of the related 'Customer'.
    int customerId;
|};

# The fields to change in a purchase; a field left out or set to `null` is not changed.
public type PurchaseUpdateInput record {|
    # The `placedOn` field of `Purchase`, as an ISO 8601 date
    string? placedOn;
    # The `shippedAt` field of `Purchase`, as an ISO 8601 date-time without an offset
    string? shippedAt;
    # The `note` field of `Purchase`
    string? note;
    # The `total` field of `Purchase`
    decimal? total;
    # The fields to set to `null`
    PurchaseUnset? unset;
|};

# The nullable fields to set to `null` in a purchase.
public type PurchaseUnset record {|
    # `true` to set `shippedAt` to `null`
    boolean? shippedAt;
    # `true` to set `note` to `null`
    boolean? note;
|};

# The fields of `Purchase` that callers can see.
final readonly & dataservice:Field[] dataservicePurchaseFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "number", column: "number", 'type: dataservice:STRING},
    {name: "status", column: "status", 'type: dataservice:STRING},
    {name: "placedOn", column: "placedOn", 'type: dataservice:DATE},
    {name: "shippedAt", column: "shippedAt", 'type: dataservice:CIVIL, nullable: true},
    {name: "note", column: "note", 'type: dataservice:STRING, nullable: true},
    {name: "total", column: "total", 'type: dataservice:DECIMAL},
    {name: "customerId", column: "customerId", 'type: dataservice:INT}
];

# The description of `Purchase` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataservicePurchaseEntity = {
    name: "Purchase",
    collection: "purchases",
    'resource: "purchases",
    datastore: dataservice:H2,
    fields: dataservicePurchaseFields,
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

isolated function transformInputToPurchaseInsert(PurchaseInsertInput value) returns PurchaseInsert|dsGraphql:Error {
    time:Date|dataservice:ConversionError placedOn = dataservice:dateFromString(value.placedOn);
    if placedOn is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(placedOn, "Purchase", "placedOn");
    }
    time:Civil?|dataservice:ConversionError shippedAt = ();
    string? shippedAtText = value.shippedAt;
    if shippedAtText !is () {
        shippedAt = dataservice:civilFromString(shippedAtText);
    }
    if shippedAt is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(shippedAt, "Purchase", "shippedAt");
    }
    return {id: value.id, number: value.number, status: "pending", internalNotes: (), tenantId: 1, placedOn, shippedAt, note: value.note, total: value.total, customerId: value.customerId};
}

isolated function transformInputToPurchaseUpdate(PurchaseUpdateInput value) returns PurchaseUpdate|dsGraphql:Error {
    PurchaseUpdate update = {};
    string? placedOnText = value.placedOn;
    if placedOnText !is () {
        time:Date|dataservice:ConversionError placedOn = dataservice:dateFromString(placedOnText);
        if placedOn is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(placedOn, "Purchase", "placedOn");
        }
        update.placedOn = placedOn;
    }
    string? shippedAtText = value.shippedAt;
    if shippedAtText !is () {
        time:Civil|dataservice:ConversionError shippedAt = dataservice:civilFromString(shippedAtText);
        if shippedAt is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(shippedAt, "Purchase", "shippedAt");
        }
        update.shippedAt = shippedAt;
    }
    string? note = value.note;
    if note !is () {
        update.note = note;
    }
    decimal? total = value.total;
    if total !is () {
        update.total = total;
    }
    PurchaseUnset? unset = value.unset;
    if unset !is () {
        if unset.shippedAt == true {
            if update.hasKey("shippedAt") {
                return dsGraphql:toSetAndUnsetError("Purchase", "shippedAt");
            }
            update.shippedAt = ();
        }
        if unset.note == true {
            if update.hasKey("note") {
                return dsGraphql:toSetAndUnsetError("Purchase", "note");
            }
            update.note = ();
        }
    }
    return update;
}

# A customer.
public isolated service class CustomerObject {
    private final readonly & Customer row;

    isolated function init(Customer row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Customer`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `name` field of `Customer`
    # + return - The value
    isolated resource function get name() returns string => self.row.name;

    # Gets the purchases of a customer.
    #
    # + filter - The conditions that the purchases match
    # + sort - The order of the purchases; each entry names one field
    # + size - The number of purchases in the page
    # + offset - The number of purchases to skip
    # + return - A page of the purchases, or the error
    @graphql:ResourceConfig {complexity: 20}
    isolated resource function get purchases(PurchaseFilter? filter = (), PurchaseSort[]? sort = (),
            int? size = (), int? offset = ()) returns PurchaseRelatedList?|error {
        dataservice:Query query = check dsGraphql:toRelatedQuery(dataserviceCustomerEntity, "purchases", self.row,
                dataservicePageConfig, filter, sort, size, offset);
        stream<Purchase, persist:Error?> rows = dataserviceClient->/purchases(Purchase,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Purchase[]|persist:Error items = from Purchase item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Customer", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Purchase item in items
                select new PurchaseRelatedObject(item), hasMore};
    }
}

# A page of customers.
#
# + items - The customers
# + hasMore - Whether records exist after this page
public type CustomerList record {
    CustomerObject[] items;
    boolean hasMore;
};

# A customer, as a related record: its own fields and no relation fields.
public isolated service class CustomerRelatedObject {
    private final readonly & Customer row;

    isolated function init(Customer row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Customer`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `name` field of `Customer`
    # + return - The value
    isolated resource function get name() returns string => self.row.name;
}

# The conditions that customers match; every condition set must hold.
public type CustomerFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `name`
    dsGraphql:StringFilter? name;
|};

# One entry of the order of customers; set exactly one field.
public type CustomerSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `name`
    dsGraphql:SortDirection? name;
|};

# The fields to create a customer with.
public type CustomerInsertInput record {|
    # The `name` field of `Customer`
    string name;
|};

# The fields of `Customer` that callers can see.
final readonly & dataservice:Field[] dataserviceCustomerFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true, generated: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `Customer` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCustomerEntity = {
    name: "Customer",
    collection: "customers",
    'resource: "customers",
    datastore: dataservice:H2,
    fields: dataserviceCustomerFields,
    relations: [
        {
            'field: "purchases",
            many: true,
            fields: ["id"],
            targetFields: ["customerId"],
            target: {
                name: "Purchase",
                collection: "purchases",
                'resource: "purchases",
                datastore: dataservice:H2,
                fields: dataservicePurchaseFields
            }
        }
    ]
};

isolated function transformInputToCustomerInsert(CustomerInsertInput value) returns CustomerInsert {
    return {name: value.name};
}

# A log entry that callers may only write.
public isolated service class AuditEntryObject {
    private final readonly & AuditEntry row;

    isolated function init(AuditEntry row) {
        self.row = row.cloneReadOnly();
    }

    # The `region` field of `AuditEntry`
    # + return - The value
    isolated resource function get region() returns string => self.row.region;

    # The `seq` field of `AuditEntry`
    # + return - The value
    isolated resource function get seq() returns int => self.row.seq;

    # The `message` field of `AuditEntry`
    # + return - The value
    isolated resource function get message() returns string => self.row.message;
}

# A page of audit entries.
#
# + items - The audit entries
# + hasMore - Whether records exist after this page
public type AuditEntryList record {
    AuditEntryObject[] items;
    boolean hasMore;
};

# The fields to create an audit entry with.
public type AuditEntryInsertInput record {|
    # The `region` field of `AuditEntry`
    string region;
    # The `seq` field of `AuditEntry`
    int seq;
    # The `message` field of `AuditEntry`
    string message;
|};

# The fields to change in an audit entry; a field left out or set to `null` is not changed.
public type AuditEntryUpdateInput record {|
    # The `message` field of `AuditEntry`
    string? message;
|};

# The fields of `AuditEntry` that callers can see.
final readonly & dataservice:Field[] dataserviceAuditEntryFields = [
    {name: "region", column: "region", 'type: dataservice:STRING, key: true},
    {name: "seq", column: "seq", 'type: dataservice:INT, key: true},
    {name: "message", column: "message", 'type: dataservice:STRING}
];

# The description of `AuditEntry` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceAuditEntryEntity = {
    name: "AuditEntry",
    collection: "auditEntries",
    'resource: "auditentries",
    datastore: dataservice:H2,
    fields: dataserviceAuditEntryFields
};

isolated function transformInputToAuditEntryInsert(AuditEntryInsertInput value) returns AuditEntryInsert {
    return {region: value.region, seq: value.seq, message: value.message};
}

isolated function transformInputToAuditEntryUpdate(AuditEntryUpdateInput value) returns AuditEntryUpdate|dsGraphql:Error {
    AuditEntryUpdate update = {};
    string? message = value.message;
    if message !is () {
        update.message = message;
    }
    return update;
}
