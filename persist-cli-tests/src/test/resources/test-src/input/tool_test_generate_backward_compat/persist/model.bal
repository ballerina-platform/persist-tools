import ballerina/persist as _;

public type Employee record {|
    readonly int id;
    string name;
|};
