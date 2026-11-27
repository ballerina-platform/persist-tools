import ballerina/test;

@test:Config
function testFilter() returns error? {
    json response = check query("{ employees(filter: {id: {gte: 2, lt: 10}, email: {isNull: false}}) " +
            "{ items { id } } byDate: employees(filter: {hiredOn: {gte: \"2022-01-01\"}, name: {startsWith: \"B\"}}) " +
            "{ items { id } } anyOf: employees(filter: {departmentId: {anyOf: [2]}}) { items { id } } }");
    test:assertEquals(response, {
        data: {employees: {items: [{id: 3}]}, byDate: {items: [{id: 2}]}, anyOf: {items: [{id: 2}]}}
    });
}

@test:Config
function testNullOperatorSetsNoCondition() returns error? {
    json response = check query("{ employees(filter: {id: {eq: null}, email: null}, size: 3) { items { id } } }");
    test:assertEquals(response, {data: {employees: {items: [{id: 1}, {id: 2}, {id: 3}]}}});
}

@test:Config
function testDecimalFilterAsVariable() returns error? {
    json response = check query("query($hours: Decimal) { assignments(filter: {hours: {gt: $hours}}) " +
            "{ items { seq } } }", {hours: 4.5});
    test:assertEquals(response, {data: {assignments: {items: [{seq: 1}]}}});
}

@test:Config
function testSort() returns error? {
    json response = check query("{ employees(sort: [{departmentId: ASC}, {name: DESC}], size: 3) " +
            "{ items { id } } }");
    test:assertEquals(response, {data: {employees: {items: [{id: 3}, {id: 1}, {id: 2}]}}});
}

@test:Config
function testPages() returns error? {
    json response = check query("{ first: employees(sort: [{hiredOn: DESC}], size: 1) { items { id } hasMore } " +
            "last: employees(sort: [{hiredOn: DESC}], size: 1, offset: 2) { items { id } hasMore } " +
            "capped: employees(size: 10) { items { id } hasMore } }");
    // maxPageSize is 3, so a size of 10 is reduced to 3.
    test:assertEquals(response, {
        data: {
            first: {items: [{id: 3}], hasMore: true},
            last: {items: [{id: 1}], hasMore: false},
            capped: {items: [{id: 1}, {id: 2}, {id: 3}], hasMore: false}
        }
    });
}

@test:Config
function testInvalidOptions() returns error? {
    json response = check query("{ employees { hasMore } size: employees(size: 0) { hasMore } " +
            "offset: employees(offset: -1) { hasMore } twoFields: employees(sort: [{id: ASC, name: ASC}]) { hasMore } " +
            "twice: employees(sort: [{id: ASC}, {id: DESC}]) { hasMore } " +
            "date: employees(filter: {hiredOn: {eq: \"01/02/2024\"}}) { hasMore } }");
    test:assertEquals(check response.data, {
        employees: {hasMore: true}, size: (), offset: (), twoFields: (), twice: (), date: ()
    });
    test:assertEquals(check errorsOf(response), [
        invalidInput("The field 'hiredOn' of the entity 'Employee' is not an ISO 8601 date.", "date"),
        invalidInput("The offset must be at least 0.", "offset"),
        invalidInput("The page size must be at least 1.", "size"),
        invalidInput("The field 'id' is named more than once in 'sort'.", "twice"),
        invalidInput("Each entry of 'sort' must name exactly one field.", "twoFields")
    ]);
}

@test:Config
function testUnknownOperatorIsARequestError() returns error? {
    json response = check query("{ employees(filter: {name: {isNull: true}}) { hasMore } }");
    test:assertFalse(response is map<json> && response.hasKey("data"), response.toJsonString());
    json[] errors = check errorsOf(response);
    test:assertEquals(errors.length(), 1);
}

@test:Config
function testGetOne() returns error? {
    json response = check query("{ employee(id: 1) { id name } missing: employee(id: 99) { id } " +
            "assignment(projectCode: \"ATLAS\", seq: 2) { hours employeeId } }");
    test:assertEquals(check response.data, {
        employee: {id: 1, name: "Ann"},
        missing: (),
        assignment: {hours: 4d, employeeId: 3}
    });
    test:assertEquals(check errorsOf(response), [
        {
            message: "A record with the key '99' does not exist for the entity 'Employee'.",
            path: ["missing"],
            extensions: {code: "NOT_FOUND"}
        }
    ]);
}

function invalidInput(string message, string path) returns json =>
    {message, path: [path], extensions: {code: "INVALID_INPUT"}};
