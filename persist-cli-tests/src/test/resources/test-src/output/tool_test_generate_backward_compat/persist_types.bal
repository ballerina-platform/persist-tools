// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Employee` entity.
public type Employee record {|
    readonly int id;
    string name;
|};

# Represents `Employee` with every field optional, for projected reads.
public type EmployeeOptionalized record {|
    int id?;
    string name?;
|};

# The `Employee` type or a projection of it, for reads.
public type EmployeeTargetType typedesc<EmployeeOptionalized>;

# The fields to insert a new `Employee`.
public type EmployeeInsert Employee;

# The fields to update on an existing `Employee`.
public type EmployeeUpdate record {|
    string name?;
|};

