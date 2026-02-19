// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.inmemory;

const PRODUCT = "products";
final isolated table<Product> key(id) productsTable = table [];

# In-Memory persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final map<inmemory:InMemoryClient> persistClients;

    public isolated function init() returns persist:Error? {
        final map<inmemory:TableMetadata> metadata = {
            [PRODUCT]: {
                keyFields: ["id"],
                query: queryProducts,
                queryOne: queryOneProducts
            }
        };
        self.persistClients = {[PRODUCT]: check new (metadata.get(PRODUCT).cloneReadOnly())};
    }

    # Get rows from Product table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get products(ProductTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from Product table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get products/[int id](ProductTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Product table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post products(ProductInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach ProductInsert value in data {
            lock {
                if productsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Product", value.id);
                }
                productsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    # Update row in Product table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put products/[int id](ProductUpdate value) returns Product|persist:Error {
        lock {
            if !productsTable.hasKey(id) {
                return persist:getNotFoundError("Product", id);
            }
            Product product = productsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                product[k] = v;
            }
            productsTable.put(product);
            return product.clone();
        }
    }

    # Delete row from Product table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete products/[int id]() returns Product|persist:Error {
        lock {
            if !productsTable.hasKey(id) {
                return persist:getNotFoundError("Product", id);
            }
            return productsTable.remove(id).clone();
        }
    }

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        return ();
    }
}

isolated function queryProducts(string[] fields) returns stream<record {}, persist:Error?> {
    table<Product> key(id) productsClonedTable;
    lock {
        productsClonedTable = productsTable.clone();
    }
    return from record {} 'object in productsClonedTable
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryOneProducts(anydata key) returns record {}|persist:NotFoundError {
    table<Product> key(id) productsClonedTable;
    lock {
        productsClonedTable = productsTable.clone();
    }
    from record {} 'object in productsClonedTable
    where persist:getKey('object, ["id"]) == key
    do {
        return {
            ...'object
        };
    };
    return persist:getNotFoundError("Product", key);
}

