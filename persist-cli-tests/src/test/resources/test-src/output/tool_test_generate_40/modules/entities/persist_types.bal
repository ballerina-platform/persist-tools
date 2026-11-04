// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Profile` entity.
public type Profile record {|
    readonly int id;
    string name;
    string? gender;
    int ownerId;
|};

# Represents `Profile` with every field optional, for projected reads.
public type ProfileOptionalized record {|
    int id?;
    string name?;
    string? gender?;
    int ownerId?;
|};

# Represents `Profile` with its related entities, for projected reads.
public type ProfileWithRelations record {|
    *ProfileOptionalized;
    UserOptionalized owner?;
|};

# The `Profile` type or a projection of it, for reads.
public type ProfileTargetType typedesc<ProfileWithRelations>;

# The fields to insert a new `Profile`.
public type ProfileInsert Profile;

# The fields to update on an existing `Profile`.
public type ProfileUpdate record {|
    string name?;
    string? gender?;
    int ownerId?;
|};

# Represents the `User` entity.
public type User record {|
    readonly int id;
|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    ProfileOptionalized profile?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
|};

