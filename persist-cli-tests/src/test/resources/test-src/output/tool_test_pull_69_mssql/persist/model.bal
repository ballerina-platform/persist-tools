import ballerina/persist as _;

public type Car record {|
    readonly int id;
    string name;
    string 'type;
    string model;
    int ownerId;
|};

