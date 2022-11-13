import ballerina/persist;
import ballerina/time;

@persist:Entity {
    key: ["needId"]
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int needId = -1;

    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};

@persist:Entity {
    key: ["id"]
}
public type AidPackageOrderItem record {|
    @persist:AutoIncrement
    readonly int id = -1;

    @persist:Relation {keyColumns: ["needId"], reference: ["needId"]}
    MedicalNeed medicalNeed?;

    int quantity;
    int totalAmount;
|};

