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
table<Workspace> key(workspaceId) workspaces = table [];
table<Building> key(buildingCode) buildings = table [];
table<Department> key(deptNo) departments = table [];
table<OrderItem> key(orderId, itemId) orderitems = table [];
table<Employee> key(empNo) employees = table [];

public client class Client {
    *persist:AbstractPersistClient;

    private final map<persist:InMemoryClient> persistClients;

    table<Workspace> key(workspaceId) workspaces = workspaces;
    table<Building> key(buildingCode) buildings = buildings;
    table<Department> key(deptNo) departments = departments;
    table<OrderItem> key(orderId, itemId) orderitems = orderitems;
    table<Employee> key(empNo) employees = employees;

    public function init() returns persist:Error? {
        final map<persist:TableMetadata> metadata = {
            [WORKSPACE] : {
                keyFields: ["workspaceId"],
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
                keyFields: ["deptNo"],
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
                keyFields: ["empNo"],
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

    isolated resource function get workspaces/[string workspaceId](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        string[] keys = [];
        foreach WorkspaceInsert value in data {
            if self.workspaces.hasKey(value.workspaceId) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + value.workspaceId.toString());
            }
            self.workspaces.put(value.clone());
            keys.push(value.workspaceId);
        }
        return keys;
    }

    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        if !self.workspaces.hasKey(workspaceId) {
            return <persist:InvalidKeyError>error("Not found: " + workspaceId.toString());
        }
        Workspace workspace = self.workspaces.get(workspaceId);
        foreach var [k, v] in value.entries() {
            workspace[k] = v;
        }
        self.workspaces.put(workspace);
        return workspace;
    }

    isolated resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        if !self.workspaces.hasKey(workspaceId) {
            return <persist:InvalidKeyError>error("Not found: " + workspaceId.toString());
        }
        return self.workspaces.remove(workspaceId);
    }

    private function queryWorkspaces(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.workspaces
            outer join var building in self.buildings on ['object.locationBuildingCode] equals [building?.buildingCode]

            select persist:filterRecord({
                ...'object,
                "building": building
            }, fields);
    }

    private function queryOneWorkspaces(anydata key) returns record {}|persist:InvalidKeyError {
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
            self.buildings.put(value.clone());
            keys.push(value.buildingCode);
        }
        return keys;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        if !self.buildings.hasKey(buildingCode) {
            return <persist:InvalidKeyError>error("Not found: " + buildingCode.toString());
        }
        Building building = self.buildings.get(buildingCode);
        foreach var [k, v] in value.entries() {
            building[k] = v;
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

    private function queryBuildings(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.buildings
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneBuildings(anydata key) returns record {}|persist:InvalidKeyError {
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

    isolated resource function get departments/[string deptNo](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        string[] keys = [];
        foreach DepartmentInsert value in data {
            if self.departments.hasKey(value.deptNo) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + value.deptNo.toString());
            }
            self.departments.put(value.clone());
            keys.push(value.deptNo);
        }
        return keys;
    }

    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        if !self.departments.hasKey(deptNo) {
            return <persist:InvalidKeyError>error("Not found: " + deptNo.toString());
        }
        Department department = self.departments.get(deptNo);
        foreach var [k, v] in value.entries() {
            department[k] = v;
        }
        self.departments.put(department);
        return department;
    }

    isolated resource function delete departments/[string deptNo]() returns Department|persist:Error {
        if !self.departments.hasKey(deptNo) {
            return <persist:InvalidKeyError>error("Not found: " + deptNo.toString());
        }
        return self.departments.remove(deptNo);
    }

    private function queryDepartments(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.departments
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneDepartments(anydata key) returns record {}|persist:InvalidKeyError {
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
            self.orderitems.put(value.clone());
            keys.push([value.orderId, value.itemId]);
        }
        return keys;
    }

    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        if !self.orderitems.hasKey([orderId, itemId]) {
            return <persist:InvalidKeyError>error("Not found: " + [orderId, itemId].toString());
        }
        OrderItem orderitem = self.orderitems.get([orderId, itemId]);
        foreach var [k, v] in value.entries() {
            orderitem[k] = v;
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

    private function queryOrderitems(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.orderitems
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneOrderitems(anydata key) returns record {}|persist:InvalidKeyError {
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

    isolated resource function get employees/[string empNo](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        string[] keys = [];
        foreach EmployeeInsert value in data {
            if self.employees.hasKey(value.empNo) {
                return <persist:DuplicateKeyError>error("Duplicate key: " + value.empNo.toString());
            }
            self.employees.put(value.clone());
            keys.push(value.empNo);
        }
        return keys;
    }

    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        if !self.employees.hasKey(empNo) {
            return <persist:InvalidKeyError>error("Not found: " + empNo.toString());
        }
        Employee employee = self.employees.get(empNo);
        foreach var [k, v] in value.entries() {
            employee[k] = v;
        }
        self.employees.put(employee);
        return employee;
    }

    isolated resource function delete employees/[string empNo]() returns Employee|persist:Error {
        if !self.employees.hasKey(empNo) {
            return <persist:InvalidKeyError>error("Not found: " + empNo.toString());
        }
        return self.employees.remove(empNo);
    }

    private function queryEmployees(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.employees
            outer join var department in self.departments on ['object.departmentDeptNo] equals [department?.deptNo]
            outer join var workspace in self.workspaces on ['object.workspaceWorkspaceId] equals [workspace?.workspaceId]

            select persist:filterRecord({
                ...'object,
                "department": department,
                "workspace": workspace
            }, fields);
    }

    private function queryOneEmployees(anydata key) returns record {}|persist:InvalidKeyError {
        from record {} 'object in self.employees
        where self.persistClients.get(EMPLOYEE).getKey('object) == key
        outer join var department in self.departments on ['object.departmentDeptNo] equals [department?.deptNo]
        outer join var workspace in self.workspaces on ['object.workspaceWorkspaceId] equals [workspace?.workspaceId]

        do {
            return {
                ...'object,
                "department": department,
                "workspace": workspace
            };
        };
        return <persist:InvalidKeyError>error("Invalid key: " + key.toString());
    }

    private function queryDepartmentsEmployees(record {} value, string[] fields) returns record {}[] {
        return from record {} 'object in self.employees
            where 'object.departmentDeptNo == value["deptNo"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryBuildingsWorkspaces(record {} value, string[] fields) returns record {}[] {
        return from record {} 'object in self.workspaces
            where 'object.locationBuildingCode == value["buildingCode"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryWorkspacesEmployees(record {} value, string[] fields) returns record {}[] {
        return from record {} 'object in self.employees
            where 'object.workspaceWorkspaceId == value["workspaceId"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public function close() returns persist:Error? {
        return ();
    }
}

