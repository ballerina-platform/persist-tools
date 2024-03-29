import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name { value: "users" }
public type User record {|
    readonly int id;
    @sql:Name { value: "user_name" }
    string name;
    @sql:Name { value: "user_age" }
    int age;
    @sql:Name { value: "addres_s" }
    string address;
|};

@sql:Name { value: "t_cars" }
public type Car record {|
    readonly int id;
    @sql:Name {value: "car_make"}
    string make;
    string model;
    @sql:Name { value: "year_manufactured" }
    int year;
|};
