import ballerina/persist as _;

public type Order record {|
    readonly int id;
    decimal total;
|};
