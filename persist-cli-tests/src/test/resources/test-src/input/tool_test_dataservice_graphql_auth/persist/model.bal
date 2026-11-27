import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE], scopes: {read: ["hr:read"], create: []}}
type Department record {|
    @sql:Generated
    readonly int id;
    string name;
    Employee[] employees;
|};

@dataservice:Expose {scopes: {read: ["hr:read"], delete: ["hr:admin"]}}
type Employee record {|
    readonly int id;
    string name;
    string? email;
    time:Date hiredOn;
    Department department;
    Assignment[] assignments;
|};

@dataservice:Expose
type Assignment record {|
    readonly string projectCode;
    readonly int seq;
    decimal hours;
    Employee employee;
|};

# A log entry that callers may only write.
@dataservice:Expose {operations: [dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE],
    scopes: {create: ["log:write"], update: ["log:write"], delete: ["log:admin"]}}
type LogEntry record {|
    readonly int id;
    string message;
|};
