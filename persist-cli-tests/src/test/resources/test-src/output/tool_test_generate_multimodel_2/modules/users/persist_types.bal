// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Customer record {|
    readonly int id;
    string name;
|};

public type CustomerOptionalized record {|
    int id?;
    string name?;
|};

public type CustomerTargetType typedesc<CustomerOptionalized>;

public type CustomerInsert Customer;

public type CustomerUpdate record {|
    string name?;
|};

