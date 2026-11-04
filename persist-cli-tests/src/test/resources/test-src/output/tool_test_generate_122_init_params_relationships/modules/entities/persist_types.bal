// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Customer` entity.
public type Customer record {|
    readonly int id;
    string name;
    string email;

|};

# Represents `Customer` with every field optional, for projected reads.
public type CustomerOptionalized record {|
    int id?;
    string name?;
    string email?;
|};

# Represents `Customer` with its related entities, for projected reads.
public type CustomerWithRelations record {|
    *CustomerOptionalized;
    OrderOptionalized[] orders?;
|};

# The `Customer` type or a projection of it, for reads.
public type CustomerTargetType typedesc<CustomerWithRelations>;

# The fields to insert a new `Customer`.
public type CustomerInsert Customer;

# The fields to update on an existing `Customer`.
public type CustomerUpdate record {|
    string name?;
    string email?;
|};

# Represents the `Order` entity.
public type Order record {|
    readonly int id;
    string orderNumber;
    decimal totalAmount;
    int customerId;

|};

# Represents `Order` with every field optional, for projected reads.
public type OrderOptionalized record {|
    int id?;
    string orderNumber?;
    decimal totalAmount?;
    int customerId?;
|};

# Represents `Order` with its related entities, for projected reads.
public type OrderWithRelations record {|
    *OrderOptionalized;
    CustomerOptionalized customer?;
    OrderItemOptionalized[] items?;
|};

# The `Order` type or a projection of it, for reads.
public type OrderTargetType typedesc<OrderWithRelations>;

# The fields to insert a new `Order`.
public type OrderInsert Order;

# The fields to update on an existing `Order`.
public type OrderUpdate record {|
    string orderNumber?;
    decimal totalAmount?;
    int customerId?;
|};

# Represents the `OrderItem` entity.
public type OrderItem record {|
    readonly int id;
    int quantity;
    decimal price;
    int orderId;
    int productId;
|};

# Represents `OrderItem` with every field optional, for projected reads.
public type OrderItemOptionalized record {|
    int id?;
    int quantity?;
    decimal price?;
    int orderId?;
    int productId?;
|};

# Represents `OrderItem` with its related entities, for projected reads.
public type OrderItemWithRelations record {|
    *OrderItemOptionalized;
    OrderOptionalized 'order?;
    ProductOptionalized product?;
|};

# The `OrderItem` type or a projection of it, for reads.
public type OrderItemTargetType typedesc<OrderItemWithRelations>;

# The fields to insert a new `OrderItem`.
public type OrderItemInsert OrderItem;

# The fields to update on an existing `OrderItem`.
public type OrderItemUpdate record {|
    int quantity?;
    decimal price?;
    int orderId?;
    int productId?;
|};

# Represents the `Product` entity.
public type Product record {|
    readonly int id;
    string name;
    string description;
    decimal price;

|};

# Represents `Product` with every field optional, for projected reads.
public type ProductOptionalized record {|
    int id?;
    string name?;
    string description?;
    decimal price?;
|};

# Represents `Product` with its related entities, for projected reads.
public type ProductWithRelations record {|
    *ProductOptionalized;
    OrderItemOptionalized[] orderItems?;
|};

# The `Product` type or a projection of it, for reads.
public type ProductTargetType typedesc<ProductWithRelations>;

# The fields to insert a new `Product`.
public type ProductInsert Product;

# The fields to update on an existing `Product`.
public type ProductUpdate record {|
    string name?;
    string description?;
    decimal price?;
|};

