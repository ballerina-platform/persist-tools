// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    int userId;
    int multipleassociationsId;
|};

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
    int userId?;
    int multipleassociationsId?;
|};

public type User record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

public type MultipleAssociations record {|
    readonly int id;
    string name;
|};

public type MultipleAssociationsInsert MultipleAssociations;

public type MultipleAssociationsUpdate record {|
    string name?;
|};

