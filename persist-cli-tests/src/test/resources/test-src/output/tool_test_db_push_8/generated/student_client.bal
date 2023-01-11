// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for Student.
// It should not be modified by hand.

import ballerinax/mysql;
import ballerina/persist;
import ballerina/sql;

public client class StudentClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Student";
    private final sql:ParameterizedQuery tableName = `Student`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        firstName: {columnName: "firstName", 'type: string},
        age: {columnName: "age", 'type: int},
        lastName: {columnName: "lastName", 'type: string},
        nicNo: {columnName: "nicNo", 'type: string}
    };
    private string[] keyFields = ["id", "firstName"];

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata);
    }

    remote function create(Student value) returns Student|persist:Error {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(record {|string firstName; int id;|} key) returns Student|persist:Error {
        return <Student>check self.persistClient.runReadByKeyQuery(Student, key);
    }

    remote function read() returns stream<Student, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Student);
        if result is persist:Error {
            return new stream<Student, persist:Error?>(new StudentStream((), result));
        } else {
            return new stream<Student, persist:Error?>(new StudentStream(result));
        }
    }

    remote function update(Student value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
    }

    remote function delete(Student value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Student student) returns boolean|persist:Error {
        Student|persist:Error result = self->readByKey({firstName: student.firstName, id: student.id});
        if result is Student {
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

public class StudentStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Student value;|}|persist:Error? {
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
                record {|Student value;|} nextRecord = {value: <Student>streamValue.value};
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

