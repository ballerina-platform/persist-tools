// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for foo.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const BYTE_TEST = "bytetest";

public client class FooClient {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "bytetest": {
            entityName: "ByteTest",
            tableName: `ByteTest`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                binary1: {columnName: "binary1", 'type: byte},
                binaryOptional: {columnName: "binaryOptional", 'type: byte}
            },
            keyFields: ["id"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {bytetest: check new (self.dbClient, self.metadata.get(BYTE_TEST))};
    }

    isolated resource function get bytetests() returns stream<ByteTest, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(BYTE_TEST).runReadQuery(ByteTest);
        if result is persist:Error {
            return new stream<ByteTest, persist:Error?>(new ByteTestStream((), result));
        } else {
            return new stream<ByteTest, persist:Error?>(new ByteTestStream(result));
        }
    }

    isolated resource function get bytetests/[int id]() returns ByteTest|persist:Error {
        ByteTest|error result = (check self.persistClients.get(BYTE_TEST).runReadByKeyQuery(ByteTest, id)).cloneWithType(ByteTest);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post bytetests(ByteTestInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(BYTE_TEST).runBatchInsertQuery(data);
        return from ByteTestInsert inserted in data
            select inserted.id;
    }

    isolated resource function put bytetests/[int id](ByteTestUpdate value) returns ByteTest|persist:Error {
        _ = check self.persistClients.get(BYTE_TEST).runUpdateQuery(id, value);
        return self->/bytetests/[id].get();
    }

    isolated resource function delete bytetests/[int id]() returns ByteTest|persist:Error {
        ByteTest result = check self->/bytetests/[id].get();
        _ = check self.persistClients.get(BYTE_TEST).runDeleteQuery(id);
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

public class ByteTestStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|ByteTest value;|}|persist:Error? {
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
                ByteTest|error value = streamValue.value.cloneWithType(ByteTest);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|ByteTest value;|} nextRecord = {value: value};
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

