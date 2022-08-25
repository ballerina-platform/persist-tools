import ballerina/time;
import ballerina/persist;

@persist:Entity {
    key: ["a"],
    tableName: "DataTypes"
}
public type DataType record {|
    @persist:AutoIncrement
    readonly int a = -1;

    string b1;
    string? b2;
    int c1;
    int? c2;
    boolean d1;
    boolean? d2;
    float e1;
    float? e2;
    decimal f1;
    decimal? f2;
    byte[] g1;
    byte[]? g2;
    xml h1;
    xml? h2;
    record {} i1;
    record {}? i2;
    time:Utc j1;
    time:Utc? j2;
    time:Civil k1;
    time:Civil? k2;
    time:Date l1;
    time:Date? l2;
    time:TimeOfDay m1;
    time:TimeOfDay? m2;
    time:Civil[] n1;
    time:Civil[]? n2;
    time:TimeOfDay[] o1;
    time:TimeOfDay[]? o2;
    string[] p1;
    string?[] p2;
    string?[]? p3;
    int[] q1;
    int?[] q2;
    int?[]? q3;
    boolean[] r1;
    boolean?[] r2;
    boolean?[]? r3;
    float[] s1;
    float?[] s2;
    float?[]? s3;
    decimal[] t1;
    decimal?[] t2;
    decimal?[]? t3;
    byte[][] u1;
    byte[]?[] u2;
    byte[]?[]? u3;
    anydata v1;
    anydata? v2;
    object {} w1;
    anydata[] x1;
    anydata[]? x2;
    object {}[]? y1;
|};
