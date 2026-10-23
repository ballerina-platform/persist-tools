// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);

service /hr/v1 on dataserviceListener {

    # Gets the employees.
    #
    # + return - The employees, or an internal error
    resource function get employees() returns EmployeeList|dsHttp:InternalError {
        Employee[]|persist:Error items = from Employee item in dataserviceClient->/employees(Employee)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets an employee by its key.
    #
    # + return - The employee, or the error response
    resource function get employees/[int id]() returns Employee|dsHttp:NotFound|dsHttp:InternalError {
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Employee", [id]);
        }
        return item;
    }

    # Creates one employee, or several from an array.
    #
    # + return - The created employee or employees, or the error response
    resource function post employees(EmployeeInsert|EmployeeInsert[] payload)
            returns EmployeeCreated|EmployeeListCreated|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        EmployeeInsert[] values = payload is EmployeeInsert[] ? payload : [payload];
        Employee[] items = [];
        if values.length() > 0 {
            int[]|persist:Error keys = dataserviceClient->/employees.post(values);
            if keys is persist:Error {
                return dsHttp:toCreateError(keys, "Employee");
            }
            foreach int id in keys {
                Employee|persist:Error item = dataserviceClient->/employees/[id];
                if item is persist:Error {
                    return dsHttp:toCreateError(item, "Employee", [id]);
                }
                items.push(item);
            }
        }
        if payload is EmployeeInsert[] {
            return <EmployeeListCreated>{body: {items}};
        }
        Employee created = items[0];
        return <EmployeeCreated>{
            headers: {"Location": dsHttp:buildLocation("/hr/v1/employees", [created.id])},
            body: created
        };
    }

    # Updates the given fields of an employee.
    #
    # + return - The updated employee, or the error response
    resource function patch employees/[int id](EmployeeUpdate payload)
            returns Employee|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
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

    # Deletes an employee and returns it.
    #
    # + return - The deleted employee, or the error response
    resource function delete employees/[int id]() returns Employee|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Employee|persist:Error item = dataserviceClient->/employees/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Employee", [id]);
        }
        return item;
    }

    # Gets the departments.
    #
    # + return - The departments, or an internal error
    resource function get departments() returns DepartmentList|dsHttp:InternalError {
        Department[]|persist:Error items = from Department item in dataserviceClient->/departments(Department)
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        return {items};
    }

    # Gets a department by its key.
    #
    # + return - The department, or the error response
    resource function get departments/[int id]() returns Department|dsHttp:NotFound|dsHttp:InternalError {
        Department|persist:Error item = dataserviceClient->/departments/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Department", [id]);
        }
        return item;
    }
}
