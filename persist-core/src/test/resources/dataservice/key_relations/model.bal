import ballerina/dataservice;
import ballerina/persist as _;
import ballerinax/persist.sql;

@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
    ProjectMember[] memberships;
    Badge? badge;
|};

@dataservice:Expose
type Project record {|
    readonly string code;
    string title;
    ProjectMember[] members;
|};

@dataservice:Expose
type ProjectMember record {|
    readonly string projectCode;
    readonly int employeeId;
    string role;
    @sql:Relation {keys: ["employeeId"]}
    Employee employee;
    @sql:Relation {keys: ["projectCode"]}
    Project project;
|};

@dataservice:Expose
type Badge record {|
    readonly int employeeId;
    string label;
    @sql:Relation {keys: ["employeeId"]}
    Employee employee;
|};
