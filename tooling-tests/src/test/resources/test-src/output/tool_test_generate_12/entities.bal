import ballerina/time;
import ballerina/persist;

@persist:Entity {
    key: [],
    tableName:
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int needId = -1;

    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};
