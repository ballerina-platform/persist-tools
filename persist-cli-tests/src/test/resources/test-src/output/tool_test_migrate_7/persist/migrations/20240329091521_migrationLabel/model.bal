import ballerina/persist as _;
import ballerina/time;

public type MedicalNeed record {|
    readonly int id;

    string needId;
    string itemId;
    string name;
    time:Civil period;
|};

public type MedicalItem record {|
    readonly string name;

    int itemId;
    int decrip;
    string unit;
    int num;
|};
