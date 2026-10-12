import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose
public type Department record {|
    readonly int id;
    string name;
    string location;
|};

public type User record {|
    readonly int id;
    string name;
    string email;
|};

