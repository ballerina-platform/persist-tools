// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

public type MedicalNeed record {|
    readonly int fooNeedId;
    int fooItemId;
    int fooBeneficiaryId;
    time:Civil period;
    int urgency;
    int foo;
|};

public type MedicalNeedOptionalized record {|
    int fooNeedId?;
    int fooItemId?;
    int fooBeneficiaryId?;
    time:Civil period?;
    int urgency?;
    int foo?;
|};

public type MedicalNeedTargetType typedesc<MedicalNeedOptionalized>;

public type MedicalNeedInsert MedicalNeed;

public type MedicalNeedUpdate record {|
    int fooItemId?;
    int fooBeneficiaryId?;
    time:Civil period?;
    int urgency?;
    int foo?;
|};

