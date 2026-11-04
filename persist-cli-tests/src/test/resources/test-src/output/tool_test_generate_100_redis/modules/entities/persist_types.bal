// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

# Represents the `Follow` entity.
public type Follow record {|
    readonly int id;
    int leaderId;
    int followerId;
    time:Civil timestamp;
|};

# Represents `Follow` with every field optional, for projected reads.
public type FollowOptionalized record {|
    int id?;
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
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
    time:Civil timestamp?;
|};

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
    CommentOptionalized[] comments?;
    FollowOptionalized[] followers?;
    FollowOptionalized[] following?;
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
    time:Civil timestamp;
    int userId;
|};

# Represents `Post` with every field optional, for projected reads.
public type PostOptionalized record {|
    int id?;
    string description?;
    string tags?;
    string category?;
    time:Civil timestamp?;
    int userId?;
|};

# Represents `Post` with its related entities, for projected reads.
public type PostWithRelations record {|
    *PostOptionalized;
    UserOptionalized user?;
    CommentOptionalized[] comments?;
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
    time:Civil timestamp?;
    int userId?;
|};

# Represents the `Comment` entity.
public type Comment record {|
    readonly int id;
    string comment;
    time:Civil timesteamp;
    int userId;
    int postId;
|};

# Represents `Comment` with every field optional, for projected reads.
public type CommentOptionalized record {|
    int id?;
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

# Represents `Comment` with its related entities, for projected reads.
public type CommentWithRelations record {|
    *CommentOptionalized;
    UserOptionalized user?;
    PostOptionalized post?;
|};

# The `Comment` type or a projection of it, for reads.
public type CommentTargetType typedesc<CommentWithRelations>;

# The fields to insert a new `Comment`.
public type CommentInsert Comment;

# The fields to update on an existing `Comment`.
public type CommentUpdate record {|
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};
