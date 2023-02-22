// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for foo.
// It should not be modified by hand.

public type ByteTest record {|
    readonly int id;
    byte[] binary1;
    byte[]? binaryOptional;
|};

public type ByteTestInsert ByteTest;

public type ByteTestUpdate record {|
    byte[] binary1?;
    byte[]? binaryOptional?;
|};

