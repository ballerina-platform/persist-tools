// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer for model.

import ballerina/dataservice;
import ballerina/http;

# A list of employees.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeeList record {|
    Employee[] items;
    boolean hasMore;
|};

# The conditions that employees match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
# + email - The conditions on the `email` field
# + departmentId - The conditions on the `departmentId` field
public type EmployeeFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
        boolean isNull?;
    |} email?;
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} departmentId?;
|};

# An employee with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Employee`
# + name - The `name` field of `Employee`
# + email - The `email` field of `Employee`
# + departmentId - The `departmentId` field of `Employee`
public type EmployeePartial record {|
    int id;
    string name?;
    string? email?;
    int departmentId?;
|};

# A list of employees with the fields named in the `fields` query parameter.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeePartialList record {|
    EmployeePartial[] items;
    boolean hasMore;
|};

# The description of `Employee` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceEmployeeEntity = {
    name: "Employee",
    'resource: "employees",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING},
        {name: "email", column: "email", 'type: dataservice:STRING, nullable: true},
        {name: "departmentId", column: "departmentId", 'type: dataservice:INT}
    ]
};

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

# The conditions that departments match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type DepartmentFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A department with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Department`
# + name - The `name` field of `Department`
public type DepartmentPartial record {|
    int id;
    string name?;
|};

# A list of departments with the fields named in the `fields` query parameter.
#
# + items - The departments
# + hasMore - Whether records exist after this page
public type DepartmentPartialList record {|
    DepartmentPartial[] items;
    boolean hasMore;
|};

# The description of `Department` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceDepartmentEntity = {
    name: "Department",
    'resource: "departments",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};
