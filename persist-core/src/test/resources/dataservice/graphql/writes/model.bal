import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@dataservice:Expose
type Purchase record {|
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
    time:Civil? shippedAt;
    string? note;
    decimal total;
    @dataservice:Immutable
    int customerId;
    @sql:Relation {keys: ["customerId"]}
    Customer customer;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type Customer record {|
    @sql:Generated
    readonly int id;
    string name;
    Purchase[] purchases;
|};

# A log entry that callers may only write.
@dataservice:Expose {operations: [dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE]}
type AuditEntry record {|
    readonly string region;
    readonly int seq;
    string message;
|};
