import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose {operations: []}
type Department record {|
    readonly int id;
    string name;
|};
