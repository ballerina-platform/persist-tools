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
    decimal salary;
    @sql:UniqueIndex {name: "favorite"}
    string favColor;
    @sql:UniqueIndex {name: "favorite"}
    string favCar;
|};

