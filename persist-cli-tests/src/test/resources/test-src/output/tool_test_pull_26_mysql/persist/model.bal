import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "ManyTypes"}
public type ManyType record {|
    //Unsupported[json] jsonType;
    readonly int id;
    string name;
    int bigIntType;
    int smallIntType;
    int tinyIntType;
    int mediumIntType;
    boolean booleanType;
    boolean booleanType2;
    //Unsupported[set('a','b','c')] setType;
    string tinyTextType;
    string textTypeType;
    string mediumTextType;
    string longTextType;
    byte[] binaryType;
    byte[] varBinaryType;
    byte[] mediumBlobType;
    byte[] longBlobType;
    byte[] blobType;
    byte[] tinyBlobType;
    //Unsupported[geometry] geometryType;
    //Unsupported[bit(8)] bitType;
|};

