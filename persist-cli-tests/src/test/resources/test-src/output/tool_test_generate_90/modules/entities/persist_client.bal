// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const USER = "users";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [USER]: {
            entityName: "User",
            tableName: "User",
            fieldMetadata: {
                id: {columnName: "id", dbGenerated: true},
                name: {columnName: "name"},
                gender: {columnName: "gender"},
                nic: {columnName: "nic"},
                salary: {columnName: "salary"}
            },
            keyFields: ["id"]
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {[USER]: check new (dbClient, self.metadata.get(USER), psql:MYSQL_SPECIFICS)};
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from User table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
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
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
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

