import ballerina/http;
import ballerina/test;
import ballerina/time;

final http:Client api = check new ("http://localhost:9593");

// The records every test reads; each test that writes uses keys of its own.
@test:BeforeSuite
function seed() returns error? {
    // persist's schema stores an int in a 32-bit INT column; the column is widened so that a 64-bit value reaches the
    // service.
    _ = check dataserviceClient->executeNativeSQL(`ALTER TABLE "Reading" ALTER COLUMN "count" BIGINT`);
    _ = check dataserviceClient->/departments.post([{name: "Research"}, {name: "Sales"}]);
    time:Date hiredOn = {year: 2021, month: 3, day: 15};
    _ = check dataserviceClient->/employees.post([
        {id: 1, name: "Ann", email: "ann@example.com", hiredOn, departmentId: 1},
        {id: 2, name: "Bob", email: (), hiredOn: {year: 2022, month: 7, day: 1}, departmentId: 2},
        {id: 3, name: "Cid", email: "cid@example.com", hiredOn: {year: 2023, month: 1, day: 10}, departmentId: 1}
    ]);
    _ = check dataserviceClient->/assignments.post([
        {projectCode: "ATLAS", seq: 1, hours: 12.50, employeeId: 1},
        {projectCode: "ATLAS", seq: 2, hours: 4, employeeId: 3}
    ]);
    _ = check dataserviceClient->/badges.post([{code: "B-1", employeeId: 1}]);
    _ = check dataserviceClient->/measures.post([{weight: 1.5, label: "small"}, {weight: 2.5, label: "large"}]);
    _ = check dataserviceClient->/timesheets.post([
        {id: 1, hours: 3, assignmentProjectCode: "ATLAS", assignmentSeq: 2},
        {id: 2, hours: 5, assignmentProjectCode: "ATLAS", assignmentSeq: 1}
    ]);
}

// Posts a document and returns the response body, which a request error answers with 400.
function query(string document, map<json>? variables = ()) returns json|error {
    map<json> payload = {query: document};
    if variables is map<json> {
        payload["variables"] = variables;
    }
    http:Response response = check api->post("/graphql", payload);
    return response.getJsonPayload();
}

// The `errors` entries of a response without their `locations`, which depend on the document's layout, ordered by
// their path, since root fields resolve concurrently.
function errorsOf(json response) returns json[]|error {
    json[] errors = check (check response.errors).ensureType();
    return from json entry in errors
        let map<json> fields = check entry.ensureType()
        order by (fields["path"] ?: "").toString()
        select map from [string, json] [name, value] in fields.entries()
            where name != "locations"
            select [name, value];
}
