// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/mysql;

const USER = "users";
const FOLLOW = "follows";

public client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:SQLMetadata...;|} metadata = {
        [USER] : {
            entityName: "User",
            tableName: `User`,
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                "follow.id": {relation: {entityName: "follow", refField: "id"}},
                "follow.followId": {relation: {entityName: "follow", refField: "followId"}},
                "follow.follow1Id": {relation: {entityName: "follow", refField: "follow1Id"}},
                "follow1.id": {relation: {entityName: "follow1", refField: "id"}},
                "follow1.followId": {relation: {entityName: "follow1", refField: "followId"}},
                "follow1.follow1Id": {relation: {entityName: "follow1", refField: "follow1Id"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                follow: {entity: Follow, fieldName: "follow", refTable: "Follow", refColumns: ["userId"], joinColumns: ["id"], 'type: persist:ONE_TO_ONE},
                follow1: {entity: Follow, fieldName: "follow1", refTable: "Follow", refColumns: ["userId"], joinColumns: ["id"], 'type: persist:ONE_TO_ONE}
            }
        },
        [FOLLOW] : {
            entityName: "Follow",
            tableName: `Follow`,
            fieldMetadata: {
                id: {columnName: "id"},
                followId: {columnName: "followId"},
                follow1Id: {columnName: "follow1Id"},
                "leader.id": {relation: {entityName: "leader", refField: "id"}},
                "leader.name": {relation: {entityName: "leader", refField: "name"}},
                "follower.id": {relation: {entityName: "follower", refField: "id"}},
                "follower.name": {relation: {entityName: "follower", refField: "name"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                leader: {entity: User, fieldName: "leader", refTable: "User", refColumns: ["id"], joinColumns: ["followId"], 'type: persist:ONE_TO_ONE},
                follower: {entity: User, fieldName: "follower", refTable: "User", refColumns: ["id"], joinColumns: ["follow1Id"], 'type: persist:ONE_TO_ONE}
            }
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [USER] : check new (self.dbClient, self.metadata.get(USER)),
            [FOLLOW] : check new (self.dbClient, self.metadata.get(FOLLOW))
        };
    }

    isolated resource function get users(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(USER).runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        _ = check self.persistClients.get(USER).runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        _ = check self.persistClients.get(USER).runDeleteQuery(id);
        return result;
    }

    isolated resource function get follows(FollowTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(FOLLOW).runBatchInsertQuery(data);
        return from FollowInsert inserted in data
            select inserted.id;
    }

    isolated resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        _ = check self.persistClients.get(FOLLOW).runUpdateQuery(id, value);
        return self->/follows/[id].get();
    }

    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        Follow result = check self->/follows/[id].get();
        _ = check self.persistClients.get(FOLLOW).runDeleteQuery(id);
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
