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

@sql:Name {value:"medical_item"}
public type MedicalItem record {|
    readonly string name;
    int itemId;
    @sql:Name {value:"description"}
    string decrip;
    string unit;
    int num;
    decimal price;
    MedicalNeed need;
|};
