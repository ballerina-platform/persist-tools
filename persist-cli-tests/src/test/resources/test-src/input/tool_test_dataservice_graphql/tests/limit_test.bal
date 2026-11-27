import ballerina/io;
import ballerina/test;

@test:Config
function testDepthLimit() returns error? {
    // __schema, types, fields and type, eleven ofType and name: a depth of 16.
    string nested = "name";
    foreach int i in 0 ..< 11 {
        nested = "ofType { " + nested + " }";
    }
    json response = check query("query Deep { __schema { types { fields { type { " + nested + " } } } } }");
    map<json> body = check response.ensureType();
    test:assertFalse(body.hasKey("data"), body.toJsonString());
    test:assertEquals((check errorsOf(body))[0].message,
            "Query \"Deep\" has depth of 16, which exceeds max depth of 15");
}

// A to-many relation field counts 20 and any other field 1, so five to-many fields go beyond 100.
@test:Config
function testComplexityLimit() returns error? {
    string toMany = "employees { items { id } } tickets { items { id } }";
    json response = check query("query Wide { a: department(id: 1) { " + toMany + " } b: department(id: 2) { " +
            toMany + " } c: department(id: 1) { employees { items { id } } } }");
    map<json> body = check response.ensureType();
    test:assertFalse(body.hasKey("data"), body.toJsonString());
    string message = (check (check errorsOf(body))[0].message).toString();
    test:assertTrue(message.startsWith("The operation Wide exceeds the maximum query complexity threshold. " +
            "Maximum allowed complexity: 100, actual complexity: "), message);
}

@test:Config
function testFourToManyFieldsAreWithinTheLimit() returns error? {
    json response = check query("{ a: department(id: 1) { employees { items { id } } tickets { items { id } } } " +
            "b: department(id: 2) { employees { items { id } } tickets { items { id } } } }");
    map<json> body = check response.ensureType();
    test:assertFalse(body.hasKey("errors"), body.toJsonString());
}

@test:Config
function testGraphiqlIntrospectionIsWithinTheLimits() returns error? {
    json response = check query(check io:fileReadString("tests/resources/introspection.graphql"));
    map<json> body = check response.ensureType();
    test:assertFalse(body.hasKey("errors"), body.toJsonString());
}

@test:Config
function testConstraintOnCreate() returns error? {
    json response = check query("mutation { createTicket(input: {id: 70, title: \"A title well beyond twenty\", " +
            "openedOn: \"2024-03-01\", departmentId: 1}) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The field 'title' of the entity 'Ticket' breaks its 'maxLength' constraint.",
            path: ["createTicket"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

@test:Config
function testConstraintOnUpdate() returns error? {
    json response = check query("mutation { updateNote(region: \"z\", seq: 1, input: {text: \"far too long\"}) }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The field 'text' of the entity 'Note' breaks its 'maxLength' constraint.",
            path: ["updateNote"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}
