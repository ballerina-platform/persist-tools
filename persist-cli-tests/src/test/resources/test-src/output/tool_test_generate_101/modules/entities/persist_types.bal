// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type User record {|
    readonly int id;
    string name;
    string nic;
    decimal salary;

|};

public type UserOptionalized record {|
    int id?;
    string name?;
    string nic?;
    decimal salary?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    CarOptionalized[] car?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    string nic?;
    decimal salary?;
|};

public type Car record {|
    readonly int id;
    string make;
    string model;
    int ownerId;
|};

public type CarOptionalized record {|
    int id?;
    string make?;
    string model?;
    int ownerId?;
|};

public type CarWithRelations record {|
    *CarOptionalized;
    UserOptionalized owner?;
|};

public type CarTargetType typedesc<CarWithRelations>;

public type CarInsert Car;

public type CarUpdate record {|
    string make?;
    string model?;
    int ownerId?;
|};

