// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Book record {|
    readonly string bookId;
    string title;
    string author;
    decimal price;
    int stock;
|};

public type BookOptionalized record {|
    string bookId?;
    string title?;
    string author?;
    decimal price?;
    int stock?;
|};

public type BookWithRelations record {|
    *BookOptionalized;
    OrderItemOptionalized orderitem?;
|};

public type BookTargetType typedesc<BookWithRelations>;

public type BookInsert Book;

public type BookUpdate record {|
    string title?;
    string author?;
    decimal price?;
    int stock?;
|};

public type Order record {|
    readonly string orderId;
    string customerId;
    string createdAt;
    decimal totalPrice;
|};

public type OrderOptionalized record {|
    string orderId?;
    string customerId?;
    string createdAt?;
    decimal totalPrice?;
|};

public type OrderWithRelations record {|
    *OrderOptionalized;
    OrderItemOptionalized[] orderItems?;
    PaymentOptionalized payment?;
|};

public type OrderTargetType typedesc<OrderWithRelations>;

public type OrderInsert Order;

public type OrderUpdate record {|
    string customerId?;
    string createdAt?;
    decimal totalPrice?;
|};

public type OrderItem record {|
    readonly string orderItemId;
    int quantity;
    decimal price;
    string orderitemBookId;
    string orderOrderId;
|};

public type OrderItemOptionalized record {|
    string orderItemId?;
    int quantity?;
    decimal price?;
    string orderitemBookId?;
    string orderOrderId?;
|};

public type OrderItemWithRelations record {|
    *OrderItemOptionalized;
    BookOptionalized book?;
    OrderOptionalized 'order?;
|};

public type OrderItemTargetType typedesc<OrderItemWithRelations>;

public type OrderItemInsert OrderItem;

public type OrderItemUpdate record {|
    int quantity?;
    decimal price?;
    string orderitemBookId?;
    string orderOrderId?;
|};

public type Payment record {|
    readonly string paymentId;
    decimal paymentAmount;
    string paymentDate;
    string paymentOrderId;
|};

public type PaymentOptionalized record {|
    string paymentId?;
    decimal paymentAmount?;
    string paymentDate?;
    string paymentOrderId?;
|};

public type PaymentWithRelations record {|
    *PaymentOptionalized;
    OrderOptionalized 'order?;
|};

public type PaymentTargetType typedesc<PaymentWithRelations>;

public type PaymentInsert Payment;

public type PaymentUpdate record {|
    decimal paymentAmount?;
    string paymentDate?;
    string paymentOrderId?;
|};

