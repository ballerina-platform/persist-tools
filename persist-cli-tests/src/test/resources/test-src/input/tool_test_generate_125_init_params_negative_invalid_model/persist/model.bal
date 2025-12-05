import ballerina/persist as _;

// Invalid model - missing readonly on key field
type User record {|
    int id;
    string name;
    string email;
|};
