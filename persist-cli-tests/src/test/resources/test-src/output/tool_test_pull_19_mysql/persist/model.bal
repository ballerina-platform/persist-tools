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
    decimal? salary;
    @sql:Mapping {name: "drives_car"}
    @sql:Index {names: ["drives_car"]}
    int? drivesCar;
    Car[] cars;
    @sql:Relation {refs: ["drivesCar"]}
    Car car;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    @sql:Index {names: ["ownerId"]}
    int ownerId;
    @sql:Relation {refs: ["ownerId"]}
    User user;
    User[] users;
|};

