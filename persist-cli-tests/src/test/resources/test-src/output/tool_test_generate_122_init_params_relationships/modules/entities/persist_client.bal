// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const CUSTOMER = "customers";
const ORDER = "orders";
const ORDER_ITEM = "orderitems";
const PRODUCT = "products";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [CUSTOMER]: {
            entityName: "Customer",
            tableName: "Customer",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                email: {columnName: "email"},
                "orders[].id": {relation: {entityName: "orders", refField: "id"}},
                "orders[].orderNumber": {relation: {entityName: "orders", refField: "orderNumber"}},
                "orders[].totalAmount": {relation: {entityName: "orders", refField: "totalAmount"}},
                "orders[].customerId": {relation: {entityName: "orders", refField: "customerId"}}
            },
            keyFields: ["id"],
            joinMetadata: {orders: {entity: Order, fieldName: "orders", refTable: "Order", refColumns: ["customerId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}}
        },
        [ORDER]: {
            entityName: "Order",
            tableName: "Order",
            fieldMetadata: {
                id: {columnName: "id"},
                orderNumber: {columnName: "orderNumber"},
                totalAmount: {columnName: "totalAmount"},
                customerId: {columnName: "customerId"},
                "customer.id": {relation: {entityName: "customer", refField: "id"}},
                "customer.name": {relation: {entityName: "customer", refField: "name"}},
                "customer.email": {relation: {entityName: "customer", refField: "email"}},
                "items[].id": {relation: {entityName: "items", refField: "id"}},
                "items[].quantity": {relation: {entityName: "items", refField: "quantity"}},
                "items[].price": {relation: {entityName: "items", refField: "price"}},
                "items[].orderId": {relation: {entityName: "items", refField: "orderId"}},
                "items[].productId": {relation: {entityName: "items", refField: "productId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                customer: {entity: Customer, fieldName: "customer", refTable: "Customer", refColumns: ["id"], joinColumns: ["customerId"], 'type: psql:ONE_TO_MANY},
                items: {entity: OrderItem, fieldName: "items", refTable: "OrderItem", refColumns: ["orderId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}
            }
        },
        [ORDER_ITEM]: {
            entityName: "OrderItem",
            tableName: "OrderItem",
            fieldMetadata: {
                id: {columnName: "id"},
                quantity: {columnName: "quantity"},
                price: {columnName: "price"},
                orderId: {columnName: "orderId"},
                productId: {columnName: "productId"},
                "order.id": {relation: {entityName: "order", refField: "id"}},
                "order.orderNumber": {relation: {entityName: "order", refField: "orderNumber"}},
                "order.totalAmount": {relation: {entityName: "order", refField: "totalAmount"}},
                "'order.customerId": {relation: {entityName: "order", refField: "customerId"}},
                "product.id": {relation: {entityName: "product", refField: "id"}},
                "product.name": {relation: {entityName: "product", refField: "name"}},
                "product.description": {relation: {entityName: "product", refField: "description"}},
                "product.price": {relation: {entityName: "product", refField: "price"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                'order: {entity: Order, fieldName: "'order", refTable: "Order", refColumns: ["id"], joinColumns: ["orderId"], 'type: psql:ONE_TO_MANY},
                product: {entity: Product, fieldName: "product", refTable: "Product", refColumns: ["id"], joinColumns: ["productId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [PRODUCT]: {
            entityName: "Product",
            tableName: "Product",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                description: {columnName: "description"},
                price: {columnName: "price"},
                "orderItems[].id": {relation: {entityName: "orderItems", refField: "id"}},
                "orderItems[].quantity": {relation: {entityName: "orderItems", refField: "quantity"}},
                "orderItems[].price": {relation: {entityName: "orderItems", refField: "price"}},
                "orderItems[].orderId": {relation: {entityName: "orderItems", refField: "orderId"}},
                "orderItems[].productId": {relation: {entityName: "orderItems", refField: "productId"}}
            },
            keyFields: ["id"],
            joinMetadata: {orderItems: {entity: OrderItem, fieldName: "orderItems", refTable: "OrderItem", refColumns: ["productId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}}
        }
    };

    # Initialize the persist client with MySQL database connection parameters.
    #
    # + host - Database server host
    # + port - Database server port
    # + user - Database username
    # + password - Database password
    # + database - Database name
    # + connectionOptions - Additional MySQL connection options
    # + return - An error if initialization fails
    public isolated function init(string host, int port, string user, string password, string database, mysql:Options connectionOptions = {}) returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [CUSTOMER]: check new (dbClient, self.metadata.get(CUSTOMER), psql:MYSQL_SPECIFICS),
            [ORDER]: check new (dbClient, self.metadata.get(ORDER), psql:MYSQL_SPECIFICS),
            [ORDER_ITEM]: check new (dbClient, self.metadata.get(ORDER_ITEM), psql:MYSQL_SPECIFICS),
            [PRODUCT]: check new (dbClient, self.metadata.get(PRODUCT), psql:MYSQL_SPECIFICS)
        };
    }

    # Get rows from Customer table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get customers(CustomerTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Customer table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get customers/[int id](CustomerTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Customer table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post customers(CustomerInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CUSTOMER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CustomerInsert inserted in data
            select inserted.id;
    }

    # Update row in Customer table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put customers/[int id](CustomerUpdate value) returns Customer|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CUSTOMER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/customers/[id].get();
    }

    # Delete row from Customer table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete customers/[int id]() returns Customer|persist:Error {
        Customer result = check self->/customers/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CUSTOMER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Order table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get orders(OrderTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Order table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get orders/[int id](OrderTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Order table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post orders(OrderInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderInsert inserted in data
            select inserted.id;
    }

    # Update row in Order table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put orders/[int id](OrderUpdate value) returns Order|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/orders/[id].get();
    }

    # Delete row from Order table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete orders/[int id]() returns Order|persist:Error {
        Order result = check self->/orders/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from OrderItem table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get orderitems(OrderItemTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from OrderItem table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get orderitems/[int id](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into OrderItem table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post orderitems(OrderItemInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select inserted.id;
    }

    # Update row in OrderItem table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put orderitems/[int id](OrderItemUpdate value) returns OrderItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/orderitems/[id].get();
    }

    # Delete row from OrderItem table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete orderitems/[int id]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Product table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get products(ProductTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Product table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get products/[int id](ProductTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Product table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post products(ProductInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PRODUCT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from ProductInsert inserted in data
            select inserted.id;
    }

    # Update row in Product table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put products/[int id](ProductUpdate value) returns Product|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PRODUCT);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/products/[id].get();
    }

    # Delete row from Product table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete products/[int id]() returns Product|persist:Error {
        Product result = check self->/products/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PRODUCT);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Execute a custom SQL query and return results.
    #
    # + sqlQuery - The SQL query to execute
    # + rowType - Defines the structure of the result rows
    # + return - A collection of result rows or an error
    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    # Execute a custom SQL command (INSERT, UPDATE, DELETE, etc.).
    #
    # + sqlQuery - The SQL command to execute
    # + return - The execution result or an error
    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

