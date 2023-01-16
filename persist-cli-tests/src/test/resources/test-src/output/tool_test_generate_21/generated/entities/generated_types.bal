// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/time;

public type MedicalNeed record {|
    readonly int needId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
    int aidpackageorderitemId;
|};

public type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    int quantity?;
    int aidpackageorderitemId?;
|};

public type AidPackageOrderItem record {|
    readonly int id;
    int quantity;
    int totalAmount;
|};

public type AidPackageOrderItemInsert AidPackageOrderItem;

public type AidPackageOrderItemUpdate record {|
    int quantity?;
    int totalAmount?;
|};

