// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.redis as predis;
import ballerinax/redis;

const USER = "users";
const POST = "posts";
const FOLLOW = "follows";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final redis:Client dbClient;

    private final map<predis:RedisClient> persistClients;

    private final record {|predis:RedisMetadata...;|} & readonly metadata = {
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
                "posts[].created_date": {relation: {entityName: "posts", refField: "created_date", refFieldDataType: predis:DATE}},
                "posts[].userId": {relation: {entityName: "posts", refField: "userId", refFieldDataType: predis:INT}},
                "leader.id": {relation: {entityName: "leader", refField: "id", refFieldDataType: predis:INT}},
                "leader.leaderId": {relation: {entityName: "leader", refField: "leaderId", refFieldDataType: predis:INT}},
                "leader.followerId": {relation: {entityName: "leader", refField: "followerId", refFieldDataType: predis:INT}},
                "leader.created_date": {relation: {entityName: "leader", refField: "created_date", refFieldDataType: predis:DATE}},
                "follower.id": {relation: {entityName: "follower", refField: "id", refFieldDataType: predis:INT}},
                "follower.leaderId": {relation: {entityName: "follower", refField: "leaderId", refFieldDataType: predis:INT}},
                "follower.followerId": {relation: {entityName: "follower", refField: "followerId", refFieldDataType: predis:INT}},
                "follower.created_date": {relation: {entityName: "follower", refField: "created_date", refFieldDataType: predis:DATE}}
            },
            keyFields: ["id"],
            refMetadata: {
                posts: {entity: Post, fieldName: "posts", refCollection: "Post", refFields: ["userId"], joinFields: ["id"], 'type: predis:MANY_TO_ONE},
                leader: {entity: Follow, fieldName: "leader", refCollection: "Follow", refFields: ["leaderId"], joinFields: ["id"], 'type: predis:ONE_TO_ONE},
                follower: {entity: Follow, fieldName: "follower", refCollection: "Follow", refFields: ["followerId"], joinFields: ["id"], 'type: predis:ONE_TO_ONE}
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
                created_date: {fieldName: "created_date", fieldDataType: predis:DATE},
                userId: {fieldName: "userId", fieldDataType: predis:INT},
                "user.id": {relation: {entityName: "user", refField: "id", refFieldDataType: predis:INT}},
                "user.name": {relation: {entityName: "user", refField: "name", refFieldDataType: predis:STRING}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate", refFieldDataType: predis:DATE}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber", refFieldDataType: predis:STRING}}
            },
            keyFields: ["id"],
            refMetadata: {user: {entity: User, fieldName: "user", refCollection: "User", refMetaDataKey: "posts", refFields: ["id"], joinFields: ["userId"], 'type: predis:ONE_TO_MANY}}
        },
        [FOLLOW]: {
            entityName: "Follow",
            collectionName: "Follow",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                leaderId: {fieldName: "leaderId", fieldDataType: predis:INT},
                followerId: {fieldName: "followerId", fieldDataType: predis:INT},
                created_date: {fieldName: "created_date", fieldDataType: predis:DATE},
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
                leader: {entity: User, fieldName: "leader", refCollection: "User", refMetaDataKey: "leader", refFields: ["id"], joinFields: ["leaderId"], 'type: predis:ONE_TO_ONE},
                follower: {entity: User, fieldName: "follower", refCollection: "User", refMetaDataKey: "follower", refFields: ["id"], joinFields: ["followerId"], 'type: predis:ONE_TO_ONE}
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
            [USER]: check new (dbClient, self.metadata.get(USER)),
            [POST]: check new (dbClient, self.metadata.get(POST)),
            [FOLLOW]: check new (dbClient, self.metadata.get(FOLLOW))
        };
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

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

