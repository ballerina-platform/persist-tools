import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

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

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type Ticket record {|
    @sql:Generated
    readonly int id;
    string subject;
    time:Date openedOn;
|};

@dataservice:Expose {operations: [dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE]}
type Badge record {|
    readonly int id;
    string label;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:DELETE]}
type Rate record {|
    readonly float factor;
    string label;
|};

@dataservice:Expose
type Invoice record {|
    readonly int id;
    @dataservice:Immutable
    string number;
    @dataservice:ReadOnly {value: "draft"}
    string state;
    @dataservice:Internal
    string? secret;
    int total;
|};

enum Category {
    BOOK,
    TOOL
}

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type Product record {|
    readonly int id;
    string name;
    decimal price;
    Category category;
    time:Date launchedOn;
    string? note;
    boolean active;
    @dataservice:Internal
    string? supplier;
|};
