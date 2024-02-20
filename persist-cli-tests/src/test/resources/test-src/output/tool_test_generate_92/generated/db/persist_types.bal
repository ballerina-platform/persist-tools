// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public enum UserGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type User record {|
    readonly int id;
    string name;
    UserGender gender;
    string nic;
    decimal? salary;
|};

public type UserOptionalized record {|
    int id?;
    string name?;
    UserGender gender?;
    string nic?;
    decimal? salary?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    CarOptionalized[] cars?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert record {|
    string name;
    UserGender gender;
    string nic;
    decimal? salary;
|};

public type UserUpdate record {|
    string name?;
    UserGender gender?;
    string nic?;
    decimal? salary?;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    int ownerId;
|};

public type CarOptionalized record {|
    int id?;
    string name?;
    string model?;
    int ownerId?;
|};

public type CarWithRelations record {|
    *CarOptionalized;
    UserOptionalized user?;
|};

public type CarTargetType typedesc<CarWithRelations>;

public type CarInsert Car;

public type CarUpdate record {|
    string name?;
    string model?;
    int ownerId?;
|};

