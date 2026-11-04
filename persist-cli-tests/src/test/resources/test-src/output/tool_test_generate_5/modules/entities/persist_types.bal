// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/time;

# Represents the `MedicalNeed` entity.
public type MedicalNeed record {|
    readonly int needId;
    int itemId;
    string name;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    string quantity;
|};

# Represents `MedicalNeed` with every field optional, for projected reads.
public type MedicalNeedOptionalized record {|
    int needId?;
    int itemId?;
    string name?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    string quantity?;
|};

# The `MedicalNeed` type or a projection of it, for reads.
public type MedicalNeedTargetType typedesc<MedicalNeedOptionalized>;

# The fields to insert a new `MedicalNeed`.
public type MedicalNeedInsert MedicalNeed;

# The fields to update on an existing `MedicalNeed`.
public type MedicalNeedUpdate record {|
    int itemId?;
    string name?;
    int beneficiaryId?;
    time:Civil period?;
    string urgency?;
    string quantity?;
|};

# Represents the `MedicalItem` entity.
public type MedicalItem record {|
    readonly int itemId;
    string name;
    string 'type;
    int unit;
|};

# Represents `MedicalItem` with every field optional, for projected reads.
public type MedicalItemOptionalized record {|
    int itemId?;
    string name?;
    string 'type?;
    int unit?;
|};

# The `MedicalItem` type or a projection of it, for reads.
public type MedicalItemTargetType typedesc<MedicalItemOptionalized>;

# The fields to insert a new `MedicalItem`.
public type MedicalItemInsert MedicalItem;

# The fields to update on an existing `MedicalItem`.
public type MedicalItemUpdate record {|
    string name?;
    string 'type?;
    int unit?;
|};

