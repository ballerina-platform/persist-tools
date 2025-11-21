// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const BOOK = "books";
const ORDER = "orders";
const ORDER_ITEM = "orderitems";
const PAYMENT = "payments";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [BOOK]: {
            entityName: "Book",
            tableName: "Book",
            fieldMetadata: {
                bookId: {columnName: "bookId"},
                title: {columnName: "title"},
                author: {columnName: "author"},
                price: {columnName: "price"},
                stock: {columnName: "stock"},
                "orderitem.orderItemId": {relation: {entityName: "orderitem", refField: "orderItemId"}},
                "orderitem.quantity": {relation: {entityName: "orderitem", refField: "quantity"}},
                "orderitem.price": {relation: {entityName: "orderitem", refField: "price"}},
                "orderitem.bookBookId": {relation: {entityName: "orderitem", refField: "bookBookId"}},
                "orderitem.orderOrderId": {relation: {entityName: "orderitem", refField: "orderOrderId"}}
            },
            keyFields: ["bookId"],
            joinMetadata: {orderitem: {entity: OrderItem, fieldName: "orderitem", refTable: "OrderItem", refColumns: ["bookBookId"], joinColumns: ["bookId"], 'type: psql:ONE_TO_ONE}}
        },
        [ORDER]: {
            entityName: "Order",
            tableName: "Order",
            fieldMetadata: {
                orderId: {columnName: "orderId"},
                customerId: {columnName: "customerId"},
                createdAt: {columnName: "createdAt"},
                totalPrice: {columnName: "totalPrice"},
                "orderItems[].orderItemId": {relation: {entityName: "orderItems", refField: "orderItemId"}},
                "orderItems[].quantity": {relation: {entityName: "orderItems", refField: "quantity"}},
                "orderItems[].price": {relation: {entityName: "orderItems", refField: "price"}},
                "orderItems[].bookBookId": {relation: {entityName: "orderItems", refField: "bookBookId"}},
                "orderItems[].orderOrderId": {relation: {entityName: "orderItems", refField: "orderOrderId"}},
                "payment.paymentId": {relation: {entityName: "payment", refField: "paymentId"}},
                "payment.paymentAmount": {relation: {entityName: "payment", refField: "paymentAmount"}},
                "payment.paymentDate": {relation: {entityName: "payment", refField: "paymentDate"}},
                "payment.orderOrderId": {relation: {entityName: "payment", refField: "orderOrderId"}}
            },
            keyFields: ["orderId"],
            joinMetadata: {
                orderItems: {entity: OrderItem, fieldName: "orderItems", refTable: "OrderItem", refColumns: ["orderOrderId"], joinColumns: ["orderId"], 'type: psql:MANY_TO_ONE},
                payment: {entity: Payment, fieldName: "payment", refTable: "Payment", refColumns: ["orderOrderId"], joinColumns: ["orderId"], 'type: psql:ONE_TO_ONE}
            }
        },
        [ORDER_ITEM]: {
            entityName: "OrderItem",
            tableName: "OrderItem",
            fieldMetadata: {
                orderItemId: {columnName: "orderItemId"},
                quantity: {columnName: "quantity"},
                price: {columnName: "price"},
                bookBookId: {columnName: "bookBookId"},
                orderOrderId: {columnName: "orderOrderId"},
                "book.bookId": {relation: {entityName: "book", refField: "bookId"}},
                "book.title": {relation: {entityName: "book", refField: "title"}},
                "book.author": {relation: {entityName: "book", refField: "author"}},
                "book.price": {relation: {entityName: "book", refField: "price"}},
                "book.stock": {relation: {entityName: "book", refField: "stock"}},
                "order.orderId": {relation: {entityName: "order", refField: "orderId"}},
                "order.customerId": {relation: {entityName: "order", refField: "customerId"}},
                "order.createdAt": {relation: {entityName: "order", refField: "createdAt"}},
                "order.totalPrice": {relation: {entityName: "order", refField: "totalPrice"}}
            },
            keyFields: ["orderItemId"],
            joinMetadata: {
                book: {entity: Book, fieldName: "book", refTable: "Book", refColumns: ["bookId"], joinColumns: ["bookBookId"], 'type: psql:ONE_TO_ONE},
                'order: {entity: Order, fieldName: "'order", refTable: "Order", refColumns: ["orderId"], joinColumns: ["orderOrderId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [PAYMENT]: {
            entityName: "Payment",
            tableName: "Payment",
            fieldMetadata: {
                paymentId: {columnName: "paymentId"},
                paymentAmount: {columnName: "paymentAmount"},
                paymentDate: {columnName: "paymentDate"},
                orderOrderId: {columnName: "orderOrderId"},
                "order.orderId": {relation: {entityName: "order", refField: "orderId"}},
                "order.customerId": {relation: {entityName: "order", refField: "customerId"}},
                "order.createdAt": {relation: {entityName: "order", refField: "createdAt"}},
                "order.totalPrice": {relation: {entityName: "order", refField: "totalPrice"}}
            },
            keyFields: ["paymentId"],
            joinMetadata: {'order: {entity: Order, fieldName: "'order", refTable: "Order", refColumns: ["orderId"], joinColumns: ["orderOrderId"], 'type: psql:ONE_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [BOOK]: check new (dbClient, self.metadata.get(BOOK), psql:MYSQL_SPECIFICS),
            [ORDER]: check new (dbClient, self.metadata.get(ORDER), psql:MYSQL_SPECIFICS),
            [ORDER_ITEM]: check new (dbClient, self.metadata.get(ORDER_ITEM), psql:MYSQL_SPECIFICS),
            [PAYMENT]: check new (dbClient, self.metadata.get(PAYMENT), psql:MYSQL_SPECIFICS)
        };
    }

    # Get rows from Book table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get books(BookTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Book table.
    #
    # + bookId - The value of the primary key field bookId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get books/[string bookId](BookTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Book table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post books(BookInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOK);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from BookInsert inserted in data
            select inserted.bookId;
    }

    # Update row in Book table.
    #
    # + bookId - The value of the primary key field bookId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put books/[string bookId](BookUpdate value) returns Book|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOK);
        }
        _ = check sqlClient.runUpdateQuery(bookId, value);
        return self->/books/[bookId].get();
    }

    # Delete row from Book table.
    #
    # + bookId - The value of the primary key field bookId
    # + return - The deleted record or an error
    isolated resource function delete books/[string bookId]() returns Book|persist:Error {
        Book result = check self->/books/[bookId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOK);
        }
        _ = check sqlClient.runDeleteQuery(bookId);
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
    # + orderId - The value of the primary key field orderId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get orders/[string orderId](OrderTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Order table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post orders(OrderInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderInsert inserted in data
            select inserted.orderId;
    }

    # Update row in Order table.
    #
    # + orderId - The value of the primary key field orderId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put orders/[string orderId](OrderUpdate value) returns Order|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runUpdateQuery(orderId, value);
        return self->/orders/[orderId].get();
    }

    # Delete row from Order table.
    #
    # + orderId - The value of the primary key field orderId
    # + return - The deleted record or an error
    isolated resource function delete orders/[string orderId]() returns Order|persist:Error {
        Order result = check self->/orders/[orderId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runDeleteQuery(orderId);
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
    # + orderItemId - The value of the primary key field orderItemId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get orderitems/[string orderItemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into OrderItem table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post orderitems(OrderItemInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select inserted.orderItemId;
    }

    # Update row in OrderItem table.
    #
    # + orderItemId - The value of the primary key field orderItemId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put orderitems/[string orderItemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runUpdateQuery(orderItemId, value);
        return self->/orderitems/[orderItemId].get();
    }

    # Delete row from OrderItem table.
    #
    # + orderItemId - The value of the primary key field orderItemId
    # + return - The deleted record or an error
    isolated resource function delete orderitems/[string orderItemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderItemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runDeleteQuery(orderItemId);
        return result;
    }

    # Get rows from Payment table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get payments(PaymentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Payment table.
    #
    # + paymentId - The value of the primary key field paymentId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get payments/[string paymentId](PaymentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Payment table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post payments(PaymentInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PAYMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PaymentInsert inserted in data
            select inserted.paymentId;
    }

    # Update row in Payment table.
    #
    # + paymentId - The value of the primary key field paymentId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put payments/[string paymentId](PaymentUpdate value) returns Payment|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PAYMENT);
        }
        _ = check sqlClient.runUpdateQuery(paymentId, value);
        return self->/payments/[paymentId].get();
    }

    # Delete row from Payment table.
    #
    # + paymentId - The value of the primary key field paymentId
    # + return - The deleted record or an error
    isolated resource function delete payments/[string paymentId]() returns Payment|persist:Error {
        Payment result = check self->/payments/[paymentId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PAYMENT);
        }
        _ = check sqlClient.runDeleteQuery(paymentId);
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

