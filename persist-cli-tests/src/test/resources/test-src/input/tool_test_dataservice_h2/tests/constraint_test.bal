import ballerina/http;
import ballerina/test;

@test:Config
function testConstraintOnCreate() returns error? {
    http:Response response = check send("POST", "/api/badges", {id: 98, label: "A"});
    test:assertEquals(response.statusCode, 400);
    json body = check response.getJsonPayload();
    test:assertEquals(body.code, "INVALID_INPUT");
}

@test:Config
function testConstraintOnPatch() returns error? {
    http:Response response = check send("PATCH", "/api/badges/99", {label: "A"});
    test:assertEquals(response.statusCode, 400);
}

@test:Config
function testConstraintOnConvertedField() returns error? {
    http:Response response = check send("POST", "/api/products",
            product(91, "Pen", 1, "BOOK", "2999-01-01", (), true));
    test:assertEquals(response.statusCode, 400);
    test:assertEquals(check response.getJsonPayload(), {
        code: "INVALID_INPUT",
        message: "The field 'launchedOn' of the entity 'Product' breaks its 'pastDate' constraint."
    });
    response = check send("GET", "/api/products/91");
    test:assertEquals(response.statusCode, 404);
}
