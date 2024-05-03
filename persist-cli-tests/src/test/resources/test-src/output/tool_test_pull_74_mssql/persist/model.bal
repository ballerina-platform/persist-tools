import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@sql:Name {value: "ManyTypes"}
public type ManyType record {|
    @sql:Generated
    readonly int id;
    int? bigIntType;
    @sql:Decimal {precision: [18, 30]}
    decimal? decimalType;
    @sql:Decimal {precision: [18, 30]}
    decimal? numericType;
    @sql:Decimal {precision: [10, 8]}
    decimal? numericTypeLen;
    boolean? bitType;
    int? smallIntType;
    int? intType;
    int? tinyIntType;
    //Unsupported[MONEY(19)] moneyType;
    //Unsupported[SMALLMONEY(10)] smallMoneyType;
    float? floatType;
    //Unsupported[REAL(24)] floatTypeLen;
    //Unsupported[REAL(24)] realType;
    time:Date? dateType;
    time:Civil? dateTimeType;
    time:Civil? dateTime2Type;
    //Unsupported[SMALLDATETIME] smallDateTimeType;
    time:TimeOfDay? timeType;
    //Unsupported[DATETIMEOFFSET] dateTimeOffsetType;
    @sql:Char {length: 1}
    string? charType;
    @sql:Char {length: 10}
    string? charTypeLen;
    @sql:Varchar {length: 1}
    string? varcharType;
    @sql:Varchar {length: 10}
    string? varcharTypeLen;
    //Unsupported[NCHAR] ncharType;
    //Unsupported[NCHAR] ncharTypeLen;
    //Unsupported[NVARCHAR] nvarcharType;
    //Unsupported[NVARCHAR] nvarcharTypeLen;
    byte[]? binaryType;
    byte[]? binaryTypeLen;
    byte[]? varBinaryType;
    byte[]? varBinaryTypeLen;
|};

