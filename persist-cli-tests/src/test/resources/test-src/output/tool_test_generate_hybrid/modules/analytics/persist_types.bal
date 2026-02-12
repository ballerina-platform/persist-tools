// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Product record {|
    readonly int id;
    string name;
|};

public type ProductOptionalized record {|
    int id?;
    string name?;
|};

public type ProductTargetType typedesc<ProductOptionalized>;

public type ProductInsert Product;

public type ProductUpdate record {|
    string name?;
|};

