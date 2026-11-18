import ballerina/constraint;
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
    Ticket[] tickets;
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

# A ticket, with a field of each rule.
@dataservice:Expose
type Ticket record {|
    readonly int id;
    @dataservice:Immutable
    @constraint:String {maxLength: 20}
    string title;
    @dataservice:ReadOnly {value: "open"}
    string status;
    string? note;
    time:Date openedOn;
    time:Civil? dueAt;
    Department department;
|};

# A note that callers may only write, with a composite key.
@dataservice:Expose {operations: [dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE]}
type Note record {|
    readonly string region;
    readonly int seq;
    @constraint:String {maxLength: 10}
    string text;
|};

# A reading, with a field of each type.
@dataservice:Expose
type Reading record {|
    readonly int id;
    int count;
    float ratio;
    float? spare;
    decimal amount;
    boolean active;
    time:Date day;
    time:TimeOfDay at;
    time:Civil localTime;
    time:Utc instant;
    byte[] payload;
    byte[]? extra;
    Status status;
    Status? previous;
|};

enum Status {
    ACTIVE,
    INACTIVE
}

# A measure, keyed by a float.
@dataservice:Expose {operations: [dataservice:READ, dataservice:UPDATE, dataservice:DELETE]}
type Measure record {|
    readonly float weight;
    string label;
|};
