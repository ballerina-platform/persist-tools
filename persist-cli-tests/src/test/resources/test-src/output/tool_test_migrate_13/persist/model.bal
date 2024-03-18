import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public type MedicalNeed record {|
    readonly int id;

    int needId;
    string itemId;
    @sql:Char {length:10}
    string name;
    time:Civil period;
    MedicalItem[] items;
|};

public type MedicalItem record {|
    readonly string name;

    int itemId;
    string decrip;
    @sql:Varchar {length:10}
    string unit;
    int num;
    @sql:Decimal {precision: [10,2]}
    decimal price;
    MedicalNeed need;
|};
