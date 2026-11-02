// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

public enum EnumType {
    TYPE_1,
    TYPE_2,
    TYPE_3,
    TYPE_4
}

# Represents the `AllTypes` entity.
public type AllTypes record {|
    readonly int id;
    boolean booleanType;
    int intType;
    float floatType;
    decimal decimalType;
    string stringType;
    time:Date dateType;
    time:TimeOfDay timeOfDayType;
    time:Utc utcType;
    time:Civil civilType;
    EnumType enumType;
    boolean booleanTypeOptional?;
    int intTypeOptional?;
    float floatTypeOptional?;
    decimal decimalTypeOptional?;
    string stringTypeOptional?;
    time:Date dateTypeOptional?;
    time:TimeOfDay timeOfDayTypeOptional?;
    time:Utc utcTypeOptional?;
    time:Civil civilTypeOptional?;
    EnumType enumTypeOptional?;
|};

# Represents `AllTypes` with every field optional, for projected reads.
public type AllTypesOptionalized record {|
    int id?;
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    time:Date dateType?;
    time:TimeOfDay timeOfDayType?;
    time:Utc utcType?;
    time:Civil civilType?;
    EnumType enumType?;
    boolean booleanTypeOptional?;
    int intTypeOptional?;
    float floatTypeOptional?;
    decimal decimalTypeOptional?;
    string stringTypeOptional?;
    time:Date dateTypeOptional?;
    time:TimeOfDay timeOfDayTypeOptional?;
    time:Utc utcTypeOptional?;
    time:Civil civilTypeOptional?;
    EnumType enumTypeOptional?;
|};

# The `AllTypes` type or a projection of it, for reads.
public type AllTypesTargetType typedesc<AllTypesOptionalized>;

# The fields to insert a new `AllTypes`.
public type AllTypesInsert AllTypes;

# The fields to update on an existing `AllTypes`.
public type AllTypesUpdate record {|
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    time:Date dateType?;
    time:TimeOfDay timeOfDayType?;
    time:Utc utcType?;
    time:Civil civilType?;
    EnumType enumType?;
    boolean booleanTypeOptional?;
    int intTypeOptional?;
    float floatTypeOptional?;
    decimal decimalTypeOptional?;
    string stringTypeOptional?;
    time:Date dateTypeOptional?;
    time:TimeOfDay timeOfDayTypeOptional?;
    time:Utc utcTypeOptional?;
    time:Civil civilTypeOptional?;
    EnumType enumTypeOptional?;
|};

# Represents the `StringIdRecord` entity.
public type StringIdRecord record {|
    readonly string id;
    string randomField;
|};

# Represents `StringIdRecord` with every field optional, for projected reads.
public type StringIdRecordOptionalized record {|
    string id?;
    string randomField?;
|};

# The `StringIdRecord` type or a projection of it, for reads.
public type StringIdRecordTargetType typedesc<StringIdRecordOptionalized>;

# The fields to insert a new `StringIdRecord`.
public type StringIdRecordInsert StringIdRecord;

# The fields to update on an existing `StringIdRecord`.
public type StringIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `IntIdRecord` entity.
public type IntIdRecord record {|
    readonly int id;
    string randomField;
|};

# Represents `IntIdRecord` with every field optional, for projected reads.
public type IntIdRecordOptionalized record {|
    int id?;
    string randomField?;
|};

# The `IntIdRecord` type or a projection of it, for reads.
public type IntIdRecordTargetType typedesc<IntIdRecordOptionalized>;

# The fields to insert a new `IntIdRecord`.
public type IntIdRecordInsert IntIdRecord;

# The fields to update on an existing `IntIdRecord`.
public type IntIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `FloatIdRecord` entity.
public type FloatIdRecord record {|
    readonly float id;
    string randomField;
|};

# Represents `FloatIdRecord` with every field optional, for projected reads.
public type FloatIdRecordOptionalized record {|
    float id?;
    string randomField?;
|};

# The `FloatIdRecord` type or a projection of it, for reads.
public type FloatIdRecordTargetType typedesc<FloatIdRecordOptionalized>;

# The fields to insert a new `FloatIdRecord`.
public type FloatIdRecordInsert FloatIdRecord;

# The fields to update on an existing `FloatIdRecord`.
public type FloatIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `DecimalIdRecord` entity.
public type DecimalIdRecord record {|
    readonly decimal id;
    string randomField;
|};

# Represents `DecimalIdRecord` with every field optional, for projected reads.
public type DecimalIdRecordOptionalized record {|
    decimal id?;
    string randomField?;
|};

# The `DecimalIdRecord` type or a projection of it, for reads.
public type DecimalIdRecordTargetType typedesc<DecimalIdRecordOptionalized>;

# The fields to insert a new `DecimalIdRecord`.
public type DecimalIdRecordInsert DecimalIdRecord;

# The fields to update on an existing `DecimalIdRecord`.
public type DecimalIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `BooleanIdRecord` entity.
public type BooleanIdRecord record {|
    readonly boolean id;
    string randomField;
|};

# Represents `BooleanIdRecord` with every field optional, for projected reads.
public type BooleanIdRecordOptionalized record {|
    boolean id?;
    string randomField?;
|};

# The `BooleanIdRecord` type or a projection of it, for reads.
public type BooleanIdRecordTargetType typedesc<BooleanIdRecordOptionalized>;

# The fields to insert a new `BooleanIdRecord`.
public type BooleanIdRecordInsert BooleanIdRecord;

# The fields to update on an existing `BooleanIdRecord`.
public type BooleanIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `CompositeAssociationRecord` entity.
public type CompositeAssociationRecord record {|
    readonly string id;
    string randomField;
    boolean alltypesidrecordBooleanType;
    int alltypesidrecordIntType;
    float alltypesidrecordFloatType;
    decimal alltypesidrecordDecimalType;
    string alltypesidrecordStringType;
|};

# Represents `CompositeAssociationRecord` with every field optional, for projected reads.
public type CompositeAssociationRecordOptionalized record {|
    string id?;
    string randomField?;
    boolean alltypesidrecordBooleanType?;
    int alltypesidrecordIntType?;
    float alltypesidrecordFloatType?;
    decimal alltypesidrecordDecimalType?;
    string alltypesidrecordStringType?;
|};

# Represents `CompositeAssociationRecord` with its related entities, for projected reads.
public type CompositeAssociationRecordWithRelations record {|
    *CompositeAssociationRecordOptionalized;
    AllTypesIdRecordOptionalized allTypesIdRecord?;
|};

# The `CompositeAssociationRecord` type or a projection of it, for reads.
public type CompositeAssociationRecordTargetType typedesc<CompositeAssociationRecordWithRelations>;

# The fields to insert a new `CompositeAssociationRecord`.
public type CompositeAssociationRecordInsert CompositeAssociationRecord;

# The fields to update on an existing `CompositeAssociationRecord`.
public type CompositeAssociationRecordUpdate record {|
    string randomField?;
    boolean alltypesidrecordBooleanType?;
    int alltypesidrecordIntType?;
    float alltypesidrecordFloatType?;
    decimal alltypesidrecordDecimalType?;
    string alltypesidrecordStringType?;
|};

# Represents the `AllTypesIdRecord` entity.
public type AllTypesIdRecord record {|
    readonly boolean booleanType;
    readonly int intType;
    readonly float floatType;
    readonly decimal decimalType;
    readonly string stringType;
    string randomField;

|};

# Represents `AllTypesIdRecord` with every field optional, for projected reads.
public type AllTypesIdRecordOptionalized record {|
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    string randomField?;
|};

# Represents `AllTypesIdRecord` with its related entities, for projected reads.
public type AllTypesIdRecordWithRelations record {|
    *AllTypesIdRecordOptionalized;
    CompositeAssociationRecordOptionalized compositeAssociationRecord?;
|};

# The `AllTypesIdRecord` type or a projection of it, for reads.
public type AllTypesIdRecordTargetType typedesc<AllTypesIdRecordWithRelations>;

# The fields to insert a new `AllTypesIdRecord`.
public type AllTypesIdRecordInsert AllTypesIdRecord;

# The fields to update on an existing `AllTypesIdRecord`.
public type AllTypesIdRecordUpdate record {|
    string randomField?;
|};
