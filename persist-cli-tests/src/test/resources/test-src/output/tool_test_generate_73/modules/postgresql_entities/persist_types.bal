// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public enum UserGender {
    MALE,
    FEMALE
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

public type UserInsert User;

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
    UserOptionalized owner?;
|};

public type CarTargetType typedesc<CarWithRelations>;

public type CarInsert Car;

public type CarUpdate record {|
    string name?;
    string model?;
    int ownerId?;
|};

public type Person record {|
    readonly string name;
    int age;
    string nic;
    decimal salary;
|};

public type PersonOptionalized record {|
    string name?;
    int age?;
    string nic?;
    decimal salary?;
|};

public type PersonTargetType typedesc<PersonOptionalized>;

public type PersonInsert Person;

public type PersonUpdate record {|
    int age?;
    string nic?;
    decimal salary?;
|};

public type Person2 record {|
    readonly string name;
    int age;
    string nic;
    decimal salary;
|};

public type Person2Optionalized record {|
    string name?;
    int age?;
    string nic?;
    decimal salary?;
|};

public type Person2TargetType typedesc<Person2Optionalized>;

public type Person2Insert Person2;

public type Person2Update record {|
    int age?;
    string nic?;
    decimal salary?;
|};

