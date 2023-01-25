// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerina/time;
import ballerinax/mysql;

const USER = "User";
const PROFILE = "Profile";

public client class EntitiesClient {

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "user": {
            entityName: "User",
            tableName: `User`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                profileId: {columnName: "profileId", 'type: int}
            },
            keyFields: ["id"]
        },
        "profile": {
            entityName: "Profile",
            tableName: `Profile`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string},
                gender: {columnName: "gender", 'type: string}
            },
            keyFields: ["id"]
        }
    };

    public function init() returns persist:Error? {
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {
            user: check new (self.dbClient, self.metadata.get(USER),
            profile: check new (self.dbClient, self.metadata.get(PROFILE)        };
    }

    isolated resource function get user() returns stream<User, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(USER).runReadQuery(User);
        if result is persist:Error {
            return new stream<User, persist:Error?>(new UserStream((), result));
        } else {
            return new stream<User, persist:Error?>(new UserStream(result));
        }
    }
    isolated resource function get user/[int id]() returns User|persist:Error {
        return (check self.persistClients.get(USER).runReadByKeyQuery(User, id)).cloneWithType(User);
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

    isolated resource function get profile() returns stream<Profile, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(PROFILE).runReadQuery(Profile);
        if result is persist:Error {
            return new stream<Profile, persist:Error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, persist:Error?>(new ProfileStream(result));
        }
    }
    isolated resource function get profile/[int id]() returns Profile|persist:Error {
        return (check self.persistClients.get(PROFILE).runReadByKeyQuery(Profile, id)).cloneWithType(Profile);
    }
    isolated resource function post profile(ProfileInsert[] data) returns [int][]|persist:Error {
        _ = check self.persistClients.get("profile").runBatchInsertQuery(data);
        return from ProfileInsert inserted in data
            select [inserted.id];
    }
    isolated resource function put profile/[int id](ProfileUpdate value) returns Profile|persist:Error {
        _ = check self.persistClients.get("profile").runUpdateQuery({"id": id, }, data);
        return self->/profile/[id].get();
    }
    isolated resource function delete profile/[int id]() returns Profile|persist:Error {
        Profile 'object = check self->/profile/[id].get();
        _ = check self.persistClients.get("profile").runDeleteQuery({"id": id, });
        return 'object;
    }

    public function close() returns persist:Error? {
        _ = check self.dbClient.close();
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
        check closeEntityStream(self.anydataStream);
    }
}

public class ProfileStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Profile value;|}|persist:Error? {
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
                record {|Profile value;|} nextRecord = {value: check streamValue.value.cloneWithType(Profile)};
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

