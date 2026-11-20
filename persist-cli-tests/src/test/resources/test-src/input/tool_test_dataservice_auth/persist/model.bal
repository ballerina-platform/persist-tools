import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose {scopes: {read: ["hr:read"], delete: ["hr:admin"]}}
type Employee record {|
    readonly int id;
    string name;
    Department department;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE], scopes: {read: ["hr:read", "org:read"], create: []}}
type Department record {|
    readonly int id;
    string name;
    Employee[] employees;
|};
