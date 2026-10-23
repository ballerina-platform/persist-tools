import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
    string? email;
    Department department;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Department record {|
    readonly int id;
    string name;
    Employee[] employees;
|};

type AuditLog record {|
    readonly int id;
    string entry;
|};
