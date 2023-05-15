import ballerina/persist as _;
import ballerina/time;

public type MedicalNeed record {|
    readonly int needId;

    boolean itemId;
    string beneficiaryId;
    time:Civil period;
    MedicalItem[] items;
|};

public type MedicalItem record {|
    readonly string name;

    int itemId;
    string types;
    int unit;
    int num;
    MedicalNeed need;
|};
