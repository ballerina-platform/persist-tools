// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Order` entity.
public type Order record {|
    readonly int id;
    decimal total;
|};

# Represents `Order` with every field optional, for projected reads.
public type OrderOptionalized record {|
    int id?;
    decimal total?;
|};

# The `Order` type or a projection of it, for reads.
public type OrderTargetType typedesc<OrderOptionalized>;

# The fields to insert a new `Order`.
public type OrderInsert Order;

# The fields to update on an existing `Order`.
public type OrderUpdate record {|
    decimal total?;
|};

