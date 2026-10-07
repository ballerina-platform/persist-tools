import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

type Department record {|
    @sql:Generated
    readonly int id;
    string name;
    Employee[] employees;
|};

type Employee record {|
    readonly int id;
    string name;
    string? email;
    time:Date hiredOn;
    Department department;
    Assignment[] assignments;
|};

type Assignment record {|
    readonly string projectCode;
    readonly int seq;
    decimal hours;
    Employee employee;
|};
