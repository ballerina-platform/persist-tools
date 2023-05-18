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
final isolated table<Workspace> key(workspaceId, workspaceType) workspacesTable = table [];
final isolated table<Building> key(buildingCode) buildingsTable = table [];
final isolated table<Department> key(deptNo, deptName) departmentsTable = table [];
final isolated table<OrderItem> key(orderId, itemId) orderitemsTable = table [];
final isolated table<Employee> key(empNo, firstName) employeesTable = table [];

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final map<persist:InMemoryClient> persistClients;

    public isolated function init() returns persist:Error? {
        final map<persist:TableMetadata> metadata = {
            [WORKSPACE] : {
                keyFields: ["workspaceId", "workspaceType"],
                query: queryWorkspaces,
                queryOne: queryOneWorkspaces,
                associationsMethods: {"employees": queryWorkspaceEmployees}
            },
            [BUILDING] : {
                keyFields: ["buildingCode"],
                query: queryBuildings,
                queryOne: queryOneBuildings,
                associationsMethods: {"workspaces": queryBuildingWorkspaces}
            },
            [DEPARTMENT] : {
                keyFields: ["deptNo", "deptName"],
                query: queryDepartments,
                queryOne: queryOneDepartments,
                associationsMethods: {"employees": queryDepartmentEmployees}
            },
            [ORDER_ITEM] : {
                keyFields: ["orderId", "itemId"],
                query: queryOrderitems,
                queryOne: queryOneOrderitems
            },
            [EMPLOYEE] : {
                keyFields: ["empNo", "firstName"],
                query: queryEmployees,
                queryOne: queryOneEmployees
            }
        };
        self.persistClients = {
            [WORKSPACE] : check new (metadata.get(WORKSPACE).cloneReadOnly()),
            [BUILDING] : check new (metadata.get(BUILDING).cloneReadOnly()),
            [DEPARTMENT] : check new (metadata.get(DEPARTMENT).cloneReadOnly()),
            [ORDER_ITEM] : check new (metadata.get(ORDER_ITEM).cloneReadOnly()),
            [EMPLOYEE] : check new (metadata.get(EMPLOYEE).cloneReadOnly())
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
            lock {
                if workspacesTable.hasKey([value.workspaceId, value.workspaceType]) {
                    return <persist:AlreadyExistsError>error("Duplicate key: " + [value.workspaceId, value.workspaceType].toString());
                }
                workspacesTable.put(value.clone());
            }
            keys.push([value.workspaceId, value.workspaceType]);
        }
        return keys;
    }

    isolated resource function put workspaces/[string workspaceId]/[string workspaceType](WorkspaceUpdate value) returns Workspace|persist:Error {
        lock {
            if !workspacesTable.hasKey([workspaceId, workspaceType]) {
                return <persist:NotFoundError>error("Not found: " + [workspaceId, workspaceType].toString());
            }
            Workspace workspace = workspacesTable.get([workspaceId, workspaceType]);
            foreach var [k, v] in value.clone().entries() {
                workspace[k] = v;
            }
            workspacesTable.put(workspace);
            return workspace.clone();
        }
    }

    isolated resource function delete workspaces/[string workspaceId]/[string workspaceType]() returns Workspace|persist:Error {
        lock {
            if !workspacesTable.hasKey([workspaceId, workspaceType]) {
                return <persist:NotFoundError>error("Not found: " + [workspaceId, workspaceType].toString());
            }
            return workspacesTable.remove([workspaceId, workspaceType]).clone();
        }
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
            lock {
                if buildingsTable.hasKey(value.buildingCode) {
                    return <persist:AlreadyExistsError>error("Duplicate key: " + value.buildingCode.toString());
                }
                buildingsTable.put(value.clone());
            }
            keys.push(value.buildingCode);
        }
        return keys;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        lock {
            if !buildingsTable.hasKey(buildingCode) {
                return <persist:NotFoundError>error("Not found: " + buildingCode.toString());
            }
            Building building = buildingsTable.get(buildingCode);
            foreach var [k, v] in value.clone().entries() {
                building[k] = v;
            }
            buildingsTable.put(building);
            return building.clone();
        }
    }

    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        lock {
            if !buildingsTable.hasKey(buildingCode) {
                return <persist:NotFoundError>error("Not found: " + buildingCode.toString());
            }
            return buildingsTable.remove(buildingCode).clone();
        }
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
            lock {
                if departmentsTable.hasKey([value.deptNo, value.deptName]) {
                    return <persist:AlreadyExistsError>error("Duplicate key: " + [value.deptNo, value.deptName].toString());
                }
                departmentsTable.put(value.clone());
            }
            keys.push([value.deptNo, value.deptName]);
        }
        return keys;
    }

    isolated resource function put departments/[string deptNo]/[string deptName](DepartmentUpdate value) returns Department|persist:Error {
        lock {
            if !departmentsTable.hasKey([deptNo, deptName]) {
                return <persist:NotFoundError>error("Not found: " + [deptNo, deptName].toString());
            }
            Department department = departmentsTable.get([deptNo, deptName]);
            foreach var [k, v] in value.clone().entries() {
                department[k] = v;
            }
            departmentsTable.put(department);
            return department.clone();
        }
    }

    isolated resource function delete departments/[string deptNo]/[string deptName]() returns Department|persist:Error {
        lock {
            if !departmentsTable.hasKey([deptNo, deptName]) {
                return <persist:NotFoundError>error("Not found: " + [deptNo, deptName].toString());
            }
            return departmentsTable.remove([deptNo, deptName]).clone();
        }
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
            lock {
                if orderitemsTable.hasKey([value.orderId, value.itemId]) {
                    return <persist:AlreadyExistsError>error("Duplicate key: " + [value.orderId, value.itemId].toString());
                }
                orderitemsTable.put(value.clone());
            }
            keys.push([value.orderId, value.itemId]);
        }
        return keys;
    }

    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        lock {
            if !orderitemsTable.hasKey([orderId, itemId]) {
                return <persist:NotFoundError>error("Not found: " + [orderId, itemId].toString());
            }
            OrderItem orderitem = orderitemsTable.get([orderId, itemId]);
            foreach var [k, v] in value.clone().entries() {
                orderitem[k] = v;
            }
            orderitemsTable.put(orderitem);
            return orderitem.clone();
        }
    }

    isolated resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        lock {
            if !orderitemsTable.hasKey([orderId, itemId]) {
                return <persist:NotFoundError>error("Not found: " + [orderId, itemId].toString());
            }
            return orderitemsTable.remove([orderId, itemId]).clone();
        }
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
            lock {
                if employeesTable.hasKey([value.empNo, value.firstName]) {
                    return <persist:AlreadyExistsError>error("Duplicate key: " + [value.empNo, value.firstName].toString());
                }
                employeesTable.put(value.clone());
            }
            keys.push([value.empNo, value.firstName]);
        }
        return keys;
    }

    isolated resource function put employees/[string empNo]/[string firstName](EmployeeUpdate value) returns Employee|persist:Error {
        lock {
            if !employeesTable.hasKey([empNo, firstName]) {
                return <persist:NotFoundError>error("Not found: " + [empNo, firstName].toString());
            }
            Employee employee = employeesTable.get([empNo, firstName]);
            foreach var [k, v] in value.clone().entries() {
                employee[k] = v;
            }
            employeesTable.put(employee);
            return employee.clone();
        }
    }

    isolated resource function delete employees/[string empNo]/[string firstName]() returns Employee|persist:Error {
        lock {
            if !employeesTable.hasKey([empNo, firstName]) {
                return <persist:NotFoundError>error("Not found: " + [empNo, firstName].toString());
            }
            return employeesTable.remove([empNo, firstName]).clone();
        }
    }

    public isolated function close() returns persist:Error? {
        return ();
    }
}

isolated function queryWorkspaces(string[] fields) returns stream<record {}, persist:Error?> {
    table<Workspace> key(workspaceId, workspaceType) workspacesClonedTable;
    lock {
        workspacesClonedTable = workspacesTable.clone();
    }
    table<Building> key(buildingCode) buildingsClonedTable;
    lock {
        buildingsClonedTable = buildingsTable.clone();
    }
    return from record {} 'object in workspacesClonedTable
        outer join var location in buildingsClonedTable on ['object.locationBuildingCode] equals [location?.buildingCode]
        select persist:filterRecord({
            ...'object,
            "location": location
        }, fields);
}

isolated function queryOneWorkspaces(anydata key) returns record {}|persist:NotFoundError {
    table<Workspace> key(workspaceId, workspaceType) workspacesClonedTable;
    lock {
        workspacesClonedTable = workspacesTable.clone();
    }
    table<Building> key(buildingCode) buildingsClonedTable;
    lock {
        buildingsClonedTable = buildingsTable.clone();
    }
    from record {} 'object in workspacesClonedTable
    where persist:getKey('object, ["workspaceId", "workspaceType"]) == key
    outer join var location in buildingsClonedTable on ['object.locationBuildingCode] equals [location?.buildingCode]
    do {
        return {
            ...'object,
            "location": location
        };
    };
    return <persist:NotFoundError>error("Invalid key: " + key.toString());
}

isolated function queryBuildings(string[] fields) returns stream<record {}, persist:Error?> {
    table<Building> key(buildingCode) buildingsClonedTable;
    lock {
        buildingsClonedTable = buildingsTable.clone();
    }
    return from record {} 'object in buildingsClonedTable
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryOneBuildings(anydata key) returns record {}|persist:NotFoundError {
    table<Building> key(buildingCode) buildingsClonedTable;
    lock {
        buildingsClonedTable = buildingsTable.clone();
    }
    from record {} 'object in buildingsClonedTable
    where persist:getKey('object, ["buildingCode"]) == key
    do {
        return {
            ...'object
        };
    };
    return <persist:NotFoundError>error("Invalid key: " + key.toString());
}

isolated function queryDepartments(string[] fields) returns stream<record {}, persist:Error?> {
    table<Department> key(deptNo, deptName) departmentsClonedTable;
    lock {
        departmentsClonedTable = departmentsTable.clone();
    }
    return from record {} 'object in departmentsClonedTable
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryOneDepartments(anydata key) returns record {}|persist:NotFoundError {
    table<Department> key(deptNo, deptName) departmentsClonedTable;
    lock {
        departmentsClonedTable = departmentsTable.clone();
    }
    from record {} 'object in departmentsClonedTable
    where persist:getKey('object, ["deptNo", "deptName"]) == key
    do {
        return {
            ...'object
        };
    };
    return <persist:NotFoundError>error("Invalid key: " + key.toString());
}

isolated function queryOrderitems(string[] fields) returns stream<record {}, persist:Error?> {
    table<OrderItem> key(orderId, itemId) orderitemsClonedTable;
    lock {
        orderitemsClonedTable = orderitemsTable.clone();
    }
    return from record {} 'object in orderitemsClonedTable
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryOneOrderitems(anydata key) returns record {}|persist:NotFoundError {
    table<OrderItem> key(orderId, itemId) orderitemsClonedTable;
    lock {
        orderitemsClonedTable = orderitemsTable.clone();
    }
    from record {} 'object in orderitemsClonedTable
    where persist:getKey('object, ["orderId", "itemId"]) == key
    do {
        return {
            ...'object
        };
    };
    return <persist:NotFoundError>error("Invalid key: " + key.toString());
}

isolated function queryEmployees(string[] fields) returns stream<record {}, persist:Error?> {
    table<Employee> key(empNo, firstName) employeesClonedTable;
    lock {
        employeesClonedTable = employeesTable.clone();
    }
    table<Department> key(deptNo, deptName) departmentsClonedTable;
    lock {
        departmentsClonedTable = departmentsTable.clone();
    }
    table<Workspace> key(workspaceId, workspaceType) workspacesClonedTable;
    lock {
        workspacesClonedTable = workspacesTable.clone();
    }
    return from record {} 'object in employeesClonedTable
        outer join var department in departmentsClonedTable on ['object.departmentDeptNo, 'object.departmentDeptName] equals [department?.deptNo, department?.deptName]
        outer join var workspace in workspacesClonedTable on ['object.workspaceWorkspaceId, 'object.workspaceWorkspaceType] equals [workspace?.workspaceId, workspace?.workspaceType]
        select persist:filterRecord({
            ...'object,
            "department": department,
            "workspace": workspace
        }, fields);
}

isolated function queryOneEmployees(anydata key) returns record {}|persist:NotFoundError {
    table<Employee> key(empNo, firstName) employeesClonedTable;
    lock {
        employeesClonedTable = employeesTable.clone();
    }
    table<Department> key(deptNo, deptName) departmentsClonedTable;
    lock {
        departmentsClonedTable = departmentsTable.clone();
    }
    table<Workspace> key(workspaceId, workspaceType) workspacesClonedTable;
    lock {
        workspacesClonedTable = workspacesTable.clone();
    }
    from record {} 'object in employeesClonedTable
    where persist:getKey('object, ["empNo", "firstName"]) == key
    outer join var department in departmentsClonedTable on ['object.departmentDeptNo, 'object.departmentDeptName] equals [department?.deptNo, department?.deptName]
    outer join var workspace in workspacesClonedTable on ['object.workspaceWorkspaceId, 'object.workspaceWorkspaceType] equals [workspace?.workspaceId, workspace?.workspaceType]
    do {
        return {
            ...'object,
            "department": department,
            "workspace": workspace
        };
    };
    return <persist:NotFoundError>error("Invalid key: " + key.toString());
}

isolated function queryWorkspaceEmployees(record {} value, string[] fields) returns record {}[] {
    table<Employee> key(empNo, firstName) employeesClonedTable;
    lock {
        employeesClonedTable = employeesTable.clone();
    }
    return from record {} 'object in employeesClonedTable
        where 'object.workspaceWorkspaceId == value["workspaceId"] && 'object.workspaceWorkspaceType == value["workspaceType"]
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryBuildingWorkspaces(record {} value, string[] fields) returns record {}[] {
    table<Workspace> key(workspaceId, workspaceType) workspacesClonedTable;
    lock {
        workspacesClonedTable = workspacesTable.clone();
    }
    return from record {} 'object in workspacesClonedTable
        where 'object.locationBuildingCode == value["buildingCode"]
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryDepartmentEmployees(record {} value, string[] fields) returns record {}[] {
    table<Employee> key(empNo, firstName) employeesClonedTable;
    lock {
        employeesClonedTable = employeesTable.clone();
    }
    return from record {} 'object in employeesClonedTable
        where 'object.departmentDeptNo == value["deptNo"] && 'object.departmentDeptName == value["deptName"]
        select persist:filterRecord({
            ...'object
        }, fields);
}

