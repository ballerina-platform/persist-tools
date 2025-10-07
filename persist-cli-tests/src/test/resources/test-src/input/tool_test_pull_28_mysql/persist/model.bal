import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "TextTest"}
public type TextTest record {| // Corrected syntax
    readonly int id;
    string textField;
|};
