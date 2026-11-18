import ballerina/http;
import ballerina/jwt;
import ballerina/test;

const BASE = "http://localhost:9594";

final http:Client api = check new (BASE);

@test:BeforeSuite
function seed() returns error? {
    _ = check dataserviceClient->/departments.post([{name: "Research"}, {name: "Sales"}]);
    _ = check dataserviceClient->/employees.post([
        {id: 1, name: "Ann", email: (), hiredOn: {year: 2021, month: 3, day: 15}, departmentId: 1},
        {id: 2, name: "Bob", email: (), hiredOn: {year: 2022, month: 7, day: 1}, departmentId: 2},
        {id: 3, name: "Cid", email: (), hiredOn: {year: 2023, month: 1, day: 10}, departmentId: 1}
    ]);
    _ = check dataserviceClient->/assignments.post([{projectCode: "ATLAS", seq: 1, hours: 4, employeeId: 1}]);
}

// Posts a document as a user of the file user store, or without a credential, and returns the response.
function send(string document, string? user = (), map<string> headers = {}) returns http:Response|error {
    map<string> all = headers.clone();
    if user is string {
        all["Authorization"] = "Basic " + (user + ":" + user).toBytes().toBase64();
    }
    return api->post("/graphql", {query: document}, all);
}

function query(string document, string user) returns json|error =>
    (check send(document, user)).getJsonPayload();

function bearer(string document, string[] scopes) returns json|error {
    string token = check jwt:issue({
        issuer: "dataservice",
        username: "caller",
        customClaims: {scope: string:'join(" ", ...scopes)},
        signatureConfig: {algorithm: jwt:HS256, config: "dataservice-test-secret"}
    });
    http:Response response = check api->post("/graphql", {query: document}, {"Authorization": "Bearer " + token});
    return response.getJsonPayload();
}

// The `errors` entries of a response without their `locations`, ordered by path.
function errorsOf(json response) returns json[]|error {
    json[] errors = check (check response.errors).ensureType();
    return from json entry in errors
        let map<json> fields = check entry.ensureType()
        order by (fields["path"] ?: "").toString()
        select map from [string, json] [name, value] in fields.entries()
            where name != "locations"
            select [name, value];
}

function forbidden(string path) returns json =>
    {message: "The caller holds no scope that the operation needs.", path: [path], extensions: {code: "FORBIDDEN"}};
