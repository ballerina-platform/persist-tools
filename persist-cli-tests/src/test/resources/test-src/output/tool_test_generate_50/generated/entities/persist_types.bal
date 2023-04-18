// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type User record {|
    readonly int id;
    string name;
|};

public type UserOptionalized record {|
    int id?;
    string name?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    FollowOptionalized follow?;
    FollowOptionalized follow1?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
|};

public type Follow record {|
    readonly int id;
    int followId;
    int follow1Id;
|};

public type FollowOptionalized record {|
    int id?;
    int followId?;
    int follow1Id?;
|};

public type FollowWithRelations record {|
    *FollowOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

public type FollowTargetType typedesc<FollowWithRelations>;

public type FollowInsert Follow;

public type FollowUpdate record {|
    int followId?;
    int follow1Id?;
|};

