import ballerina/time;
import ballerina/persist;

@persist:Entity {
    key: ["needId", "itemId"],
    tableName: "MedicalNeeds"
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int needId = -1;
    readonly int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};
