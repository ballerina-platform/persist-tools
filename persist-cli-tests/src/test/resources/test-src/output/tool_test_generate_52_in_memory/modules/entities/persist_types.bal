// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/time;

public enum Gender {
    MALE,
    FEMALE
}

public enum WorkspaceType {
    CUBICLE = "C",
    OFFICE,
    MEETING_ROOM = "MR"
}

public enum State {
    NY,
    CA,
    WY
}

# Represents the `Employee` entity.
public type Employee record {|
    readonly string empNo;
    string firstName;
    string lastName;
    time:Date hireDate;
    Gender gender;
    time:Civil dateOfBirth;
    string departmentDeptNo;
|};

# Represents `Employee` with every field optional, for projected reads.
public type EmployeeOptionalized record {|
    string empNo?;
    string firstName?;
    string lastName?;
    time:Date hireDate?;
    Gender gender?;
    time:Civil dateOfBirth?;
    string departmentDeptNo?;
|};

# Represents `Employee` with its related entities, for projected reads.
public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    DepartmentOptionalized department?;
    WorkspaceOptionalized workspace?;
|};

# The `Employee` type or a projection of it, for reads.
public type EmployeeTargetType typedesc<EmployeeWithRelations>;

# The fields to insert a new `Employee`.
public type EmployeeInsert Employee;

# The fields to update on an existing `Employee`.
public type EmployeeUpdate record {|
    string firstName?;
    string lastName?;
    time:Date hireDate?;
    Gender gender?;
    time:Civil dateOfBirth?;
    string departmentDeptNo?;
|};

# Represents the `Workspace` entity.
public type Workspace record {|
    readonly string workspaceId;
    WorkspaceType workspaceType;
    string locationBuildingCode;
    string employeeEmpNo;
|};

# Represents `Workspace` with every field optional, for projected reads.
public type WorkspaceOptionalized record {|
    string workspaceId?;
    WorkspaceType workspaceType?;
    string locationBuildingCode?;
    string employeeEmpNo?;
|};

# Represents `Workspace` with its related entities, for projected reads.
public type WorkspaceWithRelations record {|
    *WorkspaceOptionalized;
    BuildingOptionalized location?;
    EmployeeOptionalized employee?;
|};

# The `Workspace` type or a projection of it, for reads.
public type WorkspaceTargetType typedesc<WorkspaceWithRelations>;

# The fields to insert a new `Workspace`.
public type WorkspaceInsert Workspace;

# The fields to update on an existing `Workspace`.
public type WorkspaceUpdate record {|
    WorkspaceType workspaceType?;
    string locationBuildingCode?;
    string employeeEmpNo?;
|};

# Represents the `Building` entity.
public type Building record {|
    readonly string buildingCode;
    string city;
    State state;
    string country;
    string postalCode;
|};

# Represents `Building` with every field optional, for projected reads.
public type BuildingOptionalized record {|
    string buildingCode?;
    string city?;
    State state?;
    string country?;
    string postalCode?;
|};

# Represents `Building` with its related entities, for projected reads.
public type BuildingWithRelations record {|
    *BuildingOptionalized;
    WorkspaceOptionalized[] workspaces?;
|};

# The `Building` type or a projection of it, for reads.
public type BuildingTargetType typedesc<BuildingWithRelations>;

# The fields to insert a new `Building`.
public type BuildingInsert Building;

# The fields to update on an existing `Building`.
public type BuildingUpdate record {|
    string city?;
    State state?;
    string country?;
    string postalCode?;
|};

# Represents the `Department` entity.
public type Department record {|
    readonly string deptNo;
    string deptName;
|};

# Represents `Department` with every field optional, for projected reads.
public type DepartmentOptionalized record {|
    string deptNo?;
    string deptName?;
|};

# Represents `Department` with its related entities, for projected reads.
public type DepartmentWithRelations record {|
    *DepartmentOptionalized;
    EmployeeOptionalized[] employees?;
|};

# The `Department` type or a projection of it, for reads.
public type DepartmentTargetType typedesc<DepartmentWithRelations>;

# The fields to insert a new `Department`.
public type DepartmentInsert Department;

# The fields to update on an existing `Department`.
public type DepartmentUpdate record {|
    string deptName?;
|};

