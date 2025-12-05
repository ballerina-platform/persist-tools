import ballerina/persist as _;

type Customer record {|
    readonly int id;
    string name;
    string email;
    Order[] orders;
|};

type Order record {|
    readonly int id;
    string orderNumber;
    decimal totalAmount;
    Customer customer;
    OrderItem[] items;
|};

type OrderItem record {|
    readonly int id;
    int quantity;
    decimal price;
    Order 'order;
    Product product;
|};

type Product record {|
    readonly int id;
    string name;
    string description;
    decimal price;
    OrderItem[] orderItems;
|};
