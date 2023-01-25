// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for foo.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const BUILDING = "building";
const DEPARTMENT = "department";
const EMPLOYEE = "employee";
const WORKSPACE = "workspace";

public client class FooClient {

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "building": {
            entityName: "Building",
            tableName: `Building`,
            fieldMetadata: {
                buildingCode: {columnName: "buildingCode", 'type: string},
                city: {columnName: "city", 'type: string},
                state: {columnName: "state", 'type: string},
                country: {columnName: "country", 'type: string},
                postalCode: {columnName: "postalCode", 'type: string}
            },
            keyFields: ["buildingCode"]
        },
        "department": {
            entityName: "Department",
            tableName: `Department`,
            fieldMetadata: {
                deptNo: {columnName: "deptNo", 'type: string},
                deptName: {columnName: "deptName", 'type: string}
            },
            keyFields: ["deptNo"]
        },
        "employee": {
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
        "workspace": {
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
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {
            building: check new (self.dbClient, self.metadata.get(BUILDING),
            department: check new (self.dbClient, self.metadata.get(DEPARTMENT),
            employee: check new (self.dbClient, self.metadata.get(EMPLOYEE),
            workspace: check new (self.dbClient, self.metadata.get(WORKSPACE)        };
    }

    isolated resource function get building() returns stream<Building, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(BUILDING).runReadQuery(Building);
        if result is persist:Error {
            return new stream<Building, persist:Error?>(new BuildingStream((), result));
        } else {
            return new stream<Building, persist:Error?>(new BuildingStream(result));
        }
    }
    isolated resource function get building/[string buildingCode]() returns Building|persist:Error {
        return (check self.persistClients.get(BUILDING).runReadByKeyQuery(Building, buildingCode)).cloneWithType(Building);
    }
    isolated resource function post building(BuildingInsert[] data) returns [string][]|persist:Error {
        _ = check self.persistClients.get("building").runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select [inserted.buildingCode];
    }
    isolated resource function put building/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        _ = check self.persistClients.get("building").runUpdateQuery({"buildingCode": buildingCode, }, data);
        return self->/building/[buildingCode].get();
    }
    isolated resource function delete building/[string buildingCode]() returns Building|persist:Error {
        Building 'object = check self->/building/[buildingCode].get();
        _ = check self.persistClients.get("building").runDeleteQuery({"buildingCode": buildingCode, });
        return 'object;
    }

    isolated resource function get department() returns stream<Department, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(DEPARTMENT).runReadQuery(Department);
        if result is persist:Error {
            return new stream<Department, persist:Error?>(new DepartmentStream((), result));
        } else {
            return new stream<Department, persist:Error?>(new DepartmentStream(result));
        }
    }
    isolated resource function get department/[string deptNo]() returns Department|persist:Error {
        return (check self.persistClients.get(DEPARTMENT).runReadByKeyQuery(Department, deptNo)).cloneWithType(Department);
    }
    isolated resource function post department(DepartmentInsert[] data) returns [string][]|persist:Error {
        _ = check self.persistClients.get("department").runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select [inserted.deptNo];
    }
    isolated resource function put department/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        _ = check self.persistClients.get("department").runUpdateQuery({"deptNo": deptNo, }, data);
        return self->/department/[deptNo].get();
    }
    isolated resource function delete department/[string deptNo]() returns Department|persist:Error {
        Department 'object = check self->/department/[deptNo].get();
        _ = check self.persistClients.get("department").runDeleteQuery({"deptNo": deptNo, });
        return 'object;
    }

    isolated resource function get employee() returns stream<Employee, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(EMPLOYEE).runReadQuery(Employee);
        if result is persist:Error {
            return new stream<Employee, persist:Error?>(new EmployeeStream((), result));
        } else {
            return new stream<Employee, persist:Error?>(new EmployeeStream(result));
        }
    }
    isolated resource function get employee/[string empNo]() returns Employee|persist:Error {
        return (check self.persistClients.get(EMPLOYEE).runReadByKeyQuery(Employee, empNo)).cloneWithType(Employee);
    }
    isolated resource function post employee(EmployeeInsert[] data) returns [string][]|persist:Error {
        _ = check self.persistClients.get("employee").runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select [inserted.empNo];
    }
    isolated resource function put employee/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        _ = check self.persistClients.get("employee").runUpdateQuery({"empNo": empNo, }, data);
        return self->/employee/[empNo].get();
    }
    isolated resource function delete employee/[string empNo]() returns Employee|persist:Error {
        Employee 'object = check self->/employee/[empNo].get();
        _ = check self.persistClients.get("employee").runDeleteQuery({"empNo": empNo, });
        return 'object;
    }

    isolated resource function get workspace() returns stream<Workspace, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(WORKSPACE).runReadQuery(Workspace);
        if result is persist:Error {
            return new stream<Workspace, persist:Error?>(new WorkspaceStream((), result));
        } else {
            return new stream<Workspace, persist:Error?>(new WorkspaceStream(result));
        }
    }
    isolated resource function get workspace/[string workspaceId]() returns Workspace|persist:Error {
        return (check self.persistClients.get(WORKSPACE).runReadByKeyQuery(Workspace, workspaceId)).cloneWithType(Workspace);
    }
    isolated resource function post workspace(WorkspaceInsert[] data) returns [string][]|persist:Error {
        _ = check self.persistClients.get("workspace").runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select [inserted.workspaceId];
    }
    isolated resource function put workspace/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        _ = check self.persistClients.get("workspace").runUpdateQuery({"workspaceId": workspaceId, }, data);
        return self->/workspace/[workspaceId].get();
    }
    isolated resource function delete workspace/[string workspaceId]() returns Workspace|persist:Error {
        Workspace 'object = check self->/workspace/[workspaceId].get();
        _ = check self.persistClients.get("workspace").runDeleteQuery({"workspaceId": workspaceId, });
        return 'object;
    }

    public function close() returns persist:Error? {
        _ = check self.dbClient.close();
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
                record {|Building value;|} nextRecord = {value: check streamValue.value.cloneWithType(Building)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check closeEntityStream(self.anydataStream);
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
                record {|Department value;|} nextRecord = {value: check streamValue.value.cloneWithType(Department)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check closeEntityStream(self.anydataStream);
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
                record {|Employee value;|} nextRecord = {value: check streamValue.value.cloneWithType(Employee)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check closeEntityStream(self.anydataStream);
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
                record {|Workspace value;|} nextRecord = {value: check streamValue.value.cloneWithType(Workspace)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check closeEntityStream(self.anydataStream);
    }
}

