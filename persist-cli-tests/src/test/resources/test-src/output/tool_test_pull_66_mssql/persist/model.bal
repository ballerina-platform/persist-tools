import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    FEMALE = "FEMALE",
    MALE = "MALE"
}

public type User record {|
    readonly string email;
    readonly string nic;
    string name;
    UserGender gender;
    decimal? salary;
    Car[] cars;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    string ownerEmail;
    string ownerNic;
    @sql:Relation {keys: ["ownerEmail", "ownerNic"]}
    User user;
|};

