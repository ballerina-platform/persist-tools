import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

@sql:Mapping {name: "USERS"}
public type User record {|
    @sql:Mapping {name: "ID"}
    readonly int id;
    string name;
    UserGender gender;
    string nic;
    decimal? salary;
    Car[] cars;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    @sql:Index {names: ["ownerId"]}
    int ownerId;
    @sql:Relation {refs: ["ownerId"]}
    User user;
|};
