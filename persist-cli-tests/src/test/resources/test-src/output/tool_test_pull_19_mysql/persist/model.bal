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
    @sql:Name {value: "drives_car"}
    @sql:Index {name: ["drives_car"]}
    int? drivesCar;
    Car[] cars;
    @sql:Relation {keys: ["drivesCar"]}
    Car car;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    @sql:Index {name: "ownerId"}
    int ownerId;
    @sql:Relation {keys: ["ownerId"]}
    User user;
    User[] users;
|};

