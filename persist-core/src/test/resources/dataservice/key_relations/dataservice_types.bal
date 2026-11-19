// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# A list of employees.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeeList record {
    Employee[] items;
    boolean hasMore;
};

# The conditions that employees match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
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
|};

# An employee with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Employee`
# + name - The `name` field of `Employee`
# + memberships - The related project members, when `fields` names them
# + badge - The related badge, when `fields` names it
public type EmployeePartial record {|
    int id;
    string name?;
    ProjectMemberRelatedPartialList memberships?;
    BadgeRelatedPartial? badge?;
|};

# A list of employees with the fields named in the `fields` query parameter.
#
# + items - The employees
# + hasMore - Whether records exist after this page
public type EmployeePartialList record {
    EmployeePartial[] items;
    boolean hasMore;
};

# An employee read through a relation, with the fields named in the `fields` query parameter.
#
# + id - The `id` field of `Employee`
# + name - The `name` field of `Employee`
public type EmployeeRelatedPartial record {|
    int id;
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

# A list of projects.
#
# + items - The projects
# + hasMore - Whether records exist after this page
public type ProjectList record {
    Project[] items;
    boolean hasMore;
};

# The conditions that projects match, as the `filter` query parameter holds them.
#
# + code - The conditions on the `code` field
# + title - The conditions on the `title` field
public type ProjectFilter record {|
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
#
# + code - The `code` field of `Project`
# + title - The `title` field of `Project`
# + members - The related project members, when `fields` names them
public type ProjectPartial record {|
    string code;
    string title?;
    ProjectMemberRelatedPartialList members?;
|};

# A list of projects with the fields named in the `fields` query parameter.
#
# + items - The projects
# + hasMore - Whether records exist after this page
public type ProjectPartialList record {
    ProjectPartial[] items;
    boolean hasMore;
};

# A project read through a relation, with the fields named in the `fields` query parameter.
#
# + code - The `code` field of `Project`
# + title - The `title` field of `Project`
public type ProjectRelatedPartial record {|
    string code;
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
#
# + body - The created project
public type ProjectCreated record {|
    *http:Created;
    Project body;
|};

# The response to creating several projects.
#
# + body - The created projects
public type ProjectListCreated record {|
    *http:Created;
    ProjectList body;
|};

# A list of project members.
#
# + items - The project members
# + hasMore - Whether records exist after this page
public type ProjectMemberList record {
    ProjectMember[] items;
    boolean hasMore;
};

# The conditions that project members match, as the `filter` query parameter holds them.
#
# + role - The conditions on the `role` field
# + employeeId - The conditions on the `employeeId` field
# + projectCode - The conditions on the `projectCode` field
public type ProjectMemberFilter record {|
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
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} employeeId?;
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
#
# + role - The `role` field of `ProjectMember`
# + employeeId - The key of the related 'Employee'.
# + projectCode - The key of the related 'Project'.
# + employee - The related employee, when `fields` names it
# + project - The related project, when `fields` names it
public type ProjectMemberPartial record {|
    string role?;
    int employeeId;
    string projectCode;
    EmployeeRelatedPartial? employee?;
    ProjectRelatedPartial? project?;
|};

# A list of project members with the fields named in the `fields` query parameter.
#
# + items - The project members
# + hasMore - Whether records exist after this page
public type ProjectMemberPartialList record {
    ProjectMemberPartial[] items;
    boolean hasMore;
};

# A project member read through a relation, with the fields named in the `fields` query parameter.
#
# + role - The `role` field of `ProjectMember`
# + employeeId - The key of the related 'Employee'.
# + projectCode - The key of the related 'Project'.
public type ProjectMemberRelatedPartial record {|
    string role?;
    int employeeId;
    string projectCode;
|};

# The project members read through a relation, with the fields named in the `fields` query parameter.
#
# + items - The project members, at most `dataserviceConfig.pageSize`
# + hasMore - Whether more project members exist
public type ProjectMemberRelatedPartialList record {
    ProjectMemberRelatedPartial[] items;
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
#
# + body - The created project member
public type ProjectMemberCreated record {|
    *http:Created;
    ProjectMember body;
|};

# The response to creating several project members.
#
# + body - The created project members
public type ProjectMemberListCreated record {|
    *http:Created;
    ProjectMemberList body;
|};

# A list of badges.
#
# + items - The badges
# + hasMore - Whether records exist after this page
public type BadgeList record {
    Badge[] items;
    boolean hasMore;
};

# The conditions that badges match, as the `filter` query parameter holds them.
#
# + label - The conditions on the `label` field
# + employeeId - The conditions on the `employeeId` field
public type BadgeFilter record {|
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
#
# + label - The `label` field of `Badge`
# + employeeId - The key of the related 'Employee'.
# + employee - The related employee, when `fields` names it
public type BadgePartial record {|
    string label?;
    int employeeId;
    EmployeeRelatedPartial? employee?;
|};

# A list of badges with the fields named in the `fields` query parameter.
#
# + items - The badges
# + hasMore - Whether records exist after this page
public type BadgePartialList record {
    BadgePartial[] items;
    boolean hasMore;
};

# A badge read through a relation, with the fields named in the `fields` query parameter.
#
# + label - The `label` field of `Badge`
# + employeeId - The key of the related 'Employee'.
public type BadgeRelatedPartial record {|
    string label?;
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
#
# + body - The created badge
public type BadgeCreated record {|
    *http:Created;
    Badge body;
|};

# The response to creating several badges.
#
# + body - The created badges
public type BadgeListCreated record {|
    *http:Created;
    BadgeList body;
|};
