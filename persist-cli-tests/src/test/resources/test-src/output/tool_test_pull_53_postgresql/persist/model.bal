import ballerina/persist as _;

public type Task record {|
    readonly int id;
    string title;
|};
