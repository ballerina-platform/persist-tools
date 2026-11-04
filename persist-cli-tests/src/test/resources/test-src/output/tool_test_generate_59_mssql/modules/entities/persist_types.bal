// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/time;

# Represents the `Workspace` entity.
public type Workspace record {|
    readonly string workspaceId;
    string workspaceType;
    string locationBuildingCode;
|};

# Represents `Workspace` with every field optional, for projected reads.
public type WorkspaceOptionalized record {|
    string workspaceId?;
    string workspaceType?;
    string locationBuildingCode?;
|};

# Represents `Workspace` with its related entities, for projected reads.
public type WorkspaceWithRelations record {|
    *WorkspaceOptionalized;
    BuildingOptionalized location?;
    EmployeeOptionalized[] employees?;
|};

# The `Workspace` type or a projection of it, for reads.
public type WorkspaceTargetType typedesc<WorkspaceWithRelations>;

# The fields to insert a new `Workspace`.
public type WorkspaceInsert Workspace;

# The fields to update on an existing `Workspace`.
public type WorkspaceUpdate record {|
    string workspaceType?;
    string locationBuildingCode?;
|};

# Represents the `Building` entity.
public type Building record {|
    readonly string buildingCode;
    string city;
    string state;
    string country;
    string postalCode;
    string 'type;

|};

# Represents `Building` with every field optional, for projected reads.
public type BuildingOptionalized record {|
    string buildingCode?;
    string city?;
    string state?;
    string country?;
    string postalCode?;
    string 'type?;
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
    string state?;
    string country?;
    string postalCode?;
    string 'type?;
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

# Represents the `OrderItem` entity.
public type OrderItem record {|
    readonly string orderId;
    readonly string itemId;
    int quantity;
    string notes;
|};

# Represents `OrderItem` with every field optional, for projected reads.
public type OrderItemOptionalized record {|
    string orderId?;
    string itemId?;
    int quantity?;
    string notes?;
|};

# The `OrderItem` type or a projection of it, for reads.
public type OrderItemTargetType typedesc<OrderItemOptionalized>;

# The fields to insert a new `OrderItem`.
public type OrderItemInsert OrderItem;

# The fields to update on an existing `OrderItem`.
public type OrderItemUpdate record {|
    int quantity?;
    string notes?;
|};

# Represents the `Employee` entity.
public type Employee record {|
    readonly string empNo;
    string firstName;
    string lastName;
    time:Date birthDate;
    string gender;
    time:Date hireDate;
    string departmentDeptNo;
    string workspaceWorkspaceId;
|};

# Represents `Employee` with every field optional, for projected reads.
public type EmployeeOptionalized record {|
    string empNo?;
    string firstName?;
    string lastName?;
    time:Date birthDate?;
    string gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
    string workspaceWorkspaceId?;
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
    time:Date birthDate?;
    string gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
    string workspaceWorkspaceId?;
|};

