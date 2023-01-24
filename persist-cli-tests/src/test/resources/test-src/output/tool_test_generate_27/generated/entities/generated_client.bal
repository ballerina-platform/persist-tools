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

    private final map<persist:Metadata> metadata = {multipleassociations: {entityName: "MultipleAssociations", tableName: 'MultipleAssociations ', id: {columnName: "id", 'type: int}, name: {columnName: "name", 'type: string}, profileId: {columnName: "profileId", 'type: int}, userId: {columnName: "userId", 'type: int} keyFields: ["id"]}, user: {entityName: "User", tableName: 'User ', id: {columnName: "id", 'type: int}, name: {columnName: "name", 'type: string}, profileId: {columnName: "profileId", 'type: int}, keyFields: ["id"]}, profile: {entityName: "Profile", tableName: 'Profile ', id: {columnName: "id", 'type: int}, name: {columnName: "name", 'type: string}, , keyFields: ["id"]}};

    public function init() returns persist:Error? {
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {multipleassociations: check new (self.dbClient, self.metadata.get("multipleassociations").entityName, self.metadata.get("multipleassociations").tableName, self.metadata.get("multipleassociations").keyFields, self.metadata.get("multipleassociations").fieldMetadata), user: check new (self.dbClient, self.metadata.get("user").entityName, self.metadata.get("user").tableName, self.metadata.get("user").keyFields, self.metadata.get("user").fieldMetadata), profile: check new (self.dbClient, self.metadata.get("profile").entityName, self.metadata.get("profile").tableName, self.metadata.get("profile").keyFields, self.metadata.get("profile").fieldMetadata)};
    }

    isolated resource function get multipleassociations() returns stream<MultipleAssociations, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("multipleassociations").runReadQuery(MultipleAssociations);
        if result is persist:Error {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream(result));
        }
    }
    isolated resource function get multipleassociations/[int id]() returns MultipleAssociations|persist:Error {
        return (check self.persistClients.get("multipleassociations").runReadByKeyQuery(MultipleAssociations, id)).cloneWithType(MultipleAssociations);
    }
    isolated resource function post multipleassociations(MultipleAssociationsInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("MULTIPLEASSOCIATIONS").runBatchInsertQuery(data);
        return from MultipleAssociationsInsert inserted in data
            select inserted.id;
    }
    isolated resource function put multipleassociations/[int id](MultipleAssociationsUpdate value) returns MultipleAssociations|persist:Error {
        _ = check self.persistClients.get("MULTIPLEASSOCIATIONS").runUpdateQuery({"id": id}, value);
        return self->/multipleassociations/[id].get();
    }
    isolated resource function delete multipleassociations/[int id]() returns MultipleAssociations|persist:Error {
        MultipleAssociations 'object = check self->/multipleassociations/[id].get();
        _ = check self.persistClients.get("MULTIPLEASSOCIATIONS").runDeleteQuery({"id": id});
        return 'object;
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
    isolated resource function post user(UserInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("USER").runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }
    isolated resource function put user/[int id](UserUpdate value) returns User|persist:Error {
        _ = check self.persistClients.get("USER").runUpdateQuery({"id": id}, value);
        return self->/user/[id].get();
    }
    isolated resource function delete user/[int id]() returns User|persist:Error {
        User 'object = check self->/user/[id].get();
        _ = check self.persistClients.get("USER").runDeleteQuery({"id": id});
        return 'object;
    }

    isolated resource function get profile() returns stream<Profile, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("profile").runReadQuery(Profile);
        if result is persist:Error {
            return new stream<Profile, persist:Error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, persist:Error?>(new ProfileStream(result));
        }
    }
    isolated resource function get profile/[int id]() returns Profile|persist:Error {
        return (check self.persistClients.get("profile").runReadByKeyQuery(Profile, id)).cloneWithType(Profile);
    }
    isolated resource function post profile(ProfileInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("PROFILE").runBatchInsertQuery(data);
        return from ProfileInsert inserted in data
            select inserted.id;
    }
    isolated resource function put profile/[int id](ProfileUpdate value) returns Profile|persist:Error {
        _ = check self.persistClients.get("PROFILE").runUpdateQuery({"id": id}, value);
        return self->/profile/[id].get();
    }
    isolated resource function delete profile/[int id]() returns Profile|persist:Error {
        Profile 'object = check self->/profile/[id].get();
        _ = check self.persistClients.get("PROFILE").runDeleteQuery({"id": id});
        return 'object;
    }

    public function close() returns persist:Error? {
        sql:Error? e = self.dbClient.close();
        if e is sql:Error {
            return <persist:Error>error(e.message());
        }
    }
}

public class MultipleAssociationsStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|MultipleAssociations value;|}|persist:Error? {
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
                record {|MultipleAssociations value;|} nextRecord = {value: check streamValue.value.cloneWithType(MultipleAssociations)};
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
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
    }
}

