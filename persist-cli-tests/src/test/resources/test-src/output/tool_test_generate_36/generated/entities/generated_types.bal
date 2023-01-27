// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type Company record {|
    readonly int 'id;
    string name;
    string 'type;
|};

public type CompanyInsert Company;

public type CompanyUpdate record {|
    string name?;
    string 'type?;
|};

public type Employee record {|
    readonly int 'id;
    string name;
    string 'type;
    int companyId;
|};

public type EmployeeInsert Employee;

public type EmployeeUpdate record {|
    string name?;
    string 'type?;
    int companyId?;
|};

