import ballerina/persist as _;

public type Person record {|
    readonly int id;
    string name;
    int age;
|};

public type Car record {|
    readonly int id;
    string make;
    string model;
    int year;
|};
