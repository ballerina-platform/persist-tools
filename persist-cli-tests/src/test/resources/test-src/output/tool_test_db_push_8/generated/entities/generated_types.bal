// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
    byte isRegistered;
    int userId;
|};

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
    boolean isAdult?;
    float salary?;
    decimal age?;
    byte isRegistered?;
    int userId?;
|};

public type User record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

public type Dept record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

public type DeptInsert Dept;

public type DeptUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

public type Customer record {|
    readonly int id;
    string name;
    int age;
    int multipleassociationsId;
|};

public type CustomerInsert Customer;

public type CustomerUpdate record {|
    string name?;
    int age?;
    int multipleassociationsId?;
|};

public type Student record {|
    readonly int id;
    readonly string firstName;
    int age;
    string lastName;
    string nicNo;
|};

public type StudentInsert Student;

public type StudentUpdate record {|
    int age?;
    string lastName?;
    string nicNo?;
|};

public type MultipleAssociations record {|
    readonly int id;
    string name;
|};

public type MultipleAssociationsInsert MultipleAssociations;

public type MultipleAssociationsUpdate record {|
    string name?;
|};

