import ballerina/http;
import ballerina/test;

const UNAUTHENTICATED = "The request needs a valid credential.";

// contextInit runs before the document is parsed, so a request without a valid credential is refused whatever it
// holds: a valid document, one that does not parse, or an introspection query.
@test:Config {dataProvider: unauthenticatedRequests}
function testUnauthenticated(string document, string? user) returns error? {
    http:Response response = check send(document, user);
    test:assertEquals(response.statusCode, 400);
    map<json> body = check (check response.getJsonPayload()).ensureType();
    test:assertFalse(body.hasKey("data"), body.toJsonString());
    test:assertEquals(check errorsOf(body), [{message: UNAUTHENTICATED, extensions: {code: "UNAUTHENTICATED"}}]);
    test:assertFalse(response.hasHeader("WWW-Authenticate"));
}

function unauthenticatedRequests() returns [string, string?][] => [
    ["{ employees { items { id } } }", ()],
    ["{ employees { items { id }", ()],
    ["{ __schema { queryType { name } } }", ()],
    ["{ employees { items { id } } }", "nobody"]
];

@test:Config
function testOnlyPost() returns error? {
    http:Response response = check api->get("/graphql?query=%7B%20employees%20%7B%20hasMore%20%7D%20%7D",
            {"Authorization": "Basic " + "reader:reader".toBytes().toBase64()});
    test:assertEquals(response.statusCode, 400);
    json body = check response.getJsonPayload();
    test:assertEquals((check errorsOf(body))[0].message, "A request to a data service with auth must use POST.");
}

@test:Config
function testIntrospectionWithACredentialChecksNoScope() returns error? {
    json response = check query("{ __schema { queryType { name } } }", "guest");
    test:assertEquals(response, {data: {__schema: {queryType: {name: "Query"}}}});
}

@test:Config
function testScopesPerRootField() returns error? {
    json response = check query("{ departments(filter: {id: {lte: 2}}) { items { name } } assignments { items { seq } } }",
            "reader");
    test:assertEquals(check response.data, {departments: {items: [{name: "Research"}, {name: "Sales"}]},
        assignments: ()});
    test:assertEquals(check errorsOf(response), [forbidden("assignments")]);
}

@test:Config
function testJwtScopes() returns error? {
    json response = check bearer("{ employees(size: 1) { items { id } } }", ["hr:read"]);
    test:assertEquals(response, {data: {employees: {items: [{id: 1}]}}});
    json refused = check bearer("{ employees(size: 1) { items { id } } }", ["other"]);
    test:assertEquals(check errorsOf(refused), [forbidden("employees")]);
}

// Department create has an empty scope list, so it takes the provider's scopes: none for the user store, where a
// credential is enough, and `jwt:default` for the JWT validator.
@test:Config
function testEmptyScopeListTakesTheProviderScopes() returns error? {
    json byUser = check query("mutation { createDepartment(input: {name: \"Ops\"}) { name } }", "guest");
    test:assertEquals(byUser, {data: {createDepartment: {name: "Ops"}}});
    json byToken = check bearer("mutation { createDepartment(input: {name: \"Legal\"}) { name } }", ["jwt:default"]);
    test:assertEquals(byToken, {data: {createDepartment: {name: "Legal"}}});
    json refused = check bearer("mutation { createDepartment(input: {name: \"No\"}) { name } }", ["hr:read"]);
    test:assertEquals(check errorsOf(refused), [forbidden("createDepartment")]);
}

@test:Config
function testRelationScopes() returns error? {
    json response = check query("{ employees { items { name assignments { items { seq } } } } }", "reader");
    test:assertEquals(check response.data, {employees: ()});
    test:assertEquals(check errorsOf(response), [
        {
            message: "The relation 'assignments' of the entity 'Employee' needs a 'read' scope of the entity " +
                "'Assignment'.",
            path: ["employees"],
            extensions: {code: "FORBIDDEN"}
        }
    ]);
}

@test:Config
function testRelationScopesThroughAFragment() returns error? {
    json response = check query("{ employees { items { ...withAssignments } } } fragment withAssignments on " +
            "EmployeeObject { assignments { items { seq } } }", "reader");
    test:assertEquals(check response.data, {employees: ()});
}

@test:Config
function testRelationUnderAMutationNeedsTheEntityReadScope() returns error? {
    json response = check query("mutation { createEmployee(input: {id: 10, name: \"Dee\", hiredOn: \"2024-01-01\", " +
            "departmentId: 1}) { id department { name } } }", "writer");
    test:assertEquals(check errorsOf(response), [forbidden("createEmployee")]);
    json allowed = check query("mutation { createEmployee(input: {id: 11, name: \"Eve\", hiredOn: \"2024-01-01\", " +
            "departmentId: 1}) { id } }", "writer");
    test:assertEquals(allowed, {data: {createEmployee: {id: 11}}});
}

// A caller who may update but not read gets null, with no error, for the record.
@test:Config
function testPatchWithoutReadScope() returns error? {
    json response = check query("mutation { updateEmployee(id: 2, input: {name: \"Bo\"}) { id } " +
            "empty: updateEmployee(id: 2, input: {}) { id } }", "writer");
    test:assertEquals(response, {data: {updateEmployee: (), empty: ()}});
    json read = check query("{ employee(id: 2) { name } }", "reader");
    test:assertEquals(read, {data: {employee: {name: "Bo"}}});
}

@test:Config {dependsOn: [testPatchWithoutReadScope, testRelationUnderAMutationNeedsTheEntityReadScope]}
function testDeleteWithAndWithoutReadScope() returns error? {
    json withoutRead = check query("mutation { deleteEmployee(id: 3) { id } }", "remover");
    test:assertEquals(withoutRead, {data: {deleteEmployee: ()}});
    json withRead = check query("mutation { deleteEmployee(id: 11) { id name } }", "admin");
    test:assertEquals(withRead, {data: {deleteEmployee: {id: 11, name: "Eve"}}});
}

@test:Config
function testWriteOnlyEntity() returns error? {
    json created = check query("mutation { createLogEntry(input: {id: 1, message: \"hi\"}) { id message } }", "logger");
    test:assertEquals(created, {data: {createLogEntry: {id: 1, message: "hi"}}});
    json updated = check query("mutation { updateLogEntry(id: 1, input: {message: \"bye\"}) }", "logger");
    test:assertEquals(updated, {data: {updateLogEntry: true}});
    json refused = check query("mutation { deleteLogEntry(id: 1) }", "logger");
    test:assertEquals(check errorsOf(refused), [forbidden("deleteLogEntry")]);
}

@test:Config
function testCorsOnAnAuthError() returns error? {
    http:Response response = check send("{ employees { hasMore } }", headers = {"Origin": "https://app.example.com"});
    test:assertEquals(response.statusCode, 400);
    test:assertEquals(check response.getHeader("Access-Control-Allow-Origin"), "https://app.example.com");
}

@test:Config
function testGraphiqlNeedsNoCredential() returns error? {
    http:Response response = check api->get("/graphiql");
    test:assertEquals(response.statusCode, 200);
}
