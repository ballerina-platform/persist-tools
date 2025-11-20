// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/persist.sql as psql;
import ballerinax/postgresql;
import ballerinax/postgresql.driver as _;

const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";
const ORDER_ITEM = "orderitems";
const EMPLOYEE = "employees";

# PostgreSQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final postgresql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} metadata = {
        [WORKSPACE]: {
            entityName: "Workspace",
            tableName: "Workspace",
            fieldMetadata: {
                workspaceId: {columnName: "workspaceId"},
                workspaceType: {columnName: "workspaceType"},
                locationBuildingCode: {columnName: "locationBuildingCode"},
                "location.buildingCode": {relation: {entityName: "location", refField: "buildingCode"}},
                "location.city": {relation: {entityName: "location", refField: "city"}},
                "location.state": {relation: {entityName: "location", refField: "state"}},
                "location.country": {relation: {entityName: "location", refField: "country"}},
                "location.postalCode": {relation: {entityName: "location", refField: "postalCode"}},
                "location.type": {relation: {entityName: "location", refField: "type"}},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo"}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName"}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName"}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate"}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender"}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate"}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo"}},
                "employees[].departmentDeptName": {relation: {entityName: "employees", refField: "departmentDeptName"}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId"}},
                "employees[].workspaceWorkspaceType": {relation: {entityName: "employees", refField: "workspaceWorkspaceType"}}
            },
            keyFields: ["workspaceId", "workspaceType"],
            joinMetadata: {
                location: {entity: Building, fieldName: "location", refTable: "Building", refColumns: ["buildingCode"], joinColumns: ["locationBuildingCode"], 'type: psql:ONE_TO_MANY},
                employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["workspaceWorkspaceId", "workspaceWorkspaceType"], joinColumns: ["workspaceId", "workspaceType"], 'type: psql:MANY_TO_ONE}
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
                'type: {columnName: "type"},
                "workspaces[].workspaceId": {relation: {entityName: "workspaces", refField: "workspaceId"}},
                "workspaces[].workspaceType": {relation: {entityName: "workspaces", refField: "workspaceType"}},
                "workspaces[].locationBuildingCode": {relation: {entityName: "workspaces", refField: "locationBuildingCode"}}
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
                location: {columnName: "location"},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo"}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName"}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName"}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate"}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender"}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate"}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo"}},
                "employees[].departmentDeptName": {relation: {entityName: "employees", refField: "departmentDeptName"}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId"}},
                "employees[].workspaceWorkspaceType": {relation: {entityName: "employees", refField: "workspaceWorkspaceType"}}
            },
            keyFields: ["deptNo", "deptName"],
            joinMetadata: {employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["departmentDeptNo", "departmentDeptName"], joinColumns: ["deptNo", "deptName"], 'type: psql:MANY_TO_ONE}}
        },
        [ORDER_ITEM]: {
            entityName: "OrderItem",
            tableName: "OrderItem",
            fieldMetadata: {
                orderId: {columnName: "orderId"},
                itemId: {columnName: "itemId"},
                quantity: {columnName: "quantity"},
                notes: {columnName: "notes"}
            },
            keyFields: ["orderId", "itemId"]
        },
        [EMPLOYEE]: {
            entityName: "Employee",
            tableName: "Employee",
            fieldMetadata: {
                empNo: {columnName: "empNo"},
                firstName: {columnName: "firstName"},
                lastName: {columnName: "lastName"},
                birthDate: {columnName: "birthDate"},
                gender: {columnName: "gender"},
                hireDate: {columnName: "hireDate"},
                departmentDeptNo: {columnName: "departmentDeptNo"},
                departmentDeptName: {columnName: "departmentDeptName"},
                workspaceWorkspaceId: {columnName: "workspaceWorkspaceId"},
                workspaceWorkspaceType: {columnName: "workspaceWorkspaceType"},
                "department.deptNo": {relation: {entityName: "department", refField: "deptNo"}},
                "department.deptName": {relation: {entityName: "department", refField: "deptName"}},
                "department.location": {relation: {entityName: "department", refField: "location"}},
                "workspace.workspaceId": {relation: {entityName: "workspace", refField: "workspaceId"}},
                "workspace.workspaceType": {relation: {entityName: "workspace", refField: "workspaceType"}},
                "workspace.locationBuildingCode": {relation: {entityName: "workspace", refField: "locationBuildingCode"}}
            },
            keyFields: ["empNo", "firstName"],
            joinMetadata: {
                department: {entity: Department, fieldName: "department", refTable: "Department", refColumns: ["deptNo", "deptName"], joinColumns: ["departmentDeptNo", "departmentDeptName"], 'type: psql:ONE_TO_MANY},
                workspace: {entity: Workspace, fieldName: "workspace", refTable: "Workspace", refColumns: ["workspaceId", "workspaceType"], joinColumns: ["workspaceWorkspaceId", "workspaceWorkspaceType"], 'type: psql:ONE_TO_MANY}
            }
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
            [WORKSPACE]: check new (dbClient, self.metadata.get(WORKSPACE).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [ORDER_ITEM]: check new (dbClient, self.metadata.get(ORDER_ITEM).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS)
        };
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    # Get row from Workspace table.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + workspaceType - The value of the primary key field workspaceType
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get workspaces/[string workspaceId]/[string workspaceType](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Workspace table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post workspaces(WorkspaceInsert[] data) returns [string, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select [inserted.workspaceId, inserted.workspaceType];
    }

    # Update row in Workspace table.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + workspaceType - The value of the primary key field workspaceType
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put workspaces/[string workspaceId]/[string workspaceType](WorkspaceUpdate value) returns Workspace|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runUpdateQuery({"workspaceId": workspaceId, "workspaceType": workspaceType}, value);
        return self->/workspaces/[workspaceId]/[workspaceType].get();
    }

    # Delete row from Workspace table.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + workspaceType - The value of the primary key field workspaceType
    # + return - The deleted record or an error
    isolated resource function delete workspaces/[string workspaceId]/[string workspaceType]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId]/[workspaceType].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runDeleteQuery({"workspaceId": workspaceId, "workspaceType": workspaceType});
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    # Get row from Building table.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    # Get row from Department table.
    #
    # + deptNo - The value of the primary key field deptNo
    # + deptName - The value of the primary key field deptName
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get departments/[string deptNo]/[string deptName](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Department table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post departments(DepartmentInsert[] data) returns [string, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select [inserted.deptNo, inserted.deptName];
    }

    # Update row in Department table.
    #
    # + deptNo - The value of the primary key field deptNo
    # + deptName - The value of the primary key field deptName
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put departments/[string deptNo]/[string deptName](DepartmentUpdate value) returns Department|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runUpdateQuery({"deptNo": deptNo, "deptName": deptName}, value);
        return self->/departments/[deptNo]/[deptName].get();
    }

    # Delete row from Department table.
    #
    # + deptNo - The value of the primary key field deptNo
    # + deptName - The value of the primary key field deptName
    # + return - The deleted record or an error
    isolated resource function delete departments/[string deptNo]/[string deptName]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo]/[deptName].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runDeleteQuery({"deptNo": deptNo, "deptName": deptName});
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    # Get row from OrderItem table.
    #
    # + orderId - The value of the primary key field orderId
    # + itemId - The value of the primary key field itemId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get orderitems/[string orderId]/[string itemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into OrderItem table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select [inserted.orderId, inserted.itemId];
    }

    # Update row in OrderItem table.
    #
    # + orderId - The value of the primary key field orderId
    # + itemId - The value of the primary key field itemId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runUpdateQuery({"orderId": orderId, "itemId": itemId}, value);
        return self->/orderitems/[orderId]/[itemId].get();
    }

    # Delete row from OrderItem table.
    #
    # + orderId - The value of the primary key field orderId
    # + itemId - The value of the primary key field itemId
    # + return - The deleted record or an error
    isolated resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderId]/[itemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runDeleteQuery({"orderId": orderId, "itemId": itemId});
        return result;
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
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    # Get row from Employee table.
    #
    # + empNo - The value of the primary key field empNo
    # + firstName - The value of the primary key field firstName
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get employees/[string empNo]/[string firstName](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Employee table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post employees(EmployeeInsert[] data) returns [string, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select [inserted.empNo, inserted.firstName];
    }

    # Update row in Employee table.
    #
    # + empNo - The value of the primary key field empNo
    # + firstName - The value of the primary key field firstName
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put employees/[string empNo]/[string firstName](EmployeeUpdate value) returns Employee|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runUpdateQuery({"empNo": empNo, "firstName": firstName}, value);
        return self->/employees/[empNo]/[firstName].get();
    }

    # Delete row from Employee table.
    #
    # + empNo - The value of the primary key field empNo
    # + firstName - The value of the primary key field firstName
    # + return - The deleted record or an error
    isolated resource function delete employees/[string empNo]/[string firstName]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo]/[firstName].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runDeleteQuery({"empNo": empNo, "firstName": firstName});
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

