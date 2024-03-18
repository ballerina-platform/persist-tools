import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public type MedicalNeed record {|
    readonly int id;

    int needId;
    string itemId;
    string name;
    time:Civil period;
    MedicalItem[] items;
|};

public type MedicalItem record {|
    @sql:Generated
    readonly int nameId;
    int itemId;
    string decrip;
    string unit;
    int num;
    decimal price;
    MedicalNeed need;
|};
