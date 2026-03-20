// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Class record {|
    readonly int id;
    string name;

|};

public type ClassOptionalized record {|
    int id?;
    string name?;
|};

public type ClassWithRelations record {|
    *ClassOptionalized;
    EnrollmentOptionalized[] enrollments?;
|};

public type ClassTargetType typedesc<ClassWithRelations>;

public type ClassInsert Class;

public type ClassUpdate record {|
    string name?;
|};

public type Enrollment record {|
    readonly int id;
    int classId;
|};

public type EnrollmentOptionalized record {|
    int id?;
    int classId?;
|};

public type EnrollmentWithRelations record {|
    *EnrollmentOptionalized;
    ClassOptionalized 'class?;
|};

public type EnrollmentTargetType typedesc<EnrollmentWithRelations>;

public type EnrollmentInsert Enrollment;

public type EnrollmentUpdate record {|
    int classId?;
|};

