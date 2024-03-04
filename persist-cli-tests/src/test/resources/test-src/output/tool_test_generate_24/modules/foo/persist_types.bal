// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type ByteTest record {|
    readonly int id;
    byte[] binary1;
    byte[]? binaryOptional;
|};

public type ByteTestOptionalized record {|
    int id?;
    byte[] binary1?;
    byte[]? binaryOptional?;
|};

public type ByteTestTargetType typedesc<ByteTestOptionalized>;

public type ByteTestInsert ByteTest;

public type ByteTestUpdate record {|
    byte[] binary1?;
    byte[]? binaryOptional?;
|};

