import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type User record {|
    @sql:UniqueIndex {names: ["unique_user"]}
    readonly int id;
    string name;
    UserGender gender;
    @sql:Index
    @sql:UniqueIndex {names: ["unique_nic", "unique_user"]}
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
