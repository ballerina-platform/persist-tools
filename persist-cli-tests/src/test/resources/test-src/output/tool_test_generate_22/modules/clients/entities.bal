import ballerina/persist;
import ballerina/time;

@persist:Entity {
    key: ["'record"]
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int 'record = -1;

    @persist:Relation {keyColumns: ["itemId"]}
    MedicalItem item?;

    int beneficiaryId;
    time:Civil 'time;
    string urgency;
    int quantity;
|};

@persist:Entity {
    key: ["itemId"]
}
public type MedicalItem record {|
    @persist:AutoIncrement
    readonly int itemId = -1;

    string 'string;
    string 'type;
    string unit;
|};

