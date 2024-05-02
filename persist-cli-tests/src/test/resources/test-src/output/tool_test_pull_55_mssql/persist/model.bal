import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    FEMALE = "FEMALE",
    MALE = "MALE"
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
    @sql:UniqueIndex {name: "UQ__Car__7E4B714DBF97A590"}
    int ownerId;
    @sql:Relation {keys: ["ownerId"]}
    User user;
|};

