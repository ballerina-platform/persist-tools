public type User record {|
    readonly int id;
    string name;
|};

public type UserInsert User;

public type UserUpdate record {|
    string name?;
|};

