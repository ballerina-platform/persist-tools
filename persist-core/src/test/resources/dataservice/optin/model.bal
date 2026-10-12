import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose
type Department record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Employee record {|
    readonly int id;
    string name;
|};

type AuditLog record {|
    readonly int id;
    string entry;
|};
