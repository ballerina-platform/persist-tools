import ballerina/dataservice;
import ballerina/persist as _;

# A customer.
@dataservice:Expose
type Customer record {|
    readonly int id;
    string name;
    Profile? profile;
    Purchase[] purchases;
|};

@dataservice:Expose
type Profile record {|
    readonly int id;
    string bio;
    Customer customer;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Purchase record {|
    readonly string region;
    readonly int number;
    Customer customer;
    PurchaseLine[] lines;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type PurchaseLine record {|
    readonly int id;
    int quantity;
    Purchase purchase;
|};
