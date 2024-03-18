import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public type MedicalNeed record {|
    readonly int id;

    int needId;
    string itemId;
    @sql:Index
    string name;
    time:Civil period;
    MedicalItem[] items;
|};

public type MedicalItem record {|
    readonly string name;
    @sql:UniqueIndex {name: "idx_item"}
    int itemId;
    string decrip;
    string unit;
    int num;
    decimal price;
    MedicalNeed need;
|};
