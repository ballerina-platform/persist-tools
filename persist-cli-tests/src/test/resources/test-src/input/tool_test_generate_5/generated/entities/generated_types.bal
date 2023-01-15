// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/time;

public type MedicalNeed record {|
    readonly int needId;
    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int itemId?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    int quantity?;
|};

public type MedicalItem record {|
    readonly int itemId;
    string name;
    string 'type;
    string unit;
|};

type MedicalItemInsert MedicalItem;

public type MedicalItemUpdate record {|
    string name?;
    string 'type?;
    string unit?;
|};

