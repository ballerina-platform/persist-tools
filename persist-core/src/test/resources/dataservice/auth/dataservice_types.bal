// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# A list of employees.
public type EmployeeList record {
    # The employees
    Employee[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that employees match, as the `filter` query parameter holds them.
public type EmployeeFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
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
    # The conditions on the `departmentId` field
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
public type EmployeePartial record {|
    # The `id` field of `Employee`
    int id;
    # The `name` field of `Employee`
    string name?;
    # The key of the related 'Department'.
    int departmentId?;
    # The related department, when `fields` names it
    DepartmentRelatedPartial? department?;
|};

# A list of employees with the fields named in the `fields` query parameter.
public type EmployeePartialList record {
    # The employees
    EmployeePartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# An employee read through a relation, with the fields named in the `fields` query parameter.
public type EmployeeRelatedPartial record {|
    # The `id` field of `Employee`
    int id;
    # The `name` field of `Employee`
    string name?;
    # The key of the related 'Department'.
    int departmentId?;
|};

# The employees read through a relation, with the fields named in the `fields` query parameter.
public type EmployeeRelatedPartialList record {
    # The employees, at most `dataserviceConfig.pageSize`
    EmployeeRelatedPartial[] items;
    # Whether more employees exist
    boolean hasMore;
};

# The fields of `Employee` that callers can see.
final readonly & dataservice:Field[] dataserviceEmployeeFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING},
    {name: "departmentId", column: "departmentId", 'type: dataservice:INT}
];

# The description of `Employee` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceEmployeeEntity = {
    name: "Employee",
    collection: "employees",
    'resource: "employees",
    datastore: dataservice:H2,
    fields: dataserviceEmployeeFields,
    relations: [
        {
            'field: "department",
            many: false,
            fields: ["departmentId"],
            targetFields: ["id"],
            target: {
                name: "Department",
                collection: "departments",
                'resource: "departments",
                datastore: dataservice:H2,
                fields: dataserviceDepartmentFields
            }
        }
    ]
};

# The response to creating one employee.
public type EmployeeCreated record {|
    *http:Created;
    # The created employee
    Employee body;
|};

# The response to creating several employees.
public type EmployeeListCreated record {|
    *http:Created;
    # The created employees
    EmployeeList body;
|};

# A list of departments.
public type DepartmentList record {
    # The departments
    Department[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that departments match, as the `filter` query parameter holds them.
public type DepartmentFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
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
public type DepartmentPartial record {|
    # The `id` field of `Department`
    int id;
    # The `name` field of `Department`
    string name?;
    # The related employees, when `fields` names them
    EmployeeRelatedPartialList employees?;
|};

# A list of departments with the fields named in the `fields` query parameter.
public type DepartmentPartialList record {
    # The departments
    DepartmentPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# A department read through a relation, with the fields named in the `fields` query parameter.
public type DepartmentRelatedPartial record {|
    # The `id` field of `Department`
    int id;
    # The `name` field of `Department`
    string name?;
|};

# The fields of `Department` that callers can see.
final readonly & dataservice:Field[] dataserviceDepartmentFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `Department` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceDepartmentEntity = {
    name: "Department",
    collection: "departments",
    'resource: "departments",
    datastore: dataservice:H2,
    fields: dataserviceDepartmentFields,
    relations: [
        {
            'field: "employees",
            many: true,
            fields: ["id"],
            targetFields: ["departmentId"],
            target: {
                name: "Employee",
                collection: "employees",
                'resource: "employees",
                datastore: dataservice:H2,
                fields: dataserviceEmployeeFields
            }
        }
    ]
};

# The response to creating one department.
public type DepartmentCreated record {|
    *http:Created;
    # The created department
    Department body;
|};

# The response to creating several departments.
public type DepartmentListCreated record {|
    *http:Created;
    # The created departments
    DepartmentList body;
|};
