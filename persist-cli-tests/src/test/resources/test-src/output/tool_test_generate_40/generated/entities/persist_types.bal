// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    string? gender;
    int userId;
|};

public type ProfileOptionalized record {|
    int id?;
    string name?;
    string? gender?;
    int userId?;
|};

public type ProfileWithRelations record {|
    *ProfileOptionalized;
    UserOptionalized owner?;
|};

public type ProfileTargetType typedesc<ProfileWithRelations>;

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
    string? gender?;
    int userId?;
|};

public type User record {|
    readonly int id;
|};

public type UserOptionalized record {|
    int id?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    ProfileOptionalized profile?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
|};

