// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Book` entity.
public type Book record {|
    readonly string bookId;
    string title;
    string author;
    decimal price;
    int stock;
|};

# Represents `Book` with every field optional, for projected reads.
public type BookOptionalized record {|
    string bookId?;
    string title?;
    string author?;
    decimal price?;
    int stock?;
|};

# Represents `Book` with its related entities, for projected reads.
public type BookWithRelations record {|
    *BookOptionalized;
    OrderItemOptionalized orderitem?;
|};

# The `Book` type or a projection of it, for reads.
public type BookTargetType typedesc<BookWithRelations>;

# The fields to insert a new `Book`.
public type BookInsert Book;

# The fields to update on an existing `Book`.
public type BookUpdate record {|
    string title?;
    string author?;
    decimal price?;
    int stock?;
|};

# Represents the `Order` entity.
public type Order record {|
    readonly string orderId;
    string customerId;
    string createdAt;
    decimal totalPrice;
|};

# Represents `Order` with every field optional, for projected reads.
public type OrderOptionalized record {|
    string orderId?;
    string customerId?;
    string createdAt?;
    decimal totalPrice?;
|};

# Represents `Order` with its related entities, for projected reads.
public type OrderWithRelations record {|
    *OrderOptionalized;
    OrderItemOptionalized[] orderItems?;
    PaymentOptionalized payment?;
|};

# The `Order` type or a projection of it, for reads.
public type OrderTargetType typedesc<OrderWithRelations>;

# The fields to insert a new `Order`.
public type OrderInsert Order;

# The fields to update on an existing `Order`.
public type OrderUpdate record {|
    string customerId?;
    string createdAt?;
    decimal totalPrice?;
|};

# Represents the `OrderItem` entity.
public type OrderItem record {|
    readonly string orderItemId;
    int quantity;
    decimal price;
    string bookBookId;
    string orderOrderId;
|};

# Represents `OrderItem` with every field optional, for projected reads.
public type OrderItemOptionalized record {|
    string orderItemId?;
    int quantity?;
    decimal price?;
    string bookBookId?;
    string orderOrderId?;
|};

# Represents `OrderItem` with its related entities, for projected reads.
public type OrderItemWithRelations record {|
    *OrderItemOptionalized;
    BookOptionalized book?;
    OrderOptionalized 'order?;
|};

# The `OrderItem` type or a projection of it, for reads.
public type OrderItemTargetType typedesc<OrderItemWithRelations>;

# The fields to insert a new `OrderItem`.
public type OrderItemInsert OrderItem;

# The fields to update on an existing `OrderItem`.
public type OrderItemUpdate record {|
    int quantity?;
    decimal price?;
    string bookBookId?;
    string orderOrderId?;
|};

# Represents the `Payment` entity.
public type Payment record {|
    readonly string paymentId;
    decimal paymentAmount;
    string paymentDate;
    string orderOrderId;
|};

# Represents `Payment` with every field optional, for projected reads.
public type PaymentOptionalized record {|
    string paymentId?;
    decimal paymentAmount?;
    string paymentDate?;
    string orderOrderId?;
|};

# Represents `Payment` with its related entities, for projected reads.
public type PaymentWithRelations record {|
    *PaymentOptionalized;
    OrderOptionalized 'order?;
|};

# The `Payment` type or a projection of it, for reads.
public type PaymentTargetType typedesc<PaymentWithRelations>;

# The fields to insert a new `Payment`.
public type PaymentInsert Payment;

# The fields to update on an existing `Payment`.
public type PaymentUpdate record {|
    decimal paymentAmount?;
    string paymentDate?;
    string orderOrderId?;
|};

