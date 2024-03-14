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
    Phone? phone;
|};

public type Phone record {|
    @sql:Mapping {name: "user_id"}
    readonly int userId;
    string number;
    @sql:Relation {refs: ["userId"]}
    User user;
|};

