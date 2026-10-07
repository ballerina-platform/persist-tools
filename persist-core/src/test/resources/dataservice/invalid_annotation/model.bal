import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose {operations: [dataservice:EXECUTE]}
type Department record {|
    readonly int id;
    string name;
|};
