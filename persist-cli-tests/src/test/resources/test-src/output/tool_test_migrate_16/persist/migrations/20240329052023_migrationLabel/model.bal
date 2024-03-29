import ballerina/persist as _;
import ballerinax/persist.sql;

public type User record {|
    readonly int id;
    string name;
    @sql:Name { value: "user_age" }
    int age;
    @sql:Name { value: "home_address" }
    string address;
|};

@sql:Name { value: "cars" }
public type Car record {|
    readonly int id;
    string make;
    string model;
    @sql:Name { value: "year_man" }
    int year;
|};
