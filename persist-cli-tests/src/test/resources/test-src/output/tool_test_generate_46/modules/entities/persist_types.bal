// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    time:Date birthDate?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    PostOptionalized[] posts?;
    FollowerOptionalized[] followers?;
    FollowerOptionalized[] leaders?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    time:Date birthDate?;
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

# Represents the `Follower` entity.
public type Follower record {|
    readonly int id;
    time:Civil created_date;
    int leaderId;
    int followerId;
|};

# Represents `Follower` with every field optional, for projected reads.
public type FollowerOptionalized record {|
    int id?;
    time:Civil created_date?;
    int leaderId?;
    int followerId?;
|};

# Represents `Follower` with its related entities, for projected reads.
public type FollowerWithRelations record {|
    *FollowerOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

# The `Follower` type or a projection of it, for reads.
public type FollowerTargetType typedesc<FollowerWithRelations>;

# The fields to insert a new `Follower`.
public type FollowerInsert Follower;

# The fields to update on an existing `Follower`.
public type FollowerUpdate record {|
    time:Civil created_date?;
    int leaderId?;
    int followerId?;
|};

