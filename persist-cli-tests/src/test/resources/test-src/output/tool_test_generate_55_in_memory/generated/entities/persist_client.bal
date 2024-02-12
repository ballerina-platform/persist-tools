// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.inmemory;

const USER = "users";
const POST = "posts";
const FOLLOW = "follows";
const COMMENT = "comments";
final isolated table<User> key(id) usersTable = table [];
final isolated table<Post> key(id) postsTable = table [];
final isolated table<Follow> key(id) followsTable = table [];
final isolated table<Comment> key(id) commentsTable = table [];

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final map<inmemory:InMemoryClient> persistClients;

    public isolated function init() returns persist:Error? {
        final map<inmemory:TableMetadata> metadata = {
            [USER]: {
                keyFields: ["id"],
                query: queryUsers,
                queryOne: queryOneUsers,
                associationsMethods: {
                    "posts": queryUserPosts,
                    "comments": queryUserComments,
                    "followers": queryUserFollowers,
                    "following": queryUserFollowing
                }
            },
            [POST]: {
                keyFields: ["id"],
                query: queryPosts,
                queryOne: queryOnePosts,
                associationsMethods: {"comments": queryPostComments}
            },
            [FOLLOW]: {
                keyFields: ["id"],
                query: queryFollows,
                queryOne: queryOneFollows
            },
            [COMMENT]: {
                keyFields: ["id"],
                query: queryComments,
                queryOne: queryOneComments
            }
        };
        self.persistClients = {
            [USER]: check new (metadata.get(USER).cloneReadOnly()),
            [POST]: check new (metadata.get(POST).cloneReadOnly()),
            [FOLLOW]: check new (metadata.get(FOLLOW).cloneReadOnly()),
            [COMMENT]: check new (metadata.get(COMMENT).cloneReadOnly())
        };
    }

    isolated resource function get users(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach UserInsert value in data {
            lock {
                if usersTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("User", value.id);
                }
                usersTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        lock {
            if !usersTable.hasKey(id) {
                return persist:getNotFoundError("User", id);
            }
            User user = usersTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                user[k] = v;
            }
            usersTable.put(user);
            return user.clone();
        }
    }

    isolated resource function delete users/[int id]() returns User|persist:Error {
        lock {
            if !usersTable.hasKey(id) {
                return persist:getNotFoundError("User", id);
            }
            return usersTable.remove(id).clone();
        }
    }

    isolated resource function get posts(PostTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach PostInsert value in data {
            lock {
                if postsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Post", value.id);
                }
                postsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    isolated resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        lock {
            if !postsTable.hasKey(id) {
                return persist:getNotFoundError("Post", id);
            }
            Post post = postsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                post[k] = v;
            }
            postsTable.put(post);
            return post.clone();
        }
    }

    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        lock {
            if !postsTable.hasKey(id) {
                return persist:getNotFoundError("Post", id);
            }
            return postsTable.remove(id).clone();
        }
    }

    isolated resource function get follows(FollowTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach FollowInsert value in data {
            lock {
                if followsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Follow", value.id);
                }
                followsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    isolated resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        lock {
            if !followsTable.hasKey(id) {
                return persist:getNotFoundError("Follow", id);
            }
            Follow follow = followsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                follow[k] = v;
            }
            followsTable.put(follow);
            return follow.clone();
        }
    }

    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        lock {
            if !followsTable.hasKey(id) {
                return persist:getNotFoundError("Follow", id);
            }
            return followsTable.remove(id).clone();
        }
    }

    isolated resource function get comments(CommentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get comments/[int id](CommentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post comments(CommentInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach CommentInsert value in data {
            lock {
                if commentsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Comment", value.id);
                }
                commentsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    isolated resource function put comments/[int id](CommentUpdate value) returns Comment|persist:Error {
        lock {
            if !commentsTable.hasKey(id) {
                return persist:getNotFoundError("Comment", id);
            }
            Comment comment = commentsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                comment[k] = v;
            }
            commentsTable.put(comment);
            return comment.clone();
        }
    }

    isolated resource function delete comments/[int id]() returns Comment|persist:Error {
        lock {
            if !commentsTable.hasKey(id) {
                return persist:getNotFoundError("Comment", id);
            }
            return commentsTable.remove(id).clone();
        }
    }

    public isolated function close() returns persist:Error? {
        return ();
    }
}

isolated function queryUsers(string[] fields) returns stream<record {}, persist:Error?> {
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    return from record {} 'object in usersClonedTable
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryOneUsers(anydata key) returns record {}|persist:NotFoundError {
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    from record {} 'object in usersClonedTable
    where persist:getKey('object, ["id"]) == key
    do {
        return {
            ...'object
        };
    };
    return persist:getNotFoundError("User", key);
}

isolated function queryPosts(string[] fields) returns stream<record {}, persist:Error?> {
    table<Post> key(id) postsClonedTable;
    lock {
        postsClonedTable = postsTable.clone();
    }
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    return from record {} 'object in postsClonedTable
        outer join var user in usersClonedTable on ['object.userId] equals [user?.id]
        select persist:filterRecord({
                                        ...'object,
                                        "user": user
                                    }, fields);
}

isolated function queryOnePosts(anydata key) returns record {}|persist:NotFoundError {
    table<Post> key(id) postsClonedTable;
    lock {
        postsClonedTable = postsTable.clone();
    }
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    from record {} 'object in postsClonedTable
    where persist:getKey('object, ["id"]) == key
    outer join var user in usersClonedTable on ['object.userId] equals [user?.id]
    do {
        return {
            ...'object,
            "user": user
        };
    };
    return persist:getNotFoundError("Post", key);
}

isolated function queryFollows(string[] fields) returns stream<record {}, persist:Error?> {
    table<Follow> key(id) followsClonedTable;
    lock {
        followsClonedTable = followsTable.clone();
    }
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    return from record {} 'object in followsClonedTable
        outer join var leader in usersClonedTable on ['object.leaderId] equals [leader?.id]
        outer join var follower in usersClonedTable on ['object.followerId] equals [follower?.id]
        select persist:filterRecord({
                                        ...'object,
                                        "leader": leader,
                                        "follower": follower
                                    }, fields);
}

isolated function queryOneFollows(anydata key) returns record {}|persist:NotFoundError {
    table<Follow> key(id) followsClonedTable;
    lock {
        followsClonedTable = followsTable.clone();
    }
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    from record {} 'object in followsClonedTable
    where persist:getKey('object, ["id"]) == key
    outer join var leader in usersClonedTable on ['object.leaderId] equals [leader?.id]
    outer join var follower in usersClonedTable on ['object.followerId] equals [follower?.id]
    do {
        return {
            ...'object,
            "leader": leader,
            "follower": follower
        };
    };
    return persist:getNotFoundError("Follow", key);
}

isolated function queryComments(string[] fields) returns stream<record {}, persist:Error?> {
    table<Comment> key(id) commentsClonedTable;
    lock {
        commentsClonedTable = commentsTable.clone();
    }
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    table<Post> key(id) postsClonedTable;
    lock {
        postsClonedTable = postsTable.clone();
    }
    return from record {} 'object in commentsClonedTable
        outer join var user in usersClonedTable on ['object.userId] equals [user?.id]
        outer join var post in postsClonedTable on ['object.postId] equals [post?.id]
        select persist:filterRecord({
                                        ...'object,
                                        "user": user,
                                        "post": post
                                    }, fields);
}

isolated function queryOneComments(anydata key) returns record {}|persist:NotFoundError {
    table<Comment> key(id) commentsClonedTable;
    lock {
        commentsClonedTable = commentsTable.clone();
    }
    table<User> key(id) usersClonedTable;
    lock {
        usersClonedTable = usersTable.clone();
    }
    table<Post> key(id) postsClonedTable;
    lock {
        postsClonedTable = postsTable.clone();
    }
    from record {} 'object in commentsClonedTable
    where persist:getKey('object, ["id"]) == key
    outer join var user in usersClonedTable on ['object.userId] equals [user?.id]
    outer join var post in postsClonedTable on ['object.postId] equals [post?.id]
    do {
        return {
            ...'object,
            "user": user,
            "post": post
        };
    };
    return persist:getNotFoundError("Comment", key);
}

isolated function queryUserPosts(record {} value, string[] fields) returns record {}[] {
    table<Post> key(id) postsClonedTable;
    lock {
        postsClonedTable = postsTable.clone();
    }
    return from record {} 'object in postsClonedTable
        where 'object.userId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryUserComments(record {} value, string[] fields) returns record {}[] {
    table<Comment> key(id) commentsClonedTable;
    lock {
        commentsClonedTable = commentsTable.clone();
    }
    return from record {} 'object in commentsClonedTable
        where 'object.userId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryUserFollowers(record {} value, string[] fields) returns record {}[] {
    table<Follow> key(id) followsClonedTable;
    lock {
        followsClonedTable = followsTable.clone();
    }
    return from record {} 'object in followsClonedTable
        where 'object.leaderId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryUserFollowing(record {} value, string[] fields) returns record {}[] {
    table<Follow> key(id) followsClonedTable;
    lock {
        followsClonedTable = followsTable.clone();
    }
    return from record {} 'object in followsClonedTable
        where 'object.followerId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryPostComments(record {} value, string[] fields) returns record {}[] {
    table<Comment> key(id) commentsClonedTable;
    lock {
        commentsClonedTable = commentsTable.clone();
    }
    return from record {} 'object in commentsClonedTable
        where 'object.postId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

