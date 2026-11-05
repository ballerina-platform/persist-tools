// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const DEPARTMENT = "departments";
const EMPLOYEE = "employees";
const BUILDING = "buildings";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [DEPARTMENT]: {
            entityName: "Department",
            tableName: "Department",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                "employees[].id": {relation: {entityName: "employees", refField: "id"}},
                "employees[].name": {relation: {entityName: "employees", refField: "name"}},
                "employees[].age": {relation: {entityName: "employees", refField: "age"}},
                "employees[].departmentId": {relation: {entityName: "employees", refField: "departmentId"}}
            },
            keyFields: ["id"],
            joinMetadata: {employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["departmentId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}}
        },
        [EMPLOYEE]: {
            entityName: "Employee",
            tableName: "Employee",
            fieldMetadata: {
                id: {columnName: "id", dbGenerated: true},
                name: {columnName: "name"},
                age: {columnName: "age"},
                departmentId: {columnName: "departmentId"},
                "department.id": {relation: {entityName: "department", refField: "id"}},
                "department.name": {relation: {entityName: "department", refField: "name"}}
            },
            keyFields: ["id"],
            joinMetadata: {department: {entity: Department, fieldName: "department", refTable: "Department", refColumns: ["id"], joinColumns: ["departmentId"], 'type: psql:ONE_TO_MANY}}
        },
        [BUILDING]: {
            entityName: "Building",
            tableName: "Building",
            fieldMetadata: {
                code: {columnName: "code"},
                city: {columnName: "city"}
            },
            keyFields: ["code"]
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT), psql:MYSQL_SPECIFICS),
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE), psql:MYSQL_SPECIFICS),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING), psql:MYSQL_SPECIFICS)
        };
    }

    # Get rows from Department table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + offsetClause - Number of rows to skip before returning results (e.g., `10`)
    # + return - A collection of matching records or an error
    isolated resource function get departments(DepartmentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``, sql:ParameterizedQuery offsetClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryWithOffset"
    } external;

    # Get row from Department table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get departments/[string id](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Department table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select inserted.id;
    }

    # Update row in Department table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put departments/[string id](DepartmentUpdate value) returns Department|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        Department|persist:Error result = self->/departments/[id].get();
        if result is Department || result is persist:NotFoundError {
            return result;
        }
        return error persist:ReadBackError(string `The record was updated but could not be read back: ${result.message()}`, result);
    }

    # Delete row from Department table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete departments/[string id]() returns Department|persist:Error {
        Department result = check self->/departments/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Employee table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + offsetClause - Number of rows to skip before returning results (e.g., `10`)
    # + return - A collection of matching records or an error
    isolated resource function get employees(EmployeeTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``, sql:ParameterizedQuery offsetClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryWithOffset"
    } external;

    # Get row from Employee table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get employees/[int id](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
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
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
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
        Employee|persist:Error result = self->/employees/[id].get();
        if result is Employee || result is persist:NotFoundError {
            return result;
        }
        return error persist:ReadBackError(string `The record was updated but could not be read back: ${result.message()}`, result);
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

    # Get rows from Building table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + offsetClause - Number of rows to skip before returning results (e.g., `10`)
    # + return - A collection of matching records or an error
    isolated resource function get buildings(BuildingTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``, sql:ParameterizedQuery offsetClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryWithOffset"
    } external;

    # Get row from Building table.
    #
    # + code - The value of the primary key field code
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get buildings/[string code](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Building table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select inserted.code;
    }

    # Update row in Building table.
    #
    # + code - The value of the primary key field code
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put buildings/[string code](BuildingUpdate value) returns Building|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runUpdateQuery(code, value);
        Building|persist:Error result = self->/buildings/[code].get();
        if result is Building || result is persist:NotFoundError {
            return result;
        }
        return error persist:ReadBackError(string `The record was updated but could not be read back: ${result.message()}`, result);
    }

    # Delete row from Building table.
    #
    # + code - The value of the primary key field code
    # + return - The deleted record or an error
    isolated resource function delete buildings/[string code]() returns Building|persist:Error {
        Building result = check self->/buildings/[code].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runDeleteQuery(code);
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

