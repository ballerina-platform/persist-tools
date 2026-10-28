// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/openapi;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

@openapi:ServiceInfo {embed: true}
service / on dataserviceListener {

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
    resource function get employees(EmployeeFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns EmployeeList|EmployeePartialList|dsHttp:InvalidInput|dsHttp:InternalError {
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
            EmployeePartial[]|error partials = (from Employee item in items
                select dataservice:narrow(item, dataserviceEmployeeEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials);
            }
            return <EmployeePartialList>{items: partials, hasMore};
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
    resource function get employees/[int id](string[]? fields = ()) returns Employee|EmployeePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceEmployeeEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Employee");
        }
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id]);
        }
        if fields is string[] {
            EmployeePartial|error partial = dataservice:narrow(item, dataserviceEmployeeEntity, fields).cloneWithType(EmployeePartial);
            if partial is error {
                return dsHttp:toGetError(partial, "Employee", [id]);
            }
            return partial;
        }
        return item;
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
            headers: {"Location": dsHttp:buildLocation("/employees", [created.id])},
            body: created
        };
    }

    # Updates an employee by its key.
    #
    # An employee of the company.
    #
    # + id - The id of the employee
    # + payload - The fields of the employee to update
    # + return - The updated employee, or the error response
    resource function patch employees/[int id](EmployeeUpdate payload)
            returns Employee|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Employee|persist:Error item;
        if payload.length() == 0 {
            item = dataserviceClient->/employees/[id];
        } else {
            item = dataserviceClient->/employees/[id].put(payload);
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
    resource function delete employees/[int id]() returns Employee|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Employee|persist:Error item = dataserviceClient->/employees/[id].delete();
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
    resource function get departments(DepartmentFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns DepartmentList|DepartmentPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceDepartmentEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Department");
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
            DepartmentPartial[]|error partials = (from Department item in items
                select dataservice:narrow(item, dataserviceDepartmentEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials);
            }
            return <DepartmentPartialList>{items: partials, hasMore};
        }
        return <DepartmentList>{items, hasMore};
    }

    # Gets a department by its key.
    #
    # + id - The id of the department
    # + fields - The fields to return; the key fields are always returned
    # + return - The department, or the error response
    resource function get departments/[int id](string[]? fields = ()) returns Department|DepartmentPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceDepartmentEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Department");
        }
        Department|persist:Error item = dataserviceClient->/departments/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Department", [id]);
        }
        if fields is string[] {
            DepartmentPartial|error partial = dataservice:narrow(item, dataserviceDepartmentEntity, fields).cloneWithType(DepartmentPartial);
            if partial is error {
                return dsHttp:toGetError(partial, "Department", [id]);
            }
            return partial;
        }
        return item;
    }
}
