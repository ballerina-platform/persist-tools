// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const CLASS = "classes";
const ENROLLMENT = "enrollments";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [CLASS]: {
            entityName: "Class",
            tableName: "Class",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                "enrollments[].id": {relation: {entityName: "enrollments", refField: "id"}},
                "enrollments[].classId": {relation: {entityName: "enrollments", refField: "classId"}}
            },
            keyFields: ["id"],
            joinMetadata: {enrollments: {entity: Enrollment, fieldName: "enrollments", refTable: "Enrollment", refColumns: ["classId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}}
        },
        [ENROLLMENT]: {
            entityName: "Enrollment",
            tableName: "Enrollment",
            fieldMetadata: {
                id: {columnName: "id"},
                classId: {columnName: "classId"},
                "class.id": {relation: {entityName: "class", refField: "id"}},
                "class.name": {relation: {entityName: "class", refField: "name"}}
            },
            keyFields: ["id"],
            joinMetadata: {'class: {entity: Class, fieldName: "'class", refTable: "Class", refColumns: ["id"], joinColumns: ["classId"], 'type: psql:ONE_TO_MANY}}
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
            [CLASS]: check new (dbClient, self.metadata.get(CLASS), psql:MYSQL_SPECIFICS),
            [ENROLLMENT]: check new (dbClient, self.metadata.get(ENROLLMENT), psql:MYSQL_SPECIFICS)
        };
    }

    # Get rows from Class table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get classes(ClassTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Class table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get classes/[int id](ClassTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Class table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post classes(ClassInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CLASS);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from ClassInsert inserted in data
            select inserted.id;
    }

    # Update row in Class table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put classes/[int id](ClassUpdate value) returns Class|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CLASS);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/classes/[id].get();
    }

    # Delete row from Class table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete classes/[int id]() returns Class|persist:Error {
        Class result = check self->/classes/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CLASS);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Enrollment table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get enrollments(EnrollmentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Enrollment table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get enrollments/[int id](EnrollmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Enrollment table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post enrollments(EnrollmentInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ENROLLMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from EnrollmentInsert inserted in data
            select inserted.id;
    }

    # Update row in Enrollment table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put enrollments/[int id](EnrollmentUpdate value) returns Enrollment|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ENROLLMENT);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/enrollments/[id].get();
    }

    # Delete row from Enrollment table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete enrollments/[int id]() returns Enrollment|persist:Error {
        Enrollment result = check self->/enrollments/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ENROLLMENT);
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

