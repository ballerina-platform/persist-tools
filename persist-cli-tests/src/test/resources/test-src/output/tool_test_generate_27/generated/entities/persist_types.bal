// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    int userId;
    int multipleassociationsId;
|};

public type ProfileOptionalized record {|
    readonly int id?;
    string name?;
    int userId?;
    int multipleassociationsId?;
|};

public type ProfileWithRelations record {|
    *ProfileOptionalized;
    UserOptionalized owner?;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

public type ProfileTargetType typedesc<ProfileWithRelations>;

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

public type UserOptionalized record {|
    readonly int id?;
    string name?;
    int multipleassociationsId?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    ProfileOptionalized profile?;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

public type MultipleAssociations record {|
    readonly int id;
    string name;
|};

public type MultipleAssociationsOptionalized record {|
    readonly int id?;
    string name?;
|};

public type MultipleAssociationsWithRelations record {|
    *MultipleAssociationsOptionalized;
    ProfileOptionalized profile?;
    UserOptionalized owner?;
|};

public type MultipleAssociationsTargetType typedesc<MultipleAssociationsWithRelations>;

public type MultipleAssociationsInsert MultipleAssociations;

public type MultipleAssociationsUpdate record {|
    string name?;
|};

