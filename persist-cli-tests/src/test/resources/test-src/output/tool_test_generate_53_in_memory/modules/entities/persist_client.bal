// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.inmemory;

const USER = "users";
const POST = "posts";
const FOLLOW = "follows";
final isolated table<User> key(id) usersTable = table [];
final isolated table<Post> key(id) postsTable = table [];
final isolated table<Follow> key(id) followsTable = table [];

# In-Memory persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final map<inmemory:InMemoryClient> persistClients;

    public isolated function init() returns persist:Error? {
        final map<inmemory:TableMetadata> metadata = {
            [USER]: {
                keyFields: ["id"],
                query: queryUsers,
                queryOne: queryOneUsers,
                associationsMethods: {"posts": queryUserPosts}
            },
            [POST]: {
                keyFields: ["id"],
                query: queryPosts,
                queryOne: queryOnePosts
            },
            [FOLLOW]: {
                keyFields: ["id"],
                query: queryFollows,
                queryOne: queryOneFollows
            }
        };
        self.persistClients = {
            [USER]: check new (metadata.get(USER).cloneReadOnly()),
            [POST]: check new (metadata.get(POST).cloneReadOnly()),
            [FOLLOW]: check new (metadata.get(FOLLOW).cloneReadOnly())
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
    isolated resource function get users(UserTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from User table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into User table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
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

    # Update row in User table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
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

    # Delete row from User table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete users/[int id]() returns User|persist:Error {
        lock {
            if !usersTable.hasKey(id) {
                return persist:getNotFoundError("User", id);
            }
            return usersTable.remove(id).clone();
        }
    }

    # Get rows from Post table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get posts(PostTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from Post table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Post table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
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

    # Update row in Post table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
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

    # Delete row from Post table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        lock {
            if !postsTable.hasKey(id) {
                return persist:getNotFoundError("Post", id);
            }
            return postsTable.remove(id).clone();
        }
    }

    # Get rows from Follow table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get follows(FollowTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from Follow table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Follow table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
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

    # Update row in Follow table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
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

    # Delete row from Follow table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        lock {
            if !followsTable.hasKey(id) {
                return persist:getNotFoundError("Follow", id);
            }
            return followsTable.remove(id).clone();
        }
    }

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
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

