import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose
type Gadget record {|
    readonly int id;
    //Unsupported[GEOMETRY] location;
|};

type Widget record {|
    readonly int id;
    string name;
|};

type Part record {|
    readonly int id;
    string name;
|};
