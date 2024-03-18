import ballerina/persist as _;
import ballerinax/persist.sql;
import ballerina/time;

public type MedicalNeed record {|
    readonly int id;
    @sql:Char {length: 50}
    string needDetails;
    @sql:Varchar {length: 50}
    string needType;
    @sql:Decimal {precision: [10,2]}
    decimal amount;
    int needId;
    string itemId;
    string name;
    time:Civil period;
    MedicalItem[] items;
|};

public type MedicalItem record {|
    readonly string name;
    int itemId;
    string decrip;
    string unit;
    int num;
    MedicalNeed need;
|};
