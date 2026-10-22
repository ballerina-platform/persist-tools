import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type Ticket record {|
    @sql:Generated
    readonly int id;
    string subject;
    time:Date openedOn;
|};

@dataservice:Expose {operations: [dataservice:CREATE]}
type OrderItem record {|
    @sql:Generated
    readonly int id;
    string sku;
|};
