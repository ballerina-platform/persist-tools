import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "settings"}
type Setting record {|
    readonly string key;
    string value;
|};

@sql:Name {value: "person"}
type Person record {|
    readonly int id;
    string name;
|};

@sql:Name {value: "people"}
type People record {|
    readonly int id;
    string name;
|};

@sql:Name {value: "inputs"}
type Input record {|
    readonly int input;
    string name;
|};
