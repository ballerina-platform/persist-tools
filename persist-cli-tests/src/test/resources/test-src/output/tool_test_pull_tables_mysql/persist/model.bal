import ballerina/persist as _;

public type User record {|
    readonly int id;
    string name;
    string email;
|};

public type Department record {|
    readonly int id;
    string name;
    string location;
|};
