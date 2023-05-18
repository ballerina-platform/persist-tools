// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/googleapis.sheets;
import ballerina/http;

const USER = "users";
const POST = "posts";
const FOLLOW = "follows";
const COMMENT = "comments";

public client class Client {
    *persist:AbstractPersistClient;

    private final sheets:Client googleSheetClient;

    private final http:Client httpClient;

    private final map<persist:GoogleSheetsClient> persistClients;

    public function init() returns persist:Error? {
        final record {|persist:SheetMetadata...;|} metadata = {
            [USER] : {
                entityName: "User",
                tableName: "User",
                keyFields: ["id"],
                range: "A:D",
                query: self.queryUsers,
                queryOne: self.queryOneUsers,
                dataTypes: {
                    id: "int",
                    name: "string",
                    birthDate: "time:Date",
                    mobileNumber: "string"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    name: {columnName: "name", columnId: "B"},
                    birthDate: {columnName: "birthDate", columnId: "C"},
                    mobileNumber: {columnName: "mobileNumber", columnId: "D"}
                },
                associationsMethods: {
                    "posts": self.queryUserPosts,
                    "comments": self.queryUserComments,
                    "followers": self.queryUserFollowers,
                    "following": self.queryUserFollowing
                }
            },
            [POST] : {
                entityName: "Post",
                tableName: "Post",
                keyFields: ["id"],
                range: "A:F",
                query: self.queryPosts,
                queryOne: self.queryOnePosts,
                dataTypes: {
                    id: "int",
                    description: "string",
                    tags: "string",
                    category: "string",
                    timestamp: "time:Civil",
                    userId: "int"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    description: {columnName: "description", columnId: "B"},
                    tags: {columnName: "tags", columnId: "C"},
                    category: {columnName: "category", columnId: "D"},
                    timestamp: {columnName: "timestamp", columnId: "E"},
                    userId: {columnName: "userId", columnId: "F"}
                },
                associationsMethods: {"comments": self.queryPostComments}
            },
            [FOLLOW] : {
                entityName: "Follow",
                tableName: "Follow",
                keyFields: ["id"],
                range: "A:D",
                query: self.queryFollows,
                queryOne: self.queryOneFollows,
                dataTypes: {
                    id: "int",
                    leaderId: "int",
                    followerId: "int",
                    timestamp: "time:Civil"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    leaderId: {columnName: "leaderId", columnId: "B"},
                    followerId: {columnName: "followerId", columnId: "C"},
                    timestamp: {columnName: "timestamp", columnId: "D"}
                }
            },
            [COMMENT] : {
                entityName: "Comment",
                tableName: "Comment",
                keyFields: ["id"],
                range: "A:E",
                query: self.queryComments,
                queryOne: self.queryOneComments,
                dataTypes: {
                    id: "int",
                    comment: "string",
                    timesteamp: "time:Civil",
                    userId: "int",
                    postId: "int"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    comment: {columnName: "comment", columnId: "B"},
                    timesteamp: {columnName: "timesteamp", columnId: "C"},
                    userId: {columnName: "userId", columnId: "D"},
                    postId: {columnName: "postId", columnId: "E"}
                }
            }
        };
        sheets:ConnectionConfig sheetsClientConfig = {
            auth: {
                clientId: clientId,
                clientSecret: clientSecret,
                refreshUrl: sheets:REFRESH_URL,
                refreshToken: refreshToken
            }
        };
        http:ClientConfiguration httpClientConfiguration = {
            auth: {
                clientId: clientId,
                clientSecret: clientSecret,
                refreshUrl: sheets:REFRESH_URL,
                refreshToken: refreshToken
            }
        };
        http:Client|error httpClient = new ("https://docs.google.com/spreadsheets", httpClientConfiguration);
        if httpClient is error {
            return <persist:Error>error(httpClient.message());
        }
        sheets:Client|error googleSheetClient = new (sheetsClientConfig);
        if googleSheetClient is error {
            return <persist:Error>error(googleSheetClient.message());
        }
        self.googleSheetClient = googleSheetClient;
        self.httpClient = httpClient;
        map<int> sheetIds = check persist:getSheetIds(self.googleSheetClient, metadata, spreadsheetId);
        self.persistClients = {
            [USER] : check new (self.googleSheetClient, self.httpClient, metadata.get(USER), spreadsheetId, sheetIds.get(USER)),
            [POST] : check new (self.googleSheetClient, self.httpClient, metadata.get(POST), spreadsheetId, sheetIds.get(POST)),
            [FOLLOW] : check new (self.googleSheetClient, self.httpClient, metadata.get(FOLLOW), spreadsheetId, sheetIds.get(FOLLOW)),
            [COMMENT] : check new (self.googleSheetClient, self.httpClient, metadata.get(COMMENT), spreadsheetId, sheetIds.get(COMMENT))
        };
    }

    isolated resource function get users(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post users(UserInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(USER).runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        _ = check self.persistClients.get(USER).runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        _ = check self.persistClients.get(USER).runDeleteQuery(id);
        return result;
    }

    private function queryUsers(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        record {}[] outputArray = check from record {} 'object in usersStream
            select persist:filterRecord({
                ...'object
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneUsers(anydata key) returns record {}|persist:NotFoundError {
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        error? unionResult = from record {} 'object in usersStream
            where self.persistClients.get(USER).getKey('object) == key
            do {
                return {
                    ...'object
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryUsersStream(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get posts(PostTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(POST).runBatchInsertQuery(data);
        return from PostInsert inserted in data
            select inserted.id;
    }

    resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        _ = check self.persistClients.get(POST).runUpdateQuery(id, value);
        return self->/posts/[id].get();
    }

    resource function delete posts/[int id]() returns Post|persist:Error {
        Post result = check self->/posts/[id].get();
        _ = check self.persistClients.get(POST).runDeleteQuery(id);
        return result;
    }

    private function queryPosts(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Post, persist:Error?> postsStream = self.queryPostsStream();
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        record {}[] outputArray = check from record {} 'object in postsStream
            outer join var user in usersStream on ['object.userId] equals [user?.id]
            select persist:filterRecord({
                ...'object,
                "user": user
            }, fields);
        return outputArray.toStream();
    }

    private function queryOnePosts(anydata key) returns record {}|persist:NotFoundError {
        stream<Post, persist:Error?> postsStream = self.queryPostsStream();
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        error? unionResult = from record {} 'object in postsStream
            where self.persistClients.get(POST).getKey('object) == key
            outer join var user in usersStream on ['object.userId] equals [user?.id]
            do {
                return {
                    ...'object,
                    "user": user
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryPostsStream(PostTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get follows(FollowTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(FOLLOW).runBatchInsertQuery(data);
        return from FollowInsert inserted in data
            select inserted.id;
    }

    resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        _ = check self.persistClients.get(FOLLOW).runUpdateQuery(id, value);
        return self->/follows/[id].get();
    }

    resource function delete follows/[int id]() returns Follow|persist:Error {
        Follow result = check self->/follows/[id].get();
        _ = check self.persistClients.get(FOLLOW).runDeleteQuery(id);
        return result;
    }

    private function queryFollows(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Follow, persist:Error?> followsStream = self.queryFollowsStream();
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        record {}[] outputArray = check from record {} 'object in followsStream
            outer join var leader in usersStream on ['object.leaderId] equals [leader?.id]
            outer join var follower in usersStream on ['object.followerId] equals [follower?.id]
            select persist:filterRecord({
                ...'object,
                "leader": leader,
                "follower": follower
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneFollows(anydata key) returns record {}|persist:NotFoundError {
        stream<Follow, persist:Error?> followsStream = self.queryFollowsStream();
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        error? unionResult = from record {} 'object in followsStream
            where self.persistClients.get(FOLLOW).getKey('object) == key
            outer join var leader in usersStream on ['object.leaderId] equals [leader?.id]
            outer join var follower in usersStream on ['object.followerId] equals [follower?.id]
            do {
                return {
                    ...'object,
                    "leader": leader,
                    "follower": follower
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryFollowsStream(FollowTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    isolated resource function get comments(CommentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    isolated resource function get comments/[int id](CommentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    resource function post comments(CommentInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(COMMENT).runBatchInsertQuery(data);
        return from CommentInsert inserted in data
            select inserted.id;
    }

    resource function put comments/[int id](CommentUpdate value) returns Comment|persist:Error {
        _ = check self.persistClients.get(COMMENT).runUpdateQuery(id, value);
        return self->/comments/[id].get();
    }

    resource function delete comments/[int id]() returns Comment|persist:Error {
        Comment result = check self->/comments/[id].get();
        _ = check self.persistClients.get(COMMENT).runDeleteQuery(id);
        return result;
    }

    private function queryComments(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Comment, persist:Error?> commentsStream = self.queryCommentsStream();
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        stream<Post, persist:Error?> postsStream = self.queryPostsStream();
        record {}[] outputArray = check from record {} 'object in commentsStream
            outer join var user in usersStream on ['object.userId] equals [user?.id]
            outer join var post in postsStream on ['object.postId] equals [post?.id]
            select persist:filterRecord({
                ...'object,
                "user": user,
                "post": post
            }, fields);
        return outputArray.toStream();
    }

    private function queryOneComments(anydata key) returns record {}|persist:NotFoundError {
        stream<Comment, persist:Error?> commentsStream = self.queryCommentsStream();
        stream<User, persist:Error?> usersStream = self.queryUsersStream();
        stream<Post, persist:Error?> postsStream = self.queryPostsStream();
        error? unionResult = from record {} 'object in commentsStream
            where self.persistClients.get(COMMENT).getKey('object) == key
            outer join var user in usersStream on ['object.userId] equals [user?.id]
            outer join var post in postsStream on ['object.postId] equals [post?.id]
            do {
                return {
                    ...'object,
                    "user": user,
                    "post": post
                };
            };
        if unionResult is error {
            return <persist:NotFoundError>error(unionResult.message());
        }
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    private isolated function queryCommentsStream(CommentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    private function queryUserFollowers(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Follow, persist:Error?> followsStream = self.queryFollowsStream();
        return from record {} 'object in followsStream
            where 'object.leaderId == value["id"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryUserComments(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Comment, persist:Error?> commentsStream = self.queryCommentsStream();
        return from record {} 'object in commentsStream
            where 'object.userId == value["id"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryUserPosts(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Post, persist:Error?> postsStream = self.queryPostsStream();
        return from record {} 'object in postsStream
            where 'object.userId == value["id"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryPostComments(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Comment, persist:Error?> commentsStream = self.queryCommentsStream();
        return from record {} 'object in commentsStream
            where 'object.postId == value["id"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryUserFollowing(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Follow, persist:Error?> followsStream = self.queryFollowsStream();
        return from record {} 'object in followsStream
            where 'object.followerId == value["id"]
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    public isolated function close() returns persist:Error? {
        return ();
    }
}
