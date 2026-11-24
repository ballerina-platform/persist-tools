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
|};

# An employee with the fields named in the `fields` query parameter.
public type EmployeePartial record {|
    # The `id` field of `Employee`
    int id;
    # The `name` field of `Employee`
    string name?;
    # The related project members, when `fields` names them
    ProjectMemberRelatedPartialList memberships?;
    # The related badge, when `fields` names it
    BadgeRelatedPartial? badge?;
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
|};

# The fields of `Employee` that callers can see.
final readonly & dataservice:Field[] dataserviceEmployeeFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
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
            'field: "memberships",
            many: true,
            fields: ["id"],
            targetFields: ["employeeId"],
            target: {
                name: "ProjectMember",
                collection: "projectMembers",
                'resource: "projectmembers",
                datastore: dataservice:H2,
                fields: dataserviceProjectMemberFields
            }
        },
        {
            'field: "badge",
            many: false,
            fields: ["id"],
            targetFields: ["employeeId"],
            target: {
                name: "Badge",
                collection: "badges",
                'resource: "badges",
                datastore: dataservice:H2,
                fields: dataserviceBadgeFields
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

# A list of projects.
public type ProjectList record {
    # The projects
    Project[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that projects match, as the `filter` query parameter holds them.
public type ProjectFilter record {|
    # The conditions on the `code` field
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
    |} code?;
    # The conditions on the `title` field
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
    |} title?;
|};

# A project with the fields named in the `fields` query parameter.
public type ProjectPartial record {|
    # The `code` field of `Project`
    string code;
    # The `title` field of `Project`
    string title?;
    # The related project members, when `fields` names them
    ProjectMemberRelatedPartialList members?;
|};

# A list of projects with the fields named in the `fields` query parameter.
public type ProjectPartialList record {
    # The projects
    ProjectPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# A project read through a relation, with the fields named in the `fields` query parameter.
public type ProjectRelatedPartial record {|
    # The `code` field of `Project`
    string code;
    # The `title` field of `Project`
    string title?;
|};

# The fields of `Project` that callers can see.
final readonly & dataservice:Field[] dataserviceProjectFields = [
    {name: "code", column: "code", 'type: dataservice:STRING, key: true},
    {name: "title", column: "title", 'type: dataservice:STRING}
];

# The description of `Project` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceProjectEntity = {
    name: "Project",
    collection: "projects",
    'resource: "projects",
    datastore: dataservice:H2,
    fields: dataserviceProjectFields,
    relations: [
        {
            'field: "members",
            many: true,
            fields: ["code"],
            targetFields: ["projectCode"],
            target: {
                name: "ProjectMember",
                collection: "projectMembers",
                'resource: "projectmembers",
                datastore: dataservice:H2,
                fields: dataserviceProjectMemberFields
            }
        }
    ]
};

# The response to creating one project.
public type ProjectCreated record {|
    *http:Created;
    # The created project
    Project body;
|};

# The response to creating several projects.
public type ProjectListCreated record {|
    *http:Created;
    # The created projects
    ProjectList body;
|};

# A list of project members.
public type ProjectMemberList record {
    # The project members
    ProjectMember[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that project members match, as the `filter` query parameter holds them.
public type ProjectMemberFilter record {|
    # The conditions on the `role` field
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
    |} role?;
    # The conditions on the `employeeId` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} employeeId?;
    # The conditions on the `projectCode` field
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
    |} projectCode?;
|};

# A project member with the fields named in the `fields` query parameter.
public type ProjectMemberPartial record {|
    # The `role` field of `ProjectMember`
    string role?;
    # The key of the related 'Employee'.
    int employeeId;
    # The key of the related 'Project'.
    string projectCode;
    # The related employee, when `fields` names it
    EmployeeRelatedPartial? employee?;
    # The related project, when `fields` names it
    ProjectRelatedPartial? project?;
|};

# A list of project members with the fields named in the `fields` query parameter.
public type ProjectMemberPartialList record {
    # The project members
    ProjectMemberPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# A project member read through a relation, with the fields named in the `fields` query parameter.
public type ProjectMemberRelatedPartial record {|
    # The `role` field of `ProjectMember`
    string role?;
    # The key of the related 'Employee'.
    int employeeId;
    # The key of the related 'Project'.
    string projectCode;
|};

# The project members read through a relation, with the fields named in the `fields` query parameter.
public type ProjectMemberRelatedPartialList record {
    # The project members, at most `dataserviceConfig.pageSize`
    ProjectMemberRelatedPartial[] items;
    # Whether more project members exist
    boolean hasMore;
};

# The fields of `ProjectMember` that callers can see.
final readonly & dataservice:Field[] dataserviceProjectMemberFields = [
    {name: "role", column: "role", 'type: dataservice:STRING},
    {name: "projectCode", column: "projectCode", 'type: dataservice:STRING, key: true},
    {name: "employeeId", column: "employeeId", 'type: dataservice:INT, key: true}
];

# The description of `ProjectMember` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceProjectMemberEntity = {
    name: "ProjectMember",
    collection: "projectMembers",
    'resource: "projectmembers",
    datastore: dataservice:H2,
    fields: dataserviceProjectMemberFields,
    relations: [
        {
            'field: "employee",
            many: false,
            fields: ["employeeId"],
            targetFields: ["id"],
            target: {
                name: "Employee",
                collection: "employees",
                'resource: "employees",
                datastore: dataservice:H2,
                fields: dataserviceEmployeeFields
            }
        },
        {
            'field: "project",
            many: false,
            fields: ["projectCode"],
            targetFields: ["code"],
            target: {
                name: "Project",
                collection: "projects",
                'resource: "projects",
                datastore: dataservice:H2,
                fields: dataserviceProjectFields
            }
        }
    ]
};

# The response to creating one project member.
public type ProjectMemberCreated record {|
    *http:Created;
    # The created project member
    ProjectMember body;
|};

# The response to creating several project members.
public type ProjectMemberListCreated record {|
    *http:Created;
    # The created project members
    ProjectMemberList body;
|};

# A list of badges.
public type BadgeList record {
    # The badges
    Badge[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that badges match, as the `filter` query parameter holds them.
public type BadgeFilter record {|
    # The conditions on the `label` field
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
    |} label?;
    # The conditions on the `employeeId` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} employeeId?;
|};

# A badge with the fields named in the `fields` query parameter.
public type BadgePartial record {|
    # The `label` field of `Badge`
    string label?;
    # The key of the related 'Employee'.
    int employeeId;
    # The related employee, when `fields` names it
    EmployeeRelatedPartial? employee?;
|};

# A list of badges with the fields named in the `fields` query parameter.
public type BadgePartialList record {
    # The badges
    BadgePartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# A badge read through a relation, with the fields named in the `fields` query parameter.
public type BadgeRelatedPartial record {|
    # The `label` field of `Badge`
    string label?;
    # The key of the related 'Employee'.
    int employeeId;
|};

# The fields of `Badge` that callers can see.
final readonly & dataservice:Field[] dataserviceBadgeFields = [
    {name: "label", column: "label", 'type: dataservice:STRING},
    {name: "employeeId", column: "employeeId", 'type: dataservice:INT, key: true}
];

# The description of `Badge` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceBadgeEntity = {
    name: "Badge",
    collection: "badges",
    'resource: "badges",
    datastore: dataservice:H2,
    fields: dataserviceBadgeFields,
    relations: [
        {
            'field: "employee",
            many: false,
            fields: ["employeeId"],
            targetFields: ["id"],
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

# The response to creating one badge.
public type BadgeCreated record {|
    *http:Created;
    # The created badge
    Badge body;
|};

# The response to creating several badges.
public type BadgeListCreated record {|
    *http:Created;
    # The created badges
    BadgeList body;
|};
