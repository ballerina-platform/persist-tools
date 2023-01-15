public type MultipleAssociations record {|
    readonly int id;
    string name;
    int profileId;
    int userId;
|};

type MultipleAssociationsInsert MultipleAssociations;

public type MultipleAssociationsUpdate record {|
    string name?;
    int profileId?;
    int userId?;
|};

public type User record {|
    readonly int id;
    string name;
    int profileId;
|};

type UserInsert User;

public type UserUpdate record {|
    string name?;
    int profileId?;
|};

public type Profile record {|
    readonly int id;
    string name;
|};

type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
|};

