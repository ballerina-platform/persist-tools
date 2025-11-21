import ballerina/persist as _;

public type Department record {|
    readonly int id;
    string name;
    string location;
|};

public type User record {|
    readonly int id;
    string name;
    string email;
|};

