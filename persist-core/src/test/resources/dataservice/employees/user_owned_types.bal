// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer for model.

import ballerina/http;

# A list of employees.
#
# + items - The employees
public type EmployeeList record {|
    Employee[] items;
|};

# The response to creating one employee.
#
# + body - The created employee
public type EmployeeCreated record {|
    *http:Created;
    Employee body;
|};

# The response to creating several employees.
#
# + body - The created employees
public type EmployeeListCreated record {|
    *http:Created;
    EmployeeList body;
|};

# A list of departments.
#
# + items - The departments
public type DepartmentList record {|
    Department[] items;
|};
