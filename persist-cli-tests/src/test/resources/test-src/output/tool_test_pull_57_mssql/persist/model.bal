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
    Phone[] phones;
|};

public type Phone record {|
    @sql:Name {value: "user_id"}
    readonly int userId;
    readonly string number;
    @sql:Relation {keys: ["userId"]}
    User user;
|};

