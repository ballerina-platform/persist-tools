// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Employee record {|
    readonly int id;
    string name;
|};

public type EmployeeOptionalized record {|
    int id?;
    string name?;
|};

public type EmployeeTargetType typedesc<EmployeeOptionalized>;

public type EmployeeInsert Employee;

public type EmployeeUpdate record {|
    string name?;
|};

