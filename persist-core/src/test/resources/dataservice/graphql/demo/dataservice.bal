// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
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
    # + filter - The conditions that the departments match
    # + sort - The order of the departments; each entry names one field
    # + size - The number of departments in the page
    # + offset - The number of departments to skip
    # + return - A page of departments, or the error
    resource function get departments(DepartmentFilter? filter = (), DepartmentSort[]? sort = (),
            int? size = (), int? offset = ()) returns DepartmentList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceDepartmentEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Department, persist:Error?> rows = dataserviceClient->/departments(Department,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Department[]|persist:Error items = from Department item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Department");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Department item in items
                select new DepartmentObject(item), hasMore};
    }

    # Gets a department by its key.
    #
    # + id - The id of the department
    # + return - The department, or the error
    resource function get department(int id) returns DepartmentObject?|error {
        Department|persist:Error item = dataserviceClient->/departments/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Department", [id]);
        }
        return new (item);
    }

    # Gets a page of employees.
    #
    # + filter - The conditions that the employees match
    # + sort - The order of the employees; each entry names one field
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + return - A page of employees, or the error
    resource function get employees(EmployeeFilter? filter = (), EmployeeSort[]? sort = (),
            int? size = (), int? offset = ()) returns EmployeeList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceEmployeeEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Employee, persist:Error?> rows = dataserviceClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Employee[]|persist:Error items = from Employee item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Employee");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Employee item in items
                select new EmployeeObject(item), hasMore};
    }

    # Gets an employee by its key.
    #
    # + id - The id of the employee
    # + return - The employee, or the error
    resource function get employee(int id) returns EmployeeObject?|error {
        Employee|persist:Error item = dataserviceClient->/employees/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Employee", [id]);
        }
        return new (item);
    }

    # Gets a page of assignments.
    #
    # + filter - The conditions that the assignments match
    # + sort - The order of the assignments; each entry names one field
    # + size - The number of assignments in the page
    # + offset - The number of assignments to skip
    # + return - A page of assignments, or the error
    resource function get assignments(AssignmentFilter? filter = (), AssignmentSort[]? sort = (),
            int? size = (), int? offset = ()) returns AssignmentList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceAssignmentEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Assignment, persist:Error?> rows = dataserviceClient->/assignments(Assignment,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Assignment[]|persist:Error items = from Assignment item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Assignment");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Assignment item in items
                select new AssignmentObject(item), hasMore};
    }

    # Gets an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + return - The assignment, or the error
    resource function get assignment(string projectCode, int seq) returns AssignmentObject?|error {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Assignment", [projectCode, seq]);
        }
        return new (item);
    }
}
