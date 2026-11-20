import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose
type Assignment record {|
    readonly string projectCode;
    readonly int seq;
    decimal hours;
    boolean billable;
|};

@dataservice:Expose {operations: ["read", "delete"]}
type Rate record {|
    readonly decimal amount;
    readonly float factor;
    readonly boolean active;
    string label;
|};
