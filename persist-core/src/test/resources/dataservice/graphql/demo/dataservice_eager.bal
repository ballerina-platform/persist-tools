// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/graphql.dataloader;
import ballerina/http;
import ballerina/persist;

# The persist client of the data service.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener graphql:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@graphql:ServiceConfig {
    graphiql: {enabled: true},
    cors: dataserviceConfig.serviceConfig.cors,
    maxQueryDepth: dataserviceConfig.serviceConfig.maxQueryDepth,
    queryComplexityConfig: {maxComplexity: dataserviceConfig.serviceConfig.maxQueryComplexity},
    contextInit: dataserviceInitContext,
    validation: false
}
service graphql:Service /graphql on dataserviceListener {

    # Checks the query limits, so that a value out of range stops the program.
    #
    # + return - An error naming the limit that is out of range
    function init() returns error? {
        check dsGraphql:checkLimits(dataserviceConfig.serviceConfig);
    }

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
        Department[]|persist:Error items = dataserviceClient->/departments(Department,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
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

    # Creates a department.
    #
    # + input - The department to create
    # + return - The created department, or the error
    remote function createDepartment(DepartmentInsertInput input) returns DepartmentObject?|error =>
        (check dataserviceCreateDepartment([input]))[0];

    # Creates departments.
    #
    # + input - The departments to create
    # + return - The created departments, or the error
    remote function createDepartments(DepartmentInsertInput[] input) returns DepartmentList?|error =>
        {items: check dataserviceCreateDepartment(input), hasMore: false};

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
        Employee[]|persist:Error items = dataserviceClient->/employees(Employee,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
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

    # Creates an employee.
    #
    # + input - The employee to create
    # + return - The created employee, or the error
    remote function createEmployee(EmployeeInsertInput input) returns EmployeeObject?|error =>
        (check dataserviceCreateEmployee([input]))[0];

    # Creates employees.
    #
    # + input - The employees to create
    # + return - The created employees, or the error
    remote function createEmployees(EmployeeInsertInput[] input) returns EmployeeList?|error =>
        {items: check dataserviceCreateEmployee(input), hasMore: false};

    # Updates an employee by its key.
    #
    # + id - The id of the employee
    # + input - The fields to change
    # + return - The updated employee, `()` when it cannot be returned, or the error
    remote function updateEmployee(int id, EmployeeUpdateInput input)
            returns EmployeeObject?|error {
        EmployeeUpdate value = check transformInputToEmployeeUpdate(input);
        Employee|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/employees/[id];
        } else {
            item = dataserviceClient->/employees/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Employee", [id]);
        }
        return new (item);
    }

    # Deletes an employee by its key.
    #
    # + id - The id of the employee
    # + return - The deleted employee, or the error
    remote function deleteEmployee(int id) returns EmployeeObject?|error {
        Employee|persist:Error item = dataserviceClient->/employees/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Employee", [id]);
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
        Assignment[]|persist:Error items = dataserviceClient->/assignments(Assignment,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
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

    # Creates an assignment.
    #
    # + input - The assignment to create
    # + return - The created assignment, or the error
    remote function createAssignment(AssignmentInsertInput input) returns AssignmentObject?|error =>
        (check dataserviceCreateAssignment([input]))[0];

    # Creates assignments.
    #
    # + input - The assignments to create
    # + return - The created assignments, or the error
    remote function createAssignments(AssignmentInsertInput[] input) returns AssignmentList?|error =>
        {items: check dataserviceCreateAssignment(input), hasMore: false};

    # Updates an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + input - The fields to change
    # + return - The updated assignment, `()` when it cannot be returned, or the error
    remote function updateAssignment(string projectCode, int seq, AssignmentUpdateInput input)
            returns AssignmentObject?|error {
        AssignmentUpdate value = check transformInputToAssignmentUpdate(input);
        Assignment|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/assignments/[projectCode]/[seq];
        } else {
            item = dataserviceClient->/assignments/[projectCode]/[seq].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Assignment", [projectCode, seq]);
        }
        return new (item);
    }

    # Deletes an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + return - The deleted assignment, or the error
    remote function deleteAssignment(string projectCode, int seq) returns AssignmentObject?|error {
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Assignment", [projectCode, seq]);
        }
        return new (item);
    }
}

isolated function dataserviceCreateDepartment(DepartmentInsertInput[] input) returns DepartmentObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    DepartmentInsert[] values = from DepartmentInsertInput entry in input
        select transformInputToDepartmentInsert(entry);
    do {
        transaction {
            int[] keys = check dataserviceClient->/departments.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceDepartmentEntity, keyValues);
            Department[] rows = check dataserviceClient->/departments(Department,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            check dataservice:orderByKeys(rows, dataserviceDepartmentEntity, keyValues);
            check commit;
            return from Department item in rows
                select new DepartmentObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Department");
    }
}

isolated function dataserviceCreateEmployee(EmployeeInsertInput[] input) returns EmployeeObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    EmployeeInsert[] values = [];
    foreach EmployeeInsertInput entry in input {
        values.push(check transformInputToEmployeeInsert(entry));
    }
    do {
        transaction {
            int[] keys = check dataserviceClient->/employees.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceEmployeeEntity, keyValues);
            Employee[] rows = check dataserviceClient->/employees(Employee,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            check dataservice:orderByKeys(rows, dataserviceEmployeeEntity, keyValues);
            check commit;
            return from Employee item in rows
                select new EmployeeObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Employee");
    }
}

isolated function dataserviceCreateAssignment(AssignmentInsertInput[] input) returns AssignmentObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    AssignmentInsert[] values = from AssignmentInsertInput entry in input
        select transformInputToAssignmentInsert(entry);
    do {
        transaction {
            [string, int][] keys = check dataserviceClient->/assignments.post(values);
            dataservice:KeyValue[][] keyValues = keys;
            dataservice:Query query = dataservice:toKeysQuery(dataserviceAssignmentEntity, keyValues);
            Assignment[] rows = check dataserviceClient->/assignments(Assignment,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            check dataservice:orderByKeys(rows, dataserviceAssignmentEntity, keyValues);
            check commit;
            return from Assignment item in rows
                select new AssignmentObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Assignment");
    }
}

# Creates the context of a request, with a dataloader per to-one relation.
#
# + requestContext - The HTTP request context
# + request - The HTTP request
# + return - The context, or the error
isolated function dataserviceInitContext(http:RequestContext requestContext, http:Request request)
        returns graphql:Context|error {
    graphql:Context ctx = new;
    ctx.registerDataLoader("Assignment.employee",
            new dataloader:DefaultDataLoader(dataserviceLoadAssignmentEmployee));
    return ctx;
}

isolated function dataserviceLoadAssignmentEmployee(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataserviceAssignmentEntity, "employee", links,
            dataservicePageConfig);
    Employee[]|persist:Error found = dataserviceClient->/employees(Employee,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    if found is persist:Error {
        return dsGraphql:toListError(found, "Assignment", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataserviceAssignmentEntity, "employee", links);
}
