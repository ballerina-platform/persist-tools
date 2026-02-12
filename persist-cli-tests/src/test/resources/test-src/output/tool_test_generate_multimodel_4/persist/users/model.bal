import ballerina/persist as _;

public type User record {|
    readonly int id;
    string name;
    string email;
|};
