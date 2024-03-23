// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.redis as predis;
import ballerinax/redis;

const FOLLOW = "follows";
const USER = "users";
const POST = "posts";
const COMMENT = "comments";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final redis:Client dbClient;

    private final map<predis:RedisClient> persistClients;

    private final record {|predis:RedisMetadata...;|} & readonly metadata = {
        [FOLLOW]: {
            entityName: "Follow",
            collectionName: "Follow",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                leaderId: {fieldName: "leaderId", fieldDataType: predis:INT},
                followerId: {fieldName: "followerId", fieldDataType: predis:INT},
                timestamp: {fieldName: "timestamp", fieldDataType: predis:STRING},
                "leader.id": {relation: {entityName: "leader", refField: "id", refFieldDataType: predis:INT}},
                "leader.name": {relation: {entityName: "leader", refField: "name", refFieldDataType: predis:STRING}},
                "leader.birthDate": {relation: {entityName: "leader", refField: "birthDate", refFieldDataType: predis:DATE}},
                "leader.mobileNumber": {relation: {entityName: "leader", refField: "mobileNumber", refFieldDataType: predis:STRING}},
                "follower.id": {relation: {entityName: "follower", refField: "id", refFieldDataType: predis:INT}},
                "follower.name": {relation: {entityName: "follower", refField: "name", refFieldDataType: predis:STRING}},
                "follower.birthDate": {relation: {entityName: "follower", refField: "birthDate", refFieldDataType: predis:DATE}},
                "follower.mobileNumber": {relation: {entityName: "follower", refField: "mobileNumber", refFieldDataType: predis:STRING}}
            },
            keyFields: ["id"],
            refMetadata: {
                leader: {entity: User, fieldName: "leader", refCollection: "User", refMetaDataKey: "followers", refFields: ["id"], joinFields: ["leaderId"], 'type: predis:ONE_TO_MANY},
                follower: {entity: User, fieldName: "follower", refCollection: "User", refMetaDataKey: "following", refFields: ["id"], joinFields: ["followerId"], 'type: predis:ONE_TO_MANY}
            }
        },
        [USER]: {
            entityName: "User",
            collectionName: "User",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                name: {fieldName: "name", fieldDataType: predis:STRING},
                birthDate: {fieldName: "birthDate", fieldDataType: predis:DATE},
                mobileNumber: {fieldName: "mobileNumber", fieldDataType: predis:STRING},
                "posts[].id": {relation: {entityName: "posts", refField: "id", refFieldDataType: predis:INT}},
                "posts[].description": {relation: {entityName: "posts", refField: "description", refFieldDataType: predis:STRING}},
                "posts[].tags": {relation: {entityName: "posts", refField: "tags", refFieldDataType: predis:STRING}},
                "posts[].category": {relation: {entityName: "posts", refField: "category", refFieldDataType: predis:STRING}},
                "posts[].timestamp": {relation: {entityName: "posts", refField: "timestamp", refFieldDataType: predis:STRING}},
                "posts[].userId": {relation: {entityName: "posts", refField: "userId", refFieldDataType: predis:INT}},
                "comments[].id": {relation: {entityName: "comments", refField: "id", refFieldDataType: predis:INT}},
                "comments[].comment": {relation: {entityName: "comments", refField: "comment", refFieldDataType: predis:STRING}},
                "comments[].timesteamp": {relation: {entityName: "comments", refField: "timesteamp", refFieldDataType: predis:STRING}},
                "comments[].userId": {relation: {entityName: "comments", refField: "userId", refFieldDataType: predis:INT}},
                "comments[].postId": {relation: {entityName: "comments", refField: "postId", refFieldDataType: predis:INT}},
                "followers[].id": {relation: {entityName: "followers", refField: "id", refFieldDataType: predis:INT}},
                "followers[].leaderId": {relation: {entityName: "followers", refField: "leaderId", refFieldDataType: predis:INT}},
                "followers[].followerId": {relation: {entityName: "followers", refField: "followerId", refFieldDataType: predis:INT}},
                "followers[].timestamp": {relation: {entityName: "followers", refField: "timestamp", refFieldDataType: predis:STRING}},
                "following[].id": {relation: {entityName: "following", refField: "id", refFieldDataType: predis:INT}},
                "following[].leaderId": {relation: {entityName: "following", refField: "leaderId", refFieldDataType: predis:INT}},
                "following[].followerId": {relation: {entityName: "following", refField: "followerId", refFieldDataType: predis:INT}},
                "following[].timestamp": {relation: {entityName: "following", refField: "timestamp", refFieldDataType: predis:STRING}}
            },
            keyFields: ["id"],
            refMetadata: {
                posts: {entity: Post, fieldName: "posts", refCollection: "Post", refFields: ["userId"], joinFields: ["id"], 'type: predis:MANY_TO_ONE},
                comments: {entity: Comment, fieldName: "comments", refCollection: "Comment", refFields: ["userId"], joinFields: ["id"], 'type: predis:MANY_TO_ONE},
                followers: {entity: Follow, fieldName: "followers", refCollection: "Follow", refFields: ["leaderId"], joinFields: ["id"], 'type: predis:MANY_TO_ONE},
                following: {entity: Follow, fieldName: "following", refCollection: "Follow", refFields: ["followerId"], joinFields: ["id"], 'type: predis:MANY_TO_ONE}
            }
        },
        [POST]: {
            entityName: "Post",
            collectionName: "Post",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                description: {fieldName: "description", fieldDataType: predis:STRING},
                tags: {fieldName: "tags", fieldDataType: predis:STRING},
                category: {fieldName: "category", fieldDataType: predis:STRING},
                timestamp: {fieldName: "timestamp", fieldDataType: predis:STRING},
                userId: {fieldName: "userId", fieldDataType: predis:INT},
                "user.id": {relation: {entityName: "user", refField: "id", refFieldDataType: predis:INT}},
                "user.name": {relation: {entityName: "user", refField: "name", refFieldDataType: predis:STRING}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate", refFieldDataType: predis:DATE}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber", refFieldDataType: predis:STRING}},
                "comments[].id": {relation: {entityName: "comments", refField: "id", refFieldDataType: predis:INT}},
                "comments[].comment": {relation: {entityName: "comments", refField: "comment", refFieldDataType: predis:STRING}},
                "comments[].timesteamp": {relation: {entityName: "comments", refField: "timesteamp", refFieldDataType: predis:STRING}},
                "comments[].userId": {relation: {entityName: "comments", refField: "userId", refFieldDataType: predis:INT}},
                "comments[].postId": {relation: {entityName: "comments", refField: "postId", refFieldDataType: predis:INT}}
            },
            keyFields: ["id"],
            refMetadata: {
                user: {entity: User, fieldName: "user", refCollection: "User", refMetaDataKey: "posts", refFields: ["id"], joinFields: ["userId"], 'type: predis:ONE_TO_MANY},
                comments: {entity: Comment, fieldName: "comments", refCollection: "Comment", refFields: ["postId"], joinFields: ["id"], 'type: predis:MANY_TO_ONE}
            }
        },
        [COMMENT]: {
            entityName: "Comment",
            collectionName: "Comment",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                comment: {fieldName: "comment", fieldDataType: predis:STRING},
                timesteamp: {fieldName: "timesteamp", fieldDataType: predis:STRING},
                userId: {fieldName: "userId", fieldDataType: predis:INT},
                postId: {fieldName: "postId", fieldDataType: predis:INT},
                "user.id": {relation: {entityName: "user", refField: "id", refFieldDataType: predis:INT}},
                "user.name": {relation: {entityName: "user", refField: "name", refFieldDataType: predis:STRING}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate", refFieldDataType: predis:DATE}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber", refFieldDataType: predis:STRING}},
                "post.id": {relation: {entityName: "post", refField: "id", refFieldDataType: predis:INT}},
                "post.description": {relation: {entityName: "post", refField: "description", refFieldDataType: predis:STRING}},
                "post.tags": {relation: {entityName: "post", refField: "tags", refFieldDataType: predis:STRING}},
                "post.category": {relation: {entityName: "post", refField: "category", refFieldDataType: predis:STRING}},
                "post.timestamp": {relation: {entityName: "post", refField: "timestamp", refFieldDataType: predis:STRING}},
                "post.userId": {relation: {entityName: "post", refField: "userId", refFieldDataType: predis:INT}}
            },
            keyFields: ["id"],
            refMetadata: {
                user: {entity: User, fieldName: "user", refCollection: "User", refMetaDataKey: "comments", refFields: ["id"], joinFields: ["userId"], 'type: predis:ONE_TO_MANY},
                post: {entity: Post, fieldName: "post", refCollection: "Post", refMetaDataKey: "comments", refFields: ["id"], joinFields: ["postId"], 'type: predis:ONE_TO_MANY}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        redis:Client|error dbClient = new (redis);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [FOLLOW]: check new (dbClient, self.metadata.get(FOLLOW)),
            [USER]: check new (dbClient, self.metadata.get(USER)),
            [POST]: check new (dbClient, self.metadata.get(POST)),
            [COMMENT]: check new (dbClient, self.metadata.get(COMMENT))
        };
    }

    isolated resource function get follows(FollowTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(FOLLOW);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from FollowInsert inserted in data
            select inserted.id;
    }

    isolated resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(FOLLOW);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/follows/[id].get();
    }

    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        Follow result = check self->/follows/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(FOLLOW);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get users(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(USER);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(USER);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(USER);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get posts(PostTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(POST);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from PostInsert inserted in data
            select inserted.id;
    }

    isolated resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(POST);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/posts/[id].get();
    }

    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        Post result = check self->/posts/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(POST);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get comments(CommentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    isolated resource function get comments/[int id](CommentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    isolated resource function post comments(CommentInsert[] data) returns int[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(COMMENT);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from CommentInsert inserted in data
            select inserted.id;
    }

    isolated resource function put comments/[int id](CommentUpdate value) returns Comment|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(COMMENT);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/comments/[id].get();
    }

    isolated resource function delete comments/[int id]() returns Comment|persist:Error {
        Comment result = check self->/comments/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(COMMENT);
        }
        _ = check redisClient.runDeleteQuery(id);
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

