// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/constraint;
import ballerina/time;

public type MedicalNeed record {|
    readonly int needId;
    readonly int itemId;
    int beneficiaryId;
    time:Civil period;
    @constraint:String {
        maxLength: 10
    }
    string urgency;
    int quantity;
|};

public type MedicalNeedOptionalized record {|
    int needId?;
    int itemId?;
    int beneficiaryId?;
    time:Civil period?;
    @constraint:String {
        maxLength: 10
    }
    string urgency?;
    int quantity?;
|};

public type MedicalNeedTargetType typedesc<MedicalNeedOptionalized>;

public type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int beneficiaryId?;
    time:Civil period?;
    @constraint:String {
        maxLength: 10
    }
    string urgency?;
    int quantity?;
|};

