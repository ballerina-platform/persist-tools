import ballerina/http;
import ballerina/test;

@test:Config {dependsOn: [testCreateOne]}
function testForeignKeyViolationOnUpdate() returns error? {
    http:Response response = check send("PATCH", "/api/employees/1", {departmentId: 99});
    test:assertEquals(response.statusCode, 409);
    test:assertEquals(check response.getJsonPayload(), {
        code: "CONSTRAINT_VIOLATION",
        message: "The operation violates a constraint of the entity 'Employee'."
    });
}

// The persist SQL client raises a plain persist:Error for a foreign key violation on create.
@test:Config {dependsOn: [testCreateOne]}
function testForeignKeyViolationOnCreate() returns error? {
    map<json> dan = employee(40, "Dan");
    dan["departmentId"] = 99;
    http:Response response = check send("POST", "/api/employees", dan);
    test:assertEquals(response.statusCode, 500);
    test:assertEquals(check response.getJsonPayload(), {code: "INTERNAL", message: "An internal error occurred."});
}

@test:Config
function testGeneratedKeyIsReadBack() returns error? {
    http:Response response = check send("POST", "/api/tickets", {subject: "Login fails", openedOn: "2024-01-02"});
    test:assertEquals(response.statusCode, 201);
    json body = check response.getJsonPayload();
    int id = check body.id;
    test:assertEquals(check response.getHeader("Location"), string `/api/tickets/${id}`);
    test:assertEquals(body, {id, subject: "Login fails", openedOn: "2024-01-02"});
}

// Pins persist's H2 behaviour: a TIME column drops the fraction of a second.
@test:Config
function testH2DropsFractionalSeconds() returns error? {
    map<json> fractional = check document.cloneWithType();
    fractional["code"] = "D2";
    fractional["opensAt"] = "10:15:30.5";
    http:Response response = check send("POST", "/api/documents", fractional);
    test:assertEquals(response.statusCode, 201);
    test:assertEquals(check (check response.getJsonPayload()).opensAt, "10:15:30");
}
