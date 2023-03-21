// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/time;

public type MedicalItem record {|
    readonly int itemId;
    string name;
    string itemType;
    string unit;
    float quantity;
    decimal price;
|};

public type MedicalItemOptionalized record {|
    int itemId?;
    string name?;
    string itemType?;
    string unit?;
    float quantity?;
    decimal price?;
|};

public type MedicalItemTargetType typedesc<MedicalItemOptionalized>;

public type MedicalItemInsert MedicalItem;

public type MedicalItemUpdate record {|
    string name?;
    string itemType?;
    string unit?;
    float quantity?;
    decimal price?;
|};

public type MedicalNeed record {|
    readonly int needId;
    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

public type MedicalNeedOptionalized record {|
    int needId?;
    int itemId?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    int quantity?;
|};

public type MedicalNeedTargetType typedesc<MedicalNeedOptionalized>;

public type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int itemId?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    int quantity?;
|};

