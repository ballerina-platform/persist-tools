import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;

@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
    string? email;
    time:Date hiredOn;
    Department department;
    Assignment[] assignments;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type Department record {|
    readonly int id;
    string name;
    Employee[] employees;
|};

@dataservice:Expose
type Assignment record {|
    readonly string projectCode;
    readonly int seq;
    decimal hours;
    Employee employee;
|};

@dataservice:Expose
type Document record {|
    readonly string code;
    time:Date issuedOn;
    time:Date? expiresOn;
    time:TimeOfDay opensAt;
    time:Civil createdAt;
    time:Utc signedAt;
    byte[] content;
|};

type AuditLog record {|
    readonly int id;
    string entry;
|};
