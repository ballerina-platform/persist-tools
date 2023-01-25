// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities1.
// It should not be modified by hand.

public type MultipleAssociations record {|
    readonly int id;
    string name;
    int profileId;
|};

public type MultipleAssociationsInsert MultipleAssociations;

public type MultipleAssociationsUpdate record {|
    string name?;
    int profileId?;
|};

public type Profile record {|
    readonly int id;
    string name;
|};

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
|};

