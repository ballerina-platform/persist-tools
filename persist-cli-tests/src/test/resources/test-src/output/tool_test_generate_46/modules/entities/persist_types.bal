// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

public type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
|};

public type UserOptionalized record {|
    int id?;
    string name?;
    time:Date birthDate?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    PostOptionalized[] posts?;
    FollowerOptionalized[] followers?;
    FollowerOptionalized[] leaders?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    time:Date birthDate?;
|};

public type Post record {|
    readonly int id;
    string description;
    string tags;
    string category;
    time:Date created_date;
    int userId;
|};

public type PostOptionalized record {|
    int id?;
    string description?;
    string tags?;
    string category?;
    time:Date created_date?;
    int userId?;
|};

public type PostWithRelations record {|
    *PostOptionalized;
    UserOptionalized user?;
|};

public type PostTargetType typedesc<PostWithRelations>;

public type PostInsert Post;

public type PostUpdate record {|
    string description?;
    string tags?;
    string category?;
    time:Date created_date?;
    int userId?;
|};

public type Follower record {|
    readonly int id;
    time:Civil created_date;
    int leaderId;
    int followerId;
|};

public type FollowerOptionalized record {|
    int id?;
    time:Civil created_date?;
    int leaderId?;
    int followerId?;
|};

public type FollowerWithRelations record {|
    *FollowerOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

public type FollowerTargetType typedesc<FollowerWithRelations>;

public type FollowerInsert Follower;

public type FollowerUpdate record {|
    time:Civil created_date?;
    int leaderId?;
    int followerId?;
|};

