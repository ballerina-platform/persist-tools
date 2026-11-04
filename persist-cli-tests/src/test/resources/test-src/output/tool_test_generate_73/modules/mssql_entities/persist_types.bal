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

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    CarOptionalized[] cars?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    UserGender gender?;
    string nic?;
    decimal? salary?;
|};

# Represents the `Car` entity.
public type Car record {|
    readonly int id;
    string name;
    string model;
    int ownerId;
|};

# Represents `Car` with every field optional, for projected reads.
public type CarOptionalized record {|
    int id?;
    string name?;
    string model?;
    int ownerId?;
|};

# Represents `Car` with its related entities, for projected reads.
public type CarWithRelations record {|
    *CarOptionalized;
    UserOptionalized owner?;
|};

# The `Car` type or a projection of it, for reads.
public type CarTargetType typedesc<CarWithRelations>;

# The fields to insert a new `Car`.
public type CarInsert Car;

# The fields to update on an existing `Car`.
public type CarUpdate record {|
    string name?;
    string model?;
    int ownerId?;
|};

# Represents the `Person` entity. Description.
public type Person record {|
    readonly string name;
    int age;
    string nic;
    decimal salary;
|};

# Represents `Person` with every field optional, for projected reads.
public type PersonOptionalized record {|
    string name?;
    int age?;
    string nic?;
    decimal salary?;
|};

# The `Person` type or a projection of it, for reads.
public type PersonTargetType typedesc<PersonOptionalized>;

# The fields to insert a new `Person`.
public type PersonInsert Person;

# The fields to update on an existing `Person`.
public type PersonUpdate record {|
    int age?;
    string nic?;
    decimal salary?;
|};

# Represents the `Person2` entity. Description.
public type Person2 record {|
    readonly string name;
    int age;
    string nic;
    decimal salary;
|};

# Represents `Person2` with every field optional, for projected reads.
public type Person2Optionalized record {|
    string name?;
    int age?;
    string nic?;
    decimal salary?;
|};

# The `Person2` type or a projection of it, for reads.
public type Person2TargetType typedesc<Person2Optionalized>;

# The fields to insert a new `Person2`.
public type Person2Insert Person2;

# The fields to update on an existing `Person2`.
public type Person2Update record {|
    int age?;
    string nic?;
    decimal salary?;
|};

