import ballerina/http;
import ballerina/test;

final http:Client api = check new ("http://localhost:9591");

@test:Config
function testCreateOne() returns error? {
    check createDepartment(1, "Engineering");
    http:Response response = check send("POST", "/api/employees", employee(1, "Ann"));
    test:assertEquals(response.statusCode, 201);
    test:assertEquals(check response.getHeader("Location"), "/api/employees/1");
    test:assertEquals(check response.getJsonPayload(), employee(1, "Ann"));
}

@test:Config {dependsOn: [testCreateOne]}
function testCreateMany() returns error? {
    http:Response response = check send("POST", "/api/employees", [employee(2, "Bob"), employee(3, "Cid")]);
    test:assertEquals(response.statusCode, 201);
    test:assertFalse(response.hasHeader("Location"));
    test:assertEquals(check response.getJsonPayload(), {items: [employee(2, "Bob"), employee(3, "Cid")]});
}

@test:Config
function testCreateNone() returns error? {
    http:Response response = check send("POST", "/api/employees", []);
    test:assertEquals(response.statusCode, 201);
    test:assertEquals(check response.getJsonPayload(), {items: []});
}

@test:Config {dependsOn: [testCreateMany]}
function testGetMany() returns error? {
    http:Response response = check send("GET", "/api/employees");
    test:assertEquals(response.statusCode, 200);
    json body = check response.getJsonPayload();
    json[] items = check (check body.items).ensureType();
    test:assertEquals(items.length(), 3);
}

@test:Config {dependsOn: [testCreateOne]}
function testGetOne() returns error? {
    http:Response response = check send("GET", "/api/employees/1");
    test:assertEquals(response.statusCode, 200);
    test:assertEquals(check response.getJsonPayload(), employee(1, "Ann"));
}

@test:Config {dependsOn: [testGetMany, testGetOne]}
function testPatch() returns error? {
    http:Response response = check send("PATCH", "/api/employees/2", {name: "Bobby", email: ()});
    test:assertEquals(response.statusCode, 200);
    test:assertEquals(check response.getJsonPayload(), withoutEmail(employee(2, "Bobby")));
}

@test:Config {dependsOn: [testPatch]}
function testEmptyPatchReturnsTheRecord() returns error? {
    http:Response response = check send("PATCH", "/api/employees/2", {});
    test:assertEquals(response.statusCode, 200);
    test:assertEquals(check response.getJsonPayload(), withoutEmail(employee(2, "Bobby")));
}

@test:Config {dependsOn: [testGetMany]}
function testPatchKeyIsBadRequest() returns error? {
    http:Response response = check send("PATCH", "/api/employees/3", {id: 30});
    test:assertEquals(response.statusCode, 400);
}

@test:Config {dependsOn: [testEmptyPatchReturnsTheRecord, testPatchKeyIsBadRequest]}
function testDelete() returns error? {
    http:Response response = check send("DELETE", "/api/employees/3");
    test:assertEquals(response.statusCode, 200);
    test:assertEquals(check response.getJsonPayload(), employee(3, "Cid"));
    http:Response again = check send("GET", "/api/employees/3");
    test:assertEquals(again.statusCode, 404);
}

@test:Config
function testNotFound() returns error? {
    http:Response response = check send("GET", "/api/employees/99");
    test:assertEquals(response.statusCode, 404);
    test:assertEquals(check response.getJsonPayload(), {
        code: "NOT_FOUND",
        message: "A record with the key '99' does not exist for the entity 'Employee'."
    });
    test:assertEquals((check send("PATCH", "/api/employees/99", {name: "X"})).statusCode, 404);
    test:assertEquals((check send("DELETE", "/api/employees/99")).statusCode, 404);
}

@test:Config {dependsOn: [testCreateOne]}
function testConflict() returns error? {
    http:Response response = check send("POST", "/api/employees", employee(1, "Ann"));
    test:assertEquals(response.statusCode, 409);
    test:assertEquals(check response.getJsonPayload(), {
        code: "CONFLICT",
        message: "A record that conflicts with an existing record of the entity 'Employee' already exists."
    });
}

@test:Config
function testMistypedKey() returns error? {
    test:assertEquals((check send("GET", "/api/employees/abc")).statusCode, 400);
}

@test:Config
function testMissingFieldIsBadRequest() returns error? {
    test:assertEquals((check send("POST", "/api/employees", {id: 50})).statusCode, 400);
}

@test:Config
function testDisabledMethod() returns error? {
    test:assertEquals((check send("PATCH", "/api/departments/1", {name: "X"})).statusCode, 405);
    test:assertEquals((check send("DELETE", "/api/departments/1")).statusCode, 405);
    test:assertEquals((check send("PUT", "/api/employees/1", {})).statusCode, 405);
}

@test:Config
function testEntityNotExposed() returns error? {
    test:assertEquals((check send("GET", "/api/auditlogs")).statusCode, 404);
}

@test:Config {dependsOn: [testCreateOne]}
function testCompositeKeyLocation() returns error? {
    http:Response response = check send("POST", "/api/assignments", {projectCode: "P 1/a", seq: 1, hours: 2.50,
        employeeId: 1});
    test:assertEquals(response.statusCode, 201);
    test:assertEquals(check response.getHeader("Location"), "/api/assignments/P%201%2Fa/1");
    json body = check response.getJsonPayload();
    test:assertEquals(check body.hours, 2.50d);
    http:Response read = check send("GET", check response.getHeader("Location"));
    test:assertEquals(read.statusCode, 200);
    test:assertEquals(check read.getJsonPayload(), body);
}

@test:Config {dependsOn: [testCreateOne]}
function testCompositeKeyOperations() returns error? {
    _ = check send("POST", "/api/assignments", {projectCode: "P2", seq: 7, hours: 1, employeeId: 1});
    test:assertEquals((check send("GET", "/api/assignments/P2/7")).statusCode, 200);
    test:assertEquals((check send("GET", "/api/assignments/P2/x")).statusCode, 400);
    http:Response patched = check send("PATCH", "/api/assignments/P2/7", {hours: 3});
    test:assertEquals(check (check patched.getJsonPayload()).hours, 3d);
    test:assertEquals((check send("DELETE", "/api/assignments/P2/7")).statusCode, 200);
    http:Response missing = check send("GET", "/api/assignments/P2/7");
    test:assertEquals((check missing.getJsonPayload()).message,
            "A record with the key 'P2/7' does not exist for the entity 'Assignment'.");
}

@test:Config
function testIsoAndBase64RoundTrip() returns error? {
    http:Response created = check send("POST", "/api/documents", document);
    test:assertEquals(created.statusCode, 201);
    test:assertEquals(check created.getJsonPayload(), document);
    test:assertEquals(check (check send("GET", "/api/documents/D1")).getJsonPayload(), document);
    http:Response patched = check send("PATCH", "/api/documents/D1", {expiresOn: "2025-12-31", opensAt: "08:00:00"});
    json body = check patched.getJsonPayload();
    test:assertEquals(check body.expiresOn, "2025-12-31");
    test:assertEquals(check body.opensAt, "08:00:00");
    test:assertEquals(check body.issuedOn, "2024-01-02");
}

@test:Config {dataProvider: invalidValues}
function testInvalidValue(string 'field, json value, string format) returns error? {
    map<json> body = check document.cloneWithType();
    body["code"] = "BAD";
    body['field] = value;
    http:Response response = check send("POST", "/api/documents", body);
    test:assertEquals(response.statusCode, 400);
    test:assertEquals(check response.getJsonPayload(), {
        code: "INVALID_INPUT",
        message: string `The field '${'field}' of the entity 'Document' is not ${format}.`
    });
    http:Response patched = check send("PATCH", "/api/documents/D1", {['field]: value});
    test:assertEquals(patched.statusCode, value is () ? 200 : 400);
}

function invalidValues() returns [string, json, string][] => [
    ["issuedOn", "2024-13-01", "an ISO 8601 date"],
    ["expiresOn", "02/01/2024", "an ISO 8601 date"],
    ["opensAt", "10:15", "an ISO 8601 time"],
    ["createdAt", "2024-01-02T10:15:30Z", "an ISO 8601 date-time"],
    ["signedAt", "2024-01-02T10:15:30", "an ISO 8601 date-time"],
    ["content", "AAEC+v8", "a base64 string"]
];

@test:Config
function testUserServiceOnTheSameListener() returns error? {
    test:assertEquals(check (check send("GET", "/custom/ping")).getTextPayload(), "pong");
}

function send(string method, string path, json body = ()) returns http:Response|error {
    match method {
        "GET" => {
            return api->get(path);
        }
        "POST" => {
            return api->post(path, body);
        }
        "PATCH" => {
            return api->patch(path, body);
        }
        "PUT" => {
            return api->put(path, body);
        }
    }
    return api->delete(path);
}

function createDepartment(int id, string name) returns error? {
    http:Response response = check send("POST", "/api/departments", {id, name});
    test:assertEquals(response.statusCode, 201);
}

function employee(int id, string name) returns map<json> =>
    {id, name, email: string `${name.toLowerAscii()}@example.com`, hiredOn: "2024-01-02", departmentId: 1};

function withoutEmail(map<json> employee) returns map<json> {
    map<json> copy = employee.clone();
    copy["email"] = ();
    return copy;
}

@test:Config
function testGraphqlServiceOnTheSameListener() returns error? {
    http:Response response = check send("POST", "/graphql", {query: "{ greeting }"});
    test:assertEquals(check response.getJsonPayload(), {data: {greeting: "hello"}});
}

@test:Config
function testMcpServiceOnTheSameListener() returns error? {
    http:Response response = check api->post("/mcp", {
        jsonrpc: "2.0",
        id: 1,
        method: "initialize",
        params: {protocolVersion: "2025-03-26", capabilities: {}, clientInfo: {name: "test", version: "1.0.0"}}
    }, {"Accept": "application/json, text/event-stream"});
    test:assertEquals(response.statusCode, 200);
    test:assertTrue((check response.getTextPayload()).includes("\"name\":\"custom\""));
}

@test:Config
function testBindingErrorNamesEveryField() returns error? {
    http:Response response = check send("POST", "/api/employees", {id: 60, name: 1});
    test:assertEquals(response.statusCode, 400);
    string message = check (check response.getJsonPayload()).message;
    foreach string 'field in ["name", "hiredOn", "departmentId"] {
        test:assertTrue(message.includes("'" + 'field + "'"), message);
    }
}
