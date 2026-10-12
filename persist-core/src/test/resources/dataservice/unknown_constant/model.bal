import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose {operations: READ_ONLY}
type Department record {|
    readonly int id;
    string name;
|};
