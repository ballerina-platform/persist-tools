import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type User record {|
    readonly int id;
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
    @sql:Index {names: ["ownerId"]}
    int ownerId;
    @sql:Mapping {name: "OWNER_NIC"}
    @sql:Index {names: ["ownerNic"]}
    string ownerNic;
    @sql:Relation {refs: ["ownerId", "ownerNic"]}
    User user;
|};
