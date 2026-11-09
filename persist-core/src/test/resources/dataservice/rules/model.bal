import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

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
    int customerId;
    @sql:Relation {keys: ["customerId"]}
    Customer customer;
    @dataservice:ReadOnly {value: 7}
    int warehouseId;
    @sql:Relation {keys: ["warehouseId"]}
    @dataservice:Internal
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
