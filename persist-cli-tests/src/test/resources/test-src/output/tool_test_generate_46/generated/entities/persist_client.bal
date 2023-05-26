// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql;

const USER = "users";
const POST = "posts";
const FOLLOWER = "followers";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<sql:SQLClient> persistClients;

    private final record {|sql:SQLMetadata...;|} & readonly metadata = {
        [USER] : {
            entityName: "User",
            tableName: "User",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                birthDate: {columnName: "birthDate"},
                "posts[].id": {relation: {entityName: "posts", refField: "id"}},
                "posts[].description": {relation: {entityName: "posts", refField: "description"}},
                "posts[].tags": {relation: {entityName: "posts", refField: "tags"}},
                "posts[].category": {relation: {entityName: "posts", refField: "category"}},
                "posts[].created_date": {relation: {entityName: "posts", refField: "created_date"}},
                "posts[].userId": {relation: {entityName: "posts", refField: "userId"}},
                "followers[].id": {relation: {entityName: "followers", refField: "id"}},
                "followers[].created_date": {relation: {entityName: "followers", refField: "created_date"}},
                "followers[].leaderId": {relation: {entityName: "followers", refField: "leaderId"}},
                "followers[].followerId": {relation: {entityName: "followers", refField: "followerId"}},
                "leaders[].id": {relation: {entityName: "leaders", refField: "id"}},
                "leaders[].created_date": {relation: {entityName: "leaders", refField: "created_date"}},
                "leaders[].leaderId": {relation: {entityName: "leaders", refField: "leaderId"}},
                "leaders[].followerId": {relation: {entityName: "leaders", refField: "followerId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                posts: {entity: Post, fieldName: "posts", refTable: "Post", refColumns: ["userId"], joinColumns: ["id"], 'type: sql:MANY_TO_ONE},
                followers: {entity: Follower, fieldName: "followers", refTable: "Follower", refColumns: ["leaderId"], joinColumns: ["id"], 'type: sql:MANY_TO_ONE},
                leaders: {entity: Follower, fieldName: "leaders", refTable: "Follower", refColumns: ["followerId"], joinColumns: ["id"], 'type: sql:MANY_TO_ONE}
            }
        },
        [POST] : {
            entityName: "Post",
            tableName: "Post",
            fieldMetadata: {
                id: {columnName: "id"},
                description: {columnName: "description"},
                tags: {columnName: "tags"},
                category: {columnName: "category"},
                created_date: {columnName: "created_date"},
                userId: {columnName: "userId"},
                "user.id": {relation: {entityName: "user", refField: "id"}},
                "user.name": {relation: {entityName: "user", refField: "name"}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate"}}
            },
            keyFields: ["id"],
            joinMetadata: {user: {entity: User, fieldName: "user", refTable: "User", refColumns: ["id"], joinColumns: ["userId"], 'type: sql:ONE_TO_MANY}}
        },
        [FOLLOWER] : {
            entityName: "Follower",
            tableName: "Follower",
            fieldMetadata: {
                id: {columnName: "id"},
                created_date: {columnName: "created_date"},
                leaderId: {columnName: "leaderId"},
                followerId: {columnName: "followerId"},
                "leader.id": {relation: {entityName: "leader", refField: "id"}},
                "leader.name": {relation: {entityName: "leader", refField: "name"}},
                "leader.birthDate": {relation: {entityName: "leader", refField: "birthDate"}},
                "follower.id": {relation: {entityName: "follower", refField: "id"}},
                "follower.name": {relation: {entityName: "follower", refField: "name"}},
                "follower.birthDate": {relation: {entityName: "follower", refField: "birthDate"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                leader: {entity: User, fieldName: "leader", refTable: "User", refColumns: ["id"], joinColumns: ["leaderId"], 'type: sql:ONE_TO_MANY},
                follower: {entity: User, fieldName: "follower", refTable: "User", refColumns: ["id"], joinColumns: ["followerId"], 'type: sql:ONE_TO_MANY}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [USER] : check new (dbClient, self.metadata.get(USER)),
            [POST] : check new (dbClient, self.metadata.get(POST)),
            [FOLLOWER] : check new (dbClient, self.metadata.get(FOLLOWER))
        };
    }

    isolated resource function get users(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get posts(PostTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PostInsert inserted in data
            select inserted.id;
    }

    isolated resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/posts/[id].get();
    }

    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        Post result = check self->/posts/[id].get();
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get followers(FollowerTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get followers/[int id](FollowerTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post followers(FollowerInsert[] data) returns int[]|persist:Error {
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOWER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from FollowerInsert inserted in data
            select inserted.id;
    }

    isolated resource function put followers/[int id](FollowerUpdate value) returns Follower|persist:Error {
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOWER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/followers/[id].get();
    }

    isolated resource function delete followers/[int id]() returns Follower|persist:Error {
        Follower result = check self->/followers/[id].get();
        sql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOWER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

