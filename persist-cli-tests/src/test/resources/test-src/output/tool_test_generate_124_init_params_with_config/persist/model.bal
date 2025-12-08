import ballerina/persist as _;

type Product record {|
    readonly int id;
    string name;
    decimal price;
|};
