// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    string nic;
    decimal? salary;

|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    string nic?;
    decimal? salary?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    CarOptionalized drives?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    string nic?;
    decimal? salary?;
|};

# Represents the `Car` entity.
public type Car record {|
    readonly int id;
    string name;
    string model;
    int driverId;
|};

# Represents `Car` with every field optional, for projected reads.
public type CarOptionalized record {|
    int id?;
    string name?;
    string model?;
    int driverId?;
|};

# Represents `Car` with its related entities, for projected reads.
public type CarWithRelations record {|
    *CarOptionalized;
    UserOptionalized driver?;
|};

# The `Car` type or a projection of it, for reads.
public type CarTargetType typedesc<CarWithRelations>;

# The fields to insert a new `Car`.
public type CarInsert Car;

# The fields to update on an existing `Car`.
public type CarUpdate record {|
    string name?;
    string model?;
    int driverId?;
|};

# Represents the `User2` entity.
public type User2 record {|
    readonly int id;
    readonly string nic;
    string name;
    decimal? salary;

|};

# Represents `User2` with every field optional, for projected reads.
public type User2Optionalized record {|
    int id?;
    string nic?;
    string name?;
    decimal? salary?;
|};

# Represents `User2` with its related entities, for projected reads.
public type User2WithRelations record {|
    *User2Optionalized;
    Car2Optionalized drives?;
|};

# The `User2` type or a projection of it, for reads.
public type User2TargetType typedesc<User2WithRelations>;

# The fields to insert a new `User2`.
public type User2Insert User2;

# The fields to update on an existing `User2`.
public type User2Update record {|
    string name?;
    decimal? salary?;
|};

# Represents the `Car2` entity.
public type Car2 record {|
    readonly int id;
    string name;
    string model;
    int driverId;
    string driverNic;
|};

# Represents `Car2` with every field optional, for projected reads.
public type Car2Optionalized record {|
    int id?;
    string name?;
    string model?;
    int driverId?;
    string driverNic?;
|};

# Represents `Car2` with its related entities, for projected reads.
public type Car2WithRelations record {|
    *Car2Optionalized;
    User2Optionalized driver?;
|};

# The `Car2` type or a projection of it, for reads.
public type Car2TargetType typedesc<Car2WithRelations>;

# The fields to insert a new `Car2`.
public type Car2Insert Car2;

# The fields to update on an existing `Car2`.
public type Car2Update record {|
    string name?;
    string model?;
    int driverId?;
    string driverNic?;
|};

