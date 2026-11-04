// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Profile` entity.
public type Profile record {|
    readonly int id;
    string name;
    int ownerId;
    int multipleassociationsId;
|};

# Represents `Profile` with every field optional, for projected reads.
public type ProfileOptionalized record {|
    int id?;
    string name?;
    int ownerId?;
    int multipleassociationsId?;
|};

# Represents `Profile` with its related entities, for projected reads.
public type ProfileWithRelations record {|
    *ProfileOptionalized;
    UserOptionalized owner?;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

# The `Profile` type or a projection of it, for reads.
public type ProfileTargetType typedesc<ProfileWithRelations>;

# The fields to insert a new `Profile`.
public type ProfileInsert Profile;

# The fields to update on an existing `Profile`.
public type ProfileUpdate record {|
    string name?;
    int ownerId?;
    int multipleassociationsId?;
|};

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    int multipleassociationsId?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    ProfileOptionalized profile?;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

# Represents the `MultipleAssociations` entity.
public type MultipleAssociations record {|
    readonly int id;
    string name;
|};

# Represents `MultipleAssociations` with every field optional, for projected reads.
public type MultipleAssociationsOptionalized record {|
    int id?;
    string name?;
|};

# Represents `MultipleAssociations` with its related entities, for projected reads.
public type MultipleAssociationsWithRelations record {|
    *MultipleAssociationsOptionalized;
    ProfileOptionalized profile?;
    UserOptionalized owner?;
|};

# The `MultipleAssociations` type or a projection of it, for reads.
public type MultipleAssociationsTargetType typedesc<MultipleAssociationsWithRelations>;

# The fields to insert a new `MultipleAssociations`.
public type MultipleAssociationsInsert MultipleAssociations;

# The fields to update on an existing `MultipleAssociations`.
public type MultipleAssociationsUpdate record {|
    string name?;
|};

