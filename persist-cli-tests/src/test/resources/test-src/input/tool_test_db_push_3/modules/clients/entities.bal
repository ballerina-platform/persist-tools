// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina.
// It should not be modified by hand.
import ballerina/persist;

@persist:Entity {
    key: ["itemId"],
    tableName: "MedicalItems"
}
public type MedicalItem record {|
    readonly int itemId;
    string name;
    string 'type;
    string unit;
|};

