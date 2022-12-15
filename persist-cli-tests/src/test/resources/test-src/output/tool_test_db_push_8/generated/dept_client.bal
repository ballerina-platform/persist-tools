// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for Dept.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

public client class DeptClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Dept";
    private final sql:ParameterizedQuery tableName = `Dept`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string}
    };
    private string[] keyFields = ["id"];

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata);
    }

    remote function create(Dept value) returns Dept|persist:Error {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        if result.lastInsertId is () {
            return value;
        }
        return {id: <int>result.lastInsertId, name: value.name};
    }

    remote function readByKey(int key) returns Dept|persist:Error {
        return <Dept>check self.persistClient.runReadByKeyQuery(Dept, key);
    }

    remote function read() returns stream<Dept, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Dept);
        if result is persist:Error {
            return new stream<Dept, persist:Error?>(new DeptStream((), result));
        } else {
            return new stream<Dept, persist:Error?>(new DeptStream(result));
        }
    }

    remote function update(Dept value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
    }

    remote function delete(Dept value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Dept dept) returns boolean|persist:Error {
        Dept|persist:Error result = self->readByKey(dept.id);
        if result is Dept {
            return true;
        } else if result is persist:InvalidKeyError {
            return false;
        } else {
            return result;
        }
    }

    public function close() returns persist:Error? {
        return self.persistClient.close();
    }
}

public class DeptStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Dept value;|}|persist:Error? {
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
                record {|Dept value;|} nextRecord = {value: <Dept>streamValue.value};
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

