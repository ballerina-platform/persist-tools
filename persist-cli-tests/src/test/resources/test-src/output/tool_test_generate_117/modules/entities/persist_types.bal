// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type Company record {|
    readonly int id;
    string name;

|};

public type CompanyOptionalized record {|
    int id?;
    string name?;
|};

public type CompanyWithRelations record {|
    *CompanyOptionalized;
    EmployeeOptionalized[] employees?;
|};

public type CompanyTargetType typedesc<CompanyWithRelations>;

public type CompanyInsert Company;

public type CompanyUpdate record {|
    string name?;
|};

public type Employee record {|
    readonly int id;
    string name;
    int companyId;
|};

public type EmployeeOptionalized record {|
    int id?;
    string name?;
    int companyId?;
|};

public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    CompanyOptionalized company?;
|};

public type EmployeeTargetType typedesc<EmployeeWithRelations>;

public type EmployeeInsert Employee;

public type EmployeeUpdate record {|
    string name?;
    int companyId?;
|};

