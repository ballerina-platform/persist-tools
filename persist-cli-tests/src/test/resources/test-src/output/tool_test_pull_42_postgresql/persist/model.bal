import ballerina/persist as _;
import ballerinax/persist.sql;

public enum UserGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type User record {|
    @sql:UniqueIndex {name: "user"}
    readonly int id;
    string name;
    UserGender gender;
    @sql:UniqueIndex {name: ["user", "user_nic"]}
    string nic;
    decimal salary;
|};

