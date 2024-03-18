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
    @sql:Index {name: "idx_exampleIndex"}
    string exampleIndex;
|};

public type MedicalItem record {|
    readonly string name;
    int itemId;
    string decrip;
    string unit;
    int num;
    decimal price;
    MedicalNeed need;
    @sql:UniqueIndex
    string exampleUniqueIndex;
|};
