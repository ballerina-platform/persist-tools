// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina.
// It should not be modified by hand.
import ballerina/persist;

@persist:Entity {
    key: ["id"],
    tableName: "Profiles"
}
public type Profile record {|
    @persist:AutoIncrement {startValue: 10}
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
    @persist:Relation
    Profile profile?;
|};

@persist:Entity {
    key: ["id"]
}
public type Dept record {|
    readonly int id;
    string name;
|};

@persist:Entity {
    key: ["id"],
    uniqueConstraints: [["age", "name"]]
}
public type Customer record {|
    readonly int id;
    string name;
    int age;
|};

@persist:Entity {
    key: ["id", "firstName"],
    uniqueConstraints: [["age", "lastName"], ["nicNo"]]
}
public type Student record {|
    readonly int id;
    readonly string firstName;
    int age;
    string lastName;
    string nicNo;
|};

@persist:Entity {
    key: ["id"],
    tableName: "MultipleAssociations"
}
public type MultipleAssociations record {|
    readonly int id;
    string name;

    @persist:Relation {}
    User user?;

    @persist:Relation {keyColumns: ["deptId"], onDelete: persist:SET_DEFAULT}
    Dept dept?;

    @persist:Relation {reference: ["id"], onUpdate: "SET_DEFAULT"}
    Customer customer?;
|};

