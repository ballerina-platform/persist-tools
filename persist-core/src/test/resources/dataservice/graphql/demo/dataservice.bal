// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/persist;

# The persist client of the data service.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener graphql:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@graphql:ServiceConfig {
    graphiql: {enabled: true},
    cors: dataserviceConfig.serviceConfig.cors
}
service graphql:Service /graphql on dataserviceListener {

    # Gets a page of departments.
    #
    # + return - A page of departments, or the error
    resource function get departments() returns DepartmentList?|error {
        stream<Department, persist:Error?> rows = dataserviceClient->/departments(Department);
        Department[]|persist:Error items = from Department item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Department");
        }
        return {items: from Department item in items
                select new DepartmentObject(item), hasMore: false};
    }

    # Gets a page of employees.
    #
    # + return - A page of employees, or the error
    resource function get employees() returns EmployeeList?|error {
        stream<Employee, persist:Error?> rows = dataserviceClient->/employees(Employee);
        Employee[]|persist:Error items = from Employee item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Employee");
        }
        return {items: from Employee item in items
                select new EmployeeObject(item), hasMore: false};
    }

    # Gets a page of assignments.
    #
    # + return - A page of assignments, or the error
    resource function get assignments() returns AssignmentList?|error {
        stream<Assignment, persist:Error?> rows = dataserviceClient->/assignments(Assignment);
        Assignment[]|persist:Error items = from Assignment item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Assignment");
        }
        return {items: from Assignment item in items
                select new AssignmentObject(item), hasMore: false};
    }
}
