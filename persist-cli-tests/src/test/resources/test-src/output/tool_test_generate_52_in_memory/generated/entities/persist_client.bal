// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;

const EMPLOYEE = "employees";
const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";
table<Employee> key(empNo) employees = table [];
table<Workspace> key(workspaceId) workspaces = table [];
table<Building> key(buildingCode) buildings = table [];
table<Department> key(deptNo) departments = table [];

public client class Client {
    *persist:AbstractPersistClient;

    private final map<persist:InMemoryClient> persistClients;

    table<Employee> key(empNo) employees = employees;
    table<Workspace> key(workspaceId) workspaces = workspaces;
    table<Building> key(buildingCode) buildings = buildings;
    table<Department> key(deptNo) departments = departments;

    public function init() returns persist:Error? {
        final map<persist:TableMetadata> metadata = {
            [EMPLOYEE] : {
                keyFields: ["empNo"],
                query: self.queryEmployees,
                queryOne: self.queryOneEmployees
            },
            [WORKSPACE] : {
                keyFields: ["workspaceId"],
                query: self.queryWorkspaces,
                queryOne: self.queryOneWorkspaces
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
            }
        };
        self.persistClients = {
            [EMPLOYEE] : check new (metadata.get(EMPLOYEE)),
            [WORKSPACE] : check new (metadata.get(WORKSPACE)),
            [BUILDING] : check new (metadata.get(BUILDING)),
            [DEPARTMENT] : check new (metadata.get(DEPARTMENT))
        };
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
                return <persist:AlreadyExistsError>error("Duplicate key: " + value.empNo.toString());
            }
            self.employees.put(value.clone());
            keys.push(value.empNo);
        }
        return keys;
    }

    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        if !self.employees.hasKey(empNo) {
            return <persist:NotFoundError>error("Not found: " + empNo.toString());
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
            return <persist:NotFoundError>error("Not found: " + empNo.toString());
        }
        return self.employees.remove(empNo);
    }

    private function queryEmployees(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.employees
            outer join var department in self.departments on ['object.departmentDeptNo] equals [department?.deptNo]

            select persist:filterRecord({
                ...'object,
                "department": department
            }, fields);
    }

    private function queryOneEmployees(anydata key) returns record {}|persist:NotFoundError {
        from record {} 'object in self.employees
        where self.persistClients.get(EMPLOYEE).getKey('object) == key
        outer join var department in self.departments on ['object.departmentDeptNo] equals [department?.deptNo]

        do {
            return {
                ...'object,
                "department": department
            };
        };
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
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
                return <persist:AlreadyExistsError>error("Duplicate key: " + value.workspaceId.toString());
            }
            self.workspaces.put(value.clone());
            keys.push(value.workspaceId);
        }
        return keys;
    }

    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        if !self.workspaces.hasKey(workspaceId) {
            return <persist:NotFoundError>error("Not found: " + workspaceId.toString());
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
            return <persist:NotFoundError>error("Not found: " + workspaceId.toString());
        }
        return self.workspaces.remove(workspaceId);
    }

    private function queryWorkspaces(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.workspaces
            outer join var building in self.buildings on ['object.locationBuildingCode] equals [building?.buildingCode]
            outer join var employee in self.employees on ['object.workspaceEmpNo] equals [employee?.empNo]

            select persist:filterRecord({
                ...'object,
                "building": building,
                "employee": employee
            }, fields);
    }

    private function queryOneWorkspaces(anydata key) returns record {}|persist:NotFoundError {
        from record {} 'object in self.workspaces
        where self.persistClients.get(WORKSPACE).getKey('object) == key
        outer join var building in self.buildings on ['object.locationBuildingCode] equals [building?.buildingCode]
        outer join var employee in self.employees on ['object.workspaceEmpNo] equals [employee?.empNo]

        do {
            return {
                ...'object,
                "building": building,
                "employee": employee
            };
        };
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
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
                return <persist:AlreadyExistsError>error("Duplicate key: " + value.buildingCode.toString());
            }
            self.buildings.put(value.clone());
            keys.push(value.buildingCode);
        }
        return keys;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        if !self.buildings.hasKey(buildingCode) {
            return <persist:NotFoundError>error("Not found: " + buildingCode.toString());
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
            return <persist:NotFoundError>error("Not found: " + buildingCode.toString());
        }
        return self.buildings.remove(buildingCode);
    }

    private function queryBuildings(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.buildings
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneBuildings(anydata key) returns record {}|persist:NotFoundError {
        from record {} 'object in self.buildings
        where self.persistClients.get(BUILDING).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
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
                return <persist:AlreadyExistsError>error("Duplicate key: " + value.deptNo.toString());
            }
            self.departments.put(value.clone());
            keys.push(value.deptNo);
        }
        return keys;
    }

    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        if !self.departments.hasKey(deptNo) {
            return <persist:NotFoundError>error("Not found: " + deptNo.toString());
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
            return <persist:NotFoundError>error("Not found: " + deptNo.toString());
        }
        return self.departments.remove(deptNo);
    }

    private function queryDepartments(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.departments
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneDepartments(anydata key) returns record {}|persist:NotFoundError {
        from record {} 'object in self.departments
        where self.persistClients.get(DEPARTMENT).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
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

    public function close() returns persist:Error? {
        return ();
    }
}
