// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;

const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";
const ORDER_ITEM = "orderitems";
const EMPLOYEE = "employees";
table<Workspace> key(workspaceId, workspaceType) workspaces = table [];
table<Building> key(buildingCode) buildings = table [];
table<Department> key(deptNo, deptName) departments = table [];
table<OrderItem> key(orderId, itemId) orderitems = table [];
table<Employee> key(empNo, firstName) employees = table [];

public client class Client {
    *persist:AbstractPersistClient;

    private final map<persist:InMemoryClient> persistClients;

    table<Workspace> key(workspaceId, workspaceType) workspaces = workspaces;
    table<Building> key(buildingCode) buildings = buildings;
    table<Department> key(deptNo, deptName) departments = departments;
    table<OrderItem> key(orderId, itemId) orderitems = orderitems;
    table<Employee> key(empNo, firstName) employees = employees;

    public function init() returns persist:Error? {
        final map<persist:TableMetadata> metadata = {
            [WORKSPACE] : {
                keyFields: ["workspaceId", "workspaceType"],
                query: self.queryWorkspaces,
                queryOne: self.queryOneWorkspaces,
                associationsMethods: {"employees": self.queryWorkspacesEmployees}
            },
            [BUILDING] : {
                keyFields: ["buildingCode"],
                query: self.queryBuildings,
                queryOne: self.queryOneBuildings,
                associationsMethods: {"workspaces": self.queryBuildingsWorkspaces}
            },
            [DEPARTMENT] : {
                keyFields: ["deptNo", "deptName"],
                query: self.queryDepartments,
                queryOne: self.queryOneDepartments,
                associationsMethods: {"employees": self.queryDepartmentsEmployees}
            },
            [ORDER_ITEM] : {
                keyFields: ["orderId", "itemId"],
                query: self.queryOrderitems,
                queryOne: self.queryOneOrderitems
            },
            [EMPLOYEE] : {
                keyFields: ["empNo", "firstName"],
                query: self.queryEmployees,
                queryOne: self.queryOneEmployees
            }
        };
        self.persistClients = {
            [WORKSPACE] : check new (metadata.get(WORKSPACE)),
            [BUILDING] : check new (metadata.get(BUILDING)),
            [DEPARTMENT] : check new (metadata.get(DEPARTMENT)),
            [ORDER_ITEM] : check new (metadata.get(ORDER_ITEM)),
            [EMPLOYEE] : check new (metadata.get(EMPLOYEE))
        };
    }

    isolated resource function get workspaces(WorkspaceTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get workspaces/[string workspaceId]/[string workspaceType](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post workspaces(WorkspaceInsert[] data) returns [string, string][]|persist:Error {
        [string, string][] keys = [];
        foreach WorkspaceInsert value in data {
            if self.workspaces.hasKey([value.workspaceId, value.workspaceType]) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + [value.workspaceId, value.workspaceType].toString());
            }
            self.workspaces.put(value);
            keys.push([value.workspaceId, value.workspaceType]);
        }
        return keys;
    }

    isolated resource function put workspaces/[string workspaceId]/[string workspaceType](WorkspaceUpdate value) returns Workspace|persist:Error {
        if !self.workspaces.hasKey([workspaceId, workspaceType]) {
            return <persist:InvalidKeyError>error("Not found: " + [workspaceId, workspaceType].toString());
        }
        Workspace workspace = self.workspaces.get([workspaceId, workspaceType]);
        foreach string key in value.keys() {
            workspace[key] = value[key];
        }
        self.workspaces.put(workspace);
        return workspace;
    }

    isolated resource function delete workspaces/[string workspaceId]/[string workspaceType]() returns Workspace|persist:Error {
        if !self.workspaces.hasKey([workspaceId, workspaceType]) {
            return <persist:InvalidKeyError>error("Not found: " + [workspaceId, workspaceType].toString());
        }
        return self.workspaces.remove([workspaceId, workspaceType]);
    }

    public function queryWorkspaces(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.workspaces
            outer join var building in self.buildings on ['object.locationBuildingCode] equals [building?.buildingCode]

            select persist:filterRecord({
                ...'object,
                "building": building
            }, fields);
    }

    public function queryOneWorkspaces(anydata key) returns record {}|persist:InvalidKeyError {
        from record {} 'object in self.workspaces
        where self.persistClients.get(WORKSPACE).getKey('object) == key
        outer join var building in self.buildings on ['object.locationBuildingCode] equals [building?.buildingCode]

        do {
            return {
                ...'object,
                "building": building
            };
        };
        return <persist:InvalidKeyError>error("Invalid key: " + key.toString());
    }

    isolated resource function get buildings(BuildingTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        string[] keys = [];
        foreach BuildingInsert value in data {
            if self.buildings.hasKey(value.buildingCode) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + value.buildingCode.toString());
            }
            self.buildings.put(value);
            keys.push(value.buildingCode);
        }
        return keys;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        if !self.buildings.hasKey(buildingCode) {
            return <persist:InvalidKeyError>error("Not found: " + buildingCode.toString());
        }
        Building building = self.buildings.get(buildingCode);
        foreach string key in value.keys() {
            building[key] = value[key];
        }
        self.buildings.put(building);
        return building;
    }

    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        if !self.buildings.hasKey(buildingCode) {
            return <persist:InvalidKeyError>error("Not found: " + buildingCode.toString());
        }
        return self.buildings.remove(buildingCode);
    }

    public function queryBuildings(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.buildings
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function queryOneBuildings(anydata key) returns record {}|persist:InvalidKeyError {
        from record {} 'object in self.buildings
        where self.persistClients.get(BUILDING).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:InvalidKeyError>error("Invalid key: " + key.toString());
    }

    isolated resource function get departments(DepartmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get departments/[string deptNo]/[string deptName](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post departments(DepartmentInsert[] data) returns [string, string][]|persist:Error {
        [string, string][] keys = [];
        foreach DepartmentInsert value in data {
            if self.departments.hasKey([value.deptNo, value.deptName]) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + [value.deptNo, value.deptName].toString());
            }
            self.departments.put(value);
            keys.push([value.deptNo, value.deptName]);
        }
        return keys;
    }

    isolated resource function put departments/[string deptNo]/[string deptName](DepartmentUpdate value) returns Department|persist:Error {
        if !self.departments.hasKey([deptNo, deptName]) {
            return <persist:InvalidKeyError>error("Not found: " + [deptNo, deptName].toString());
        }
        Department department = self.departments.get([deptNo, deptName]);
        foreach string key in value.keys() {
            department[key] = value[key];
        }
        self.departments.put(department);
        return department;
    }

    isolated resource function delete departments/[string deptNo]/[string deptName]() returns Department|persist:Error {
        if !self.departments.hasKey([deptNo, deptName]) {
            return <persist:InvalidKeyError>error("Not found: " + [deptNo, deptName].toString());
        }
        return self.departments.remove([deptNo, deptName]);
    }

    public function queryDepartments(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.departments
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function queryOneDepartments(anydata key) returns record {}|persist:InvalidKeyError {
        from record {} 'object in self.departments
        where self.persistClients.get(DEPARTMENT).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:InvalidKeyError>error("Invalid key: " + key.toString());
    }

    isolated resource function get orderitems(OrderItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get orderitems/[string orderId]/[string itemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        [string, string][] keys = [];
        foreach OrderItemInsert value in data {
            if self.orderitems.hasKey([value.orderId, value.itemId]) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + [value.orderId, value.itemId].toString());
            }
            self.orderitems.put(value);
            keys.push([value.orderId, value.itemId]);
        }
        return keys;
    }

    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        if !self.orderitems.hasKey([orderId, itemId]) {
            return <persist:InvalidKeyError>error("Not found: " + [orderId, itemId].toString());
        }
        OrderItem orderitem = self.orderitems.get([orderId, itemId]);
        foreach string key in value.keys() {
            orderitem[key] = value[key];
        }
        self.orderitems.put(orderitem);
        return orderitem;
    }

    isolated resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        if !self.orderitems.hasKey([orderId, itemId]) {
            return <persist:InvalidKeyError>error("Not found: " + [orderId, itemId].toString());
        }
        return self.orderitems.remove([orderId, itemId]);
    }

    public function queryOrderitems(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.orderitems
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function queryOneOrderitems(anydata key) returns record {}|persist:InvalidKeyError {
        from record {} 'object in self.orderitems
        where self.persistClients.get(ORDER_ITEM).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:InvalidKeyError>error("Invalid key: " + key.toString());
    }

    isolated resource function get employees(EmployeeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get employees/[string empNo]/[string firstName](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post employees(EmployeeInsert[] data) returns [string, string][]|persist:Error {
        [string, string][] keys = [];
        foreach EmployeeInsert value in data {
            if self.employees.hasKey([value.empNo, value.firstName]) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + [value.empNo, value.firstName].toString());
            }
            self.employees.put(value);
            keys.push([value.empNo, value.firstName]);
        }
        return keys;
    }

    isolated resource function put employees/[string empNo]/[string firstName](EmployeeUpdate value) returns Employee|persist:Error {
        if !self.employees.hasKey([empNo, firstName]) {
            return <persist:InvalidKeyError>error("Not found: " + [empNo, firstName].toString());
        }
        Employee employee = self.employees.get([empNo, firstName]);
        foreach string key in value.keys() {
            employee[key] = value[key];
        }
        self.employees.put(employee);
        return employee;
    }

    isolated resource function delete employees/[string empNo]/[string firstName]() returns Employee|persist:Error {
        if !self.employees.hasKey([empNo, firstName]) {
            return <persist:InvalidKeyError>error("Not found: " + [empNo, firstName].toString());
        }
        return self.employees.remove([empNo, firstName]);
    }

    public function queryEmployees(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.employees
            outer join var department in self.departments on ['object.departmentDeptNo, 'object.departmentDeptName] equals [department?.deptNo, department?.deptName]
            outer join var workspace in self.workspaces on ['object.workspaceWorkspaceId, 'object.workspaceWorkspaceType] equals [workspace?.workspaceId, workspace?.workspaceType]

            select persist:filterRecord({
                ...'object,
                "department": department,
                "workspace": workspace
            }, fields);
    }

    public function queryOneEmployees(anydata key) returns record {}|persist:InvalidKeyError {
        from record {} 'object in self.employees
        where self.persistClients.get(EMPLOYEE).getKey('object) == key
        outer join var department in self.departments on ['object.departmentDeptNo, 'object.departmentDeptName] equals [department?.deptNo, department?.deptName]
        outer join var workspace in self.workspaces on ['object.workspaceWorkspaceId, 'object.workspaceWorkspaceType] equals [workspace?.workspaceId, workspace?.workspaceType]

        do {
            return {
                ...'object,
                "department": department,
                "workspace": workspace
            };
        };
        return <persist:InvalidKeyError>error("Invalid key: " + key.toString());
    }

    public function queryDepartmentsEmployees(record {} value, string[] fields) returns record {}[] {
        return from record {} 'object in self.employees
            where 'object.departmentDeptNo == value["deptNo"] && 'object.departmentDeptName == value["deptName"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function queryBuildingsWorkspaces(record {} value, string[] fields) returns record {}[] {
        return from record {} 'object in self.workspaces
            where 'object.locationBuildingCode == value["buildingCode"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function queryWorkspacesEmployees(record {} value, string[] fields) returns record {}[] {
        return from record {} 'object in self.employees
            where 'object.workspaceWorkspaceId == value["workspaceId"] && 'object.workspaceWorkspaceType == value["workspaceType"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function close() returns persist:Error? {
        return ();
    }
}
