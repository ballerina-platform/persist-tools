// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
    string mobileNumber;
|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    PostOptionalized[] posts?;
    FollowOptionalized leader?;
    FollowOptionalized follower?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

# Represents the `Post` entity.
public type Post record {|
    readonly int id;
    string description;
    string tags;
    string category;
    time:Date created_date;
    int userId;
|};

# Represents `Post` with every field optional, for projected reads.
public type PostOptionalized record {|
    int id?;
    string description?;
    string tags?;
    string category?;
    time:Date created_date?;
    int userId?;
|};

# Represents `Post` with its related entities, for projected reads.
public type PostWithRelations record {|
    *PostOptionalized;
    UserOptionalized user?;
|};

# The `Post` type or a projection of it, for reads.
public type PostTargetType typedesc<PostWithRelations>;

# The fields to insert a new `Post`.
public type PostInsert Post;

# The fields to update on an existing `Post`.
public type PostUpdate record {|
    string description?;
    string tags?;
    string category?;
    time:Date created_date?;
    int userId?;
|};

# Represents the `Follow` entity.
public type Follow record {|
    readonly int id;
    int leaderId;
    int followerId;
    time:Date created_date;
|};

# Represents `Follow` with every field optional, for projected reads.
public type FollowOptionalized record {|
    int id?;
    int leaderId?;
    int followerId?;
    time:Date created_date?;
|};

# Represents `Follow` with its related entities, for projected reads.
public type FollowWithRelations record {|
    *FollowOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

# The `Follow` type or a projection of it, for reads.
public type FollowTargetType typedesc<FollowWithRelations>;

# The fields to insert a new `Follow`.
public type FollowInsert Follow;

# The fields to update on an existing `Follow`.
public type FollowUpdate record {|
    int leaderId?;
    int followerId?;
    time:Date created_date?;
|};
