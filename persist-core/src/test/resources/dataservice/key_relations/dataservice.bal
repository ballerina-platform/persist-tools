// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/openapi;
import ballerina/persist;

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@openapi:ServiceInfo {embed: true, title: "hr"}
@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}
service http:InterceptableService /api on dataserviceListener {

    private final Client persistClient;
    private final dataservice:PageConfig pageConfig;

    # Creates the persist client, and checks the page sizes.
    #
    # + return - An error when the client cannot be created or a setting is invalid
    function init() returns error? {
        self.persistClient = check new ();
        self.pageConfig = check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
    }

    # Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
    #
    # + return - The error interceptor
    public function createInterceptors() returns DataserviceErrorInterceptor => new;

    # Gets a page of employees.
    #
    # + filter - The conditions that the employees match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of employees, or the error response
    resource function get employees(EmployeeFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns EmployeeList|EmployeePartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceEmployeeEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Employee");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceEmployeeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        stream<Employee, persist:Error?> rows = self.persistClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Employee[]|persist:Error items = from Employee item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items, "Employee");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            Employee[] sources = items;
            map<anydata>[] partials = from Employee item in sources
                select dataservice:selectFields(item, dataserviceEmployeeEntity, fields);
            do {
                if dataservice:isIncluded(fields, "memberships") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:toIncludeQuery(
                                dataserviceEmployeeEntity, "memberships", [sources[i]], self.pageConfig);
                        stream<ProjectMember, persist:Error?> includedRows = self.persistClient->/projectmembers(ProjectMember,
                            whereClause = include.whereClause, orderByClause = include.orderByClause,
                            limitClause = include.limitClause
                        );
                        ProjectMember[] included = check from ProjectMember row in includedRows
                            select row;
                        boolean includedMore = dataservice:trimToPage(included, include);
                        dataservice:attachMany(
                                partials[i], dataserviceEmployeeEntity, "memberships", included, includedMore, fields);
                    }
                }
                if dataservice:isIncluded(fields, "badge") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceEmployeeEntity, "badge", sources, self.pageConfig);
                    stream<Badge, persist:Error?> includedRows = self.persistClient->/badges(Badge,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Badge[] included = check from Badge row in includedRows
                        select row;
                    dataservice:attachOne(
                            partials, sources, dataserviceEmployeeEntity, "badge", included, fields);
                }
            } on fail error err {
                return dsHttp:toListError(err, "Employee");
            }
            EmployeePartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Employee");
            }
            return <EmployeePartialList>{items: converted, hasMore};
        }
        return <EmployeeList>{items, hasMore};
    }

    # Gets an employee by its key.
    #
    # + id - The id of the employee
    # + fields - The fields to return; the key fields are always returned
    # + return - The employee, or the error response
    resource function get employees/[int id](string[]? fields = ())
            returns Employee|EmployeePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceEmployeeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        Employee|persist:Error item = self.persistClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id]);
        }
        if fields is string[] {
            map<anydata> partial = dataservice:selectFields(item, dataserviceEmployeeEntity, fields);
            do {
                if dataservice:isIncluded(fields, "memberships") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceEmployeeEntity, "memberships", [item], self.pageConfig);
                    stream<ProjectMember, persist:Error?> includedRows = self.persistClient->/projectmembers(ProjectMember,
                        whereClause = include.whereClause, orderByClause = include.orderByClause,
                        limitClause = include.limitClause
                    );
                    ProjectMember[] included = check from ProjectMember row in includedRows
                        select row;
                    boolean includedMore = dataservice:trimToPage(included, include);
                    dataservice:attachMany(
                            partial, dataserviceEmployeeEntity, "memberships", included, includedMore, fields);
                }
                if dataservice:isIncluded(fields, "badge") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceEmployeeEntity, "badge", [item], self.pageConfig);
                    stream<Badge, persist:Error?> includedRows = self.persistClient->/badges(Badge,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Badge[] included = check from Badge row in includedRows
                        select row;
                    dataservice:attachOne(
                            [partial], [item], dataserviceEmployeeEntity, "badge", included, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Employee", [id]);
            }
            EmployeePartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Employee", [id]);
            }
            return converted;
        }
        return item;
    }

    # Gets the memberships of an employee.
    #
    # + id - The id of the employee
    # + filter - The conditions that the memberships match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of memberships in the page
    # + offset - The number of memberships to skip
    # + fields - The fields of the memberships to return; the key fields are always returned
    # + return - A page of the memberships, or the error response
    resource function get employees/[int id]/memberships(ProjectMemberFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns ProjectMemberList|ProjectMemberRelatedPartialList|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectMemberEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ProjectMember");
        }
        map<anydata> itemKey = {"id": id};
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toRelatedQuery(
                dataserviceEmployeeEntity, "memberships", itemKey, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ProjectMember");
        }
        stream<ProjectMember, persist:Error?> rows = self.persistClient->/projectmembers(ProjectMember,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ProjectMember[]|persist:Error items = from ProjectMember related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items, "Employee", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if items.length() == 0 {
            Employee|persist:Error item = self.persistClient->/employees/[id];
            if item is persist:Error {
                return dsHttp:toGetError(item, "Employee", [id], "get related");
            }
            return <ProjectMemberList>{items, hasMore};
        }
        if fields is string[] {
            ProjectMemberRelatedPartial[]|error partials = (from ProjectMember related in items
                select dataservice:selectFields(related, dataserviceProjectMemberEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials, "Employee", "get related");
            }
            return <ProjectMemberRelatedPartialList>{items: partials, hasMore};
        }
        return <ProjectMemberList>{items, hasMore};
    }

    # Gets the badge of an employee.
    #
    # + id - The id of the employee
    # + fields - The fields of the badge to return; the key fields are always returned
    # + return - The badge, or the error response
    resource function get employees/[int id]/badge(string[]? fields = ())
            returns Badge|BadgeRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceBadgeEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Badge");
        }
        map<anydata> itemKey = {"id": id};
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toRelatedQuery(
                dataserviceEmployeeEntity, "badge", itemKey, self.pageConfig);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Badge");
        }
        stream<Badge, persist:Error?> rows = self.persistClient->/badges(Badge,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Badge[]|persist:Error items = from Badge related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items, "Employee", "get related");
        }
        if items.length() == 0 {
            Employee|persist:Error item = self.persistClient->/employees/[id];
            if item is persist:Error {
                return dsHttp:toGetError(item, "Employee", [id], "get related");
            }
            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(dataserviceEmployeeEntity, "badge", [id])};
        }
        if fields is string[] {
            BadgeRelatedPartial|error partial =
                dataservice:selectFields(items[0], dataserviceBadgeEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial, "Employee", "get related");
            }
            return partial;
        }
        return items[0];
    }

    # Creates one or more employees.
    #
    # + payload - The employee, or the employees, to create
    # + return - The created employee or employees, or the error response
    resource function post employees(EmployeeInsert|EmployeeInsert[] payload)
            returns EmployeeCreated|EmployeeListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        EmployeeInsert[] values = payload is EmployeeInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Employee[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check self.persistClient->/employees.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceEmployeeEntity, keyValues);
                    stream<Employee, persist:Error?> stored = self.persistClient->/employees(Employee,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Employee[] rows = check from Employee item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceEmployeeEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Employee");
            }
        }
        if payload is EmployeeInsert[] {
            return <EmployeeListCreated>{body: {items, hasMore: false}};
        }
        Employee created = items[0];
        return <EmployeeCreated>{
            headers: {"Location": dsHttp:toLocation("/api/employees", [created.id])},
            body: created
        };
    }

    # Updates an employee by its key.
    #
    # + id - The id of the employee
    # + payload - The fields of the employee to update
    # + return - The updated employee, or no content when it cannot be read back, or the error response
    resource function patch employees/[int id](EmployeeUpdate payload)
            returns Employee|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Employee|persist:Error item;
        if payload.length() == 0 {
            item = self.persistClient->/employees/[id];
        } else {
            item = self.persistClient->/employees/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Employee", [id]);
        }
        return item;
    }

    # Deletes an employee by its key.
    #
    # + id - The id of the employee
    # + return - The deleted employee, or the error response
    resource function delete employees/[int id]()
            returns Employee|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Employee|persist:Error item = self.persistClient->/employees/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Employee", [id]);
        }
        return item;
    }

    # Gets a page of projects.
    #
    # + filter - The conditions that the projects match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of projects in the page
    # + offset - The number of projects to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of projects, or the error response
    resource function get projects(ProjectFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns ProjectList|ProjectPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceProjectEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Project");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Project");
        }
        stream<Project, persist:Error?> rows = self.persistClient->/projects(Project,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Project[]|persist:Error items = from Project item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items, "Project");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            Project[] sources = items;
            map<anydata>[] partials = from Project item in sources
                select dataservice:selectFields(item, dataserviceProjectEntity, fields);
            do {
                if dataservice:isIncluded(fields, "members") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:toIncludeQuery(
                                dataserviceProjectEntity, "members", [sources[i]], self.pageConfig);
                        stream<ProjectMember, persist:Error?> includedRows = self.persistClient->/projectmembers(ProjectMember,
                            whereClause = include.whereClause, orderByClause = include.orderByClause,
                            limitClause = include.limitClause
                        );
                        ProjectMember[] included = check from ProjectMember row in includedRows
                            select row;
                        boolean includedMore = dataservice:trimToPage(included, include);
                        dataservice:attachMany(
                                partials[i], dataserviceProjectEntity, "members", included, includedMore, fields);
                    }
                }
            } on fail error err {
                return dsHttp:toListError(err, "Project");
            }
            ProjectPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Project");
            }
            return <ProjectPartialList>{items: converted, hasMore};
        }
        return <ProjectList>{items, hasMore};
    }

    # Gets a project by its key.
    #
    # + code - The code of the project
    # + fields - The fields to return; the key fields are always returned
    # + return - The project, or the error response
    resource function get projects/[string code](string[]? fields = ())
            returns Project|ProjectPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Project");
        }
        Project|persist:Error item = self.persistClient->/projects/[code];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Project", [code]);
        }
        if fields is string[] {
            map<anydata> partial = dataservice:selectFields(item, dataserviceProjectEntity, fields);
            do {
                if dataservice:isIncluded(fields, "members") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceProjectEntity, "members", [item], self.pageConfig);
                    stream<ProjectMember, persist:Error?> includedRows = self.persistClient->/projectmembers(ProjectMember,
                        whereClause = include.whereClause, orderByClause = include.orderByClause,
                        limitClause = include.limitClause
                    );
                    ProjectMember[] included = check from ProjectMember row in includedRows
                        select row;
                    boolean includedMore = dataservice:trimToPage(included, include);
                    dataservice:attachMany(
                            partial, dataserviceProjectEntity, "members", included, includedMore, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Project", [code]);
            }
            ProjectPartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Project", [code]);
            }
            return converted;
        }
        return item;
    }

    # Gets the members of a project.
    #
    # + code - The code of the project
    # + filter - The conditions that the members match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of members in the page
    # + offset - The number of members to skip
    # + fields - The fields of the members to return; the key fields are always returned
    # + return - A page of the members, or the error response
    resource function get projects/[string code]/members(ProjectMemberFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns ProjectMemberList|ProjectMemberRelatedPartialList|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectMemberEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ProjectMember");
        }
        map<anydata> itemKey = {"code": code};
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toRelatedQuery(
                dataserviceProjectEntity, "members", itemKey, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ProjectMember");
        }
        stream<ProjectMember, persist:Error?> rows = self.persistClient->/projectmembers(ProjectMember,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ProjectMember[]|persist:Error items = from ProjectMember related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items, "Project", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if items.length() == 0 {
            Project|persist:Error item = self.persistClient->/projects/[code];
            if item is persist:Error {
                return dsHttp:toGetError(item, "Project", [code], "get related");
            }
            return <ProjectMemberList>{items, hasMore};
        }
        if fields is string[] {
            ProjectMemberRelatedPartial[]|error partials = (from ProjectMember related in items
                select dataservice:selectFields(related, dataserviceProjectMemberEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials, "Project", "get related");
            }
            return <ProjectMemberRelatedPartialList>{items: partials, hasMore};
        }
        return <ProjectMemberList>{items, hasMore};
    }

    # Creates one or more projects.
    #
    # + payload - The project, or the projects, to create
    # + return - The created project or projects, or the error response
    resource function post projects(ProjectInsert|ProjectInsert[] payload)
            returns ProjectCreated|ProjectListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ProjectInsert[] values = payload is ProjectInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Project[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    string[] keys = check self.persistClient->/projects.post(values);
                    dataservice:KeyValue[][] keyValues = from string key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceProjectEntity, keyValues);
                    stream<Project, persist:Error?> stored = self.persistClient->/projects(Project,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Project[] rows = check from Project item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceProjectEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Project");
            }
        }
        if payload is ProjectInsert[] {
            return <ProjectListCreated>{body: {items, hasMore: false}};
        }
        Project created = items[0];
        return <ProjectCreated>{
            headers: {"Location": dsHttp:toLocation("/api/projects", [created.code])},
            body: created
        };
    }

    # Updates a project by its key.
    #
    # + code - The code of the project
    # + payload - The fields of the project to update
    # + return - The updated project, or no content when it cannot be read back, or the error response
    resource function patch projects/[string code](ProjectUpdate payload)
            returns Project|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Project|persist:Error item;
        if payload.length() == 0 {
            item = self.persistClient->/projects/[code];
        } else {
            item = self.persistClient->/projects/[code].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Project", [code]);
        }
        return item;
    }

    # Deletes a project by its key.
    #
    # + code - The code of the project
    # + return - The deleted project, or the error response
    resource function delete projects/[string code]()
            returns Project|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Project|persist:Error item = self.persistClient->/projects/[code].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Project", [code]);
        }
        return item;
    }

    # Gets a page of project members.
    #
    # + filter - The conditions that the project members match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of project members in the page
    # + offset - The number of project members to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of project members, or the error response
    resource function get projectMembers(ProjectMemberFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns ProjectMemberList|ProjectMemberPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceProjectMemberEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "ProjectMember");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectMemberEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ProjectMember");
        }
        stream<ProjectMember, persist:Error?> rows = self.persistClient->/projectmembers(ProjectMember,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        ProjectMember[]|persist:Error items = from ProjectMember item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items, "ProjectMember");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            ProjectMember[] sources = items;
            map<anydata>[] partials = from ProjectMember item in sources
                select dataservice:selectFields(item, dataserviceProjectMemberEntity, fields);
            do {
                if dataservice:isIncluded(fields, "employee") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceProjectMemberEntity, "employee", sources, self.pageConfig);
                    stream<Employee, persist:Error?> includedRows = self.persistClient->/employees(Employee,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Employee[] included = check from Employee row in includedRows
                        select row;
                    dataservice:attachOne(
                            partials, sources, dataserviceProjectMemberEntity, "employee", included, fields);
                }
                if dataservice:isIncluded(fields, "project") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceProjectMemberEntity, "project", sources, self.pageConfig);
                    stream<Project, persist:Error?> includedRows = self.persistClient->/projects(Project,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Project[] included = check from Project row in includedRows
                        select row;
                    dataservice:attachOne(
                            partials, sources, dataserviceProjectMemberEntity, "project", included, fields);
                }
            } on fail error err {
                return dsHttp:toListError(err, "ProjectMember");
            }
            ProjectMemberPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "ProjectMember");
            }
            return <ProjectMemberPartialList>{items: converted, hasMore};
        }
        return <ProjectMemberList>{items, hasMore};
    }

    # Gets a project member by its key.
    #
    # + projectCode - The projectCode of the project member
    # + employeeId - The employeeId of the project member
    # + fields - The fields to return; the key fields are always returned
    # + return - The project member, or the error response
    resource function get projectMembers/[string projectCode]/[int employeeId](string[]? fields = ())
            returns ProjectMember|ProjectMemberPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectMemberEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "ProjectMember");
        }
        ProjectMember|persist:Error item = self.persistClient->/projectmembers/[projectCode]/[employeeId];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ProjectMember", [projectCode, employeeId]);
        }
        if fields is string[] {
            map<anydata> partial = dataservice:selectFields(item, dataserviceProjectMemberEntity, fields);
            do {
                if dataservice:isIncluded(fields, "employee") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceProjectMemberEntity, "employee", [item], self.pageConfig);
                    stream<Employee, persist:Error?> includedRows = self.persistClient->/employees(Employee,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Employee[] included = check from Employee row in includedRows
                        select row;
                    dataservice:attachOne(
                            [partial], [item], dataserviceProjectMemberEntity, "employee", included, fields);
                }
                if dataservice:isIncluded(fields, "project") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceProjectMemberEntity, "project", [item], self.pageConfig);
                    stream<Project, persist:Error?> includedRows = self.persistClient->/projects(Project,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Project[] included = check from Project row in includedRows
                        select row;
                    dataservice:attachOne(
                            [partial], [item], dataserviceProjectMemberEntity, "project", included, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "ProjectMember", [projectCode, employeeId]);
            }
            ProjectMemberPartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "ProjectMember", [projectCode, employeeId]);
            }
            return converted;
        }
        return item;
    }

    # Gets the employee of a project member.
    #
    # + projectCode - The projectCode of the project member
    # + employeeId - The employeeId of the project member
    # + fields - The fields of the employee to return; the key fields are always returned
    # + return - The employee, or the error response
    resource function get projectMembers/[string projectCode]/[int employeeId]/employee(string[]? fields = ())
            returns Employee|EmployeeRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceEmployeeEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        ProjectMember|persist:Error item = self.persistClient->/projectmembers/[projectCode]/[employeeId];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ProjectMember", [projectCode, employeeId], "get related");
        }
        Employee|persist:Error related = self.persistClient->/employees/[item.employeeId];
        if related is persist:NotFoundError {
            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(dataserviceProjectMemberEntity, "employee", [projectCode, employeeId])};
        }
        if related is persist:Error {
            return dsHttp:toListError(related, "ProjectMember", "get related");
        }
        if fields is string[] {
            EmployeeRelatedPartial|error partial =
                dataservice:selectFields(related, dataserviceEmployeeEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial, "ProjectMember", "get related");
            }
            return partial;
        }
        return related;
    }

    # Gets the project of a project member.
    #
    # + projectCode - The projectCode of the project member
    # + employeeId - The employeeId of the project member
    # + fields - The fields of the project to return; the key fields are always returned
    # + return - The project, or the error response
    resource function get projectMembers/[string projectCode]/[int employeeId]/project(string[]? fields = ())
            returns Project|ProjectRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceProjectEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Project");
        }
        ProjectMember|persist:Error item = self.persistClient->/projectmembers/[projectCode]/[employeeId];
        if item is persist:Error {
            return dsHttp:toGetError(item, "ProjectMember", [projectCode, employeeId], "get related");
        }
        Project|persist:Error related = self.persistClient->/projects/[item.projectCode];
        if related is persist:NotFoundError {
            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(dataserviceProjectMemberEntity, "project", [projectCode, employeeId])};
        }
        if related is persist:Error {
            return dsHttp:toListError(related, "ProjectMember", "get related");
        }
        if fields is string[] {
            ProjectRelatedPartial|error partial =
                dataservice:selectFields(related, dataserviceProjectEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial, "ProjectMember", "get related");
            }
            return partial;
        }
        return related;
    }

    # Creates one or more project members.
    #
    # + payload - The project member, or the project members, to create
    # + return - The created project member or project members, or the error response
    resource function post projectMembers(ProjectMemberInsert|ProjectMemberInsert[] payload)
            returns ProjectMemberCreated|ProjectMemberListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ProjectMemberInsert[] values = payload is ProjectMemberInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        ProjectMember[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    [string, int][] keys = check self.persistClient->/projectmembers.post(values);
                    dataservice:KeyValue[][] keyValues = keys;
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceProjectMemberEntity, keyValues);
                    stream<ProjectMember, persist:Error?> stored = self.persistClient->/projectmembers(ProjectMember,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    ProjectMember[] rows = check from ProjectMember item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceProjectMemberEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "ProjectMember");
            }
        }
        if payload is ProjectMemberInsert[] {
            return <ProjectMemberListCreated>{body: {items, hasMore: false}};
        }
        ProjectMember created = items[0];
        return <ProjectMemberCreated>{
            headers: {"Location": dsHttp:toLocation("/api/projectMembers", [created.projectCode, created.employeeId])},
            body: created
        };
    }

    # Updates a project member by its key.
    #
    # + projectCode - The projectCode of the project member
    # + employeeId - The employeeId of the project member
    # + payload - The fields of the project member to update
    # + return - The updated project member, or no content when it cannot be read back, or the error response
    resource function patch projectMembers/[string projectCode]/[int employeeId](ProjectMemberUpdate payload)
            returns ProjectMember|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ProjectMember|persist:Error item;
        if payload.length() == 0 {
            item = self.persistClient->/projectmembers/[projectCode]/[employeeId];
        } else {
            item = self.persistClient->/projectmembers/[projectCode]/[employeeId].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "ProjectMember", [projectCode, employeeId]);
        }
        return item;
    }

    # Deletes a project member by its key.
    #
    # + projectCode - The projectCode of the project member
    # + employeeId - The employeeId of the project member
    # + return - The deleted project member, or the error response
    resource function delete projectMembers/[string projectCode]/[int employeeId]()
            returns ProjectMember|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        ProjectMember|persist:Error item = self.persistClient->/projectmembers/[projectCode]/[employeeId].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "ProjectMember", [projectCode, employeeId]);
        }
        return item;
    }

    # Gets a page of badges.
    #
    # + filter - The conditions that the badges match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of badges in the page
    # + offset - The number of badges to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of badges, or the error response
    resource function get badges(BadgeFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns BadgeList|BadgePartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceBadgeEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Badge");
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceBadgeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Badge");
        }
        stream<Badge, persist:Error?> rows = self.persistClient->/badges(Badge,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Badge[]|persist:Error items = from Badge item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items, "Badge");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            Badge[] sources = items;
            map<anydata>[] partials = from Badge item in sources
                select dataservice:selectFields(item, dataserviceBadgeEntity, fields);
            do {
                if dataservice:isIncluded(fields, "employee") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceBadgeEntity, "employee", sources, self.pageConfig);
                    stream<Employee, persist:Error?> includedRows = self.persistClient->/employees(Employee,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Employee[] included = check from Employee row in includedRows
                        select row;
                    dataservice:attachOne(
                            partials, sources, dataserviceBadgeEntity, "employee", included, fields);
                }
            } on fail error err {
                return dsHttp:toListError(err, "Badge");
            }
            BadgePartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Badge");
            }
            return <BadgePartialList>{items: converted, hasMore};
        }
        return <BadgeList>{items, hasMore};
    }

    # Gets a badge by its key.
    #
    # + employeeId - The employeeId of the badge
    # + fields - The fields to return; the key fields are always returned
    # + return - The badge, or the error response
    resource function get badges/[int employeeId](string[]? fields = ())
            returns Badge|BadgePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceBadgeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Badge");
        }
        Badge|persist:Error item = self.persistClient->/badges/[employeeId];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Badge", [employeeId]);
        }
        if fields is string[] {
            map<anydata> partial = dataservice:selectFields(item, dataserviceBadgeEntity, fields);
            do {
                if dataservice:isIncluded(fields, "employee") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceBadgeEntity, "employee", [item], self.pageConfig);
                    stream<Employee, persist:Error?> includedRows = self.persistClient->/employees(Employee,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Employee[] included = check from Employee row in includedRows
                        select row;
                    dataservice:attachOne(
                            [partial], [item], dataserviceBadgeEntity, "employee", included, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Badge", [employeeId]);
            }
            BadgePartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Badge", [employeeId]);
            }
            return converted;
        }
        return item;
    }

    # Gets the employee of a badge.
    #
    # + employeeId - The employeeId of the badge
    # + fields - The fields of the employee to return; the key fields are always returned
    # + return - The employee, or the error response
    resource function get badges/[int employeeId]/employee(string[]? fields = ())
            returns Employee|EmployeeRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceEmployeeEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        Badge|persist:Error item = self.persistClient->/badges/[employeeId];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Badge", [employeeId], "get related");
        }
        Employee|persist:Error related = self.persistClient->/employees/[item.employeeId];
        if related is persist:NotFoundError {
            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(dataserviceBadgeEntity, "employee", [employeeId])};
        }
        if related is persist:Error {
            return dsHttp:toListError(related, "Badge", "get related");
        }
        if fields is string[] {
            EmployeeRelatedPartial|error partial =
                dataservice:selectFields(related, dataserviceEmployeeEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial, "Badge", "get related");
            }
            return partial;
        }
        return related;
    }

    # Creates one or more badges.
    #
    # + payload - The badge, or the badges, to create
    # + return - The created badge or badges, or the error response
    resource function post badges(BadgeInsert|BadgeInsert[] payload)
            returns BadgeCreated|BadgeListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        BadgeInsert[] values = payload is BadgeInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Badge[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check self.persistClient->/badges.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceBadgeEntity, keyValues);
                    stream<Badge, persist:Error?> stored = self.persistClient->/badges(Badge,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Badge[] rows = check from Badge item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceBadgeEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Badge");
            }
        }
        if payload is BadgeInsert[] {
            return <BadgeListCreated>{body: {items, hasMore: false}};
        }
        Badge created = items[0];
        return <BadgeCreated>{
            headers: {"Location": dsHttp:toLocation("/api/badges", [created.employeeId])},
            body: created
        };
    }

    # Updates a badge by its key.
    #
    # + employeeId - The employeeId of the badge
    # + payload - The fields of the badge to update
    # + return - The updated badge, or no content when it cannot be read back, or the error response
    resource function patch badges/[int employeeId](BadgeUpdate payload)
            returns Badge|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Badge|persist:Error item;
        if payload.length() == 0 {
            item = self.persistClient->/badges/[employeeId];
        } else {
            item = self.persistClient->/badges/[employeeId].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Badge", [employeeId]);
        }
        return item;
    }

    # Deletes a badge by its key.
    #
    # + employeeId - The employeeId of the badge
    # + return - The deleted badge, or the error response
    resource function delete badges/[int employeeId]()
            returns Badge|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Badge|persist:Error item = self.persistClient->/badges/[employeeId].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Badge", [employeeId]);
        }
        return item;
    }
}

# Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
isolated service class DataserviceErrorInterceptor {
    *http:ResponseErrorInterceptor;

    isolated remote function interceptResponseError(error err, http:RequestContext ctx, http:Response response)
            returns http:NextService? {
        dsHttp:setErrorBody(response, err);
        // The response is changed in place; an error return would add a 500 response to the OpenAPI definition.
        http:NextService|error? next = ctx.next();
        return next is http:NextService ? next : ();
    }
}
