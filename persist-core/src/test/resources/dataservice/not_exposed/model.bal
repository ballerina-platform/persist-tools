import ballerina/persist as _;

type Department record {|
    readonly int id;
    string name;
|};

type Employee record {|
    readonly int id;
    string name;
|};
