// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/graphql.dataloader;
import ballerina/http;
import ballerina/persist;

# The persist client of the data service.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener graphql:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@graphql:ServiceConfig {
    graphiql: {enabled: true},
    cors: dataserviceConfig.serviceConfig.cors,
    contextInit: dataserviceInitContext
}
service graphql:Service /graphql on dataserviceListener {

    # Gets a page of customers.
    #
    # A customer.
    #
    # + filter - The conditions that the customers match
    # + sort - The order of the customers; each entry names one field
    # + size - The number of customers in the page
    # + offset - The number of customers to skip
    # + return - A page of customers, or the error
    resource function get customers(CustomerFilter? filter = (), CustomerSort[]? sort = (),
            int? size = (), int? offset = ()) returns CustomerList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceCustomerEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Customer, persist:Error?> rows = dataserviceClient->/customers(Customer,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Customer[]|persist:Error items = from Customer item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Customer");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Customer item in items
                select new CustomerObject(item), hasMore};
    }

    # Gets a customer by its key.
    #
    # A customer.
    #
    # + id - The id of the customer
    # + return - The customer, or the error
    resource function get customer(int id) returns CustomerObject?|error {
        Customer|persist:Error item = dataserviceClient->/customers/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Customer", [id]);
        }
        return new (item);
    }

    # Gets a page of profiles.
    #
    # + filter - The conditions that the profiles match
    # + sort - The order of the profiles; each entry names one field
    # + size - The number of profiles in the page
    # + offset - The number of profiles to skip
    # + return - A page of profiles, or the error
    resource function get profiles(ProfileFilter? filter = (), ProfileSort[]? sort = (),
            int? size = (), int? offset = ()) returns ProfileList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceProfileEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Profile, persist:Error?> rows = dataserviceClient->/profiles(Profile,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Profile[]|persist:Error items = from Profile item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Profile");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Profile item in items
                select new ProfileObject(item), hasMore};
    }

    # Gets a profile by its key.
    #
    # + id - The id of the profile
    # + return - The profile, or the error
    resource function get profile(int id) returns ProfileObject?|error {
        Profile|persist:Error item = dataserviceClient->/profiles/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Profile", [id]);
        }
        return new (item);
    }

    # Gets a page of purchases.
    #
    # + filter - The conditions that the purchases match
    # + sort - The order of the purchases; each entry names one field
    # + size - The number of purchases in the page
    # + offset - The number of purchases to skip
    # + return - A page of purchases, or the error
    resource function get purchases(PurchaseFilter? filter = (), PurchaseSort[]? sort = (),
            int? size = (), int? offset = ()) returns PurchaseList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataservicePurchaseEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Purchase, persist:Error?> rows = dataserviceClient->/purchases(Purchase,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Purchase[]|persist:Error items = from Purchase item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Purchase");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Purchase item in items
                select new PurchaseObject(item), hasMore};
    }

    # Gets a purchase by its key.
    #
    # + region - The region of the purchase
    # + number - The number of the purchase
    # + return - The purchase, or the error
    resource function get purchase(string region, int number) returns PurchaseObject?|error {
        Purchase|persist:Error item = dataserviceClient->/purchases/[region]/[number];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Purchase", [region, number]);
        }
        return new (item);
    }

    # Gets a page of purchase lines.
    #
    # + filter - The conditions that the purchase lines match
    # + sort - The order of the purchase lines; each entry names one field
    # + size - The number of purchase lines in the page
    # + offset - The number of purchase lines to skip
    # + return - A page of purchase lines, or the error
    resource function get purchaseLines(PurchaseLineFilter? filter = (), PurchaseLineSort[]? sort = (),
            int? size = (), int? offset = ()) returns PurchaseLineList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataservicePurchaseLineEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<PurchaseLine, persist:Error?> rows = dataserviceClient->/purchaselines(PurchaseLine,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        PurchaseLine[]|persist:Error items = from PurchaseLine item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "PurchaseLine");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from PurchaseLine item in items
                select new PurchaseLineObject(item), hasMore};
    }

    # Gets a purchase line by its key.
    #
    # + id - The id of the purchase line
    # + return - The purchase line, or the error
    resource function get purchaseLine(int id) returns PurchaseLineObject?|error {
        PurchaseLine|persist:Error item = dataserviceClient->/purchaselines/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "PurchaseLine", [id]);
        }
        return new (item);
    }
}

# Creates the context of a request, with a dataloader per to-one relation.
#
# + requestContext - The HTTP request context
# + request - The HTTP request
# + return - The context, or the error
isolated function dataserviceInitContext(http:RequestContext requestContext, http:Request request)
        returns graphql:Context|error {
    graphql:Context ctx = new;
    ctx.registerDataLoader("Customer.profile",
            new dataloader:DefaultDataLoader(dataserviceLoadCustomerProfile));
    ctx.registerDataLoader("Profile.customer",
            new dataloader:DefaultDataLoader(dataserviceLoadProfileCustomer));
    ctx.registerDataLoader("Purchase.customer",
            new dataloader:DefaultDataLoader(dataserviceLoadPurchaseCustomer));
    ctx.registerDataLoader("PurchaseLine.purchase",
            new dataloader:DefaultDataLoader(dataserviceLoadPurchaseLinePurchase));
    return ctx;
}

isolated function dataserviceLoadCustomerProfile(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataserviceCustomerEntity, "profile", links,
            dataservicePageConfig);
    stream<Profile, persist:Error?> rows = dataserviceClient->/profiles(Profile,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    Profile[]|persist:Error found = from Profile row in rows
        select row;
    if found is persist:Error {
        return dsGraphql:toListError(found, "Customer", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataserviceCustomerEntity, "profile", links);
}

isolated function dataserviceLoadProfileCustomer(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataserviceProfileEntity, "customer", links,
            dataservicePageConfig);
    stream<Customer, persist:Error?> rows = dataserviceClient->/customers(Customer,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    Customer[]|persist:Error found = from Customer row in rows
        select row;
    if found is persist:Error {
        return dsGraphql:toListError(found, "Profile", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataserviceProfileEntity, "customer", links);
}

isolated function dataserviceLoadPurchaseCustomer(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataservicePurchaseEntity, "customer", links,
            dataservicePageConfig);
    stream<Customer, persist:Error?> rows = dataserviceClient->/customers(Customer,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    Customer[]|persist:Error found = from Customer row in rows
        select row;
    if found is persist:Error {
        return dsGraphql:toListError(found, "Purchase", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataservicePurchaseEntity, "customer", links);
}

isolated function dataserviceLoadPurchaseLinePurchase(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataservicePurchaseLineEntity, "purchase", links,
            dataservicePageConfig);
    stream<Purchase, persist:Error?> rows = dataserviceClient->/purchases(Purchase,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    Purchase[]|persist:Error found = from Purchase row in rows
        select row;
    if found is persist:Error {
        return dsGraphql:toListError(found, "PurchaseLine", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataservicePurchaseLineEntity, "purchase", links);
}
