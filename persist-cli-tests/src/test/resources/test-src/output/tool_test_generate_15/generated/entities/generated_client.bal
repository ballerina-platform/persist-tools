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

    private final map<persist:Metadata> metadata = {user: {entityName: "User", tableName: 'User ', id: {columnName: "id", 'type: int}, name: {columnName: "name", 'type: string} keyFields: ["id"]}};

    public function init() returns persist:Error? {
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {user: check new (self.dbClient, self.metadata.get("user").entityName, self.metadata.get("user").tableName, self.metadata.get("user").keyFields, self.metadata.get("user").fieldMetadata)};
    }

    public function close() returns persist:Error? {
        sql:Error? e = self.dbClient.close();
        if e is sql:Error {
            return <persist:Error>error(e.message());
        }
    }

    isolated resource function get user() returns stream<User, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("user").runReadQuery(User);
        if result is persist:Error {
            return new stream<User, persist:Error?>(new UserStream((), result));
        } else {
            return new stream<User, persist:Error?>(new UserStream(result));
        }
    }
    isolated resource function get user/[int id]() returns User|persist:Error {
        return (check self.persistClients.get("user").runReadByKeyQuery(User, id)).cloneWithType(User);
    }
    isolated resource function post user(UserInsert[] data) returns [int][]|persist:Error {
        _ = check self.persistClients.get("user").runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select [inserted.id];
    }
    isolated resource function put user/[int id](UserUpdate value) returns User|persist:Error {
        _ = check self.persistClients.get("user").runUpdateQuery({"id": id, }, data);
        return self->/user/[id].get();
    }
    isolated resource function delete user/[int id]() returns User|persist:Error {
        User 'object = check self->/user/[id].get();
        _ = check self.persistClients.get("user").runDeleteQuery({"id": id, });
        return 'object;
    }
}

public class UserStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|User value;|}|persist:Error? {
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
                record {|User value;|} nextRecord = {value: check streamValue.value.cloneWithType(User)};
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

