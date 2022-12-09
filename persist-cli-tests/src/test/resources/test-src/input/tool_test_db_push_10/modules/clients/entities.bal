// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is a auto-generated client by Ballerina persist library.
// It should not be modified by hand.
import ballerina/persist;

@persist:Entity {
    key: ["id"],
    uniqueConstraints: [["name"]],
    tableName: "Profiles"
}
public type Profile record {|
    @persist:AutoIncrement
    readonly int id = 5;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Users"
}
public type User record  {|
    @persist:AutoIncrement
    readonly int id;
    string name;
|};
