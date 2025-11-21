// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/h2.driver as _;
import ballerinax/java.jdbc;
import ballerinax/persist.sql as psql;

const CAR = "cars";

# H2 persist client.
public isolated client class H2Client {
    *persist:AbstractPersistClient;

    private final jdbc:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [CAR]: {
            entityName: "Car",
            tableName: "Car",
            fieldMetadata: {
                id: {columnName: "id"},
                make: {columnName: "make"},
                model: {columnName: "model"},
                ownerId: {columnName: "ownerId"},
                "owner.id": {relation: {entityName: "owner", refField: "id"}},
                "owner.name": {relation: {entityName: "owner", refField: "name"}},
                "owner.nic": {relation: {entityName: "owner", refField: "nic"}},
                "owner.salary": {relation: {entityName: "owner", refField: "salary"}}
            },
            keyFields: ["id"],
            joinMetadata: {owner: {entity: User, fieldName: "owner", refTable: "User", refColumns: ["id"], joinColumns: ["ownerId"], 'type: psql:ONE_TO_MANY}}
        }
    };

    public isolated function init(string url, string? user = (), string? password = (), jdbc:Options? connectionOptions = ()) returns persist:Error? {
        jdbc:Client|error dbClient = new (url = url, user = user, password = password, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {[CAR]: check new (dbClient, self.metadata.get(CAR), psql:H2_SPECIFICS)};
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

