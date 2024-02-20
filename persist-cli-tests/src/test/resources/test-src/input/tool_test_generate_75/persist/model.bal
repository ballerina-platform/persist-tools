import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type User record {|
    readonly int id;
    string name;
    UserGender gender;
    @sql:VarChar {length: 12}
    string nic;
    @sql:Decimal {precision: [10, 2]}
    decimal? salary;
    Car[] cars;
|};

public type Car record {|
    readonly int id;
    string name;
    @sql:Char {length: 10}
    string model;
    @sql:Index {names: ["ownerId"]}
    int ownerId;
    @sql:Relation {refs: ["ownerId"]}
    User owner;
|};
