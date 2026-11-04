// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Customer` entity.
public type Customer record {|
    readonly int id;
    string name;
|};

# Represents `Customer` with every field optional, for projected reads.
public type CustomerOptionalized record {|
    int id?;
    string name?;
|};

# The `Customer` type or a projection of it, for reads.
public type CustomerTargetType typedesc<CustomerOptionalized>;

# The fields to insert a new `Customer`.
public type CustomerInsert Customer;

# The fields to update on an existing `Customer`.
public type CustomerUpdate record {|
    string name?;
|};

