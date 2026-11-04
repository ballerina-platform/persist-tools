// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Product` entity.
public type Product record {|
    readonly int id;
    string name;
    decimal price;
|};

# Represents `Product` with every field optional, for projected reads.
public type ProductOptionalized record {|
    int id?;
    string name?;
    decimal price?;
|};

# The `Product` type or a projection of it, for reads.
public type ProductTargetType typedesc<ProductOptionalized>;

# The fields to insert a new `Product`.
public type ProductInsert Product;

# The fields to update on an existing `Product`.
public type ProductUpdate record {|
    string name?;
    decimal price?;
|};

