import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;

@dataservice:Expose
type Order record {|
    readonly int id;
    @dataservice:Immutable
    string number;
    @dataservice:ReadOnly {value: "pending"}
    string status;
    @dataservice:Internal
    string? internalNotes;
    @dataservice:Internal {value: 1}
    int tenantId;
    time:Date placedOn;
    @dataservice:Immutable
    Customer customer;
    @dataservice:ReadOnly {value: 7}
    Warehouse warehouse;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Customer record {|
    readonly int id;
    string name;
    @dataservice:Internal
    Order[] orders;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Warehouse record {|
    readonly int id;
    string city;
    Order[] orders;
|};
