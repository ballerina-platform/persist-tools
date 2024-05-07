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
    decimal? salary;
    @sql:Name {value: "drives_car"}
    int? drivesCar;
    Car[] cars;
    @sql:Relation {keys: ["drivesCar"]}
    Car car;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    int ownerId;
    @sql:Relation {keys: ["ownerId"]}
    User user;
    User[] users;
|};

