// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerina/sql;
import ballerinax/mssql;
import ballerinax/mssql.driver as _;
import ballerinax/persist.sql as psql;

const USER = "users";
const POST = "posts";
const FOLLOW = "follows";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mssql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [USER] : {
            entityName: "User",
            tableName: "User",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                birthDate: {columnName: "birthDate"},
                mobileNumber: {columnName: "mobileNumber"},
                "posts[].id": {relation: {entityName: "posts", refField: "id"}},
                "posts[].description": {relation: {entityName: "posts", refField: "description"}},
                "posts[].tags": {relation: {entityName: "posts", refField: "tags"}},
                "posts[].category": {relation: {entityName: "posts", refField: "category"}},
                "posts[].created_date": {relation: {entityName: "posts", refField: "created_date"}},
                "posts[].userId": {relation: {entityName: "posts", refField: "userId"}},
                "leader.id": {relation: {entityName: "leader", refField: "id"}},
                "leader.leaderId": {relation: {entityName: "leader", refField: "leaderId"}},
                "leader.followerId": {relation: {entityName: "leader", refField: "followerId"}},
                "leader.created_date": {relation: {entityName: "leader", refField: "created_date"}},
                "follower.id": {relation: {entityName: "follower", refField: "id"}},
                "follower.leaderId": {relation: {entityName: "follower", refField: "leaderId"}},
                "follower.followerId": {relation: {entityName: "follower", refField: "followerId"}},
                "follower.created_date": {relation: {entityName: "follower", refField: "created_date"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                posts: {entity: Post, fieldName: "posts", refTable: "Post", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                leader: {entity: Follow, fieldName: "leader", refTable: "Follow", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:ONE_TO_ONE},
                follower: {entity: Follow, fieldName: "follower", refTable: "Follow", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:ONE_TO_ONE}
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
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate"}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber"}}
            },
            keyFields: ["id"],
            joinMetadata: {user: {entity: User, fieldName: "user", refTable: "User", refColumns: ["id"], joinColumns: ["userId"], 'type: psql:ONE_TO_MANY}}
        },
        [FOLLOW] : {
            entityName: "Follow",
            tableName: "Follow",
            fieldMetadata: {
                id: {columnName: "id"},
                leaderId: {columnName: "leaderId"},
                followerId: {columnName: "followerId"},
                created_date: {columnName: "created_date"},
                "leader.id": {relation: {entityName: "leader", refField: "id"}},
                "leader.name": {relation: {entityName: "leader", refField: "name"}},
                "leader.birthDate": {relation: {entityName: "leader", refField: "birthDate"}},
                "leader.mobileNumber": {relation: {entityName: "leader", refField: "mobileNumber"}},
                "follower.id": {relation: {entityName: "follower", refField: "id"}},
                "follower.name": {relation: {entityName: "follower", refField: "name"}},
                "follower.birthDate": {relation: {entityName: "follower", refField: "birthDate"}},
                "follower.mobileNumber": {relation: {entityName: "follower", refField: "mobileNumber"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                leader: {entity: User, fieldName: "leader", refTable: "User", refColumns: ["id"], joinColumns: ["leaderId"], 'type: psql:ONE_TO_ONE},
                follower: {entity: User, fieldName: "follower", refTable: "User", refColumns: ["id"], joinColumns: ["followerId"], 'type: psql:ONE_TO_ONE}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        mssql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [USER] : check new (dbClient, self.metadata.get(USER), psql:MSSQL_SPECIFICS),
            [POST] : check new (dbClient, self.metadata.get(POST), psql:MSSQL_SPECIFICS),
            [FOLLOW] : check new (dbClient, self.metadata.get(FOLLOW), psql:MSSQL_SPECIFICS)
        };
    }

    isolated resource function get users(UserTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get posts(PostTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PostInsert inserted in data
            select inserted.id;
    }

    isolated resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/posts/[id].get();
    }

    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        Post result = check self->/posts/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get follows(FollowTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from FollowInsert inserted in data
            select inserted.id;
    }

    isolated resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/follows/[id].get();
    }

    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        Follow result = check self->/follows/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

