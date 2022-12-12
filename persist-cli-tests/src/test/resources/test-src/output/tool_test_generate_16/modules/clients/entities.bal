// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina.
// It should not be modified by hand.
import ballerina/persist;

@persist:Entity {
    key: ["id"],
    tableName: "Companies"
}
public type Company record {|
    readonly int id;
    string name;
    Employee[] employees?;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Employees"
}
public type Employee record {|
    readonly int id;
    string name;

    @persist:Relation {keyColumns: ["companyId"], reference: ["id"]}
    Company company?;
|};

