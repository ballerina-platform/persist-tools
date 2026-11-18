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

# The `read` scopes that a caller needs to read each relation of `Department`.
final readonly & map<dsGraphql:RelationScopes> dataserviceDepartmentRelations = {
    "employees": {target: "Employee", scopes: ["hr:read"]}
};

# The `read` scopes that a caller needs to read each relation of `Employee`.
final readonly & map<dsGraphql:RelationScopes> dataserviceEmployeeRelations = {
    "department": {target: "Department", scopes: ["hr:read"]},
    "assignments": {target: "Assignment", scopes: ["assignments:read"]}
};

# The `read` scopes that a caller needs to read each relation of `Assignment`.
final readonly & map<dsGraphql:RelationScopes> dataserviceAssignmentRelations = {
    "employee": {target: "Employee", scopes: ["hr:read"]}
};

@graphql:ServiceConfig {
    graphiql: {enabled: true},
    cors: dataserviceConfig.serviceConfig.cors,
    contextInit: dataserviceInitContext
}
service graphql:Service /graphql on dataserviceListener {

    # Gets a page of departments.
    #
    # + filter - The conditions that the departments match
    # + sort - The order of the departments; each entry names one field
    # + size - The number of departments in the page
    # + offset - The number of departments to skip
    # + return - A page of departments, or the error
    resource function get departments(graphql:Context ctx, graphql:Field 'field, DepartmentFilter? filter = (), DepartmentSort[]? sort = (),
            int? size = (), int? offset = ()) returns DepartmentList?|error {
        check dsGraphql:checkScopes(ctx, ["hr:read"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Department", dataserviceDepartmentRelations);
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
    resource function get department(graphql:Context ctx, graphql:Field 'field, int id) returns DepartmentObject?|error {
        check dsGraphql:checkScopes(ctx, ["hr:read"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Department", dataserviceDepartmentRelations);
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
    remote function createDepartment(graphql:Context ctx, graphql:Field 'field, DepartmentInsertInput input)
            returns DepartmentObject?|error {
        check dsGraphql:checkScopes(ctx, []);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Department", dataserviceDepartmentRelations, ["hr:read"]);
        return (check dataserviceCreateDepartment([input]))[0];
    }

    # Creates departments.
    #
    # + input - The departments to create
    # + return - The created departments, or the error
    remote function createDepartments(graphql:Context ctx, graphql:Field 'field, DepartmentInsertInput[] input)
            returns DepartmentList?|error {
        check dsGraphql:checkScopes(ctx, []);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Department", dataserviceDepartmentRelations, ["hr:read"]);
        return {items: check dataserviceCreateDepartment(input), hasMore: false};
    }

    # Gets a page of employees.
    #
    # + filter - The conditions that the employees match
    # + sort - The order of the employees; each entry names one field
    # + size - The number of employees in the page
    # + offset - The number of employees to skip
    # + return - A page of employees, or the error
    resource function get employees(graphql:Context ctx, graphql:Field 'field, EmployeeFilter? filter = (), EmployeeSort[]? sort = (),
            int? size = (), int? offset = ()) returns EmployeeList?|error {
        check dsGraphql:checkScopes(ctx, ["hr:read"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Employee", dataserviceEmployeeRelations);
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
    resource function get employee(graphql:Context ctx, graphql:Field 'field, int id) returns EmployeeObject?|error {
        check dsGraphql:checkScopes(ctx, ["hr:read"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Employee", dataserviceEmployeeRelations);
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
    remote function createEmployee(graphql:Context ctx, graphql:Field 'field, EmployeeInsertInput input)
            returns EmployeeObject?|error {
        check dsGraphql:checkScopes(ctx, ["employees:create"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Employee", dataserviceEmployeeRelations, ["hr:read"]);
        return (check dataserviceCreateEmployee([input]))[0];
    }

    # Creates employees.
    #
    # + input - The employees to create
    # + return - The created employees, or the error
    remote function createEmployees(graphql:Context ctx, graphql:Field 'field, EmployeeInsertInput[] input)
            returns EmployeeList?|error {
        check dsGraphql:checkScopes(ctx, ["employees:create"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Employee", dataserviceEmployeeRelations, ["hr:read"]);
        return {items: check dataserviceCreateEmployee(input), hasMore: false};
    }

    # Updates an employee by its key.
    #
    # + id - The id of the employee
    # + input - The fields to change
    # + return - The updated employee, `()` when it cannot be returned, or the error
    remote function updateEmployee(graphql:Context ctx, graphql:Field 'field, int id, EmployeeUpdateInput input)
            returns EmployeeObject?|error {
        check dsGraphql:checkScopes(ctx, ["employees:update"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Employee", dataserviceEmployeeRelations, ["hr:read"]);
        boolean readable = dsGraphql:hasScope(ctx, ["hr:read"]);
        EmployeeUpdate value = check transformInputToEmployeeUpdate(input);
        Employee|persist:Error item;
        if value.length() == 0 {
            if !readable {
                return ();
            }
            item = dataserviceClient->/employees/[id];
        } else {
            item = dataserviceClient->/employees/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Employee", [id]);
        }
        return readable ? new (item) : ();
    }

    # Deletes an employee by its key.
    #
    # + id - The id of the employee
    # + return - The deleted employee, or the error
    remote function deleteEmployee(graphql:Context ctx, graphql:Field 'field, int id) returns EmployeeObject?|error {
        check dsGraphql:checkScopes(ctx, ["hr:admin"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Employee", dataserviceEmployeeRelations, ["hr:read"]);
        Employee|persist:Error item = dataserviceClient->/employees/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Employee", [id]);
        }
        return dsGraphql:hasScope(ctx, ["hr:read"]) ? new (item) : ();
    }

    # Gets a page of assignments.
    #
    # + filter - The conditions that the assignments match
    # + sort - The order of the assignments; each entry names one field
    # + size - The number of assignments in the page
    # + offset - The number of assignments to skip
    # + return - A page of assignments, or the error
    resource function get assignments(graphql:Context ctx, graphql:Field 'field, AssignmentFilter? filter = (), AssignmentSort[]? sort = (),
            int? size = (), int? offset = ()) returns AssignmentList?|error {
        check dsGraphql:checkScopes(ctx, ["assignments:read"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Assignment", dataserviceAssignmentRelations);
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
    resource function get assignment(graphql:Context ctx, graphql:Field 'field, string projectCode, int seq) returns AssignmentObject?|error {
        check dsGraphql:checkScopes(ctx, ["assignments:read"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Assignment", dataserviceAssignmentRelations);
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
    remote function createAssignment(graphql:Context ctx, graphql:Field 'field, AssignmentInsertInput input)
            returns AssignmentObject?|error {
        check dsGraphql:checkScopes(ctx, ["assignments:create"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Assignment", dataserviceAssignmentRelations, ["assignments:read"]);
        return (check dataserviceCreateAssignment([input]))[0];
    }

    # Creates assignments.
    #
    # + input - The assignments to create
    # + return - The created assignments, or the error
    remote function createAssignments(graphql:Context ctx, graphql:Field 'field, AssignmentInsertInput[] input)
            returns AssignmentList?|error {
        check dsGraphql:checkScopes(ctx, ["assignments:create"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Assignment", dataserviceAssignmentRelations, ["assignments:read"]);
        return {items: check dataserviceCreateAssignment(input), hasMore: false};
    }

    # Updates an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + input - The fields to change
    # + return - The updated assignment, `()` when it cannot be returned, or the error
    remote function updateAssignment(graphql:Context ctx, graphql:Field 'field, string projectCode, int seq, AssignmentUpdateInput input)
            returns AssignmentObject?|error {
        check dsGraphql:checkScopes(ctx, ["assignments:update"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Assignment", dataserviceAssignmentRelations, ["assignments:read"]);
        boolean readable = dsGraphql:hasScope(ctx, ["assignments:read"]);
        AssignmentUpdate value = check transformInputToAssignmentUpdate(input);
        Assignment|persist:Error item;
        if value.length() == 0 {
            if !readable {
                return ();
            }
            item = dataserviceClient->/assignments/[projectCode]/[seq];
        } else {
            item = dataserviceClient->/assignments/[projectCode]/[seq].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Assignment", [projectCode, seq]);
        }
        return readable ? new (item) : ();
    }

    # Deletes an assignment by its key.
    #
    # + projectCode - The projectCode of the assignment
    # + seq - The seq of the assignment
    # + return - The deleted assignment, or the error
    remote function deleteAssignment(graphql:Context ctx, graphql:Field 'field, string projectCode, int seq) returns AssignmentObject?|error {
        check dsGraphql:checkScopes(ctx, ["assignments:delete"]);
        check dsGraphql:checkRelationScopes(ctx, 'field, "Assignment", dataserviceAssignmentRelations, ["assignments:read"]);
        Assignment|persist:Error item = dataserviceClient->/assignments/[projectCode]/[seq].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Assignment", [projectCode, seq]);
        }
        return dsGraphql:hasScope(ctx, ["assignments:read"]) ? new (item) : ();
    }

    # Creates a log entry.
    #
    # A log entry that callers may only write.
    #
    # + input - The log entry to create
    # + return - The created log entry, or the error
    remote function createLogEntry(graphql:Context ctx, LogEntryInsertInput input)
            returns LogEntryObject?|error {
        check dsGraphql:checkScopes(ctx, ["log:write"]);
        return (check dataserviceCreateLogEntry([input]))[0];
    }

    # Creates log entries.
    #
    # A log entry that callers may only write.
    #
    # + input - The log entries to create
    # + return - The created log entries, or the error
    remote function createLogEntries(graphql:Context ctx, LogEntryInsertInput[] input)
            returns LogEntryList?|error {
        check dsGraphql:checkScopes(ctx, ["log:write"]);
        return {items: check dataserviceCreateLogEntry(input), hasMore: false};
    }

    # Updates a log entry by its key.
    #
    # A log entry that callers may only write.
    #
    # + id - The id of the log entry
    # + input - The fields to change
    # + return - `true` when the log entry is updated, or the error
    remote function updateLogEntry(graphql:Context ctx, int id, LogEntryUpdateInput input)
            returns boolean?|error {
        check dsGraphql:checkScopes(ctx, ["log:write"]);
        LogEntryUpdate value = check transformInputToLogEntryUpdate(input);
        if value.length() == 0 {
            return true;
        }
        LogEntry|persist:Error item = dataserviceClient->/logentries/[id].put(value);
        if item is persist:Error {
            check dsGraphql:toUpdateError(item, "LogEntry", [id]);
        }
        return true;
    }

    # Deletes a log entry by its key.
    #
    # A log entry that callers may only write.
    #
    # + id - The id of the log entry
    # + return - `true` when the log entry is deleted, or the error
    remote function deleteLogEntry(graphql:Context ctx, int id) returns boolean?|error {
        check dsGraphql:checkScopes(ctx, ["log:admin"]);
        LogEntry|persist:Error item = dataserviceClient->/logentries/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "LogEntry", [id]);
        }
        return true;
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
            stream<Department, persist:Error?> stored = dataserviceClient->/departments(Department,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Department[] rows = check from Department item in stored
                select item;
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
            stream<Employee, persist:Error?> stored = dataserviceClient->/employees(Employee,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Employee[] rows = check from Employee item in stored
                select item;
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
            stream<Assignment, persist:Error?> stored = dataserviceClient->/assignments(Assignment,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Assignment[] rows = check from Assignment item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceAssignmentEntity, keyValues);
            check commit;
            return from Assignment item in rows
                select new AssignmentObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Assignment");
    }
}

isolated function dataserviceCreateLogEntry(LogEntryInsertInput[] input) returns LogEntryObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    LogEntryInsert[] values = from LogEntryInsertInput entry in input
        select transformInputToLogEntryInsert(entry);
    do {
        transaction {
            int[] keys = check dataserviceClient->/logentries.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceLogEntryEntity, keyValues);
            stream<LogEntry, persist:Error?> stored = dataserviceClient->/logentries(LogEntry,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            LogEntry[] rows = check from LogEntry item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceLogEntryEntity, keyValues);
            check commit;
            return from LogEntry item in rows
                select new LogEntryObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "LogEntry");
    }
}

# Creates the context of a request, after authenticating it, with a dataloader per to-one relation.
#
# + requestContext - The HTTP request context
# + request - The HTTP request
# + return - The context, or the error
isolated function dataserviceInitContext(http:RequestContext requestContext, http:Request request)
        returns graphql:Context|error {
    graphql:Context ctx = new;
    check dataserviceAuthenticator.authenticate(ctx, request);
    ctx.registerDataLoader("Employee.department",
            new dataloader:DefaultDataLoader(dataserviceLoadEmployeeDepartment));
    ctx.registerDataLoader("Assignment.employee",
            new dataloader:DefaultDataLoader(dataserviceLoadAssignmentEmployee));
    return ctx;
}

isolated function dataserviceLoadEmployeeDepartment(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataserviceEmployeeEntity, "department", links,
            dataservicePageConfig);
    stream<Department, persist:Error?> rows = dataserviceClient->/departments(Department,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    Department[]|persist:Error found = from Department row in rows
        select row;
    if found is persist:Error {
        return dsGraphql:toListError(found, "Employee", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataserviceEmployeeEntity, "department", links);
}

isolated function dataserviceLoadAssignmentEmployee(readonly & anydata[] keys) returns anydata[]|error {
    map<anydata>[] links = from anydata key in keys
        select check key.ensureType();
    dataservice:Query query = dataservice:toIncludeQuery(dataserviceAssignmentEntity, "employee", links,
            dataservicePageConfig);
    stream<Employee, persist:Error?> rows = dataserviceClient->/employees(Employee,
        whereClause = query.whereClause, orderByClause = query.orderByClause
    );
    Employee[]|persist:Error found = from Employee row in rows
        select row;
    if found is persist:Error {
        return dsGraphql:toListError(found, "Assignment", "get related");
    }
    return dsGraphql:toKeyOrder(found, dataserviceAssignmentEntity, "employee", links);
}
