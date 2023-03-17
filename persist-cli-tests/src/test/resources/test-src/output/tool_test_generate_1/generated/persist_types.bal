// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/time;

public type Employee record {|
    readonly string empNo;
    string firstName;
    string lastName;
    time:Date birthDate;
    string gender;
    time:Date hireDate;
    string departmentDeptNo;
|};

public type EmployeeOptionalized record {|
    string empNo?;
    string firstName?;
    string lastName?;
    time:Date birthDate?;
    string gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
|};

public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    DepartmentOptionalized department?;
    WorkspaceOptionalized workspace?;
|};

public type EmployeeTargetType typedesc<EmployeeWithRelations>;

public type EmployeeInsert Employee;

public type EmployeeUpdate record {|
    string firstName?;
    string lastName?;
    time:Date birthDate?;
    string gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
|};

public type Workspace record {|
    readonly string workspaceId;
    string workspaceType;
    string buildingBuildingCode;
    string employeeEmpNo;
|};

public type WorkspaceOptionalized record {|
    string workspaceId?;
    string workspaceType?;
    string buildingBuildingCode?;
    string employeeEmpNo?;
|};

public type WorkspaceWithRelations record {|
    *WorkspaceOptionalized;
    BuildingOptionalized location?;
    EmployeeOptionalized employee?;
|};

public type WorkspaceTargetType typedesc<WorkspaceWithRelations>;

public type WorkspaceInsert Workspace;

public type WorkspaceUpdate record {|
    string workspaceType?;
    string buildingBuildingCode?;
    string employeeEmpNo?;
|};

public type Building record {|
    readonly string buildingCode;
    string city;
    string state;
    string country;
    string postalCode;
|};

public type BuildingOptionalized record {|
    string buildingCode?;
    string city?;
    string state?;
    string country?;
    string postalCode?;
|};

public type BuildingWithRelations record {|
    *BuildingOptionalized;
    WorkspaceOptionalized[] workspaces?;
|};

public type BuildingTargetType typedesc<BuildingWithRelations>;

public type BuildingInsert Building;

public type BuildingUpdate record {|
    string city?;
    string state?;
    string country?;
    string postalCode?;
|};

public type Department record {|
    readonly string deptNo;
    string deptName;
|};

public type DepartmentOptionalized record {|
    string deptNo?;
    string deptName?;
|};

public type DepartmentWithRelations record {|
    *DepartmentOptionalized;
    EmployeeOptionalized[] employees?;
|};

public type DepartmentTargetType typedesc<DepartmentWithRelations>;

public type DepartmentInsert Department;

public type DepartmentUpdate record {|
    string deptName?;
|};

