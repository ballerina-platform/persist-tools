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
    # + id - The id of the purchase
    # + return - The purchase, or the error
    resource function get purchase(int id) returns PurchaseObject?|error {
        Purchase|persist:Error item = dataserviceClient->/purchases/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Purchase", [id]);
        }
        return new (item);
    }

    # Creates a purchase.
    #
    # + input - The purchase to create
    # + return - The created purchase, or the error
    remote function createPurchase(PurchaseInsertInput input) returns PurchaseObject?|error =>
        (check dataserviceCreatePurchase([input]))[0];

    # Creates purchases.
    #
    # + input - The purchases to create
    # + return - The created purchases, or the error
    remote function createPurchases(PurchaseInsertInput[] input) returns PurchaseList?|error =>
        {items: check dataserviceCreatePurchase(input), hasMore: false};

    # Updates a purchase by its key.
    #
    # + id - The id of the purchase
    # + input - The fields to change
    # + return - The updated purchase, `()` when it cannot be returned, or the error
    remote function updatePurchase(int id, PurchaseUpdateInput input)
            returns PurchaseObject?|error {
        PurchaseUpdate value = check transformInputToPurchaseUpdate(input);
        Purchase|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/purchases/[id];
        } else {
            item = dataserviceClient->/purchases/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Purchase", [id]);
        }
        return new (item);
    }

    # Deletes a purchase by its key.
    #
    # + id - The id of the purchase
    # + return - The deleted purchase, or the error
    remote function deletePurchase(int id) returns PurchaseObject?|error {
        Purchase|persist:Error item = dataserviceClient->/purchases/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Purchase", [id]);
        }
        return new (item);
    }

    # Gets a page of customers.
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
    # + input - The customer to create
    # + return - The created customer, or the error
    remote function createCustomer(CustomerInsertInput input) returns CustomerObject?|error =>
        (check dataserviceCreateCustomer([input]))[0];

    # Creates customers.
    #
    # + input - The customers to create
    # + return - The created customers, or the error
    remote function createCustomers(CustomerInsertInput[] input) returns CustomerList?|error =>
        {items: check dataserviceCreateCustomer(input), hasMore: false};

    # Creates an audit entry.
    #
    # A log entry that callers may only write.
    #
    # + input - The audit entry to create
    # + return - The created audit entry, or the error
    remote function createAuditEntry(AuditEntryInsertInput input) returns AuditEntryObject?|error =>
        (check dataserviceCreateAuditEntry([input]))[0];

    # Creates audit entries.
    #
    # A log entry that callers may only write.
    #
    # + input - The audit entries to create
    # + return - The created audit entries, or the error
    remote function createAuditEntries(AuditEntryInsertInput[] input) returns AuditEntryList?|error =>
        {items: check dataserviceCreateAuditEntry(input), hasMore: false};

    # Updates an audit entry by its key.
    #
    # A log entry that callers may only write.
    #
    # + region - The region of the audit entry
    # + seq - The seq of the audit entry
    # + input - The fields to change
    # + return - `true` when the audit entry is updated, or the error
    remote function updateAuditEntry(string region, int seq, AuditEntryUpdateInput input)
            returns boolean?|error {
        AuditEntryUpdate value = check transformInputToAuditEntryUpdate(input);
        if value.length() == 0 {
            return true;
        }
        AuditEntry|persist:Error item = dataserviceClient->/auditentries/[region]/[seq].put(value);
        if item is persist:Error {
            check dsGraphql:toUpdateError(item, "AuditEntry", [region, seq]);
        }
        return true;
    }

    # Deletes an audit entry by its key.
    #
    # A log entry that callers may only write.
    #
    # + region - The region of the audit entry
    # + seq - The seq of the audit entry
    # + return - `true` when the audit entry is deleted, or the error
    remote function deleteAuditEntry(string region, int seq) returns boolean?|error {
        AuditEntry|persist:Error item = dataserviceClient->/auditentries/[region]/[seq].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "AuditEntry", [region, seq]);
        }
        return true;
    }
}

isolated function dataserviceCreatePurchase(PurchaseInsertInput[] input) returns PurchaseObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    PurchaseInsert[] values = [];
    foreach PurchaseInsertInput entry in input {
        values.push(check transformInputToPurchaseInsert(entry));
    }
    do {
        transaction {
            int[] keys = check dataserviceClient->/purchases.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataservicePurchaseEntity, keyValues);
            stream<Purchase, persist:Error?> stored = dataserviceClient->/purchases(Purchase,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Purchase[] rows = check from Purchase item in stored
                select item;
            check dataservice:orderByKeys(rows, dataservicePurchaseEntity, keyValues);
            check commit;
            return from Purchase item in rows
                select new PurchaseObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Purchase");
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

isolated function dataserviceCreateAuditEntry(AuditEntryInsertInput[] input) returns AuditEntryObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    AuditEntryInsert[] values = from AuditEntryInsertInput entry in input
        select transformInputToAuditEntryInsert(entry);
    do {
        transaction {
            [string, int][] keys = check dataserviceClient->/auditentries.post(values);
            dataservice:KeyValue[][] keyValues = keys;
            dataservice:Query query = dataservice:toKeysQuery(dataserviceAuditEntryEntity, keyValues);
            stream<AuditEntry, persist:Error?> stored = dataserviceClient->/auditentries(AuditEntry,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            AuditEntry[] rows = check from AuditEntry item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceAuditEntryEntity, keyValues);
            check commit;
            return from AuditEntry item in rows
                select new AuditEntryObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "AuditEntry");
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
    ctx.registerDataLoader("Purchase.customer",
            new dataloader:DefaultDataLoader(dataserviceLoadPurchaseCustomer));
    return ctx;
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
