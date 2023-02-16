// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    string? gender;
    int userId;
|};

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
    string? gender?;
    int userId?;
|};

public type User record {|
    readonly int id;
|};

public type UserInsert User;

public type UserUpdate record {|
|};

