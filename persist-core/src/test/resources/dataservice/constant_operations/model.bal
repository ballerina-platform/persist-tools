import ballerina/dataservice;
import ballerina/persist as _;

const READ_ONLY = [dataservice:READ];

@dataservice:Expose {operations: READ_ONLY}
type Department record {|
    readonly int id;
    string name;
|};
