import ballerina/http;
import ballerina/test;

// INTERNAL is forced by renaming a column for the length of a test; renaming a table would let H2's INIT script
// create an empty one on the next connection.

@test:Config {dependsOn: [testToOneWithACompositeForeignKey]}
function testInternalErrorOnAList() returns error? {
    _ = check dataserviceClient->executeNativeSQL(`ALTER TABLE "Timesheet" ALTER COLUMN "hours" RENAME TO "minutes"`);
    json|error response = query("{ timesheets { items { id } } badges { items { code } } }");
    _ = check dataserviceClient->executeNativeSQL(`ALTER TABLE "Timesheet" ALTER COLUMN "minutes" RENAME TO "hours"`);
    json body = check response;
    // The other root field keeps its data, and no database text reaches the client.
    test:assertEquals(check body.data, {timesheets: (), badges: {items: [{code: "B-1"}]}});
    test:assertEquals(check errorsOf(body), [
        {message: "An internal error occurred.", path: ["timesheets"], extensions: {code: "INTERNAL"}}
    ]);
}

@test:Config {dependsOn: [testOneToOneFromTheSideWithoutTheForeignKey, testInternalErrorOnAList]}
function testInternalErrorInADataloader() returns error? {
    _ = check dataserviceClient->executeNativeSQL(`ALTER TABLE "Employee" ALTER COLUMN "email" RENAME TO "mail"`);
    json|error response = query("{ badges { items { code employee { name } } } }");
    _ = check dataserviceClient->executeNativeSQL(`ALTER TABLE "Employee" ALTER COLUMN "mail" RENAME TO "email"`);
    json body = check response;
    test:assertEquals(check body.data, {badges: {items: [{code: "B-1", employee: ()}]}});
    test:assertEquals(check errorsOf(body), [
        {
            message: "An internal error occurred.",
            path: ["badges", "items", 0, "employee"],
            extensions: {code: "INTERNAL"}
        }
    ]);
}

@test:Config {dataProvider: requestErrors}
function testRequestError(string document, string message) returns error? {
    http:Response response = check api->post("/graphql", {query: document});
    test:assertEquals(response.statusCode, 400);
    map<json> body = check (check response.getJsonPayload()).ensureType();
    test:assertFalse(body.hasKey("data"), body.toJsonString());
    json[] errors = check (check body.errors).ensureType();
    test:assertTrue((check errors[0].message).toString().includes(message), errors.toJsonString());
    // ballerina/graphql writes these, so they carry no code.
    foreach json entry in errors {
        map<json> fields = check entry.ensureType();
        test:assertFalse(fields.hasKey("extensions"), entry.toJsonString());
    }
}

function requestErrors() returns [string, string][] => [
    ["{ employees { items { id }", "Syntax Error"],
    ["{ employees { items { salary } } }", "Cannot query field \"salary\" on type \"EmployeeObject\"."],
    ["{ employee(id: \"one\") { id } }", "Int cannot represent non Int value: one"],
    ["{ employee { id } }", "Field \"employee\" argument \"id\" of type \"Int!\" is required, but it was not provided."],
    ["{ employees(limit: 2) { hasMore } }", "Unknown argument \"limit\" on field \"Query.employees\"."],
    ["{ employees(filter: {name: {like: \"A\"}}) { hasMore } }", "Field \"like\" is not defined by type"]
];

@test:Config
function testFieldErrorsAnswerOk() returns error? {
    http:Response response = check api->post("/graphql", {query: "{ employee(id: 99) { id } }"});
    test:assertEquals(response.statusCode, 200);
}
