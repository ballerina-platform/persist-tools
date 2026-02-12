import ballerina/persist as _;

public type Customer record {|
    readonly int id;
    string name;
|};
