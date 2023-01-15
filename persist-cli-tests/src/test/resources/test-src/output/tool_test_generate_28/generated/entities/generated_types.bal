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

