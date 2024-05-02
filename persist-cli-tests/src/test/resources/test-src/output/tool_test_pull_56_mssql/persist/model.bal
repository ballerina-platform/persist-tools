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
    Phone? phone;
|};

public type Phone record {|
    @sql:Name {value: "user_id"}
    @sql:UniqueIndex {name: "UQ__Phone__B9BE370E1A7ABFEC"}
    readonly int userId;
    string number;
    @sql:Relation {keys: ["userId"]}
    User user;
|};

