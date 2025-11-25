import ballerina/persist as _;
import ballerinax/persist.sql;

public type Department record {|
    @sql:Generated
    readonly int id;
    string name;
    string location;
    @sql:Name {value: "manager_id"}
    @sql:Index {name: "manager_id"}
    int? managerId;
    @sql:Relation {keys: ["managerId"]}
    User user;
    User[] users;
|};

public type User record {|
    @sql:Generated
    readonly int id;
    string name;
    string email;
    @sql:Name {value: "department_id"}
    @sql:Index {name: "department_id"}
    int? departmentId;
    Department[] departments;
    @sql:Relation {keys: ["departmentId"]}
    Department department;
|};

