// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/persist.sql as psql;
import ballerinax/postgresql;
import ballerinax/postgresql.driver as _;

const COMPANY = "companies";
const EMPLOYEE = "employees";

# PostgreSQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final postgresql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} metadata = {
        [COMPANY]: {
            entityName: "Company",
            tableName: "Company",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                "employees[].id": {relation: {entityName: "employees", refField: "id"}},
                "employees[].name": {relation: {entityName: "employees", refField: "name"}},
                "employees[].companyId": {relation: {entityName: "employees", refField: "companyId"}}
            },
            keyFields: ["id"],
            joinMetadata: {employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["companyId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}}
        },
        [EMPLOYEE]: {
            entityName: "Employee",
            tableName: "Employee",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                companyId: {columnName: "companyId"},
                "company.id": {relation: {entityName: "company", refField: "id"}},
                "company.name": {relation: {entityName: "company", refField: "name"}}
            },
            keyFields: ["id"],
            joinMetadata: {company: {entity: Company, fieldName: "company", refTable: "Company", refColumns: ["id"], joinColumns: ["companyId"], 'type: psql:ONE_TO_MANY}}
        }
    };

    public isolated function init() returns persist:Error? {
        postgresql:Client|error dbClient = new (host = host, username = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        if defaultSchema != () {
            lock {
                foreach string key in self.metadata.keys() {
                    psql:SQLMetadata metadata = self.metadata.get(key);
                    if metadata.schemaName == () {
                        metadata.schemaName = defaultSchema;
                    }
                    map<psql:JoinMetadata>? joinMetadataMap = metadata.joinMetadata;
                    if joinMetadataMap == () {
                        continue;
                    }
                    foreach [string, psql:JoinMetadata] [_, joinMetadata] in joinMetadataMap.entries() {
                        if joinMetadata.refSchema == () {
                            joinMetadata.refSchema = defaultSchema;
                        }
                    }
                }
            }
        }
        self.persistClients = {
            [COMPANY]: check new (dbClient, self.metadata.get(COMPANY).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS)
        };
    }

    # Get rows from Company table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get companies(CompanyTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns targetType[]|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryAsList"
    } external;

    # Get row from Company table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get companies/[int id](CompanyTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Company table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post companies(CompanyInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMPANY);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CompanyInsert inserted in data
            select inserted.id;
    }

    # Update row in Company table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put companies/[int id](CompanyUpdate value) returns Company|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMPANY);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/companies/[id].get();
    }

    # Delete row from Company table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete companies/[int id]() returns Company|persist:Error {
        Company result = check self->/companies/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMPANY);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Employee table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get employees(EmployeeTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns targetType[]|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryAsList"
    } external;

    # Get row from Employee table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get employees/[int id](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Employee table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post employees(EmployeeInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.id;
    }

    # Update row in Employee table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put employees/[int id](EmployeeUpdate value) returns Employee|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/employees/[id].get();
    }

    # Delete row from Employee table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete employees/[int id]() returns Employee|persist:Error {
        Employee result = check self->/employees/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor"
    } external;

    # Execute a custom SQL command (INSERT, UPDATE, DELETE, etc.).
    #
    # + sqlQuery - The SQL command to execute
    # + return - The execution result or an error
    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor"
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

