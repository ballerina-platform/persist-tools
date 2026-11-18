import ballerina/http;
import ballerina/test;
import ballerina/time;

final http:Client api = check new ("http://localhost:9593");

// The records every test reads; each test that writes uses keys of its own.
@test:BeforeSuite
function seed() returns error? {
    _ = check dataserviceClient->/departments.post([{name: "Research"}, {name: "Sales"}]);
    time:Date hiredOn = {year: 2021, month: 3, day: 15};
    _ = check dataserviceClient->/employees.post([
        {id: 1, name: "Ann", email: "ann@example.com", hiredOn, departmentId: 1},
        {id: 2, name: "Bob", email: (), hiredOn: {year: 2022, month: 7, day: 1}, departmentId: 2}
    ]);
    _ = check dataserviceClient->/assignments.post([{projectCode: "ATLAS", seq: 1, hours: 12.50, employeeId: 1}]);
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
