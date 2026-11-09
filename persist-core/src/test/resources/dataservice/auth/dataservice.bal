// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

# The scopes of each operation, by collection and then by HTTP method.
final readonly & map<map<string[]>> dataserviceScopes = {
    "employees": {"GET": ["hr:read"], "POST": ["employees:create"], "PATCH": ["employees:update"], "DELETE": ["hr:admin"]},
    "departments": {"GET": ["hr:read", "org:read"]}
};

@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}
service http:InterceptableService /api on dataserviceListener {

    private final Client persistClient;
    private final dataservice:PageConfig pageConfig;
    private final dsHttp:Authenticator authenticator;

    # Creates the persist client, and checks the page sizes and the auth providers.
    #
    # + return - An error when the client cannot be created or a setting is invalid
    function init() returns error? {
        self.persistClient = check new ();
        self.pageConfig = check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
        self.authenticator = check new (dataserviceConfig.serviceConfig.auth, dataserviceScopes,
            dataserviceConfig.serviceConfig.cors
        );
    }

    # Checks auth before each request is dispatched, and answers the errors that `ballerina/http`
    # raises with the `DataserviceError` body.
    #
    # + return - The interceptors
    public function createInterceptors()
            returns [DataserviceAuthInterceptor, DataserviceErrorInterceptor] => [new (self.authenticator), new];

    # Gets a page of employees.
    #
    # + filter - The conditions that the employees match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of employees, or the error response
    resource function get employees(http:RequestContext ctx, EmployeeFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns EmployeeList|EmployeePartialList|dsHttp:InvalidInput|dsHttp:Forbidden|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceEmployeeEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Employee");
        }
        if dataservice:isIncluded(fields, "department") {
            dsHttp:Forbidden? forbidden =
                dsHttp:checkRelationScopes(ctx, "Employee", "department", "Department", ["hr:read", "org:read"]);
            if forbidden is dsHttp:Forbidden {
                return forbidden;
            }
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
                if dataservice:isIncluded(fields, "department") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceEmployeeEntity, "department", sources, self.pageConfig);
                    stream<Department, persist:Error?> includedRows = self.persistClient->/departments(Department,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Department[] included = check from Department row in includedRows
                        select row;
                    dataservice:attachOne(
                            partials, sources, dataserviceEmployeeEntity, "department", included, fields);
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
    resource function get employees/[int id](http:RequestContext ctx, string[]? fields = ())
            returns Employee|EmployeePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Forbidden|dsHttp:InternalError {
        if dataservice:isIncluded(fields, "department") {
            dsHttp:Forbidden? forbidden =
                dsHttp:checkRelationScopes(ctx, "Employee", "department", "Department", ["hr:read", "org:read"]);
            if forbidden is dsHttp:Forbidden {
                return forbidden;
            }
        }
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
                if dataservice:isIncluded(fields, "department") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceEmployeeEntity, "department", [item], self.pageConfig);
                    stream<Department, persist:Error?> includedRows = self.persistClient->/departments(Department,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Department[] included = check from Department row in includedRows
                        select row;
                    dataservice:attachOne(
                            [partial], [item], dataserviceEmployeeEntity, "department", included, fields);
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

    # Gets the department of an employee.
    #
    # + id - The id of the employee
    # + fields - The fields of the department to return; the key fields are always returned
    # + return - The department, or the error response
    resource function get employees/[int id]/department(http:RequestContext ctx, string[]? fields = ())
            returns Department|DepartmentRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Forbidden|dsHttp:InternalError {
        dsHttp:Forbidden? forbidden =
            dsHttp:checkRelationScopes(ctx, "Employee", "department", "Department", ["hr:read", "org:read"]);
        if forbidden is dsHttp:Forbidden {
            return forbidden;
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceDepartmentEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        Employee|persist:Error item = self.persistClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id], "get related");
        }
        Department|persist:Error related = self.persistClient->/departments/[item.departmentId];
        if related is persist:NotFoundError {
            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(dataserviceEmployeeEntity, "department", [id])};
        }
        if related is persist:Error {
            return dsHttp:toListError(related, "Employee", "get related");
        }
        if fields is string[] {
            DepartmentRelatedPartial|error partial =
                dataservice:selectFields(related, dataserviceDepartmentEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial, "Employee", "get related");
            }
            return partial;
        }
        return related;
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
    # + return - The updated employee, or no content when the caller may not read it or it cannot be read back, or the error response
    resource function patch employees/[int id](http:RequestContext ctx, EmployeeUpdate payload)
            returns Employee|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        boolean readable = dsHttp:hasScope(ctx, ["hr:read"]);
        Employee|persist:Error item;
        if payload.length() == 0 {
            if !readable {
                return http:NO_CONTENT;
            }
            item = self.persistClient->/employees/[id];
        } else {
            item = self.persistClient->/employees/[id].put(payload);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Employee", [id]);
        }
        return readable ? item : http:NO_CONTENT;
    }

    # Deletes an employee by its key.
    #
    # + id - The id of the employee
    # + return - The deleted employee, or no content when the caller may not read it, or the error response
    resource function delete employees/[int id](http:RequestContext ctx)
            returns Employee|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        boolean readable = dsHttp:hasScope(ctx, ["hr:read"]);
        Employee|persist:Error item = self.persistClient->/employees/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Employee", [id]);
        }
        return readable ? item : http:NO_CONTENT;
    }

    # Gets a page of departments.
    #
    # + filter - The conditions that the departments match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of departments in the page
    # + offset - The number of departments to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of departments, or the error response
    resource function get departments(http:RequestContext ctx, DepartmentFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns DepartmentList|DepartmentPartialList|dsHttp:InvalidInput|dsHttp:Forbidden|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceDepartmentEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Department");
        }
        if dataservice:isIncluded(fields, "employees") {
            dsHttp:Forbidden? forbidden =
                dsHttp:checkRelationScopes(ctx, "Department", "employees", "Employee", ["hr:read"]);
            if forbidden is dsHttp:Forbidden {
                return forbidden;
            }
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceDepartmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        stream<Department, persist:Error?> rows = self.persistClient->/departments(Department,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Department[]|persist:Error items = from Department item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items, "Department");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if fields is string[] {
            Department[] sources = items;
            map<anydata>[] partials = from Department item in sources
                select dataservice:selectFields(item, dataserviceDepartmentEntity, fields);
            do {
                if dataservice:isIncluded(fields, "employees") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:toIncludeQuery(
                                dataserviceDepartmentEntity, "employees", [sources[i]], self.pageConfig);
                        stream<Employee, persist:Error?> includedRows = self.persistClient->/employees(Employee,
                            whereClause = include.whereClause, orderByClause = include.orderByClause,
                            limitClause = include.limitClause
                        );
                        Employee[] included = check from Employee row in includedRows
                            select row;
                        boolean includedMore = dataservice:trimToPage(included, include);
                        dataservice:attachMany(
                                partials[i], dataserviceDepartmentEntity, "employees", included, includedMore, fields);
                    }
                }
            } on fail error err {
                return dsHttp:toListError(err, "Department");
            }
            DepartmentPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted, "Department");
            }
            return <DepartmentPartialList>{items: converted, hasMore};
        }
        return <DepartmentList>{items, hasMore};
    }

    # Gets a department by its key.
    #
    # + id - The id of the department
    # + fields - The fields to return; the key fields are always returned
    # + return - The department, or the error response
    resource function get departments/[int id](http:RequestContext ctx, string[]? fields = ())
            returns Department|DepartmentPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Forbidden|dsHttp:InternalError {
        if dataservice:isIncluded(fields, "employees") {
            dsHttp:Forbidden? forbidden =
                dsHttp:checkRelationScopes(ctx, "Department", "employees", "Employee", ["hr:read"]);
            if forbidden is dsHttp:Forbidden {
                return forbidden;
            }
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceDepartmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        Department|persist:Error item = self.persistClient->/departments/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Department", [id]);
        }
        if fields is string[] {
            map<anydata> partial = dataservice:selectFields(item, dataserviceDepartmentEntity, fields);
            do {
                if dataservice:isIncluded(fields, "employees") {
                    dataservice:Query include = dataservice:toIncludeQuery(
                            dataserviceDepartmentEntity, "employees", [item], self.pageConfig);
                    stream<Employee, persist:Error?> includedRows = self.persistClient->/employees(Employee,
                        whereClause = include.whereClause, orderByClause = include.orderByClause,
                        limitClause = include.limitClause
                    );
                    Employee[] included = check from Employee row in includedRows
                        select row;
                    boolean includedMore = dataservice:trimToPage(included, include);
                    dataservice:attachMany(
                            partial, dataserviceDepartmentEntity, "employees", included, includedMore, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Department", [id]);
            }
            DepartmentPartial|error converted = partial.cloneWithType();
            if converted is error {
                return dsHttp:toGetError(converted, "Department", [id]);
            }
            return converted;
        }
        return item;
    }

    # Gets the employees of a department.
    #
    # + id - The id of the department
    # + filter - The conditions that the employees match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + fields - The fields of the employees to return; the key fields are always returned
    # + return - A page of the employees, or the error response
    resource function get departments/[int id]/employees(http:RequestContext ctx, EmployeeFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns EmployeeList|EmployeeRelatedPartialList|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Forbidden|dsHttp:InternalError {
        dsHttp:Forbidden? forbidden =
            dsHttp:checkRelationScopes(ctx, "Department", "employees", "Employee", ["hr:read"]);
        if forbidden is dsHttp:Forbidden {
            return forbidden;
        }
        dataservice:DataserviceError? invalidFields =
            dataservice:checkFields(dataserviceEmployeeEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        map<anydata> itemKey = {"id": id};
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toRelatedQuery(
                dataserviceDepartmentEntity, "employees", itemKey, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Employee");
        }
        stream<Employee, persist:Error?> rows = self.persistClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Employee[]|persist:Error items = from Employee related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items, "Department", "get related");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        if items.length() == 0 {
            Department|persist:Error item = self.persistClient->/departments/[id];
            if item is persist:Error {
                return dsHttp:toGetError(item, "Department", [id], "get related");
            }
            return <EmployeeList>{items, hasMore};
        }
        if fields is string[] {
            EmployeeRelatedPartial[]|error partials = (from Employee related in items
                select dataservice:selectFields(related, dataserviceEmployeeEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials, "Department", "get related");
            }
            return <EmployeeRelatedPartialList>{items: partials, hasMore};
        }
        return <EmployeeList>{items, hasMore};
    }

    # Creates one or more departments.
    #
    # + payload - The department, or the departments, to create
    # + return - The created department or departments, or the error response
    resource function post departments(DepartmentInsert|DepartmentInsert[] payload)
            returns DepartmentCreated|DepartmentListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        DepartmentInsert[] values = payload is DepartmentInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), self.pageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Department[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check self.persistClient->/departments.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:toKeysQuery(dataserviceDepartmentEntity, keyValues);
                    stream<Department, persist:Error?> stored = self.persistClient->/departments(Department,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Department[] rows = check from Department item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceDepartmentEntity, keyValues);
                    items = rows;
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Department");
            }
        }
        if payload is DepartmentInsert[] {
            return <DepartmentListCreated>{body: {items, hasMore: false}};
        }
        Department created = items[0];
        return <DepartmentCreated>{
            headers: {"Location": dsHttp:toLocation("/api/departments", [created.id])},
            body: created
        };
    }
}

# Checks the credential and the scopes of each request before it is dispatched.
isolated service class DataserviceAuthInterceptor {
    *http:RequestInterceptor;

    private final dsHttp:Authenticator authenticator;

    # Creates the interceptor.
    #
    # + authenticator - The authenticator of the service
    isolated function init(dsHttp:Authenticator authenticator) {
        self.authenticator = authenticator;
    }

    isolated resource function 'default [string... path](http:RequestContext ctx, http:Request request)
            returns http:NextService|dsHttp:Unauthorized|dsHttp:Forbidden|error? =>
        self.authenticator.authorize(ctx, request, path);
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
