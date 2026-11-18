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
    maxQueryDepth: dataserviceConfig.serviceConfig.maxQueryDepth,
    queryComplexityConfig: {maxComplexity: dataserviceConfig.serviceConfig.maxQueryComplexity},
    contextInit: dataserviceInitContext,
    validation: false
}
service graphql:Service /graphql on dataserviceListener {

    # Checks the query limits, so that a value out of range stops the program.
    #
    # + return - An error naming the limit that is out of range
    function init() returns error? {
        check dsGraphql:checkLimits(dataserviceConfig.serviceConfig);
    }

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

    # Creates a customer.
    #
    # A customer.
    #
    # + input - The customer to create
    # + return - The created customer, or the error
    remote function createCustomer(CustomerInsertInput input) returns CustomerObject?|error =>
        (check dataserviceCreateCustomer([input]))[0];

    # Creates customers.
    #
    # A customer.
    #
    # + input - The customers to create
    # + return - The created customers, or the error
    remote function createCustomers(CustomerInsertInput[] input) returns CustomerList?|error =>
        {items: check dataserviceCreateCustomer(input), hasMore: false};

    # Updates a customer by its key.
    #
    # A customer.
    #
    # + id - The id of the customer
    # + input - The fields to change
    # + return - The updated customer, `()` when it cannot be returned, or the error
    remote function updateCustomer(int id, CustomerUpdateInput input)
            returns CustomerObject?|error {
        CustomerUpdate value = check transformInputToCustomerUpdate(input);
        Customer|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/customers/[id];
        } else {
            item = dataserviceClient->/customers/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Customer", [id]);
        }
        return new (item);
    }

    # Deletes a customer by its key.
    #
    # A customer.
    #
    # + id - The id of the customer
    # + return - The deleted customer, or the error
    remote function deleteCustomer(int id) returns CustomerObject?|error {
        Customer|persist:Error item = dataserviceClient->/customers/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Customer", [id]);
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

    # Creates a profile.
    #
    # + input - The profile to create
    # + return - The created profile, or the error
    remote function createProfile(ProfileInsertInput input) returns ProfileObject?|error =>
        (check dataserviceCreateProfile([input]))[0];

    # Creates profiles.
    #
    # + input - The profiles to create
    # + return - The created profiles, or the error
    remote function createProfiles(ProfileInsertInput[] input) returns ProfileList?|error =>
        {items: check dataserviceCreateProfile(input), hasMore: false};

    # Updates a profile by its key.
    #
    # + id - The id of the profile
    # + input - The fields to change
    # + return - The updated profile, `()` when it cannot be returned, or the error
    remote function updateProfile(int id, ProfileUpdateInput input)
            returns ProfileObject?|error {
        ProfileUpdate value = check transformInputToProfileUpdate(input);
        Profile|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/profiles/[id];
        } else {
            item = dataserviceClient->/profiles/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Profile", [id]);
        }
        return new (item);
    }

    # Deletes a profile by its key.
    #
    # + id - The id of the profile
    # + return - The deleted profile, or the error
    remote function deleteProfile(int id) returns ProfileObject?|error {
        Profile|persist:Error item = dataserviceClient->/profiles/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Profile", [id]);
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

isolated function dataserviceCreateCustomer(CustomerInsertInput[] input) returns CustomerObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    CustomerInsert[] values = from CustomerInsertInput entry in input
        select transformInputToCustomerInsert(entry);
    do {
        transaction {
            int[] keys = check dataserviceClient->/customers.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceCustomerEntity, keyValues);
            stream<Customer, persist:Error?> stored = dataserviceClient->/customers(Customer,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Customer[] rows = check from Customer item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceCustomerEntity, keyValues);
            check commit;
            return from Customer item in rows
                select new CustomerObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Customer");
    }
}

isolated function dataserviceCreateProfile(ProfileInsertInput[] input) returns ProfileObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    ProfileInsert[] values = from ProfileInsertInput entry in input
        select transformInputToProfileInsert(entry);
    do {
        transaction {
            int[] keys = check dataserviceClient->/profiles.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceProfileEntity, keyValues);
            stream<Profile, persist:Error?> stored = dataserviceClient->/profiles(Profile,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Profile[] rows = check from Profile item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceProfileEntity, keyValues);
            check commit;
            return from Profile item in rows
                select new ProfileObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Profile");
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
