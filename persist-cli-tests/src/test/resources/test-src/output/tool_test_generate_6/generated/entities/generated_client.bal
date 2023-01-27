// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerina/time;
import ballerinax/mysql;

const DATA_TYPE = "datatype";

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "datatype": {
            entityName: "DataType",
            tableName: `DataType`,
            fieldMetadata: {
                a: {columnName: "a", 'type: int},
                b1: {columnName: "b1", 'type: string},
                c1: {columnName: "c1", 'type: int},
                d1: {columnName: "d1", 'type: boolean},
                e1: {columnName: "e1", 'type: float},
                f1: {columnName: "f1", 'type: decimal},
                j1: {columnName: "j1", 'type: time:Utc},
                k1: {columnName: "k1", 'type: time:Civil},
                l1: {columnName: "l1", 'type: time:Date},
                m1: {columnName: "m1", 'type: time:TimeOfDay}
            },
            keyFields: ["a"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {datatype: check new (self.dbClient, self.metadata.get(DATA_TYPE))};
    }

    isolated resource function get datatype() returns stream<DataType, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(DATA_TYPE).runReadQuery(DataType);
        if result is persist:Error {
            return new stream<DataType, persist:Error?>(new DataTypeStream((), result));
        } else {
            return new stream<DataType, persist:Error?>(new DataTypeStream(result));
        }
    }

    isolated resource function get datatype/[int a]() returns DataType|persist:Error {
        DataType|error result = (check self.persistClients.get(DATA_TYPE).runReadByKeyQuery(DataType, a)).cloneWithType(DataType);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post datatype(DataTypeInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(DATA_TYPE).runBatchInsertQuery(data);
        return from DataTypeInsert inserted in data
            select inserted.a;
    }

    isolated resource function put datatype/[int a](DataTypeUpdate value) returns DataType|persist:Error {
        _ = check self.persistClients.get(DATA_TYPE).runUpdateQuery(a, value);
        return self->/datatype/[a].get();
    }

    isolated resource function delete datatype/[int a]() returns DataType|persist:Error {
        DataType result = check self->/datatype/[a].get();
        _ = check self.persistClients.get(DATA_TYPE).runDeleteQuery(a);
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

public class DataTypeStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|DataType value;|}|persist:Error? {
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
                DataType|error value = streamValue.value.cloneWithType(DataType);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|DataType value;|} nextRecord = {value: value};
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

