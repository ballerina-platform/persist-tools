// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/h2.driver as _;
import ballerinax/java.jdbc;
import ballerinax/persist.sql as psql;

const USER = "users";
const CAR = "cars";
const USER2 = "user2s";
const CAR2 = "car2s";

# H2 persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final jdbc:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [USER]: {
            entityName: "User",
            tableName: "User",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                nic: {columnName: "nic"},
                salary: {columnName: "salary"},
                "drives.id": {relation: {entityName: "drives", refField: "id"}},
                "drives.name": {relation: {entityName: "drives", refField: "name"}},
                "drives.model": {relation: {entityName: "drives", refField: "model"}},
                "drives.driverId": {relation: {entityName: "drives", refField: "driverId"}}
            },
            keyFields: ["id"],
            joinMetadata: {drives: {entity: Car, fieldName: "drives", refTable: "Car", refColumns: ["driverId"], joinColumns: ["id"], 'type: psql:ONE_TO_ONE}}
        },
        [CAR]: {
            entityName: "Car",
            tableName: "Car",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                model: {columnName: "model"},
                driverId: {columnName: "driverId"},
                "driver.id": {relation: {entityName: "driver", refField: "id"}},
                "driver.name": {relation: {entityName: "driver", refField: "name"}},
                "driver.nic": {relation: {entityName: "driver", refField: "nic"}},
                "driver.salary": {relation: {entityName: "driver", refField: "salary"}}
            },
            keyFields: ["id"],
            joinMetadata: {driver: {entity: User, fieldName: "driver", refTable: "User", refColumns: ["id"], joinColumns: ["driverId"], 'type: psql:ONE_TO_ONE}}
        },
        [USER2]: {
            entityName: "User2",
            tableName: "User2",
            fieldMetadata: {
                id: {columnName: "id"},
                nic: {columnName: "nic"},
                name: {columnName: "name"},
                salary: {columnName: "salary"},
                "drives.id": {relation: {entityName: "drives", refField: "id"}},
                "drives.name": {relation: {entityName: "drives", refField: "name"}},
                "drives.model": {relation: {entityName: "drives", refField: "model"}},
                "drives.driverId": {relation: {entityName: "drives", refField: "driverId"}},
                "drives.driverNic": {relation: {entityName: "drives", refField: "driverNic"}}
            },
            keyFields: ["id", "nic"],
            joinMetadata: {drives: {entity: Car2, fieldName: "drives", refTable: "Car2", refColumns: ["driverId", "driverNic"], joinColumns: ["id", "nic"], 'type: psql:ONE_TO_ONE}}
        },
        [CAR2]: {
            entityName: "Car2",
            tableName: "Car2",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                model: {columnName: "model"},
                driverId: {columnName: "driverId"},
                driverNic: {columnName: "driverNic"},
                "driver.id": {relation: {entityName: "driver", refField: "id"}},
                "driver.nic": {relation: {entityName: "driver", refField: "nic"}},
                "driver.name": {relation: {entityName: "driver", refField: "name"}},
                "driver.salary": {relation: {entityName: "driver", refField: "salary"}}
            },
            keyFields: ["id"],
            joinMetadata: {driver: {entity: User2, fieldName: "driver", refTable: "User2", refColumns: ["id", "nic"], joinColumns: ["driverId", "driverNic"], 'type: psql:ONE_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        jdbc:Client|error dbClient = new (url = url, user = user, password = password, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [USER]: check new (dbClient, self.metadata.get(USER), psql:H2_SPECIFICS),
            [CAR]: check new (dbClient, self.metadata.get(CAR), psql:H2_SPECIFICS),
            [USER2]: check new (dbClient, self.metadata.get(USER2), psql:H2_SPECIFICS),
            [CAR2]: check new (dbClient, self.metadata.get(CAR2), psql:H2_SPECIFICS)
        };
    }

    # Get rows from User table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get users(UserTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "query"
    } external;

    # Get row from User table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "queryOne"
    } external;

    # Insert rows into User table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    # Update row in User table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    # Delete row from User table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Car table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get cars(CarTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "query"
    } external;

    # Get row from Car table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get cars/[int id](CarTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "queryOne"
    } external;

    # Insert rows into Car table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post cars(CarInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CarInsert inserted in data
            select inserted.id;
    }

    # Update row in Car table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put cars/[int id](CarUpdate value) returns Car|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/cars/[id].get();
    }

    # Delete row from Car table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete cars/[int id]() returns Car|persist:Error {
        Car result = check self->/cars/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from User2 table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get user2s(User2TargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "query"
    } external;

    # Get row from User2 table.
    #
    # + id - The value of the primary key field id
    # + nic - The value of the primary key field nic
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get user2s/[int id]/[string nic](User2TargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "queryOne"
    } external;

    # Insert rows into User2 table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post user2s(User2Insert[] data) returns [int, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER2);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from User2Insert inserted in data
            select [inserted.id, inserted.nic];
    }

    # Update row in User2 table.
    #
    # + id - The value of the primary key field id
    # + nic - The value of the primary key field nic
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put user2s/[int id]/[string nic](User2Update value) returns User2|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER2);
        }
        _ = check sqlClient.runUpdateQuery({"id": id, "nic": nic}, value);
        return self->/user2s/[id]/[nic].get();
    }

    # Delete row from User2 table.
    #
    # + id - The value of the primary key field id
    # + nic - The value of the primary key field nic
    # + return - The deleted record or an error
    isolated resource function delete user2s/[int id]/[string nic]() returns User2|persist:Error {
        User2 result = check self->/user2s/[id]/[nic].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER2);
        }
        _ = check sqlClient.runDeleteQuery({"id": id, "nic": nic});
        return result;
    }

    # Get rows from Car2 table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get car2s(Car2TargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "query"
    } external;

    # Get row from Car2 table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get car2s/[int id](Car2TargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "queryOne"
    } external;

    # Insert rows into Car2 table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post car2s(Car2Insert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR2);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from Car2Insert inserted in data
            select inserted.id;
    }

    # Update row in Car2 table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put car2s/[int id](Car2Update value) returns Car2|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR2);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/car2s/[id].get();
    }

    # Delete row from Car2 table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete car2s/[int id]() returns Car2|persist:Error {
        Car2 result = check self->/car2s/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR2);
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor"
    } external;

    # Execute a custom SQL command (INSERT, UPDATE, DELETE, etc.).
    #
    # + sqlQuery - The SQL command to execute
    # + return - The execution result or an error
    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor"
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

