import ballerina/persist;

@persist:Entity {
    key: ["id"],
    tableName: "Profiles"
}
public type Profile record {|
    readonly int id;
    string name;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Users"
}
public type User record {|
    readonly int id;
    string name;
|};

@persist:Entity {
    key: ["id"]
}
public type Dept record {|
    readonly int id;
    string name;
|};

@persist:Entity {
    key: ["id"]
}
public type Customer record {|
    readonly int id;
    string name;
|};

@persist:Entity {
    key: ["id"],
    tableName: "MultipleAssociations"
}
public type MultipleAssociations record {|
    readonly int id;
    string name;

    @persist:Relation
    Profile profile?;

    @persist:Relation {}
    User user?;

    @persist:Relation {keyColumns: ["profileId"]}
    Dept dept?;

    @persist:Relation {reference: ["id"]}
    Customer customer?;
|};

