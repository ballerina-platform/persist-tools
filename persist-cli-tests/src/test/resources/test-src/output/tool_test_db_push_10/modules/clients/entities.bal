import ballerina/persist;

@persist:Entity {
    key: ["id"],
    uniqueConstraints: [["name"]],
    tableName: "Profiles"
}
public type Profile record {|
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Users"
}
public type User record {|
    readonly int id;
    string name;
|};

