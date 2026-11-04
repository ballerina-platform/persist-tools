// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `Company` entity.
public type Company record {|
    readonly int id;
    string name;
|};

# Represents `Company` with every field optional, for projected reads.
public type CompanyOptionalized record {|
    int id?;
    string name?;
|};

# Represents `Company` with its related entities, for projected reads.
public type CompanyWithRelations record {|
    *CompanyOptionalized;
    EmployeeOptionalized[] employees?;
|};

# The `Company` type or a projection of it, for reads.
public type CompanyTargetType typedesc<CompanyWithRelations>;

# The fields to insert a new `Company`.
public type CompanyInsert Company;

# The fields to update on an existing `Company`.
public type CompanyUpdate record {|
    string name?;
|};

# Represents the `Employee` entity.
public type Employee record {|
    readonly int id;
    string name;
    int companyId;
|};

# Represents `Employee` with every field optional, for projected reads.
public type EmployeeOptionalized record {|
    int id?;
    string name?;
    int companyId?;
|};

# Represents `Employee` with its related entities, for projected reads.
public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    CompanyOptionalized company?;
|};

# The `Employee` type or a projection of it, for reads.
public type EmployeeTargetType typedesc<EmployeeWithRelations>;

# The fields to insert a new `Employee`.
public type EmployeeInsert Employee;

# The fields to update on an existing `Employee`.
public type EmployeeUpdate record {|
    string name?;
    int companyId?;
|};

