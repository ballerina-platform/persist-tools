// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/googleapis.sheets;
import ballerina/http;

const EMPLOYEE = "employees";
const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";
const ORDER_ITEM = "orderitems";

public client class Client {
    *persist:AbstractPersistClient;

    private final sheets:Client googleSheetClient;

    private final http:Client httpClient;

    private final map<persist:GoogleSheetsClient> persistClients;

    public function init() returns persist:Error? {
        final record {|persist:SheetMetadata...;|} metadata = {
            [EMPLOYEE] : {
                entityName: "Employee",
                tableName: "Employee",
                keyFields: ["empNo"],
                range: "A:H",
                query: self.queryEmployees,
                queryOne: self.queryOneEmployees,
                dataTypes: {
                    empNo: "string",
                    firstName: "string",
                    lastName: "string",
                    birthDate: "time:Date",
                    gender: "string",
                    hireDate: "time:Date",
                    departmentDeptNo: "string",
                    workspaceWorkspaceId: "string"
                },
                fieldMetadata: {
                    empNo: {columnName: "empNo", columnId: "A"},
                    firstName: {columnName: "firstName", columnId: "B"},
                    lastName: {columnName: "lastName", columnId: "C"},
                    birthDate: {columnName: "birthDate", columnId: "D"},
                    gender: {columnName: "gender", columnId: "E"},
                    hireDate: {columnName: "hireDate", columnId: "F"},
                    departmentDeptNo: {columnName: "departmentDeptNo", columnId: "G"},
                    workspaceWorkspaceId: {columnName: "workspaceWorkspaceId", columnId: "H"}
                }
            },
            [WORKSPACE] : {
                entityName: "Workspace",
                tableName: "Workspace",
                keyFields: ["workspaceId"],
                range: "A:C",
                query: self.queryWorkspaces,
                queryOne: self.queryOneWorkspaces,
                dataTypes: {
                    workspaceId: "string",
                    workspaceType: "string",
                    locationBuildingCode: "string"
                },
                fieldMetadata: {
                    workspaceId: {columnName: "workspaceId", columnId: "A"},
                    workspaceType: {columnName: "workspaceType", columnId: "B"},
                    locationBuildingCode: {columnName: "locationBuildingCode", columnId: "C"}
                },
                associationsMethods: {"employees": self.queryWorkspaceEmployees}
            },
            [BUILDING] : {
                entityName: "Building",
                tableName: "Building",
                keyFields: ["buildingCode"],
                range: "A:F",
                query: self.queryBuildings,
                queryOne: self.queryOneBuildings,
                dataTypes: {
                    buildingCode: "string",
                    city: "string",
                    state: "string",
                    country: "string",
                    postalCode: "string",
                    'type: "string"
                },
                fieldMetadata: {
                    buildingCode: {columnName: "buildingCode", columnId: "A"},
                    city: {columnName: "city", columnId: "B"},
                    state: {columnName: "state", columnId: "C"},
                    country: {columnName: "country", columnId: "D"},
                    postalCode: {columnName: "postalCode", columnId: "E"},
                    'type: {columnName: "type", columnId: "F"}
                },
                associationsMethods: {"workspaces": self.queryBuildingWorkspaces}
            },
            [DEPARTMENT] : {
                entityName: "Department",
                tableName: "Department",
                keyFields: ["deptNo"],
                range: "A:B",
                query: self.queryDepartments,
                queryOne: self.queryOneDepartments,
                dataTypes: {
                    deptNo: "string",
                    deptName: "string"
                },
                fieldMetadata: {
                    deptNo: {columnName: "deptNo", columnId: "A"},
                    deptName: {columnName: "deptName", columnId: "B"}
                },
                associationsMethods: {"employees": self.queryDepartmentEmployees}
            },
            [ORDER_ITEM] : {
                entityName: "OrderItem",
                tableName: "OrderItem",
                keyFields: ["orderId", "itemId"],
                range: "A:D",
                query: self.queryOrderitems,
                queryOne: self.queryOneOrderitems,
                dataTypes: {
                    orderId: "string",
                    itemId: "string",
                    quantity: "int",
                    notes: "string"
                },
                fieldMetadata: {
                    orderId: {columnName: "orderId", columnId: "A"},
                    itemId: {columnName: "itemId", columnId: "B"},
                    quantity: {columnName: "quantity", columnId: "C"},
                    notes: {columnName: "notes", columnId: "D"}
                }
            }
        };
        sheets:ConnectionConfig sheetsClientConfig = {
            auth: {
                clientId: clientId,
                clientSecret: clientSecret,
                refreshUrl: sheets:REFRESH_URL,
                refreshToken: refreshToken
            }
        };
        http:ClientConfiguration httpClientConfiguration = {
            auth: {
                clientId: clientId,
                clientSecret: clientSecret,
                refreshUrl: sheets:REFRESH_URL,
                refreshToken: refreshToken
            }
        };
        http:Client|error httpClient = new ("https://docs.google.com/spreadsheets", httpClientConfiguration);
        if httpClient is error {
            return <persist:Error>error(httpClient.message());
        }
        sheets:Client|error googleSheetClient = new (sheetsClientConfig);
        if googleSheetClient is error {
            return <persist:Error>error(googleSheetClient.message());
        }
        self.googleSheetClient = googleSheetClient;
        self.httpClient = httpClient;
        map<int> sheetIds = check persist:getSheetIds(self.googleSheetClient, metadata, spreadsheetId);
        self.persistClients = {
            [EMPLOYEE] : check new (self.googleSheetClient, self.httpClient, metadata.get(EMPLOYEE), spreadsheetId, sheetIds.get(EMPLOYEE)),
            [WORKSPACE] : check new (self.googleSheetClient, self.httpClient, metadata.get(WORKSPACE), spreadsheetId, sheetIds.get(WORKSPACE)),
            [BUILDING] : check new (self.googleSheetClient, self.httpClient, metadata.get(BUILDING), spreadsheetId, sheetIds.get(BUILDING)),
            [DEPARTMENT] : check new (self.googleSheetClient, self.httpClient, metadata.get(DEPARTMENT), spreadsheetId, sheetIds.get(DEPARTMENT)),
            [ORDER_ITEM] : check new (self.googleSheetClient, self.httpClient, metadata.get(ORDER_ITEM), spreadsheetId, sheetIds.get(ORDER_ITEM))
        };
    }

    isolated resource function get employees(EmployeeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get employees/[string empNo](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(EMPLOYEE).runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.empNo;
    }

    resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        _ = check self.persistClients.get(EMPLOYEE).runUpdateQuery(empNo, value);
        return self->/employees/[empNo].get();
    }

    resource function delete employees/[string empNo]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo].get();
        _ = check self.persistClients.get(EMPLOYEE).runDeleteQuery(empNo);
        return result;
    }

    private function queryEmployees(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Employee, persist:Error?> employeesStream = self.queryEmployeesStream();
        stream<Department, persist:Error?> departmentsStream = self.queryDepartmentsStream();
        stream<Workspace, persist:Error?> workspacesStream = self.queryWorkspacesStream();
        record {}[] outputArray = check from record {} 'object in employeesStream
            outer join var department in departmentsStream on ['object.departmentDeptNo] equals [department?.deptNo]
            outer join var workspace in workspacesStream on ['object.workspaceWorkspaceId] equals [workspace?.workspaceId]
            select persist:filterRecord({
                ...'object,
                "department": department,
                "workspace": workspace
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneEmployees(anydata key) returns record {}|persist:NotFoundError {
        stream<Employee, persist:Error?> employeesStream = self.queryEmployeesStream();
        stream<Department, persist:Error?> departmentsStream = self.queryDepartmentsStream();
        stream<Workspace, persist:Error?> workspacesStream = self.queryWorkspacesStream();
        error? unionResult = from record {} 'object in employeesStream
            where self.persistClients.get(EMPLOYEE).getKey('object) == key
            outer join var department in departmentsStream on ['object.departmentDeptNo] equals [department?.deptNo]
            outer join var workspace in workspacesStream on ['object.workspaceWorkspaceId] equals [workspace?.workspaceId]
            do {
                return {
                    ...'object,
                    "department": department,
                    "workspace": workspace
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryEmployeesStream(EmployeeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get workspaces(WorkspaceTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get workspaces/[string workspaceId](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(WORKSPACE).runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select inserted.workspaceId;
    }

    resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        _ = check self.persistClients.get(WORKSPACE).runUpdateQuery(workspaceId, value);
        return self->/workspaces/[workspaceId].get();
    }

    resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId].get();
        _ = check self.persistClients.get(WORKSPACE).runDeleteQuery(workspaceId);
        return result;
    }

    private function queryWorkspaces(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Workspace, persist:Error?> workspacesStream = self.queryWorkspacesStream();
        stream<Building, persist:Error?> buildingsStream = self.queryBuildingsStream();
        record {}[] outputArray = check from record {} 'object in workspacesStream
            outer join var location in buildingsStream on ['object.locationBuildingCode] equals [location?.buildingCode]
            select persist:filterRecord({
                ...'object,
                "location": location
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneWorkspaces(anydata key) returns record {}|persist:NotFoundError {
        stream<Workspace, persist:Error?> workspacesStream = self.queryWorkspacesStream();
        stream<Building, persist:Error?> buildingsStream = self.queryBuildingsStream();
        error? unionResult = from record {} 'object in workspacesStream
            where self.persistClients.get(WORKSPACE).getKey('object) == key
            outer join var location in buildingsStream on ['object.locationBuildingCode] equals [location?.buildingCode]
            do {
                return {
                    ...'object,
                    "location": location
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryWorkspacesStream(WorkspaceTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get buildings(BuildingTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(BUILDING).runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select inserted.buildingCode;
    }

    resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        _ = check self.persistClients.get(BUILDING).runUpdateQuery(buildingCode, value);
        return self->/buildings/[buildingCode].get();
    }

    resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        Building result = check self->/buildings/[buildingCode].get();
        _ = check self.persistClients.get(BUILDING).runDeleteQuery(buildingCode);
        return result;
    }

    private function queryBuildings(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Building, persist:Error?> buildingsStream = self.queryBuildingsStream();
        record {}[] outputArray = check from record {} 'object in buildingsStream
            select persist:filterRecord({
                ...'object
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneBuildings(anydata key) returns record {}|persist:NotFoundError {
        stream<Building, persist:Error?> buildingsStream = self.queryBuildingsStream();
        error? unionResult = from record {} 'object in buildingsStream
            where self.persistClients.get(BUILDING).getKey('object) == key
            do {
                return {
                    ...'object
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryBuildingsStream(BuildingTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get departments(DepartmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get departments/[string deptNo](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(DEPARTMENT).runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select inserted.deptNo;
    }

    resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        _ = check self.persistClients.get(DEPARTMENT).runUpdateQuery(deptNo, value);
        return self->/departments/[deptNo].get();
    }

    resource function delete departments/[string deptNo]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo].get();
        _ = check self.persistClients.get(DEPARTMENT).runDeleteQuery(deptNo);
        return result;
    }

    private function queryDepartments(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Department, persist:Error?> departmentsStream = self.queryDepartmentsStream();
        record {}[] outputArray = check from record {} 'object in departmentsStream
            select persist:filterRecord({
                ...'object
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneDepartments(anydata key) returns record {}|persist:NotFoundError {
        stream<Department, persist:Error?> departmentsStream = self.queryDepartmentsStream();
        error? unionResult = from record {} 'object in departmentsStream
            where self.persistClients.get(DEPARTMENT).getKey('object) == key
            do {
                return {
                    ...'object
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryDepartmentsStream(DepartmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get orderitems(OrderItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get orderitems/[string orderId]/[string itemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        _ = check self.persistClients.get(ORDER_ITEM).runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select [inserted.orderId, inserted.itemId];
    }

    resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        _ = check self.persistClients.get(ORDER_ITEM).runUpdateQuery({"orderId": orderId, "itemId": itemId}, value);
        return self->/orderitems/[orderId]/[itemId].get();
    }

    resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderId]/[itemId].get();
        _ = check self.persistClients.get(ORDER_ITEM).runDeleteQuery({"orderId": orderId, "itemId": itemId});
        return result;
    }

    private function queryOrderitems(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<OrderItem, persist:Error?> orderitemsStream = self.queryOrderitemsStream();
        record {}[] outputArray = check from record {} 'object in orderitemsStream
            select persist:filterRecord({
                ...'object
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneOrderitems(anydata key) returns record {}|persist:NotFoundError {
        stream<OrderItem, persist:Error?> orderitemsStream = self.queryOrderitemsStream();
        error? unionResult = from record {} 'object in orderitemsStream
            where self.persistClients.get(ORDER_ITEM).getKey('object) == key
            do {
                return {
                    ...'object
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryOrderitemsStream(OrderItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    private function queryBuildingWorkspaces(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Workspace, persist:Error?> workspacesStream = self.queryWorkspacesStream();
        return from record {} 'object in workspacesStream
            where 'object.locationBuildingCode == value["buildingCode"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryDepartmentEmployees(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Employee, persist:Error?> employeesStream = self.queryEmployeesStream();
        return from record {} 'object in employeesStream
            where 'object.departmentDeptNo == value["deptNo"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryWorkspaceEmployees(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Employee, persist:Error?> employeesStream = self.queryEmployeesStream();
        return from record {} 'object in employeesStream
            where 'object.workspaceWorkspaceId == value["workspaceId"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public isolated function close() returns persist:Error? {
        return ();
    }
}

