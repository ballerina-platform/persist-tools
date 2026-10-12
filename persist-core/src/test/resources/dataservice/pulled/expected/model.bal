import ballerina/dataservice;
import ballerina/persist as _;
import ballerinax/persist.sql;

@dataservice:Expose
@sql:Name {value: "departments"}
type Department record {|
    readonly int id;
    string name;
|};

@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
|};
