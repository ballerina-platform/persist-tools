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
    # An employee of the company.
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
    # An employee of the company.
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
    # An employee of the company.
    #
    # + id - The id of the employee
    # + fields - The fields of the department to return; the key fields are always returned
    # + return - The department, or the error response
    resource function get employees/[int id]/department(string[]? fields = ())
            returns Department|DepartmentRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
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
    # An employee of the company.
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
    # An employee of the company.
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
    # An employee of the company.
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

    # Gets a page of departments.
    #
    # + filter - The conditions that the departments match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of departments in the page
    # + offset - The number of departments to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of departments, or the error response
    resource function get departments(DepartmentFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns DepartmentList|DepartmentPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:toQuery(dataserviceDepartmentEntity, self.pageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Department");
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
    resource function get departments/[int id](string[]? fields = ())
            returns Department|DepartmentPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
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
    resource function get departments/[int id]/employees(EmployeeFilter? filter = (),
            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())
            returns EmployeeList|EmployeeRelatedPartialList|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
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
