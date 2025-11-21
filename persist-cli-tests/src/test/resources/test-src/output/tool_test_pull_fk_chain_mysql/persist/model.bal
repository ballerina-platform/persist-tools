import ballerina/persist as _;
import ballerinax/persist.sql;

public type Address record {|
    readonly int id;
    string street;
    @sql:Index {name: "cityId"}
    int cityId;
    @sql:Relation {keys: ["cityId"]}
    City city;
|};

public type Country record {|
    readonly int id;
    string name;
    City[] cities;
|};

public type City record {|
    readonly int id;
    string name;
    @sql:Index {name: "countryId"}
    int countryId;
    Address[] addresses;
    @sql:Relation {keys: ["countryId"]}
    Country country;
|};
