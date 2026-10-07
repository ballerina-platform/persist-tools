import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose {operations: [dataservice:READ]}
type Department record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: ["read", "update", "delete"]}
type Employee record {|
    readonly int id;
    string name;
|};

type Project record {|
    readonly string code;
    string title;
|};
