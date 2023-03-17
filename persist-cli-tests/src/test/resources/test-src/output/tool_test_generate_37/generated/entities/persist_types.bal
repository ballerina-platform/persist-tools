// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
    byte[] isRegistered;
    int userId;
|};

public type ProfileOptionalized record {|
    int id?;
    string name?;
    boolean isAdult?;
    float salary?;
    decimal age?;
    byte[] isRegistered?;
    int userId?;
|};

public type ProfileWithRelations record {|
    *ProfileOptionalized;
    UserOptionalized owner?;
|};

public type ProfileTargetType typedesc<ProfileWithRelations>;

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
    boolean isAdult?;
    float salary?;
    decimal age?;
    byte[] isRegistered?;
    int userId?;
|};

public type User record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

public type UserOptionalized record {|
    int id?;
    string name?;
    int multipleassociationsId?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    ProfileOptionalized profile?;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

public type UserTargetType typedesc<UserWithRelations>;

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

public type DeptOptionalized record {|
    int id?;
    string name?;
    int multipleassociationsId?;
|};

public type DeptWithRelations record {|
    *DeptOptionalized;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

public type DeptTargetType typedesc<DeptWithRelations>;

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

public type CustomerOptionalized record {|
    int id?;
    string name?;
    int age?;
    int multipleassociationsId?;
|};

public type CustomerWithRelations record {|
    *CustomerOptionalized;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

public type CustomerTargetType typedesc<CustomerWithRelations>;

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

public type StudentOptionalized record {|
    int id?;
    string firstName?;
    int age?;
    string lastName?;
    string nicNo?;
|};

public type StudentTargetType typedesc<StudentOptionalized>;

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

public type MultipleAssociationsOptionalized record {|
    int id?;
    string name?;
|};

public type MultipleAssociationsWithRelations record {|
    *MultipleAssociationsOptionalized;
    UserOptionalized owner?;
    DeptOptionalized dept?;
    CustomerOptionalized customer?;
|};

public type MultipleAssociationsTargetType typedesc<MultipleAssociationsWithRelations>;

public type MultipleAssociationsInsert MultipleAssociations;

public type MultipleAssociationsUpdate record {|
    string name?;
|};

