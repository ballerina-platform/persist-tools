import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "order_items"}
type OrderItem record {|
    readonly int id;
    string sku;
|};

type Customer record {|
    readonly int id;
    string name;
|};

@sql:Name {value: "gadgets"}
type Gadget record {|
    readonly int id;
    //Unsupported[GEOMETRY] location;
|};
