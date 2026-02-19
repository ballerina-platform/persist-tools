// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Order record {|
    readonly int id;
    decimal total;
|};

public type OrderOptionalized record {|
    int id?;
    decimal total?;
|};

public type OrderTargetType typedesc<OrderOptionalized>;

public type OrderInsert Order;

public type OrderUpdate record {|
    decimal total?;
|};

