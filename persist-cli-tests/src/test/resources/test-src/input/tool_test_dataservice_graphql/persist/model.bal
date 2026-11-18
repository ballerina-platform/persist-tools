import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type Department record {|
    @sql:Generated
    readonly int id;
    string name;
    Employee[] employees;
|};

@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
    string? email;
    time:Date hiredOn;
    @dataservice:Internal
    Department department;
    Assignment[] assignments;
    Badge? badge;
|};

@dataservice:Expose
type Assignment record {|
    readonly string projectCode;
    readonly int seq;
    decimal hours;
    Employee employee;
    Timesheet[] timesheets;
|};

# A badge, the side of a one-to-one relation that holds the foreign key.
@dataservice:Expose {operations: [dataservice:READ]}
type Badge record {|
    readonly string code;
    Employee employee;
|};

# A timesheet, whose relation to its assignment has a composite foreign key.
@dataservice:Expose {operations: [dataservice:READ]}
type Timesheet record {|
    readonly int id;
    decimal hours;
    Assignment assignment;
|};
