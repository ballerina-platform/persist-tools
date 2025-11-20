// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mssql;
import ballerinax/mssql.driver as _;
import ballerinax/persist.sql as psql;

const USER = "users";
const POST = "posts";
const FOLLOW = "follows";
const COMMENT = "comments";

# MSSQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mssql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} metadata = {
        [USER]: {
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
                "posts[].timestamp": {relation: {entityName: "posts", refField: "timestamp"}},
                "posts[].userId": {relation: {entityName: "posts", refField: "userId"}},
                "comments[].id": {relation: {entityName: "comments", refField: "id"}},
                "comments[].comment": {relation: {entityName: "comments", refField: "comment"}},
                "comments[].timesteamp": {relation: {entityName: "comments", refField: "timesteamp"}},
                "comments[].userId": {relation: {entityName: "comments", refField: "userId"}},
                "comments[].postId": {relation: {entityName: "comments", refField: "postId"}},
                "followers[].id": {relation: {entityName: "followers", refField: "id"}},
                "followers[].leaderId": {relation: {entityName: "followers", refField: "leaderId"}},
                "followers[].followerId": {relation: {entityName: "followers", refField: "followerId"}},
                "followers[].timestamp": {relation: {entityName: "followers", refField: "timestamp"}},
                "following[].id": {relation: {entityName: "following", refField: "id"}},
                "following[].leaderId": {relation: {entityName: "following", refField: "leaderId"}},
                "following[].followerId": {relation: {entityName: "following", refField: "followerId"}},
                "following[].timestamp": {relation: {entityName: "following", refField: "timestamp"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                posts: {entity: Post, fieldName: "posts", refTable: "Post", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                comments: {entity: Comment, fieldName: "comments", refTable: "Comment", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                followers: {entity: Follow, fieldName: "followers", refTable: "Follow", refColumns: ["leaderId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                following: {entity: Follow, fieldName: "following", refTable: "Follow", refColumns: ["followerId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}
            }
        },
        [POST]: {
            entityName: "Post",
            tableName: "Post",
            fieldMetadata: {
                id: {columnName: "id"},
                description: {columnName: "description"},
                tags: {columnName: "tags"},
                category: {columnName: "category"},
                timestamp: {columnName: "timestamp"},
                userId: {columnName: "userId"},
                "user.id": {relation: {entityName: "user", refField: "id"}},
                "user.name": {relation: {entityName: "user", refField: "name"}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate"}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber"}},
                "comments[].id": {relation: {entityName: "comments", refField: "id"}},
                "comments[].comment": {relation: {entityName: "comments", refField: "comment"}},
                "comments[].timesteamp": {relation: {entityName: "comments", refField: "timesteamp"}},
                "comments[].userId": {relation: {entityName: "comments", refField: "userId"}},
                "comments[].postId": {relation: {entityName: "comments", refField: "postId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                user: {entity: User, fieldName: "user", refTable: "User", refColumns: ["id"], joinColumns: ["userId"], 'type: psql:ONE_TO_MANY},
                comments: {entity: Comment, fieldName: "comments", refTable: "Comment", refColumns: ["postId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}
            }
        },
        [FOLLOW]: {
            entityName: "Follow",
            tableName: "Follow",
            fieldMetadata: {
                id: {columnName: "id"},
                leaderId: {columnName: "leaderId"},
                followerId: {columnName: "followerId"},
                timestamp: {columnName: "timestamp"},
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
                leader: {entity: User, fieldName: "leader", refTable: "User", refColumns: ["id"], joinColumns: ["leaderId"], 'type: psql:ONE_TO_MANY},
                follower: {entity: User, fieldName: "follower", refTable: "User", refColumns: ["id"], joinColumns: ["followerId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [COMMENT]: {
            entityName: "Comment",
            tableName: "Comment",
            fieldMetadata: {
                id: {columnName: "id"},
                comment: {columnName: "comment"},
                timesteamp: {columnName: "timesteamp"},
                userId: {columnName: "userId"},
                postId: {columnName: "postId"},
                "user.id": {relation: {entityName: "user", refField: "id"}},
                "user.name": {relation: {entityName: "user", refField: "name"}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate"}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber"}},
                "post.id": {relation: {entityName: "post", refField: "id"}},
                "post.description": {relation: {entityName: "post", refField: "description"}},
                "post.tags": {relation: {entityName: "post", refField: "tags"}},
                "post.category": {relation: {entityName: "post", refField: "category"}},
                "post.timestamp": {relation: {entityName: "post", refField: "timestamp"}},
                "post.userId": {relation: {entityName: "post", refField: "userId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                user: {entity: User, fieldName: "user", refTable: "User", refColumns: ["id"], joinColumns: ["userId"], 'type: psql:ONE_TO_MANY},
                post: {entity: Post, fieldName: "post", refTable: "Post", refColumns: ["id"], joinColumns: ["postId"], 'type: psql:ONE_TO_MANY}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        mssql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        if defaultSchema != () {
            lock {
                foreach string key in self.metadata.keys() {
                    psql:SQLMetadata metadata = self.metadata.get(key);
                    if metadata.schemaName == () {
                        metadata.schemaName = defaultSchema;
                    }
                    map<psql:JoinMetadata>? joinMetadataMap = metadata.joinMetadata;
                    if joinMetadataMap == () {
                        continue;
                    }
                    foreach [string, psql:JoinMetadata] [_, joinMetadata] in joinMetadataMap.entries() {
                        if joinMetadata.refSchema == () {
                            joinMetadata.refSchema = defaultSchema;
                        }
                    }
                }
            }
        }
        self.persistClients = {
            [USER]: check new (dbClient, self.metadata.get(USER).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [POST]: check new (dbClient, self.metadata.get(POST).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [FOLLOW]: check new (dbClient, self.metadata.get(FOLLOW).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [COMMENT]: check new (dbClient, self.metadata.get(COMMENT).cloneReadOnly(), psql:MSSQL_SPECIFICS)
        };
    }

    # Get rows from User table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get users(UserTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from User table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into User table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    # Update row in User table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    # Delete row from User table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Post table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get posts(PostTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from Post table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Post table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PostInsert inserted in data
            select inserted.id;
    }

    # Update row in Post table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/posts/[id].get();
    }

    # Delete row from Post table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        Post result = check self->/posts/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Follow table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get follows(FollowTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from Follow table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Follow table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from FollowInsert inserted in data
            select inserted.id;
    }

    # Update row in Follow table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/follows/[id].get();
    }

    # Delete row from Follow table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        Follow result = check self->/follows/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from Comment table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get comments(CommentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from Comment table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get comments/[int id](CommentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Comment table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post comments(CommentInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CommentInsert inserted in data
            select inserted.id;
    }

    # Update row in Comment table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put comments/[int id](CommentUpdate value) returns Comment|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMMENT);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/comments/[id].get();
    }

    # Delete row from Comment table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete comments/[int id]() returns Comment|persist:Error {
        Comment result = check self->/comments/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMMENT);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    # Execute a custom SQL query and return results.
    #
    # + sqlQuery - The SQL query to execute
    # + rowType - Defines the structure of the result rows
    # + return - A collection of result rows or an error
    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    # Execute a custom SQL command (INSERT, UPDATE, DELETE, etc.).
    #
    # + sqlQuery - The SQL command to execute
    # + return - The execution result or an error
    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

