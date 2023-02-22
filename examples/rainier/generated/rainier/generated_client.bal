// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for rainier.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerina/time;
import ballerinax/mysql;

const BUILDINGS = "buildings";
const DEPARTMENTS = "departments";
const EMPLOYEES = "employees";
const ORDER_ITEMS = "orderitems";
const WORKSPACES = "workspaces";

public client class RainierClient {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "buildings": {
            entityName: "Building",
            tableName: `Building`,
            fieldMetadata: {
                buildingCode: {columnName: "buildingCode", 'type: string},
                city: {columnName: "city", 'type: string},
                state: {columnName: "state", 'type: string},
                country: {columnName: "country", 'type: string},
                postalCode: {columnName: "postalCode", 'type: string},
                'type: {columnName: "type", 'type: string}
            },
            keyFields: ["buildingCode"]
        },
        "departments": {
            entityName: "Department",
            tableName: `Department`,
            fieldMetadata: {
                deptNo: {columnName: "deptNo", 'type: string},
                deptName: {columnName: "deptName", 'type: string}
            },
            keyFields: ["deptNo"]
        },
        "employees": {
            entityName: "Employee",
            tableName: `Employee`,
            fieldMetadata: {
                empNo: {columnName: "empNo", 'type: string},
                firstName: {columnName: "firstName", 'type: string},
                lastName: {columnName: "lastName", 'type: string},
                birthDate: {columnName: "birthDate", 'type: time:Date},
                gender: {columnName: "gender", 'type: string},
                hireDate: {columnName: "hireDate", 'type: time:Date},
                departmentDeptNo: {columnName: "departmentDeptNo", 'type: string},
                workspaceWorkspaceId: {columnName: "workspaceWorkspaceId", 'type: string}
            },
            keyFields: ["empNo"]
        },
        "orderitems": {
            entityName: "OrderItem",
            tableName: `OrderItem`,
            fieldMetadata: {
                orderId: {columnName: "orderId", 'type: string},
                itemId: {columnName: "itemId", 'type: string},
                quantity: {columnName: "quantity", 'type: int},
                notes: {columnName: "notes", 'type: string}
            },
            keyFields: ["orderId", "itemId"]
        },
        "workspaces": {
            entityName: "Workspace",
            tableName: `Workspace`,
            fieldMetadata: {
                workspaceId: {columnName: "workspaceId", 'type: string},
                workspaceType: {columnName: "workspaceType", 'type: string},
                buildingBuildingCode: {columnName: "buildingBuildingCode", 'type: string}
            },
            keyFields: ["workspaceId"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            buildings: check new (self.dbClient, self.metadata.get(BUILDINGS)),
            departments: check new (self.dbClient, self.metadata.get(DEPARTMENTS)),
            employees: check new (self.dbClient, self.metadata.get(EMPLOYEES)),
            orderitems: check new (self.dbClient, self.metadata.get(ORDER_ITEMS)),
            workspaces: check new (self.dbClient, self.metadata.get(WORKSPACES))
        };
    }

    isolated resource function get buildings() returns stream<Building, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(BUILDINGS).runReadQuery(Building);
        if result is persist:Error {
            return new stream<Building, persist:Error?>(new BuildingStream((), result));
        } else {
            return new stream<Building, persist:Error?>(new BuildingStream(result));
        }
    }

    isolated resource function get buildings/[string buildingCode]() returns Building|persist:Error {
        Building|error result = (check self.persistClients.get(BUILDINGS).runReadByKeyQuery(Building, buildingCode)).cloneWithType(Building);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(BUILDINGS).runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select inserted.buildingCode;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        _ = check self.persistClients.get(BUILDINGS).runUpdateQuery(buildingCode, value);
        return self->/buildings/[buildingCode].get();
    }

    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        Building result = check self->/buildings/[buildingCode].get();
        _ = check self.persistClients.get(BUILDINGS).runDeleteQuery(buildingCode);
        return result;
    }

    isolated resource function get departments() returns stream<Department, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(DEPARTMENTS).runReadQuery(Department);
        if result is persist:Error {
            return new stream<Department, persist:Error?>(new DepartmentStream((), result));
        } else {
            return new stream<Department, persist:Error?>(new DepartmentStream(result));
        }
    }

    isolated resource function get departments/[string deptNo]() returns Department|persist:Error {
        Department|error result = (check self.persistClients.get(DEPARTMENTS).runReadByKeyQuery(Department, deptNo)).cloneWithType(Department);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(DEPARTMENTS).runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select inserted.deptNo;
    }

    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        _ = check self.persistClients.get(DEPARTMENTS).runUpdateQuery(deptNo, value);
        return self->/departments/[deptNo].get();
    }

    isolated resource function delete departments/[string deptNo]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo].get();
        _ = check self.persistClients.get(DEPARTMENTS).runDeleteQuery(deptNo);
        return result;
    }

    isolated resource function get employees() returns stream<Employee, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(EMPLOYEES).runReadQuery(Employee);
        if result is persist:Error {
            return new stream<Employee, persist:Error?>(new EmployeeStream((), result));
        } else {
            return new stream<Employee, persist:Error?>(new EmployeeStream(result));
        }
    }

    isolated resource function get employees/[string empNo]() returns Employee|persist:Error {
        Employee|error result = (check self.persistClients.get(EMPLOYEES).runReadByKeyQuery(Employee, empNo)).cloneWithType(Employee);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(EMPLOYEES).runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.empNo;
    }

    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        _ = check self.persistClients.get(EMPLOYEES).runUpdateQuery(empNo, value);
        return self->/employees/[empNo].get();
    }

    isolated resource function delete employees/[string empNo]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo].get();
        _ = check self.persistClients.get(EMPLOYEES).runDeleteQuery(empNo);
        return result;
    }

    isolated resource function get orderitems() returns stream<OrderItem, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(ORDER_ITEMS).runReadQuery(OrderItem);
        if result is persist:Error {
            return new stream<OrderItem, persist:Error?>(new OrderItemStream((), result));
        } else {
            return new stream<OrderItem, persist:Error?>(new OrderItemStream(result));
        }
    }

    isolated resource function get orderitems/[string itemId]/[string orderId]() returns OrderItem|persist:Error {
        OrderItem|error result = (check self.persistClients.get(ORDER_ITEMS).runReadByKeyQuery(OrderItem, {itemId: itemId, orderId: orderId})).cloneWithType(OrderItem);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        _ = check self.persistClients.get(ORDER_ITEMS).runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select [inserted.orderId, inserted.itemId];
    }

    isolated resource function put orderitems/[string itemId]/[string orderId](OrderItemUpdate value) returns OrderItem|persist:Error {
        _ = check self.persistClients.get(ORDER_ITEMS).runUpdateQuery({"itemId": itemId, "orderId": orderId}, value);
        return self->/orderitems/[itemId]/[orderId].get();
    }

    isolated resource function delete orderitems/[string itemId]/[string orderId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[itemId]/[orderId].get();
        _ = check self.persistClients.get(ORDER_ITEMS).runDeleteQuery({"itemId": itemId, "orderId": orderId});
        return result;
    }

    isolated resource function get workspaces() returns stream<Workspace, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(WORKSPACES).runReadQuery(Workspace);
        if result is persist:Error {
            return new stream<Workspace, persist:Error?>(new WorkspaceStream((), result));
        } else {
            return new stream<Workspace, persist:Error?>(new WorkspaceStream(result));
        }
    }

    isolated resource function get workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace|error result = (check self.persistClients.get(WORKSPACES).runReadByKeyQuery(Workspace, workspaceId)).cloneWithType(Workspace);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        _ = check self.persistClients.get(WORKSPACES).runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select inserted.workspaceId;
    }

    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        _ = check self.persistClients.get(WORKSPACES).runUpdateQuery(workspaceId, value);
        return self->/workspaces/[workspaceId].get();
    }

    isolated resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId].get();
        _ = check self.persistClients.get(WORKSPACES).runDeleteQuery(workspaceId);
        return result;
    }

    public function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

public class BuildingStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Building value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Building|error value = streamValue.value.cloneWithType(Building);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Building value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

public class DepartmentStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Department value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Department|error value = streamValue.value.cloneWithType(Department);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Department value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

public class EmployeeStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Employee value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Employee|error value = streamValue.value.cloneWithType(Employee);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Employee value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

public class OrderItemStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|OrderItem value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                OrderItem|error value = streamValue.value.cloneWithType(OrderItem);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|OrderItem value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

public class WorkspaceStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Workspace value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Workspace|error value = streamValue.value.cloneWithType(Workspace);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Workspace value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

