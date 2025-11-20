// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.redis as predis;
import ballerinax/redis;

const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";
const ORDER_ITEM = "orderitems";
const EMPLOYEE = "employees";

# Redis persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final redis:Client dbClient;

    private final map<predis:RedisClient> persistClients;

    private final record {|predis:RedisMetadata...;|} & readonly metadata = {
        [WORKSPACE]: {
            entityName: "Workspace",
            collectionName: "Workspace",
            fieldMetadata: {
                workspaceId: {fieldName: "workspaceId", fieldDataType: predis:STRING},
                workspaceType: {fieldName: "workspaceType", fieldDataType: predis:STRING},
                locationBuildingCode: {fieldName: "locationBuildingCode", fieldDataType: predis:STRING},
                "location.buildingCode": {relation: {entityName: "location", refField: "buildingCode", refFieldDataType: predis:STRING}},
                "location.city": {relation: {entityName: "location", refField: "city", refFieldDataType: predis:STRING}},
                "location.state": {relation: {entityName: "location", refField: "state", refFieldDataType: predis:STRING}},
                "location.country": {relation: {entityName: "location", refField: "country", refFieldDataType: predis:STRING}},
                "location.postalCode": {relation: {entityName: "location", refField: "postalCode", refFieldDataType: predis:STRING}},
                "location.type": {relation: {entityName: "location", refField: "type", refFieldDataType: predis:STRING}},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo", refFieldDataType: predis:STRING}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName", refFieldDataType: predis:STRING}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName", refFieldDataType: predis:STRING}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate", refFieldDataType: predis:DATE}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender", refFieldDataType: predis:STRING}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate", refFieldDataType: predis:DATE}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo", refFieldDataType: predis:STRING}},
                "employees[].departmentDeptName": {relation: {entityName: "employees", refField: "departmentDeptName", refFieldDataType: predis:STRING}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId", refFieldDataType: predis:STRING}},
                "employees[].workspaceWorkspaceType": {relation: {entityName: "employees", refField: "workspaceWorkspaceType", refFieldDataType: predis:STRING}}
            },
            keyFields: ["workspaceId", "workspaceType"],
            refMetadata: {
                location: {entity: Building, fieldName: "location", refCollection: "Building", refMetaDataKey: "workspaces", refFields: ["buildingCode"], joinFields: ["locationBuildingCode"], 'type: predis:ONE_TO_MANY},
                employees: {entity: Employee, fieldName: "employees", refCollection: "Employee", refFields: ["workspaceWorkspaceId", "workspaceWorkspaceType"], joinFields: ["workspaceId", "workspaceType"], 'type: predis:MANY_TO_ONE}
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
                'type: {fieldName: "type", fieldDataType: predis:STRING},
                "workspaces[].workspaceId": {relation: {entityName: "workspaces", refField: "workspaceId", refFieldDataType: predis:STRING}},
                "workspaces[].workspaceType": {relation: {entityName: "workspaces", refField: "workspaceType", refFieldDataType: predis:STRING}},
                "workspaces[].locationBuildingCode": {relation: {entityName: "workspaces", refField: "locationBuildingCode", refFieldDataType: predis:STRING}}
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
                location: {fieldName: "location", fieldDataType: predis:STRING},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo", refFieldDataType: predis:STRING}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName", refFieldDataType: predis:STRING}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName", refFieldDataType: predis:STRING}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate", refFieldDataType: predis:DATE}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender", refFieldDataType: predis:STRING}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate", refFieldDataType: predis:DATE}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo", refFieldDataType: predis:STRING}},
                "employees[].departmentDeptName": {relation: {entityName: "employees", refField: "departmentDeptName", refFieldDataType: predis:STRING}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId", refFieldDataType: predis:STRING}},
                "employees[].workspaceWorkspaceType": {relation: {entityName: "employees", refField: "workspaceWorkspaceType", refFieldDataType: predis:STRING}}
            },
            keyFields: ["deptNo", "deptName"],
            refMetadata: {employees: {entity: Employee, fieldName: "employees", refCollection: "Employee", refFields: ["departmentDeptNo", "departmentDeptName"], joinFields: ["deptNo", "deptName"], 'type: predis:MANY_TO_ONE}}
        },
        [ORDER_ITEM]: {
            entityName: "OrderItem",
            collectionName: "OrderItem",
            fieldMetadata: {
                orderId: {fieldName: "orderId", fieldDataType: predis:STRING},
                itemId: {fieldName: "itemId", fieldDataType: predis:STRING},
                quantity: {fieldName: "quantity", fieldDataType: predis:INT},
                notes: {fieldName: "notes", fieldDataType: predis:STRING}
            },
            keyFields: ["orderId", "itemId"]
        },
        [EMPLOYEE]: {
            entityName: "Employee",
            collectionName: "Employee",
            fieldMetadata: {
                empNo: {fieldName: "empNo", fieldDataType: predis:STRING},
                firstName: {fieldName: "firstName", fieldDataType: predis:STRING},
                lastName: {fieldName: "lastName", fieldDataType: predis:STRING},
                birthDate: {fieldName: "birthDate", fieldDataType: predis:DATE},
                gender: {fieldName: "gender", fieldDataType: predis:STRING},
                hireDate: {fieldName: "hireDate", fieldDataType: predis:DATE},
                departmentDeptNo: {fieldName: "departmentDeptNo", fieldDataType: predis:STRING},
                departmentDeptName: {fieldName: "departmentDeptName", fieldDataType: predis:STRING},
                workspaceWorkspaceId: {fieldName: "workspaceWorkspaceId", fieldDataType: predis:STRING},
                workspaceWorkspaceType: {fieldName: "workspaceWorkspaceType", fieldDataType: predis:STRING},
                "department.deptNo": {relation: {entityName: "department", refField: "deptNo", refFieldDataType: predis:STRING}},
                "department.deptName": {relation: {entityName: "department", refField: "deptName", refFieldDataType: predis:STRING}},
                "department.location": {relation: {entityName: "department", refField: "location", refFieldDataType: predis:STRING}},
                "workspace.workspaceId": {relation: {entityName: "workspace", refField: "workspaceId", refFieldDataType: predis:STRING}},
                "workspace.workspaceType": {relation: {entityName: "workspace", refField: "workspaceType", refFieldDataType: predis:STRING}},
                "workspace.locationBuildingCode": {relation: {entityName: "workspace", refField: "locationBuildingCode", refFieldDataType: predis:STRING}}
            },
            keyFields: ["empNo", "firstName"],
            refMetadata: {
                department: {entity: Department, fieldName: "department", refCollection: "Department", refMetaDataKey: "employees", refFields: ["deptNo", "deptName"], joinFields: ["departmentDeptNo", "departmentDeptName"], 'type: predis:ONE_TO_MANY},
                workspace: {entity: Workspace, fieldName: "workspace", refCollection: "Workspace", refMetaDataKey: "employees", refFields: ["workspaceId", "workspaceType"], joinFields: ["workspaceWorkspaceId", "workspaceWorkspaceType"], 'type: predis:ONE_TO_MANY}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        redis:Client|error dbClient = new (connectionConfig);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [WORKSPACE]: check new (dbClient, self.metadata.get(WORKSPACE), cacheConfig.maxAge),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING), cacheConfig.maxAge),
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT), cacheConfig.maxAge),
            [ORDER_ITEM]: check new (dbClient, self.metadata.get(ORDER_ITEM), cacheConfig.maxAge),
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE), cacheConfig.maxAge)
        };
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
    # + workspaceType - The value of the primary key field workspaceType
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get workspaces/[string workspaceId]/[string workspaceType](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Workspace key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post workspaces(WorkspaceInsert[] data) returns [string, string][]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select [inserted.workspaceId, inserted.workspaceType];
    }

    # Update row in Workspace key space.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + workspaceType - The value of the primary key field workspaceType
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put workspaces/[string workspaceId]/[string workspaceType](WorkspaceUpdate value) returns Workspace|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runUpdateQuery({"workspaceId": workspaceId, "workspaceType": workspaceType}, value);
        return self->/workspaces/[workspaceId]/[workspaceType].get();
    }

    # Delete row from Workspace key space.
    #
    # + workspaceId - The value of the primary key field workspaceId
    # + workspaceType - The value of the primary key field workspaceType
    # + return - The deleted record or an error
    isolated resource function delete workspaces/[string workspaceId]/[string workspaceType]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId]/[workspaceType].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runDeleteQuery({"workspaceId": workspaceId, "workspaceType": workspaceType});
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
    # + deptName - The value of the primary key field deptName
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get departments/[string deptNo]/[string deptName](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Department key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post departments(DepartmentInsert[] data) returns [string, string][]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select [inserted.deptNo, inserted.deptName];
    }

    # Update row in Department key space.
    #
    # + deptNo - The value of the primary key field deptNo
    # + deptName - The value of the primary key field deptName
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put departments/[string deptNo]/[string deptName](DepartmentUpdate value) returns Department|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runUpdateQuery({"deptNo": deptNo, "deptName": deptName}, value);
        return self->/departments/[deptNo]/[deptName].get();
    }

    # Delete row from Department key space.
    #
    # + deptNo - The value of the primary key field deptNo
    # + deptName - The value of the primary key field deptName
    # + return - The deleted record or an error
    isolated resource function delete departments/[string deptNo]/[string deptName]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo]/[deptName].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runDeleteQuery({"deptNo": deptNo, "deptName": deptName});
        return result;
    }

    # Get rows from OrderItem key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get orderitems(OrderItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from OrderItem key space.
    #
    # + orderId - The value of the primary key field orderId
    # + itemId - The value of the primary key field itemId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get orderitems/[string orderId]/[string itemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into OrderItem key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select [inserted.orderId, inserted.itemId];
    }

    # Update row in OrderItem key space.
    #
    # + orderId - The value of the primary key field orderId
    # + itemId - The value of the primary key field itemId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check redisClient.runUpdateQuery({"orderId": orderId, "itemId": itemId}, value);
        return self->/orderitems/[orderId]/[itemId].get();
    }

    # Delete row from OrderItem key space.
    #
    # + orderId - The value of the primary key field orderId
    # + itemId - The value of the primary key field itemId
    # + return - The deleted record or an error
    isolated resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderId]/[itemId].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check redisClient.runDeleteQuery({"orderId": orderId, "itemId": itemId});
        return result;
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
    # + firstName - The value of the primary key field firstName
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get employees/[string empNo]/[string firstName](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Employee key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post employees(EmployeeInsert[] data) returns [string, string][]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select [inserted.empNo, inserted.firstName];
    }

    # Update row in Employee key space.
    #
    # + empNo - The value of the primary key field empNo
    # + firstName - The value of the primary key field firstName
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put employees/[string empNo]/[string firstName](EmployeeUpdate value) returns Employee|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runUpdateQuery({"empNo": empNo, "firstName": firstName}, value);
        return self->/employees/[empNo]/[firstName].get();
    }

    # Delete row from Employee key space.
    #
    # + empNo - The value of the primary key field empNo
    # + firstName - The value of the primary key field firstName
    # + return - The deleted record or an error
    isolated resource function delete employees/[string empNo]/[string firstName]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo]/[firstName].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runDeleteQuery({"empNo": empNo, "firstName": firstName});
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

