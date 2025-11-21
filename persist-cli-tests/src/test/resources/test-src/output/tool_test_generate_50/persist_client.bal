// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const EMPLOYEE = "employees";
const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [EMPLOYEE]: {
            entityName: "Employee",
            tableName: "Employee",
            fieldMetadata: {
                empNo: {columnName: "empNo"},
                firstName: {columnName: "firstName"},
                lastName: {columnName: "lastName"},
                gender: {columnName: "gender"},
                departmentDeptNo: {columnName: "departmentDeptNo"},
                "department.deptNo": {relation: {entityName: "department", refField: "deptNo"}},
                "department.deptName": {relation: {entityName: "department", refField: "deptName"}},
                "workspace.workspaceId": {relation: {entityName: "workspace", refField: "workspaceId"}},
                "workspace.workspaceType": {relation: {entityName: "workspace", refField: "workspaceType"}},
                "workspace.locationBuildingCode": {relation: {entityName: "workspace", refField: "locationBuildingCode"}},
                "workspace.employeeEmpNo": {relation: {entityName: "workspace", refField: "employeeEmpNo"}}
            },
            keyFields: ["empNo"],
            joinMetadata: {
                department: {entity: Department, fieldName: "department", refTable: "Department", refColumns: ["deptNo"], joinColumns: ["departmentDeptNo"], 'type: psql:ONE_TO_MANY},
                workspace: {entity: Workspace, fieldName: "workspace", refTable: "Workspace", refColumns: ["employeeEmpNo"], joinColumns: ["empNo"], 'type: psql:ONE_TO_ONE}
            }
        },
        [WORKSPACE]: {
            entityName: "Workspace",
            tableName: "Workspace",
            fieldMetadata: {
                workspaceId: {columnName: "workspaceId"},
                workspaceType: {columnName: "workspaceType"},
                locationBuildingCode: {columnName: "locationBuildingCode"},
                employeeEmpNo: {columnName: "employeeEmpNo"},
                "location.buildingCode": {relation: {entityName: "location", refField: "buildingCode"}},
                "location.city": {relation: {entityName: "location", refField: "city"}},
                "location.state": {relation: {entityName: "location", refField: "state"}},
                "location.country": {relation: {entityName: "location", refField: "country"}},
                "location.postalCode": {relation: {entityName: "location", refField: "postalCode"}},
                "employee.empNo": {relation: {entityName: "employee", refField: "empNo"}},
                "employee.firstName": {relation: {entityName: "employee", refField: "firstName"}},
                "employee.lastName": {relation: {entityName: "employee", refField: "lastName"}},
                "employee.gender": {relation: {entityName: "employee", refField: "gender"}},
                "employee.departmentDeptNo": {relation: {entityName: "employee", refField: "departmentDeptNo"}}
            },
            keyFields: ["workspaceId"],
            joinMetadata: {
                location: {entity: Building, fieldName: "location", refTable: "Building", refColumns: ["buildingCode"], joinColumns: ["locationBuildingCode"], 'type: psql:ONE_TO_MANY},
                employee: {entity: Employee, fieldName: "employee", refTable: "Employee", refColumns: ["empNo"], joinColumns: ["employeeEmpNo"], 'type: psql:ONE_TO_ONE}
            }
        },
        [BUILDING]: {
            entityName: "Building",
            tableName: "Building",
            fieldMetadata: {
                buildingCode: {columnName: "buildingCode"},
                city: {columnName: "city"},
                state: {columnName: "state"},
                country: {columnName: "country"},
                postalCode: {columnName: "postalCode"},
                "workspaces[].workspaceId": {relation: {entityName: "workspaces", refField: "workspaceId"}},
                "workspaces[].workspaceType": {relation: {entityName: "workspaces", refField: "workspaceType"}},
                "workspaces[].locationBuildingCode": {relation: {entityName: "workspaces", refField: "locationBuildingCode"}},
                "workspaces[].employeeEmpNo": {relation: {entityName: "workspaces", refField: "employeeEmpNo"}}
            },
            keyFields: ["buildingCode"],
            joinMetadata: {workspaces: {entity: Workspace, fieldName: "workspaces", refTable: "Workspace", refColumns: ["locationBuildingCode"], joinColumns: ["buildingCode"], 'type: psql:MANY_TO_ONE}}
        },
        [DEPARTMENT]: {
            entityName: "Department",
            tableName: "Department",
            fieldMetadata: {
                deptNo: {columnName: "deptNo"},
                deptName: {columnName: "deptName"},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo"}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName"}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName"}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender"}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo"}}
            },
            keyFields: ["deptNo"],
            joinMetadata: {employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["departmentDeptNo"], joinColumns: ["deptNo"], 'type: psql:MANY_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE), psql:MYSQL_SPECIFICS),
            [WORKSPACE]: check new (dbClient, self.metadata.get(WORKSPACE), psql:MYSQL_SPECIFICS),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING), psql:MYSQL_SPECIFICS),
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT), psql:MYSQL_SPECIFICS)
        };
    }

    # Get rows from Employee table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get employees(EmployeeTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Employee table.
    #
    # + empNo - The value of the primary key field empNo
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get employees/[string empNo](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Employee table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.empNo;
    }

    # Update row in Employee table.
    #
    # + empNo - The value of the primary key field empNo
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runUpdateQuery(empNo, value);
        return self->/employees/[empNo].get();
    }

    # Delete row from Employee table.
    #
    # + empNo - The value of the primary key field empNo
    # + return - The deleted record or an error
    isolated resource function delete employees/[string empNo]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runDeleteQuery(empNo);
        return result;
    }

    # Get rows from Workspace table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get workspaces(WorkspaceTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Workspace table.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get workspaces/[string workspaceId](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Workspace table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select inserted.workspaceId;
    }

    # Update row in Workspace table.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runUpdateQuery(workspaceId, value);
        return self->/workspaces/[workspaceId].get();
    }

    # Delete row from Workspace table.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + return - The deleted record or an error
    isolated resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runDeleteQuery(workspaceId);
        return result;
    }

    # Get rows from Building table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get buildings(BuildingTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Building table.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
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
            select inserted.buildingCode;
    }

    # Update row in Building table.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runUpdateQuery(buildingCode, value);
        return self->/buildings/[buildingCode].get();
    }

    # Delete row from Building table.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + return - The deleted record or an error
    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        Building result = check self->/buildings/[buildingCode].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runDeleteQuery(buildingCode);
        return result;
    }

    # Get rows from Department table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get departments(DepartmentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from Department table.
    #
    # + deptNo - The value of the primary key field deptNo
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get departments/[string deptNo](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
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
            select inserted.deptNo;
    }

    # Update row in Department table.
    #
    # + deptNo - The value of the primary key field deptNo
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runUpdateQuery(deptNo, value);
        return self->/departments/[deptNo].get();
    }

    # Delete row from Department table.
    #
    # + deptNo - The value of the primary key field deptNo
    # + return - The deleted record or an error
    isolated resource function delete departments/[string deptNo]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runDeleteQuery(deptNo);
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

