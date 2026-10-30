import ballerina/http;
import ballerina/jwt;
import ballerina/test;

const BASE = "http://localhost:9592";

final http:Client anonymous = check new (BASE);
final http:Client reader = check new (BASE, auth = {username: "reader", password: "reader"});
final http:Client orgReader = check new (BASE, auth = {username: "org", password: "org"});
final http:Client writer = check new (BASE, auth = {username: "writer", password: "writer"});
final http:Client hrBearer = check new (BASE, auth = {token: check token(["hr:read"])});
final http:Client orgBearer = check new (BASE, auth = {token: check token(["org:read"])});

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
function testCheckedRelation() returns error? {
    check assertCheckedRelation(reader, orgReader);
}

@test:Config {dependsOn: [testDefaultScope]}
function testCheckedRelationWithJwt() returns error? {
    check assertCheckedRelation(hrBearer, orgBearer);
}

// `allowed` holds a `read` scope of both entities, and `refused` only one of `Department`.
function assertCheckedRelation(http:Client allowed, http:Client refused) returns error? {
    json forbidden = {
        code: "FORBIDDEN",
        message: "The relation 'employees' of the entity 'Department' needs a 'read' scope of the entity 'Employee'."
    };
    http:Response response = check allowed->get("/api/departments/1/employees");
    test:assertEquals(check response.getJsonPayload(), {items: [{id: 1, name: "Ann", departmentId: 1}],
        hasMore: false});
    response = check allowed->get("/api/departments/1?fields=employees.name");
    test:assertEquals(check response.getJsonPayload(), {id: 1, employees: {items: [{id: 1, name: "Ann"}],
        hasMore: false}});
    foreach string path in ["/api/departments/1/employees", "/api/departments?fields=employees.name",
            "/api/departments/1?fields=employees"] {
        response = check refused->get(path);
        test:assertEquals(response.statusCode, 403, path);
        test:assertEquals(check response.getJsonPayload(), forbidden, path);
    }
    response = check refused->get("/api/departments/1?fields=name");
    test:assertEquals(check response.getJsonPayload(), {id: 1, name: "Research"});
}

function token(string[] scopes) returns string|error => jwt:issue({
    issuer: "dataservice",
    username: "caller",
    customClaims: {scope: string:'join(" ", ...scopes)},
    signatureConfig: {algorithm: jwt:HS256, config: "dataservice-test-secret"}
});

// Disabled until ballerina-library#9436: the definition is not embedded when the listener port is a record field.
@test:Config {enable: false}
function testIntrospectionByDefault() returns error? {
    http:Response response = check reader->get("/api/openapi-doc-dygixywsw");
    test:assertEquals(response.statusCode, 200);
}
