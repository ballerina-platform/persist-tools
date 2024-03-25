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
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId", refFieldDataType: predis:STRING}}
            },
            keyFields: ["workspaceId"],
            refMetadata: {
                location: {entity: Building, fieldName: "location", refCollection: "Building", refMetaDataKey: "workspaces", refFields: ["buildingCode"], joinFields: ["locationBuildingCode"], 'type: predis:ONE_TO_MANY},
                employees: {entity: Employee, fieldName: "employees", refCollection: "Employee", refFields: ["workspaceWorkspaceId"], joinFields: ["workspaceId"], 'type: predis:MANY_TO_ONE}
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
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo", refFieldDataType: predis:STRING}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName", refFieldDataType: predis:STRING}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName", refFieldDataType: predis:STRING}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate", refFieldDataType: predis:DATE}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender", refFieldDataType: predis:STRING}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate", refFieldDataType: predis:DATE}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo", refFieldDataType: predis:STRING}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId", refFieldDataType: predis:STRING}}
            },
            keyFields: ["deptNo"],
            refMetadata: {employees: {entity: Employee, fieldName: "employees", refCollection: "Employee", refFields: ["departmentDeptNo"], joinFields: ["deptNo"], 'type: predis:MANY_TO_ONE}}
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
                workspaceWorkspaceId: {fieldName: "workspaceWorkspaceId", fieldDataType: predis:STRING},
                "department.deptNo": {relation: {entityName: "department", refField: "deptNo", refFieldDataType: predis:STRING}},
                "department.deptName": {relation: {entityName: "department", refField: "deptName", refFieldDataType: predis:STRING}},
                "workspace.workspaceId": {relation: {entityName: "workspace", refField: "workspaceId", refFieldDataType: predis:STRING}},
                "workspace.workspaceType": {relation: {entityName: "workspace", refField: "workspaceType", refFieldDataType: predis:STRING}},
                "workspace.locationBuildingCode": {relation: {entityName: "workspace", refField: "locationBuildingCode", refFieldDataType: predis:STRING}}
            },
            keyFields: ["empNo"],
            refMetadata: {
                department: {entity: Department, fieldName: "department", refCollection: "Department", refMetaDataKey: "employees", refFields: ["deptNo"], joinFields: ["departmentDeptNo"], 'type: predis:ONE_TO_MANY},
                workspace: {entity: Workspace, fieldName: "workspace", refCollection: "Workspace", refMetaDataKey: "employees", refFields: ["workspaceId"], joinFields: ["workspaceWorkspaceId"], 'type: predis:ONE_TO_MANY}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        redis:Client|error dbClient = new (redis);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [WORKSPACE]: check new (dbClient, self.metadata.get(WORKSPACE)),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING)),
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT)),
            [ORDER_ITEM]: check new (dbClient, self.metadata.get(ORDER_ITEM)),
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE))
        };
    }

    isolated resource function get workspaces(WorkspaceTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get workspaces/[string workspaceId](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select inserted.workspaceId;
    }

    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runUpdateQuery(workspaceId, value);
        return self->/workspaces/[workspaceId].get();
    }

    isolated resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(WORKSPACE);
        }
        _ = check redisClient.runDeleteQuery(workspaceId);
        return result;
    }

    isolated resource function get buildings(BuildingTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BUILDING);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select inserted.buildingCode;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BUILDING);
        }
        _ = check redisClient.runUpdateQuery(buildingCode, value);
        return self->/buildings/[buildingCode].get();
    }

    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        Building result = check self->/buildings/[buildingCode].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BUILDING);
        }
        _ = check redisClient.runDeleteQuery(buildingCode);
        return result;
    }

    isolated resource function get departments(DepartmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get departments/[string deptNo](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select inserted.deptNo;
    }

    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runUpdateQuery(deptNo, value);
        return self->/departments/[deptNo].get();
    }

    isolated resource function delete departments/[string deptNo]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check redisClient.runDeleteQuery(deptNo);
        return result;
    }

    isolated resource function get orderitems(OrderItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get orderitems/[string orderId]/[string itemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select [inserted.orderId, inserted.itemId];
    }

    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check redisClient.runUpdateQuery({"orderId": orderId, "itemId": itemId}, value);
        return self->/orderitems/[orderId]/[itemId].get();
    }

    isolated resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderId]/[itemId].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check redisClient.runDeleteQuery({"orderId": orderId, "itemId": itemId});
        return result;
    }

    isolated resource function get employees(EmployeeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get employees/[string empNo](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.empNo;
    }

    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runUpdateQuery(empNo, value);
        return self->/employees/[empNo].get();
    }

    isolated resource function delete employees/[string empNo]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check redisClient.runDeleteQuery(empNo);
        return result;
    }

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}
