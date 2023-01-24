// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type User record {|
    readonly int id;
    string name;
|};

public type UserInsert User;

public type UserUpdate record {|
    string name?;
|};

