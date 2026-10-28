// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer for model.

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
service /api on dataserviceListener {

    # Gets a page of employees.
    #
    # An employee of the company.
    #
    # + return - A page of employees, or the error response
    resource function get employees() returns EmployeeList|dsHttp:InvalidInput|dsHttp:InternalError {
        Employee[]|persist:Error items = from Employee item in dataserviceClient->/employees(Employee)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets an employee by its key.
    #
    # An employee of the company.
    #
    # + id - The id of the employee
    # + return - The employee, or the error response
    resource function get employees/[int id]() returns Employee|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id]);
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
                    foreach int id in keys {
                        Employee item = check dataserviceClient->/employees/[id];
                        items.push(item);
                    }
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
    # + return - A page of departments, or the error response
    resource function get departments() returns DepartmentList|dsHttp:InvalidInput|dsHttp:InternalError {
        Department[]|persist:Error items = from Department item in dataserviceClient->/departments(Department)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items, hasMore: false};
    }

    # Gets a department by its key.
    #
    # + id - The id of the department
    # + return - The department, or the error response
    resource function get departments/[int id]() returns Department|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        Department|persist:Error item = dataserviceClient->/departments/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Department", [id]);
        }
        return item;
    }
}
