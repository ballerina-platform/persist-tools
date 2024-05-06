// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type User record {|
    readonly int id;
    string name;
    string nic;
    decimal? salary;

|};

public type UserOptionalized record {|
    int id?;
    string name?;
    string nic?;
    decimal? salary?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    CarOptionalized drives?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    string nic?;
    decimal? salary?;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    int driverId;
|};

public type CarOptionalized record {|
    int id?;
    string name?;
    string model?;
    int driverId?;
|};

public type CarWithRelations record {|
    *CarOptionalized;
    UserOptionalized driver?;
|};

public type CarTargetType typedesc<CarWithRelations>;

public type CarInsert Car;

public type CarUpdate record {|
    string name?;
    string model?;
    int driverId?;
|};

public type User2 record {|
    readonly int id;
    readonly string nic;
    string name;
    decimal? salary;

|};

public type User2Optionalized record {|
    int id?;
    string nic?;
    string name?;
    decimal? salary?;
|};

public type User2WithRelations record {|
    *User2Optionalized;
    Car2Optionalized drives?;
|};

public type User2TargetType typedesc<User2WithRelations>;

public type User2Insert User2;

public type User2Update record {|
    string name?;
    decimal? salary?;
|};

public type Car2 record {|
    readonly int id;
    string name;
    string model;
    int driverId;
    string driverNic;
|};

public type Car2Optionalized record {|
    int id?;
    string name?;
    string model?;
    int driverId?;
    string driverNic?;
|};

public type Car2WithRelations record {|
    *Car2Optionalized;
    User2Optionalized driver?;
|};

public type Car2TargetType typedesc<Car2WithRelations>;

public type Car2Insert Car2;

public type Car2Update record {|
    string name?;
    string model?;
    int driverId?;
    string driverNic?;
|};

