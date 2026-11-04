// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Class` entity.
public type Class record {|
    readonly int id;
    string name;

|};

# Represents `Class` with every field optional, for projected reads.
public type ClassOptionalized record {|
    int id?;
    string name?;
|};

# Represents `Class` with its related entities, for projected reads.
public type ClassWithRelations record {|
    *ClassOptionalized;
    EnrollmentOptionalized[] enrollments?;
|};

# The `Class` type or a projection of it, for reads.
public type ClassTargetType typedesc<ClassWithRelations>;

# The fields to insert a new `Class`.
public type ClassInsert Class;

# The fields to update on an existing `Class`.
public type ClassUpdate record {|
    string name?;
|};

# Represents the `Enrollment` entity.
public type Enrollment record {|
    readonly int id;
    int classId;
|};

# Represents `Enrollment` with every field optional, for projected reads.
public type EnrollmentOptionalized record {|
    int id?;
    int classId?;
|};

# Represents `Enrollment` with its related entities, for projected reads.
public type EnrollmentWithRelations record {|
    *EnrollmentOptionalized;
    ClassOptionalized 'class?;
|};

# The `Enrollment` type or a projection of it, for reads.
public type EnrollmentTargetType typedesc<EnrollmentWithRelations>;

# The fields to insert a new `Enrollment`.
public type EnrollmentInsert Enrollment;

# The fields to update on an existing `Enrollment`.
public type EnrollmentUpdate record {|
    int classId?;
|};

