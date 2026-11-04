// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Profile` entity.
public type Profile record {|
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
    byte[] isRegistered;
    int ownerId;
|};

# Represents `Profile` with every field optional, for projected reads.
public type ProfileOptionalized record {|
    int id?;
    string name?;
    boolean isAdult?;
    float salary?;
    decimal age?;
    byte[] isRegistered?;
    int ownerId?;
|};

# Represents `Profile` with its related entities, for projected reads.
public type ProfileWithRelations record {|
    *ProfileOptionalized;
    UserOptionalized owner?;
|};

# The `Profile` type or a projection of it, for reads.
public type ProfileTargetType typedesc<ProfileWithRelations>;

# The fields to insert a new `Profile`.
public type ProfileInsert Profile;

# The fields to update on an existing `Profile`.
public type ProfileUpdate record {|
    string name?;
    boolean isAdult?;
    float salary?;
    decimal age?;
    byte[] isRegistered?;
    int ownerId?;
|};

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    int multipleassociationsId?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    ProfileOptionalized profile?;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

# Represents the `Dept` entity.
public type Dept record {|
    readonly int id;
    string name;
    int multipleassociationsId;
|};

# Represents `Dept` with every field optional, for projected reads.
public type DeptOptionalized record {|
    int id?;
    string name?;
    int multipleassociationsId?;
|};

# Represents `Dept` with its related entities, for projected reads.
public type DeptWithRelations record {|
    *DeptOptionalized;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

# The `Dept` type or a projection of it, for reads.
public type DeptTargetType typedesc<DeptWithRelations>;

# The fields to insert a new `Dept`.
public type DeptInsert Dept;

# The fields to update on an existing `Dept`.
public type DeptUpdate record {|
    string name?;
    int multipleassociationsId?;
|};

# Represents the `Customer` entity.
public type Customer record {|
    readonly int id;
    string name;
    int age;
    int multipleassociationsId;
|};

# Represents `Customer` with every field optional, for projected reads.
public type CustomerOptionalized record {|
    int id?;
    string name?;
    int age?;
    int multipleassociationsId?;
|};

# Represents `Customer` with its related entities, for projected reads.
public type CustomerWithRelations record {|
    *CustomerOptionalized;
    MultipleAssociationsOptionalized multipleAssociations?;
|};

# The `Customer` type or a projection of it, for reads.
public type CustomerTargetType typedesc<CustomerWithRelations>;

# The fields to insert a new `Customer`.
public type CustomerInsert Customer;

# The fields to update on an existing `Customer`.
public type CustomerUpdate record {|
    string name?;
    int age?;
    int multipleassociationsId?;
|};

# Represents the `Student` entity.
public type Student record {|
    readonly int id;
    readonly string firstName;
    int age;
    string lastName;
    string nicNo;
|};

# Represents `Student` with every field optional, for projected reads.
public type StudentOptionalized record {|
    int id?;
    string firstName?;
    int age?;
    string lastName?;
    string nicNo?;
|};

# The `Student` type or a projection of it, for reads.
public type StudentTargetType typedesc<StudentOptionalized>;

# The fields to insert a new `Student`.
public type StudentInsert Student;

# The fields to update on an existing `Student`.
public type StudentUpdate record {|
    int age?;
    string lastName?;
    string nicNo?;
|};

# Represents the `MultipleAssociations` entity.
public type MultipleAssociations record {|
    readonly int id;
    string name;
|};

# Represents `MultipleAssociations` with every field optional, for projected reads.
public type MultipleAssociationsOptionalized record {|
    int id?;
    string name?;
|};

# Represents `MultipleAssociations` with its related entities, for projected reads.
public type MultipleAssociationsWithRelations record {|
    *MultipleAssociationsOptionalized;
    UserOptionalized owner?;
    DeptOptionalized dept?;
    CustomerOptionalized customer?;
|};

# The `MultipleAssociations` type or a projection of it, for reads.
public type MultipleAssociationsTargetType typedesc<MultipleAssociationsWithRelations>;

# The fields to insert a new `MultipleAssociations`.
public type MultipleAssociationsInsert MultipleAssociations;

# The fields to update on an existing `MultipleAssociations`.
public type MultipleAssociationsUpdate record {|
    string name?;
|};

