import ballerina/persist as _;
import ballerina/time;

public type MedicalNeed record {|
    readonly int id;

    int needId;
    string itemId;
    string name;
    time:Civil period;
|};
