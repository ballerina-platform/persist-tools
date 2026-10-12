import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@dataservice:Expose
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
    Department department;
    Assignment[] assignments;
|};

@dataservice:Expose {}
type Assignment record {|
    readonly string projectCode;
    readonly int seq;
    decimal hours;
    Employee employee;
|};
