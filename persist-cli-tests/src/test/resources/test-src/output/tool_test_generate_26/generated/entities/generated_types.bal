// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/time;

public type MedicalNeed record {|
    readonly int 'record;
    int medicalitemItemId;
    int beneficiaryId;
    time:Civil 'time;
    string urgency;
    int quantity;
|};

public type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int medicalitemItemId?;
    int beneficiaryId?;
    time:Civil 'time?;
    string urgency?;
    int quantity?;
|};

public type MedicalItem record {|
    readonly int itemId;
    string 'string;
    string 'type;
    string unit;
|};

public type MedicalItemInsert MedicalItem;

public type MedicalItemUpdate record {|
    string 'string?;
    string 'type?;
    string unit?;
|};

