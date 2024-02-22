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
    Car[] cars;
|};

@sql:Mapping {name: "cars"}
public type Car record {|
    readonly int id;
    string name;
    @sql:Mapping {name: "MODEL"}
    string model;
    @sql:Index {names: ["ownerId"]}
    int ownerId;
    @sql:Relation {refs: ["ownerId"]}
    User owner;
|};
