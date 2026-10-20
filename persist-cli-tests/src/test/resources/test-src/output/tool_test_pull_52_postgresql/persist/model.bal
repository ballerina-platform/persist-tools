import ballerina/persist as _;
import ballerinax/persist.sql;

public type Project record {|
    readonly int id;
    string name;
    int ownerId;
    Task[] tasks;
|};

public type Task record {|
    readonly int id;
    int projectId;
    @sql:Relation {keys: ["projectId"]}
    Project project;
|};
