// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/http;

# A list of employees.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeeList record {|
    Employee[] items;
    boolean hasMore;
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
# + hasMore - Whether records exist after this page
public type DepartmentList record {|
    Department[] items;
    boolean hasMore;
|};
