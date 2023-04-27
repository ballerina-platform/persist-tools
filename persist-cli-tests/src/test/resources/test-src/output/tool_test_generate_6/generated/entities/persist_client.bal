// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

const DATA_TYPE = "datatypes";

public client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:SQLMetadata...;|} metadata = {
        [DATA_TYPE] : {
            entityName: "DataType",
            tableName: `DataType`,
            fieldMetadata: {
                a: {columnName: "a"},
                b1: {columnName: "b1"},
                c1: {columnName: "c1"},
                d1: {columnName: "d1"},
                bA: {columnName: "bA"},
                e1: {columnName: "e1"},
                f1: {columnName: "f1"},
                j1: {columnName: "j1"},
                k1: {columnName: "k1"},
                l1: {columnName: "l1"},
                m1: {columnName: "m1"}
            },
            keyFields: ["a"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {[DATA_TYPE] : check new (self.dbClient, self.metadata.get(DATA_TYPE))};
    }

    isolated resource function get datatypes(DataTypeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get datatypes/[int a](DataTypeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post datatypes(DataTypeInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(DATA_TYPE).runBatchInsertQuery(data);
        return from DataTypeInsert inserted in data
            select inserted.a;
    }

    isolated resource function put datatypes/[int a](DataTypeUpdate value) returns DataType|persist:Error {
        _ = check self.persistClients.get(DATA_TYPE).runUpdateQuery(a, value);
        return self->/datatypes/[a].get();
    }

    isolated resource function delete datatypes/[int a]() returns DataType|persist:Error {
        DataType result = check self->/datatypes/[a].get();
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

