// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Customer record {|
    readonly int id;
    string name;
    string email;

|};

public type CustomerOptionalized record {|
    int id?;
    string name?;
    string email?;
|};

public type CustomerWithRelations record {|
    *CustomerOptionalized;
    OrderOptionalized[] orders?;
|};

public type CustomerTargetType typedesc<CustomerWithRelations>;

public type CustomerInsert Customer;

public type CustomerUpdate record {|
    string name?;
    string email?;
|};

public type Order record {|
    readonly int id;
    string orderNumber;
    decimal totalAmount;
    int customerId;

|};

public type OrderOptionalized record {|
    int id?;
    string orderNumber?;
    decimal totalAmount?;
    int customerId?;
|};

public type OrderWithRelations record {|
    *OrderOptionalized;
    CustomerOptionalized customer?;
    OrderItemOptionalized[] items?;
|};

public type OrderTargetType typedesc<OrderWithRelations>;

public type OrderInsert Order;

public type OrderUpdate record {|
    string orderNumber?;
    decimal totalAmount?;
    int customerId?;
|};

public type OrderItem record {|
    readonly int id;
    int quantity;
    decimal price;
    int orderId;
    int productId;
|};

public type OrderItemOptionalized record {|
    int id?;
    int quantity?;
    decimal price?;
    int orderId?;
    int productId?;
|};

public type OrderItemWithRelations record {|
    *OrderItemOptionalized;
    OrderOptionalized 'order?;
    ProductOptionalized product?;
|};

public type OrderItemTargetType typedesc<OrderItemWithRelations>;

public type OrderItemInsert OrderItem;

public type OrderItemUpdate record {|
    int quantity?;
    decimal price?;
    int orderId?;
    int productId?;
|};

public type Product record {|
    readonly int id;
    string name;
    string description;
    decimal price;

|};

public type ProductOptionalized record {|
    int id?;
    string name?;
    string description?;
    decimal price?;
|};

public type ProductWithRelations record {|
    *ProductOptionalized;
    OrderItemOptionalized[] orderItems?;
|};

public type ProductTargetType typedesc<ProductWithRelations>;

public type ProductInsert Product;

public type ProductUpdate record {|
    string name?;
    string description?;
    decimal price?;
|};

