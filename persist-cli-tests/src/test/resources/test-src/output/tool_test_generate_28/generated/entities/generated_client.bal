// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerina/time;

public client class EntitiesClient {

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final map<persist:Metadata> metadata = {company: {entityName: "Company", tableName: 'Company ', id: {columnName: "id", 'type: int}, name: {columnName: "name", 'type: string}, keyFields: ["id"]}, employee: {entityName: "Employee", tableName: 'Employee ', id: {columnName: "id", 'type: int}, name: {columnName: "name", 'type: string}, companyId: {columnName: "companyId", 'type: int} keyFields: ["id"]}};

    public function init() returns persist:Error? {
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {company: check new (self.dbClient, self.metadata.get("company").entityName, self.metadata.get("company").tableName, self.metadata.get("company").keyFields, self.metadata.get("company").fieldMetadata), employee: check new (self.dbClient, self.metadata.get("employee").entityName, self.metadata.get("employee").tableName, self.metadata.get("employee").keyFields, self.metadata.get("employee").fieldMetadata)};
    }

    isolated resource function get company() returns stream<Company, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("company").runReadQuery(Company);
        if result is persist:Error {
            return new stream<Company, persist:Error?>(new CompanyStream((), result));
        } else {
            return new stream<Company, persist:Error?>(new CompanyStream(result));
        }
    }
    isolated resource function get company/[int id]() returns Company|persist:Error {
        return (check self.persistClients.get("company").runReadByKeyQuery(Company, id)).cloneWithType(Company);
    }
    isolated resource function post company(CompanyInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("company").runBatchInsertQuery(data);
        return from CompanyInsert inserted in data
            select inserted.id;
    }
    isolated resource function put company/[int id](CompanyUpdate value) returns Company|persist:Error {
        _ = check self.persistClients.get("company").runUpdateQuery({"id": id}, value);
        return self->/company/[id].get();
    }
    isolated resource function delete company/[int id]() returns Company|persist:Error {
        Company 'object = check self->/company/[id].get();
        _ = check self.persistClients.get("company").runDeleteQuery({"id": id});
        return 'object;
    }

    isolated resource function get employee() returns stream<Employee, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("employee").runReadQuery(Employee);
        if result is persist:Error {
            return new stream<Employee, persist:Error?>(new EmployeeStream((), result));
        } else {
            return new stream<Employee, persist:Error?>(new EmployeeStream(result));
        }
    }
    isolated resource function get employee/[int id]() returns Employee|persist:Error {
        return (check self.persistClients.get("employee").runReadByKeyQuery(Employee, id)).cloneWithType(Employee);
    }
    isolated resource function post employee(EmployeeInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("employee").runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.id;
    }
    isolated resource function put employee/[int id](EmployeeUpdate value) returns Employee|persist:Error {
        _ = check self.persistClients.get("employee").runUpdateQuery({"id": id}, value);
        return self->/employee/[id].get();
    }
    isolated resource function delete employee/[int id]() returns Employee|persist:Error {
        Employee 'object = check self->/employee/[id].get();
        _ = check self.persistClients.get("employee").runDeleteQuery({"id": id});
        return 'object;
    }

    public function close() returns persist:Error? {
        sql:Error? e = self.dbClient.close();
        if e is sql:Error {
            return <persist:Error>error(e.message());
        }
    }
}

public class CompanyStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Company value;|}|persist:Error? {
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
                record {|Company value;|} nextRecord = {value: check streamValue.value.cloneWithType(Company)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
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
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
    }
}

