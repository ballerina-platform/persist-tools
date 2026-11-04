// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public enum UserGender {
    MALE,
    FEMALE
}

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    UserGender gender;
    string nic;
    decimal? salary;
|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    UserGender gender?;
    string nic?;
    decimal? salary?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserOptionalized>;

# The fields to insert a new `User`.
public type UserInsert record {|
    string name;
    UserGender gender;
    string nic;
    decimal? salary;
|};

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    UserGender gender?;
    string nic?;
    decimal? salary?;
|};

