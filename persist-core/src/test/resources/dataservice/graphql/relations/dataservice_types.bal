// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/persist;

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

    isolated function prefetchProfile(graphql:Context ctx) {
        ctx.getDataLoader("Customer.profile").add({"id": self.row.id});
    }

    # Gets the profile of a customer.
    #
    # + ctx - The request context, which holds the dataloader of the relation
    # + return - The profile, `()` when there is none, or the error
    @graphql:ResourceConfig {prefetchMethodName: "prefetchProfile"}
    isolated resource function get profile(graphql:Context ctx) returns ProfileRelatedObject?|error {
        Profile? row = check ctx.getDataLoader("Customer.profile").get({"id": self.row.id});
        return row is Profile ? new (row) : ();
    }

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
    # The `id` field of `Customer`
    int id;
    # The `name` field of `Customer`
    string name;
|};

# The fields to change in a customer; a field left out or set to `null` is not changed.
public type CustomerUpdateInput record {|
    # The `name` field of `Customer`
    string? name;
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
    fields: dataserviceCustomerFields,
    relations: [
        {
            'field: "profile",
            many: false,
            fields: ["id"],
            targetFields: ["customerId"],
            target: {
                name: "Profile",
                collection: "profiles",
                'resource: "profiles",
                datastore: dataservice:H2,
                fields: dataserviceProfileFields
            }
        },
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
    return {id: value.id, name: value.name};
}

isolated function transformInputToCustomerUpdate(CustomerUpdateInput value) returns CustomerUpdate|dsGraphql:Error {
    CustomerUpdate update = {};
    string? name = value.name;
    if name !is () {
        update.name = name;
    }
    return update;
}

# A profile.
public isolated service class ProfileObject {
    private final readonly & Profile row;

    isolated function init(Profile row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Profile`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `bio` field of `Profile`
    # + return - The value
    isolated resource function get bio() returns string => self.row.bio;

    # The key of the related 'Customer'.
    # + return - The value
    isolated resource function get customerId() returns int => self.row.customerId;

    isolated function prefetchCustomer(graphql:Context ctx) {
        ctx.getDataLoader("Profile.customer").add({"customerId": self.row.customerId});
    }

    # Gets the customer of a profile.
    #
    # + ctx - The request context, which holds the dataloader of the relation
    # + return - The customer, `()` when there is none, or the error
    @graphql:ResourceConfig {prefetchMethodName: "prefetchCustomer"}
    isolated resource function get customer(graphql:Context ctx) returns CustomerRelatedObject?|error {
        Customer? row = check ctx.getDataLoader("Profile.customer").get({"customerId": self.row.customerId});
        return row is Customer ? new (row) : ();
    }
}

# A page of profiles.
#
# + items - The profiles
# + hasMore - Whether records exist after this page
public type ProfileList record {
    ProfileObject[] items;
    boolean hasMore;
};

# A profile, as a related record: its own fields and no relation fields.
public isolated service class ProfileRelatedObject {
    private final readonly & Profile row;

    isolated function init(Profile row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Profile`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `bio` field of `Profile`
    # + return - The value
    isolated resource function get bio() returns string => self.row.bio;

    # The key of the related 'Customer'.
    # + return - The value
    isolated resource function get customerId() returns int => self.row.customerId;
}

# The conditions that profiles match; every condition set must hold.
public type ProfileFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `bio`
    dsGraphql:StringFilter? bio;
    # The conditions on `customerId`
    dsGraphql:IntFilter? customerId;
|};

# One entry of the order of profiles; set exactly one field.
public type ProfileSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `bio`
    dsGraphql:SortDirection? bio;
    # Sorts by `customerId`
    dsGraphql:SortDirection? customerId;
|};

# The fields to create a profile with.
public type ProfileInsertInput record {|
    # The `id` field of `Profile`
    int id;
    # The `bio` field of `Profile`
    string bio;
    # The key of the related 'Customer'.
    int customerId;
|};

# The fields to change in a profile; a field left out or set to `null` is not changed.
public type ProfileUpdateInput record {|
    # The `bio` field of `Profile`
    string? bio;
    # The key of the related 'Customer'.
    int? customerId;
|};

# The fields of `Profile` that callers can see.
final readonly & dataservice:Field[] dataserviceProfileFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "bio", column: "bio", 'type: dataservice:STRING},
    {name: "customerId", column: "customerId", 'type: dataservice:INT}
];

# The description of `Profile` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceProfileEntity = {
    name: "Profile",
    collection: "profiles",
    'resource: "profiles",
    datastore: dataservice:H2,
    fields: dataserviceProfileFields,
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

isolated function transformInputToProfileInsert(ProfileInsertInput value) returns ProfileInsert {
    return {id: value.id, bio: value.bio, customerId: value.customerId};
}

isolated function transformInputToProfileUpdate(ProfileUpdateInput value) returns ProfileUpdate|dsGraphql:Error {
    ProfileUpdate update = {};
    string? bio = value.bio;
    if bio !is () {
        update.bio = bio;
    }
    int? customerId = value.customerId;
    if customerId !is () {
        update.customerId = customerId;
    }
    return update;
}

# A purchase.
public isolated service class PurchaseObject {
    private final readonly & Purchase row;

    isolated function init(Purchase row) {
        self.row = row.cloneReadOnly();
    }

    # The `region` field of `Purchase`
    # + return - The value
    isolated resource function get region() returns string => self.row.region;

    # The `number` field of `Purchase`
    # + return - The value
    isolated resource function get number() returns int => self.row.number;

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

    # Gets the lines of a purchase.
    #
    # + filter - The conditions that the lines match
    # + sort - The order of the lines; each entry names one field
    # + size - The number of lines in the page
    # + offset - The number of lines to skip
    # + return - A page of the lines, or the error
    @graphql:ResourceConfig {complexity: 20}
    isolated resource function get lines(PurchaseLineFilter? filter = (), PurchaseLineSort[]? sort = (),
            int? size = (), int? offset = ()) returns PurchaseLineRelatedList?|error {
        dataservice:Query query = check dsGraphql:toRelatedQuery(dataservicePurchaseEntity, "lines", self.row,
                dataservicePageConfig, filter, sort, size, offset);
        stream<PurchaseLine, persist:Error?> rows = dataserviceClient->/purchaselines(PurchaseLine,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        PurchaseLine[]|persist:Error items = from PurchaseLine item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Purchase", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from PurchaseLine item in items
                select new PurchaseLineRelatedObject(item), hasMore};
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

    # The `region` field of `Purchase`
    # + return - The value
    isolated resource function get region() returns string => self.row.region;

    # The `number` field of `Purchase`
    # + return - The value
    isolated resource function get number() returns int => self.row.number;

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
    # The conditions on `region`
    dsGraphql:StringFilter? region;
    # The conditions on `number`
    dsGraphql:IntFilter? number;
    # The conditions on `customerId`
    dsGraphql:IntFilter? customerId;
|};

# One entry of the order of purchases; set exactly one field.
public type PurchaseSort record {|
    # Sorts by `region`
    dsGraphql:SortDirection? region;
    # Sorts by `number`
    dsGraphql:SortDirection? number;
    # Sorts by `customerId`
    dsGraphql:SortDirection? customerId;
|};

# The fields of `Purchase` that callers can see.
final readonly & dataservice:Field[] dataservicePurchaseFields = [
    {name: "region", column: "region", 'type: dataservice:STRING, key: true},
    {name: "number", column: "number", 'type: dataservice:INT, key: true},
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
        },
        {
            'field: "lines",
            many: true,
            fields: ["region", "number"],
            targetFields: ["purchaseRegion", "purchaseNumber"],
            target: {
                name: "PurchaseLine",
                collection: "purchaseLines",
                'resource: "purchaselines",
                datastore: dataservice:H2,
                fields: dataservicePurchaseLineFields
            }
        }
    ]
};

# A purchase line.
public isolated service class PurchaseLineObject {
    private final readonly & PurchaseLine row;

    isolated function init(PurchaseLine row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `PurchaseLine`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `quantity` field of `PurchaseLine`
    # + return - The value
    isolated resource function get quantity() returns int => self.row.quantity;

    # The key of the related 'Purchase'.
    # + return - The value
    isolated resource function get purchaseRegion() returns string => self.row.purchaseRegion;

    # The key of the related 'Purchase'.
    # + return - The value
    isolated resource function get purchaseNumber() returns int => self.row.purchaseNumber;

    isolated function prefetchPurchase(graphql:Context ctx) {
        ctx.getDataLoader("PurchaseLine.purchase").add({"purchaseRegion": self.row.purchaseRegion, "purchaseNumber": self.row.purchaseNumber});
    }

    # Gets the purchase of a purchase line.
    #
    # + ctx - The request context, which holds the dataloader of the relation
    # + return - The purchase, `()` when there is none, or the error
    @graphql:ResourceConfig {prefetchMethodName: "prefetchPurchase"}
    isolated resource function get purchase(graphql:Context ctx) returns PurchaseRelatedObject?|error {
        Purchase? row = check ctx.getDataLoader("PurchaseLine.purchase").get({"purchaseRegion": self.row.purchaseRegion, "purchaseNumber": self.row.purchaseNumber});
        return row is Purchase ? new (row) : ();
    }
}

# A page of purchase lines.
#
# + items - The purchase lines
# + hasMore - Whether records exist after this page
public type PurchaseLineList record {
    PurchaseLineObject[] items;
    boolean hasMore;
};

# A purchase line, as a related record: its own fields and no relation fields.
public isolated service class PurchaseLineRelatedObject {
    private final readonly & PurchaseLine row;

    isolated function init(PurchaseLine row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `PurchaseLine`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `quantity` field of `PurchaseLine`
    # + return - The value
    isolated resource function get quantity() returns int => self.row.quantity;

    # The key of the related 'Purchase'.
    # + return - The value
    isolated resource function get purchaseRegion() returns string => self.row.purchaseRegion;

    # The key of the related 'Purchase'.
    # + return - The value
    isolated resource function get purchaseNumber() returns int => self.row.purchaseNumber;
}

# A page of related purchase lines.
#
# + items - The purchase lines
# + hasMore - Whether records exist after this page; read them with `offset`
public type PurchaseLineRelatedList record {
    PurchaseLineRelatedObject[] items;
    boolean hasMore;
};

# The conditions that purchase lines match; every condition set must hold.
public type PurchaseLineFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `quantity`
    dsGraphql:IntFilter? quantity;
    # The conditions on `purchaseRegion`
    dsGraphql:StringFilter? purchaseRegion;
    # The conditions on `purchaseNumber`
    dsGraphql:IntFilter? purchaseNumber;
|};

# One entry of the order of purchase lines; set exactly one field.
public type PurchaseLineSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `quantity`
    dsGraphql:SortDirection? quantity;
    # Sorts by `purchaseRegion`
    dsGraphql:SortDirection? purchaseRegion;
    # Sorts by `purchaseNumber`
    dsGraphql:SortDirection? purchaseNumber;
|};

# The fields of `PurchaseLine` that callers can see.
final readonly & dataservice:Field[] dataservicePurchaseLineFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "quantity", column: "quantity", 'type: dataservice:INT},
    {name: "purchaseRegion", column: "purchaseRegion", 'type: dataservice:STRING},
    {name: "purchaseNumber", column: "purchaseNumber", 'type: dataservice:INT}
];

# The description of `PurchaseLine` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataservicePurchaseLineEntity = {
    name: "PurchaseLine",
    collection: "purchaseLines",
    'resource: "purchaselines",
    datastore: dataservice:H2,
    fields: dataservicePurchaseLineFields,
    relations: [
        {
            'field: "purchase",
            many: false,
            fields: ["purchaseRegion", "purchaseNumber"],
            targetFields: ["region", "number"],
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
