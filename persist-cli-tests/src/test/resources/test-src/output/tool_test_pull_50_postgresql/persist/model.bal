import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@sql:Name {value: "ManyTypes"}
public type ManyType record {|
    readonly int id;
    int? smallIntType;
    int? intType;
    int? integerType;
    int? bigIntType;
    decimal? decimalType;
    decimal? numericType;
    float? realType;
    float? doublePrecisionType;
    @sql:Generated
    int smallSerialType;
    @sql:Generated
    int serialType;
    @sql:Generated
    int bigSerialType;
    //Unsupported[MONEY] moneyType;
    string? characterVaryingType;
    string? varcharType;
    @sql:Char {length: 1}
    string? characterType;
    @sql:Char {length: 1}
    string? charType;
    @sql:Char {length: 3}
    string? bpCharType;
    string? textType;
    byte[]? byteaType;
    time:Utc? timestampType;
    time:Utc? timestampWithTimeZoneType;
    time:Utc? timestampWithOutTimeZoneType;
    time:Date? dateType;
    time:TimeOfDay? timeType;
    time:TimeOfDay? timeWithTimeZoneType;
    time:TimeOfDay? timeWithOutTimeZoneType;
    //Unsupported[INTERVAL] intervalType;
    boolean? booleanType;
    //Unsupported[MOOD] enumType;
    //Unsupported[CIRCLE] circleType;
    //Unsupported[BIT(1)] bitType;
    //Unsupported[BIT VARYING] bitVaryingType;
    //Unsupported[UUID] uuidType;
    //Unsupported[INTEGER[]] arrayType;
|};

