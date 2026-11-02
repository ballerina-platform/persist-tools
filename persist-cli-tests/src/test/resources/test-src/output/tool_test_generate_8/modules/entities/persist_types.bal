// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

# Represents the `MedicalNeed` entity.
public type MedicalNeed record {|
    readonly string needId;
    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

# Represents `MedicalNeed` with every field optional, for projected reads.
public type MedicalNeedOptionalized record {|
    string needId?;
    int itemId?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    int quantity?;
|};

# The `MedicalNeed` type or a projection of it, for reads.
public type MedicalNeedTargetType typedesc<MedicalNeedOptionalized>;

# The fields to insert a new `MedicalNeed`.
public type MedicalNeedInsert MedicalNeed;

# The fields to update on an existing `MedicalNeed`.
public type MedicalNeedUpdate record {|
    int itemId?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    int quantity?;
|};

