import ballerina/persist as _;

public type Product record {|
    readonly int id;
    string name;
    decimal price;
|};
