// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `ByteTest` entity.
public type ByteTest record {|
    readonly int id;
    byte[] binary1;
    byte[]? binaryOptional;
|};

# Represents `ByteTest` with every field optional, for projected reads.
public type ByteTestOptionalized record {|
    int id?;
    byte[] binary1?;
    byte[]? binaryOptional?;
|};

# The `ByteTest` type or a projection of it, for reads.
public type ByteTestTargetType typedesc<ByteTestOptionalized>;

# The fields to insert a new `ByteTest`.
public type ByteTestInsert ByteTest;

# The fields to update on an existing `ByteTest`.
public type ByteTestUpdate record {|
    byte[] binary1?;
    byte[]? binaryOptional?;
|};

