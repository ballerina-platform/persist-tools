public type Vehicle record {|
    readonly int model;
    string name;
    int employeeId;
|};

type VehicleInsert Vehicle;

public type VehicleUpdate record {|
    string name?;
    int employeeId?;
|};

public type Company record {|
    readonly int id;
    string name;
|};

type CompanyInsert Company;

public type CompanyUpdate record {|
    string name?;
|};

public type Employee record {|
    readonly int id;
    string name;
    int companyId;
|};

type EmployeeInsert Employee;

public type EmployeeUpdate record {|
    string name?;
    int companyId?;
|};

