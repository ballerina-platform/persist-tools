// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina.
// It should not be modified by hand.
import ballerina/persist;
import ballerina/time;

@persist:Entity {
    key: ["needId"],
    tableName: "MedicalNeeds"
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int needId = -1;

    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

