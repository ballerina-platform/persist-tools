// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.redis as predis;
import ballerinax/redis;

const EMPLOYEE = "employees";
const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";

# Redis persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final redis:Client dbClient;

    private final map<predis:RedisClient> persistClients;

    private final record {|predis:RedisMetadata...;|} & readonly metadata = {
        [EMPLOYEE]: {
            entityName: "Employee",
            collectionName: "Employee",
            fieldMetadata: {
                empNo: {fieldName: "empNo", fieldDataType: predis:STRING},
                firstName: {fieldName: "firstName", fieldDataType: predis:STRING},
                lastName: {fieldName: "lastName", fieldDataType: predis:STRING},
                hireDate: {fieldName: "hireDate", fieldDataType: predis:DATE},
                gender: {fieldName: "gender", fieldDataType: predis:STRING},
                dateOfBirth: {fieldName: "dateOfBirth", fieldDataType: predis:CIVIL},
                departmentDeptNo: {fieldName: "departmentDeptNo", fieldDataType: predis:STRING},
                "department.deptNo": {relation: {entityName: "department", refField: "deptNo", refFieldDataType: predis:STRING}},
                "department.deptName": {relation: {entityName: "department", refField: "deptName", refFieldDataType: predis:STRING}},
                "workspace.workspaceId": {relation: {entityName: "workspace", refField: "workspaceId", refFieldDataType: predis:STRING}},
                "workspace.workspaceType": {relation: {entityName: "workspace", refField: "workspaceType", refFieldDataType: predis:STRING}},
                "workspace.locationBuildingCode": {relation: {entityName: "workspace", refField: "locationBuildingCode", refFieldDataType: predis:STRING}},
                "workspace.employeeEmpNo": {relation: {entityName: "workspace", refField: "employeeEmpNo", refFieldDataType: predis:STRING}}
            },
            keyFields: ["empNo"],
            refMetadata: {
                department: {entity: Department, fieldName: "department", refCollection: "Department", refMetaDataKey: "employees", refFields: ["deptNo"], joinFields: ["departmentDeptNo"], 'type: predis:ONE_TO_MANY},
                workspace: {entity: Workspace, fieldName: "workspace", refCollection: "Workspace", refFields: ["employeeEmpNo"], joinFields: ["empNo"], 'type: predis:ONE_TO_ONE}
            }
        },
        [WORKSPACE]: {
            entityName: "Workspace",
            collectionName: "Workspace",
            fieldMetadata: {
                workspaceId: {fieldName: "workspaceId", fieldDataType: predis:STRING},
                workspaceType: {fieldName: "workspaceType", fieldDataType: predis:STRING},
                locationBuildingCode: {fieldName: "locationBuildingCode", fieldDataType: predis:STRING},
                employeeEmpNo: {fieldName: "employeeEmpNo", fieldDataType: predis:STRING},
                "location.buildingCode": {relation: {entityName: "location", refField: "buildingCode", refFieldDataType: predis:STRING}},
                "location.city": {relation: {entityName: "location", refField: "city", refFieldDataType: predis:STRING}},
                "location.state": {relation: {entityName: "location", refField: "state", refFieldDataType: predis:STRING}},
                "location.country": {relation: {entityName: "location", refField: "country", refFieldDataType: predis:STRING}},
                "location.postalCode": {relation: {entityName: "location", refField: "postalCode", refFieldDataType: predis:STRING}},
                "employee.empNo": {relation: {entityName: "employee", refField: "empNo", refFieldDataType: predis:STRING}},
                "employee.firstName": {relation: {entityName: "employee", refField: "firstName", refFieldDataType: predis:STRING}},
                "employee.lastName": {relation: {entityName: "employee", refField: "lastName", refFieldDataType: predis:STRING}},
                "employee.hireDate": {relation: {entityName: "employee", refField: "hireDate", refFieldDataType: predis:DATE}},
                "employee.gender": {relation: {entityName: "employee", refField: "gender", refFieldDataType: predis:STRING}},
                "employee.dateOfBirth": {relation: {entityName: "employee", refField: "dateOfBirth", refFieldDataType: predis:CIVIL}},
                "employee.departmentDeptNo": {relation: {entityName: "employee", refField: "departmentDeptNo", refFieldDataType: predis:STRING}}
            },
            keyFields: ["workspaceId"],
            refMetadata: {
                location: {entity: Building, fieldName: "location", refCollection: "Building", refMetaDataKey: "workspaces", refFields: ["buildingCode"], joinFields: ["locationBuildingCode"], 'type: predis:ONE_TO_MANY},
                employee: {entity: Employee, fieldName: "employee", refCollection: "Employee", refMetaDataKey: "workspace", refFields: ["empNo"], joinFields: ["employeeEmpNo"], 'type: predis:ONE_TO_ONE}
            }
        },
        [BUILDING]: {
            entityName: "Building",
            collectionName: "Building",
            fieldMetadata: {
                buildingCode: {fieldName: "buildingCode", fieldDataType: predis:STRING},
                city: {fieldName: "city", fieldDataType: predis:STRING},
                state: {fieldName: "state", fieldDataType: predis:STRING},
                country: {fieldName: "country", fieldDataType: predis:STRING},
                postalCode: {fieldName: "postalCode", fieldDataType: predis:STRING},
                "workspaces[].workspaceId": {relation: {entityName: "workspaces", refField: "workspaceId", refFieldDataType: predis:STRING}},
                "workspaces[].workspaceType": {relation: {entityName: "workspaces", refField: "workspaceType", refFieldDataType: predis:STRING}},
                "workspaces[].locationBuildingCode": {relation: {entityName: "workspaces", refField: "locationBuildingCode", refFieldDataType: predis:STRING}},
                "workspaces[].employeeEmpNo": {relation: {entityName: "workspaces", refField: "employeeEmpNo", refFieldDataType: predis:STRING}}
            },
            keyFields: ["buildingCode"],
            refMetadata: {workspaces: {entity: Workspace, fieldName: "workspaces", refCollection: "Workspace", refFields: ["locationBuildingCode"], joinFields: ["buildingCode"], 'type: predis:MANY_TO_ONE}}
        },
        [DEPARTMENT]: {
            entityName: "Department",
            collectionName: "Department",
            fieldMetadata: {
                deptNo: {fieldName: "deptNo", fieldDataType: predis:STRING},
                deptName: {fieldName: "deptName", fieldDataType: predis:STRING},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo", refFieldDataType: predis:STRING}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName", refFieldDataType: predis:STRING}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName", refFieldDataType: predis:STRING}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate", refFieldDataType: predis:DATE}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender", refFieldDataType: predis:STRING}},
                "employees[].dateOfBirth": {relation: {entityName: "employees", refField: "dateOfBirth", refFieldDataType: predis:CIVIL}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo", refFieldDataType: predis:STRING}}
            },
            keyFields: ["deptNo"],
            refMetadata: {employees: {entity: Employee, fieldName: "employees", refCollection: "Employee", refFields: ["departmentDeptNo"], joinFields: ["deptNo"], 'type: predis:MANY_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        redis:Client|error dbClient = new (connectionConfig);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE), cacheConfig.maxAge),
            [WORKSPACE]: check new (dbClient, self.metadata.get(WORKSPACE), cacheConfig.maxAge),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING), cacheConfig.maxAge),
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT), cacheConfig.maxAge)
        };
    }

    # Get rows from Employee key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get employees(EmployeeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from Employee key space.
    #
    # + empNo - The value of the primary key field empNo
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get employees/[string empNo](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Employee key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.empNo;
    }

    # Update row in Employee key space.
    #
    # + empNo - The value of the primary key field empNo
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runUpdateQuery(empNo, value);
        return self->/employees/[empNo].get();
    }

    # Delete row from Employee key space.
    #
    # + empNo - The value of the primary key field empNo
    # + return - The deleted record or an error
    isolated resource function delete employees/[string empNo]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runDeleteQuery(empNo);
        return result;
    }

    # Get rows from Workspace key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get workspaces(WorkspaceTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from Workspace key space.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get workspaces/[string workspaceId](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Workspace key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select inserted.workspaceId;
    }

    # Update row in Workspace key space.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runUpdateQuery(workspaceId, value);
        return self->/workspaces/[workspaceId].get();
    }

    # Delete row from Workspace key space.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + return - The deleted record or an error
    isolated resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runDeleteQuery(workspaceId);
        return result;
    }

    # Get rows from Building key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get buildings(BuildingTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from Building key space.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Building key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BUILDING);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select inserted.buildingCode;
    }

    # Update row in Building key space.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BUILDING);
        }
        _ = check redisClient.runUpdateQuery(buildingCode, value);
        return self->/buildings/[buildingCode].get();
    }

    # Delete row from Building key space.
    #
    # + buildingCode - The value of the primary key field buildingCode
    # + return - The deleted record or an error
    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        Building result = check self->/buildings/[buildingCode].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BUILDING);
        }
        _ = check redisClient.runDeleteQuery(buildingCode);
        return result;
    }

    # Get rows from Department key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get departments(DepartmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from Department key space.
    #
    # + deptNo - The value of the primary key field deptNo
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get departments/[string deptNo](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Department key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select inserted.deptNo;
    }

    # Update row in Department key space.
    #
    # + deptNo - The value of the primary key field deptNo
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runUpdateQuery(deptNo, value);
        return self->/departments/[deptNo].get();
    }

    # Delete row from Department key space.
    #
    # + deptNo - The value of the primary key field deptNo
    # + return - The deleted record or an error
    isolated resource function delete departments/[string deptNo]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runDeleteQuery(deptNo);
        return result;
    }

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

