// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

# Represents the `MedicalNeed` entity.
public type MedicalNeed record {|
    readonly int 'record;
    int itemItemId;
    int beneficiaryId;
    time:Civil 'time;
    string urgency;
    int quantity;
|};

# Represents `MedicalNeed` with every field optional, for projected reads.
public type MedicalNeedOptionalized record {|
    int 'record?;
    int itemItemId?;
    int beneficiaryId?;
    time:Civil 'time?;
    string urgency?;
    int quantity?;
|};

# Represents `MedicalNeed` with its related entities, for projected reads.
public type MedicalNeedWithRelations record {|
    *MedicalNeedOptionalized;
    MedicalItemOptionalized item?;
|};

# The `MedicalNeed` type or a projection of it, for reads.
public type MedicalNeedTargetType typedesc<MedicalNeedWithRelations>;

# The fields to insert a new `MedicalNeed`.
public type MedicalNeedInsert MedicalNeed;

# The fields to update on an existing `MedicalNeed`.
public type MedicalNeedUpdate record {|
    int itemItemId?;
    int beneficiaryId?;
    time:Civil 'time?;
    string urgency?;
    int quantity?;
|};

# Represents the `MedicalItem` entity.
public type MedicalItem record {|
    readonly int itemId;
    string 'string;
    string 'type;
    string unit;
|};

# Represents `MedicalItem` with every field optional, for projected reads.
public type MedicalItemOptionalized record {|
    int itemId?;
    string 'string?;
    string 'type?;
    string unit?;
|};

# Represents `MedicalItem` with its related entities, for projected reads.
public type MedicalItemWithRelations record {|
    *MedicalItemOptionalized;
    MedicalNeedOptionalized need?;
|};

# The `MedicalItem` type or a projection of it, for reads.
public type MedicalItemTargetType typedesc<MedicalItemWithRelations>;

# The fields to insert a new `MedicalItem`.
public type MedicalItemInsert MedicalItem;

# The fields to update on an existing `MedicalItem`.
public type MedicalItemUpdate record {|
    string 'string?;
    string 'type?;
    string unit?;
|};

