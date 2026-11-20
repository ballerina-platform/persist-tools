// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/constraint;

# Represents the `Department` entity. A department in the company, grouping its employees.
public type Department record {|
    # The department's id
    readonly string id;
    # The department's name
    string name;

|};

# Represents `Department` with every field optional, for projected reads.
public type DepartmentOptionalized record {|
    # The department's id
    string id?;
    # The department's name
    string name?;
|};

# Represents `Department` with its related entities, for projected reads.
public type DepartmentWithRelations record {|
    *DepartmentOptionalized;
    # The employees in the department
    EmployeeOptionalized[] employees?;
|};

# The `Department` type or a projection of it, for reads.
public type DepartmentTargetType typedesc<DepartmentWithRelations>;

# The fields to insert a new `Department`.
public type DepartmentInsert Department;

# The fields to update on an existing `Department`.
public type DepartmentUpdate record {|
    # The department's name
    string name?;
|};

# Represents the `Employee` entity. A person employed by the company.
public type Employee record {|
    # The employee's id
    readonly int id;
    # The employee's full name
    @constraint:String {maxLength: 50}
    string name;
    int age;
    string departmentId;
|};

# Represents `Employee` with every field optional, for projected reads.
public type EmployeeOptionalized record {|
    # The employee's id
    int id?;
    # The employee's full name
    @constraint:String {maxLength: 50}
    string name?;
    int age?;
    string departmentId?;
|};

# Represents `Employee` with its related entities, for projected reads.
public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    # The department the employee works in
    DepartmentOptionalized department?;
|};

# The `Employee` type or a projection of it, for reads.
public type EmployeeTargetType typedesc<EmployeeWithRelations>;

# The fields to insert a new `Employee`.
public type EmployeeInsert record {|
    # The employee's full name
    @constraint:String {maxLength: 50}
    string name;
    int age;
    string departmentId;
|};

# The fields to update on an existing `Employee`.
public type EmployeeUpdate record {|
    # The employee's full name
    @constraint:String {maxLength: 50}
    string name?;
    int age?;
    string departmentId?;
|};

# Represents the `Building` entity.
public type Building record {|
    readonly string code;
    string city;
|};

# Represents `Building` with every field optional, for projected reads.
public type BuildingOptionalized record {|
    string code?;
    string city?;
|};

# The `Building` type or a projection of it, for reads.
public type BuildingTargetType typedesc<BuildingOptionalized>;

# The fields to insert a new `Building`.
public type BuildingInsert Building;

# The fields to update on an existing `Building`.
public type BuildingUpdate record {|
    string city?;
|};

