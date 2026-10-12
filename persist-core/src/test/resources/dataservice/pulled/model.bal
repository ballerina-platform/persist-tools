import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "departments"}
type Department record {|
    readonly int id;
    string name;
|};

type Employee record {|
    readonly int id;
    string name;
|};
