import ballerina/persist as _;
import ballerinax/persist.sql;

public type Order record {|
    readonly int id;
    int userId;
    @sql:Decimal {precision: [10, 2]}
    decimal total;
    @sql:Relation {keys: ["userId"]}
    User user;
|};

public type User record {|
    readonly int id;
    string name;
    string email;
    Order[] orders;
|};

