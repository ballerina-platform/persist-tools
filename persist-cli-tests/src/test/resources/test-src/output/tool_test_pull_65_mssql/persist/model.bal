import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    FEMALE = "FEMALE",
    MALE = "MALE"
}

public type User record {|
    @sql:Index {name: "user"}
    readonly int id;
    string name;
    UserGender gender;
    @sql:Index {name: ["user", "user_nic"]}
    string nic;
    decimal salary;
|};

