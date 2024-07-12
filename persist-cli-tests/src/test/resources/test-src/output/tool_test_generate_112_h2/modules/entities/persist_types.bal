// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/time;

public type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
    string mobileNumber;

|};

public type UserOptionalized record {|
    int id?;
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    PostOptionalized[] posts?;
    CommentOptionalized[] comments?;
    FollowOptionalized[] followers?;
    FollowOptionalized[] following?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

public type Post record {|
    readonly int id;
    string description;
    string tags;
    string category;
    time:Civil timestamp;
    int userId;

|};

public type PostOptionalized record {|
    int id?;
    string description?;
    string tags?;
    string category?;
    time:Civil timestamp?;
    int userId?;
|};

public type PostWithRelations record {|
    *PostOptionalized;
    UserOptionalized user?;
    CommentOptionalized[] comments?;
|};

public type PostTargetType typedesc<PostWithRelations>;

public type PostInsert Post;

public type PostUpdate record {|
    string description?;
    string tags?;
    string category?;
    time:Civil timestamp?;
    int userId?;
|};

public type Follow record {|
    readonly int id;
    int leaderId;
    int followerId;
    time:Civil timestamp;
|};

public type FollowOptionalized record {|
    int id?;
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
|};

public type FollowWithRelations record {|
    *FollowOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

public type FollowTargetType typedesc<FollowWithRelations>;

public type FollowInsert Follow;

public type FollowUpdate record {|
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
|};

public type Comment record {|
    readonly int id;
    string comment;
    time:Civil timesteamp;
    int userId;
    int postId;
|};

public type CommentOptionalized record {|
    int id?;
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

public type CommentWithRelations record {|
    *CommentOptionalized;
    UserOptionalized user?;
    PostOptionalized post?;
|};

public type CommentTargetType typedesc<CommentWithRelations>;

public type CommentInsert Comment;

public type CommentUpdate record {|
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

