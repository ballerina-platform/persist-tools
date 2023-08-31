// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

public type MedicalNeed record {|
    readonly int 'record;
    int itemItemId;
    int beneficiaryId;
    time:Civil 'time;
    string urgency;
    int quantity;
|};

public type MedicalNeedOptionalized record {|
    int 'record?;
    int itemItemId?;
    int beneficiaryId?;
    time:Civil 'time?;
    string urgency?;
    int quantity?;
|};

public type MedicalNeedWithRelations record {|
    *MedicalNeedOptionalized;
    MedicalItemOptionalized item?;
|};

public type MedicalNeedTargetType typedesc<MedicalNeedWithRelations>;

public type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int itemItemId?;
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

public type MedicalItemOptionalized record {|
    int itemId?;
    string 'string?;
    string 'type?;
    string unit?;
|};

public type MedicalItemWithRelations record {|
    *MedicalItemOptionalized;
    MedicalNeedOptionalized medicalNeed?;
|};

public type MedicalItemTargetType typedesc<MedicalItemWithRelations>;

public type MedicalItemInsert MedicalItem;

public type MedicalItemUpdate record {|
    string 'string?;
    string 'type?;
    string unit?;
|};

