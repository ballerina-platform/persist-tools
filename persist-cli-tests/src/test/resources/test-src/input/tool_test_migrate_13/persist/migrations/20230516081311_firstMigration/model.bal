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
    readonly string name;

    int itemId;
    string decrip;
    string unit;
    int num;
    decimal price;
    @sql:Decimal {precision:[10,2]}
    decimal existDecimal;
    @sql:Char {length:1}
    string existChar;
    @sql:Varchar {length:10}
    string existVarchar;
    @sql:Varchar {length:1}
    string existVarcharToChar;
    @sql:Char {length:10}
    string existCharToVarchar;
    MedicalNeed need;
|};

