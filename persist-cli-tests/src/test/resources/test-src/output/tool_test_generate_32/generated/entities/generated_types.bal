// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type MedicalItem record {|
    readonly int itemId;
    string name;
    string 'type;
    string unit;
|};

public type MedicalItemInsert MedicalItem;

public type MedicalItemUpdate record {|
    string name?;
    string 'type?;
    string unit?;
|};

