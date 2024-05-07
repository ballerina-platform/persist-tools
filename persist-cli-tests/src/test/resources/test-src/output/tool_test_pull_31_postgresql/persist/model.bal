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
    string nic;
    decimal? salary;
    Car? car;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    @sql:UniqueIndex {name: "Car_ownerId_key"}
    int ownerId;
    @sql:Relation {keys: ["ownerId"]}
    User user;
|};

