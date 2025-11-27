import ballerina/persist as _;
import ballerinax/persist.sql;

public type Employee record {|
    @sql:Generated
    readonly int id;
    string name;
    string email;
    @sql:Name {value: "manager_id"}
    @sql:Index {name: "manager_id"}
    int? managerId;
    Employee[] employees;
    @sql:Relation {keys: ["managerId"]}
    Employee employee;
|};
