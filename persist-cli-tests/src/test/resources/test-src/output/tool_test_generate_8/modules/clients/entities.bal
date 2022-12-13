// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/time;

@persist:Entity {
    key: ["needId"],
    tableName: "MedicalNeeds"
}
public type MedicalNeed record {|
    readonly string needId;

    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

