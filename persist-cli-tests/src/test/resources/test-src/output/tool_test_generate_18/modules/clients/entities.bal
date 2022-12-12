// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina.
// It should not be modified by hand.
import ballerina/persist;
import ballerina/time;

@persist:Entity {
    key: ["needId"]
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int needId = -1;

    @persist:Relation
    MedicalItem item?;

    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

@persist:Entity {
    key: ["itemId"]
}
public type MedicalItem record {|
    @persist:AutoIncrement
    readonly int itemId = -1;

    string name;
    string 'type;
    string unit;
|};

