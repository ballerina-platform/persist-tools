import ballerina/persist as _;

type User record {|
    readonly int id;
    string name;
    string email;
|};
