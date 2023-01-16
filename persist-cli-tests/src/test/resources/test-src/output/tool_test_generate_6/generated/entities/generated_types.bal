// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/time;

public type DataType record {|
    readonly int a;
    string b1;
    int c1;
    boolean d1;
    float e1;
    decimal f1;
    time:Utc j1;
    time:Civil k1;
    time:Date l1;
    time:TimeOfDay m1;
|};

public type DataTypeInsert DataType;

public type DataTypeUpdate record {|
    string b1?;
    int c1?;
    boolean d1?;
    float e1?;
    decimal f1?;
    time:Utc j1?;
    time:Civil k1?;
    time:Date l1?;
    time:TimeOfDay m1?;
|};

