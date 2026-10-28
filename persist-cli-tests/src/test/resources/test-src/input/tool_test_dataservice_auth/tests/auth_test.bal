import ballerina/http;
import ballerina/test;

const BASE = "http://localhost:9592";

final http:Client anonymous = check new (BASE);
final http:Client reader = check new (BASE, auth = {username: "reader", password: "reader"});
final http:Client orgReader = check new (BASE, auth = {username: "org", password: "org"});
final http:Client writer = check new (BASE, auth = {username: "writer", password: "writer"});

@test:Config
function testNoCredentials() returns error? {
    http:Response response = check anonymous->get("/api/employees");
    test:assertEquals(response.statusCode, 401);
}

@test:Config
function testEmptyScopesNeedOnlyAuthentication() returns error? {
    http:Response response = check reader->post("/api/departments", {id: 1, name: "Research"});
    test:assertEquals(response.statusCode, 201);
}

@test:Config {dependsOn: [testEmptyScopesNeedOnlyAuthentication]}
function testDefaultScope() returns error? {
    json employee = {id: 1, name: "Ann", departmentId: 1};
    http:Response response = check reader->post("/api/employees", employee);
    test:assertEquals(response.statusCode, 403);
    response = check writer->post("/api/employees", employee);
    test:assertEquals(response.statusCode, 201);
}

@test:Config {dependsOn: [testDefaultScope]}
function testListedScope() returns error? {
    http:Response response = check writer->get("/api/employees");
    test:assertEquals(response.statusCode, 403);
    response = check reader->get("/api/employees");
    test:assertEquals(response.statusCode, 200);
    response = check reader->delete("/api/employees/1");
    test:assertEquals(response.statusCode, 403);
}

@test:Config {dependsOn: [testDefaultScope]}
function testCoveredRelation() returns error? {
    http:Response response = check reader->get("/api/employees/1/department");
    test:assertEquals(check response.getJsonPayload(), {id: 1, name: "Research"});
    response = check reader->get("/api/employees?fields=name,department.name");
    test:assertEquals(check response.getJsonPayload(), {
        items: [{id: 1, name: "Ann", department: {id: 1, name: "Research"}}],
        hasMore: false
    });
}

@test:Config {dependsOn: [testDefaultScope]}
function testRefusedRelation() returns error? {
    json forbidden = {
        code: "FORBIDDEN",
        message: "The relation 'employees' of the entity 'Department' needs 'read' scopes that the entity's " +
            "'read' scopes do not cover."
    };
    foreach string path in ["/api/departments/1/employees", "/api/departments?fields=employees.name",
            "/api/departments/1?fields=employees"] {
        http:Response response = check orgReader->get(path);
        test:assertEquals(response.statusCode, 403, path);
        test:assertEquals(check response.getJsonPayload(), forbidden, path);
    }
    http:Response response = check orgReader->get("/api/departments/1?fields=name");
    test:assertEquals(check response.getJsonPayload(), {id: 1, name: "Research"});
}

@test:Config
function testNoIntrospectionByDefault() returns error? {
    http:Response response = check reader->get("/api/openapi-doc-dygixywsw");
    test:assertNotEquals(response.statusCode, 200);
}
