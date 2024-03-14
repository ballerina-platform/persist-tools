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

    isolated resource function get books(BookTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get books/[string bookId](BookTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post books(BookInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOK);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from BookInsert inserted in data
            select inserted.bookId;
    }

    isolated resource function put books/[string bookId](BookUpdate value) returns Book|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOK);
        }
        _ = check sqlClient.runUpdateQuery(bookId, value);
        return self->/books/[bookId].get();
    }

    isolated resource function delete books/[string bookId]() returns Book|persist:Error {
        Book result = check self->/books/[bookId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOK);
        }
        _ = check sqlClient.runDeleteQuery(bookId);
        return result;
    }

    isolated resource function get orders(OrderTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get orders/[string orderId](OrderTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post orders(OrderInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderInsert inserted in data
            select inserted.orderId;
    }

    isolated resource function put orders/[string orderId](OrderUpdate value) returns Order|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runUpdateQuery(orderId, value);
        return self->/orders/[orderId].get();
    }

    isolated resource function delete orders/[string orderId]() returns Order|persist:Error {
        Order result = check self->/orders/[orderId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER);
        }
        _ = check sqlClient.runDeleteQuery(orderId);
        return result;
    }

    isolated resource function get orderitems(OrderItemTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get orderitems/[string orderItemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post orderitems(OrderItemInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select inserted.orderItemId;
    }

    isolated resource function put orderitems/[string orderItemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runUpdateQuery(orderItemId, value);
        return self->/orderitems/[orderItemId].get();
    }

    isolated resource function delete orderitems/[string orderItemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderItemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runDeleteQuery(orderItemId);
        return result;
    }

    isolated resource function get payments(PaymentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get payments/[string paymentId](PaymentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post payments(PaymentInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PAYMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PaymentInsert inserted in data
            select inserted.paymentId;
    }

    isolated resource function put payments/[string paymentId](PaymentUpdate value) returns Payment|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PAYMENT);
        }
        _ = check sqlClient.runUpdateQuery(paymentId, value);
        return self->/payments/[paymentId].get();
    }

    isolated resource function delete payments/[string paymentId]() returns Payment|persist:Error {
        Payment result = check self->/payments/[paymentId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PAYMENT);
        }
        _ = check sqlClient.runDeleteQuery(paymentId);
        return result;
    }

    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

