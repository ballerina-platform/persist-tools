import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "currency"}
public type Currency record {|
    readonly int id;
    @sql:Varchar {length: 3}
    string code;
|};

