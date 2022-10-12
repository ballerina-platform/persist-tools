import ballerina/persist;

@persist:Entity {
    key: ["id"],
    tableName: "Profiles"
}
public type Profile record {|
    readonly int id;
    string name;
    @persist:Relation {keyColumns: ["userId"], reference: ["id"]}
    User user?;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Users"
}
public type User record {|
    readonly int id;
    string name;
    Profile profile?;
|};

@persist:Entity {
    key: ["id"],
    tableName: "MultipleAssociations"
}
public type MultipleAssociations record {|
    readonly int id;
    string name;

    @persist:Relation {keyColumns: ["profileId"], reference: ["id"]}
    Profile profile?;

    @persist:Relation {keyColumns: ["userId"], reference: ["id"]}
    User user?;
|};

