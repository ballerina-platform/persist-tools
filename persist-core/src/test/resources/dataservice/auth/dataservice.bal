// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@http:ServiceConfig {auth: dataserviceAuthConfig}
service http:InterceptableService /api on dataserviceListener {

    # Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
    #
    # + return - The error interceptor
    public function createInterceptors() returns dsHttp:ErrorInterceptor => new;

    # Gets a page of employees.
    #
    # + filter - The conditions that the employees match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of employees, or the error response
    @http:ResourceConfig {auth: {scopes: ["hr:read"]}}
    resource function get employees(EmployeeFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns EmployeeList|EmployeePartialList|dsHttp:InvalidInput|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceEmployeeEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Employee");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceEmployeeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        stream<Employee, persist:Error?> rows = dataserviceClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Employee[]|persist:Error items = from Employee item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            Employee[] sources = items;
            map<anydata>[] partials = from Employee item in sources
                select dataservice:narrow(item, dataserviceEmployeeEntity, fields);
            do {
                if dataservice:includes(fields, "department") {
                    dataservice:Query include = dataservice:includeQuery(dataserviceEmployeeEntity, "department", sources, dataservicePageConfig);
                    stream<Department, persist:Error?> includedRows = dataserviceClient->/departments(Department,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Department[] included = check from Department row in includedRows
                        select row;
                    dataservice:attachOne(partials, sources, dataserviceEmployeeEntity, "department", included, fields);
                }
            } on fail error err {
                return dsHttp:toListError(err);
            }
            EmployeePartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
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
    @http:ResourceConfig {auth: {scopes: ["hr:read"]}}
    resource function get employees/[int id](string[]? fields = ()) returns Employee|EmployeePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceEmployeeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id]);
        }
        if fields is string[] {
            Employee[] sources = [item];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceEmployeeEntity, fields)];
            do {
                if dataservice:includes(fields, "department") {
                    dataservice:Query include = dataservice:includeQuery(dataserviceEmployeeEntity, "department", sources, dataservicePageConfig);
                    stream<Department, persist:Error?> includedRows = dataserviceClient->/departments(Department,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Department[] included = check from Department row in includedRows
                        select row;
                    dataservice:attachOne(partials, sources, dataserviceEmployeeEntity, "department", included, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Employee", [id]);
            }
            EmployeePartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Employee", [id]);
            }
            return partial;
        }
        return item;
    }

    # Gets the department of an employee.
    #
    # + id - The id of the employee
    # + fields - The fields of the department to return; the key fields are always returned
    # + return - The department, or the error response
    @http:ResourceConfig {auth: {scopes: ["hr:read"]}}
    resource function get employees/[int id]/department(string[]? fields = ()) returns Department|DepartmentRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceDepartmentEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id]);
        }
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:relatedQuery(dataserviceEmployeeEntity, "department", item, dataservicePageConfig);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Department");
        }
        stream<Department, persist:Error?> rows = dataserviceClient->/departments(Department,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Department[]|persist:Error items = from Department related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        if items.length() == 0 {
            return <dsHttp:NotFound>{body: dataservice:noRelated(dataserviceEmployeeEntity, "department", [id])};
        }
        if fields is string[] {
            DepartmentRelatedPartial|error partial = dataservice:narrow(items[0], dataserviceDepartmentEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial);
            }
            return partial;
        }
        return items[0];
    }

    # Creates one or more employees.
    #
    # + payload - The employee, or the employees, to create
    # + return - The created employee or employees, or the error response
    @http:ResourceConfig {auth: {scopes: ["employees:create"]}}
    resource function post employees(EmployeeInsert|EmployeeInsert[] payload)
            returns EmployeeCreated|EmployeeListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        EmployeeInsert[] values = payload is EmployeeInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Employee[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/employees.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:keysQuery(dataserviceEmployeeEntity, keyValues);
                    stream<Employee, persist:Error?> stored = dataserviceClient->/employees(Employee,
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
            headers: {"Location": dsHttp:buildLocation("/api/employees", [created.id])},
            body: created
        };
    }

    # Updates an employee by its key.
    #
    # + id - The id of the employee
    # + payload - The fields of the employee to update
    # + return - The updated employee, or no content when the caller may not read it, or the error response
    @http:ResourceConfig {auth: {scopes: ["employees:update"]}}
    resource function patch employees/[int id](http:Request request, EmployeeUpdate payload)
            returns Employee|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        boolean readable = dsHttp:holdsScope(request, ["hr:read"]);
        Employee|persist:Error item;
        if payload.length() == 0 {
            if !readable {
                return http:NO_CONTENT;
            }
            item = dataserviceClient->/employees/[id];
        } else {
            item = dataserviceClient->/employees/[id].put(payload);
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
    @http:ResourceConfig {auth: {scopes: ["hr:admin"]}}
    resource function delete employees/[int id](http:Request request) returns Employee|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        boolean readable = dsHttp:holdsScope(request, ["hr:read"]);
        Employee|persist:Error item = dataserviceClient->/employees/[id].delete();
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
    @http:ResourceConfig {auth: {scopes: ["hr:read", "org:read"]}}
    resource function get departments(http:Request request, DepartmentFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns DepartmentList|DepartmentPartialList|dsHttp:InvalidInput|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceDepartmentEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Department");
        }
        if dataservice:includes(fields, "employees") {
            dsHttp:Forbidden? forbidden = dsHttp:authorizeRelation(request, "Department", "employees", "Employee", ["hr:read"]);
            if forbidden is dsHttp:Forbidden {
                return forbidden;
            }
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceDepartmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        stream<Department, persist:Error?> rows = dataserviceClient->/departments(Department,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Department[]|persist:Error items = from Department item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            Department[] sources = items;
            map<anydata>[] partials = from Department item in sources
                select dataservice:narrow(item, dataserviceDepartmentEntity, fields);
            do {
                if dataservice:includes(fields, "employees") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:includeQuery(dataserviceDepartmentEntity, "employees", [sources[i]], dataservicePageConfig);
                        stream<Employee, persist:Error?> includedRows = dataserviceClient->/employees(Employee,
                            whereClause = include.whereClause, orderByClause = include.orderByClause, limitClause = include.limitClause
                        );
                        Employee[] included = check from Employee row in includedRows
                            select row;
                        boolean includedMore = dataservice:page(included, include);
                        dataservice:attachMany(partials[i], dataserviceDepartmentEntity, "employees", included, includedMore, fields);
                    }
                }
            } on fail error err {
                return dsHttp:toListError(err);
            }
            DepartmentPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
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
    @http:ResourceConfig {auth: {scopes: ["hr:read", "org:read"]}}
    resource function get departments/[int id](http:Request request, string[]? fields = ()) returns Department|DepartmentPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        if dataservice:includes(fields, "employees") {
            dsHttp:Forbidden? forbidden = dsHttp:authorizeRelation(request, "Department", "employees", "Employee", ["hr:read"]);
            if forbidden is dsHttp:Forbidden {
                return forbidden;
            }
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceDepartmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        Department|persist:Error item = dataserviceClient->/departments/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Department", [id]);
        }
        if fields is string[] {
            Department[] sources = [item];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceDepartmentEntity, fields)];
            do {
                if dataservice:includes(fields, "employees") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:includeQuery(dataserviceDepartmentEntity, "employees", [sources[i]], dataservicePageConfig);
                        stream<Employee, persist:Error?> includedRows = dataserviceClient->/employees(Employee,
                            whereClause = include.whereClause, orderByClause = include.orderByClause, limitClause = include.limitClause
                        );
                        Employee[] included = check from Employee row in includedRows
                            select row;
                        boolean includedMore = dataservice:page(included, include);
                        dataservice:attachMany(partials[i], dataserviceDepartmentEntity, "employees", included, includedMore, fields);
                    }
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Department", [id]);
            }
            DepartmentPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Department", [id]);
            }
            return partial;
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
    @http:ResourceConfig {auth: {scopes: ["hr:read", "org:read"]}}
    resource function get departments/[int id]/employees(http:Request request, EmployeeFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns EmployeeList|EmployeeRelatedPartialList|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Unauthorized|dsHttp:Forbidden|dsHttp:InternalError {
        dsHttp:Forbidden? forbidden = dsHttp:authorizeRelation(request, "Department", "employees", "Employee", ["hr:read"]);
        if forbidden is dsHttp:Forbidden {
            return forbidden;
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceEmployeeEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:relatedQuery(dataserviceDepartmentEntity, "employees", {"id": id}, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Employee");
        }
        stream<Employee, persist:Error?> rows = dataserviceClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Employee[]|persist:Error items = from Employee related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if items.length() == 0 {
            Department|persist:Error item = dataserviceClient->/departments/[id];
            if item is persist:Error {
                return dsHttp:toGetError(item, "Department", [id]);
            }
            return <EmployeeList>{items, hasMore};
        }
        if fields is string[] {
            EmployeeRelatedPartial[]|error partials = (from Employee related in items
                select dataservice:narrow(related, dataserviceEmployeeEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials);
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
            returns DepartmentCreated|DepartmentListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:Unauthorized|dsHttp:InternalError {
        DepartmentInsert[] values = payload is DepartmentInsert[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(values.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        Department[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/departments.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:keysQuery(dataserviceDepartmentEntity, keyValues);
                    stream<Department, persist:Error?> stored = dataserviceClient->/departments(Department,
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
            headers: {"Location": dsHttp:buildLocation("/api/departments", [created.id])},
            body: created
        };
    }
}
