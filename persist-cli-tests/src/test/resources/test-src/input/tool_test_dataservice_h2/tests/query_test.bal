import ballerina/http;
import ballerina/test;
import ballerina/url;

@test:Config
function testCreateReturnsRequestOrder() returns error? {
    http:Response response = check send("POST", "/api/products", [
        product(7, "Wrench", 9.99, "TOOL", "2024-04-04", "50 pieces", true),
        product(6, "Notebook", 3.00, "BOOK", "2024-05-05", (), false),
        product(5, "Nails", 1.25, "TOOL", "2024-03-01", "box", true),
        product(4, "Atlas", 30.00, "BOOK", "2023-12-01", (), true),
        product(3, "Hand saw", 20.00, "TOOL", "2024-02-15", "a_b", false)
    ]);
    test:assertEquals(response.statusCode, 201);
    test:assertEquals(ids(check response.getJsonPayload()), [7, 6, 5, 4, 3]);
    response = check send("POST", "/api/products", [
        product(1, "Apple pie", 5.50, "BOOK", "2024-01-10", "50% off", true),
        product(2, "Hammer", 12.00, "TOOL", "2024-03-01", (), true)
    ]);
    test:assertEquals(ids(check response.getJsonPayload()), [1, 2]);
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testPages() returns error? {
    json body = check get("/api/products");
    test:assertEquals(ids(body), [1, 2, 3, 4, 5]);
    test:assertEquals(check body.hasMore, true);
    body = check get("/api/products?offset=5");
    test:assertEquals(ids(body), [6, 7]);
    test:assertEquals(check body.hasMore, false);
    body = check get("/api/products?size=2&offset=1");
    test:assertEquals(ids(body), [2, 3]);
    test:assertEquals(check body.hasMore, true);
    test:assertEquals(ids(check get("/api/products?size=50")), [1, 2, 3, 4, 5]);
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testInvalidPage() returns error? {
    check assertInvalid("/api/products?size=0", "The page size must be at least 1.");
    check assertInvalid("/api/products?offset=-1", "The offset must be at least 0.");
    check assertInvalid("/api/products?offset=9223372036854775806",
            "The offset and the page size together exceed the largest integer.");
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testSort() returns error? {
    test:assertEquals(ids(check get("/api/products?sort=-price")), [4, 3, 2, 7, 1]);
    test:assertEquals(ids(check get("/api/products?sort=launchedOn,-id")), [4, 1, 3, 5, 2]);
    test:assertEquals(ids(check get("/api/products?sort=category&sort=-price")), [4, 1, 6, 3, 2]);
    // Nulls sort last ascending and first descending.
    test:assertEquals(ids(check get("/api/products?sort=note&offset=2")), [3, 5, 2, 4, 6]);
    test:assertEquals(ids(check get("/api/products?sort=-note")), [2, 4, 6, 5, 3]);
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testInvalidSort() returns error? {
    check assertInvalid("/api/products?sort=supplier",
            "The field 'supplier' of the entity 'Product' cannot be used in 'sort'.");
    check assertInvalid("/api/products?sort=name,-name", "The field 'name' is named more than once in 'sort'.");
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testFilter() returns error? {
    test:assertEquals(ids(check filtered({category: {eq: "TOOL"}, price: {gte: 10}})), [2, 3]);
    test:assertEquals(ids(check filtered({name: {startsWith: "Ha"}})), [2, 3]);
    test:assertEquals(ids(check filtered({note: {contains: "%"}})), [1]);
    test:assertEquals(ids(check filtered({note: {contains: "_"}})), [3]);
    test:assertEquals(ids(check filtered({note: {isNull: true}})), [2, 4, 6]);
    test:assertEquals(ids(check filtered({note: {ne: "box"}})), [1, 3, 7]);
    test:assertEquals(ids(check filtered({launchedOn: {anyOf: ["2024-03-01"]}})), [2, 5]);
    test:assertEquals(ids(check filtered({launchedOn: {lt: "2024-01-10"}})), [4]);
    test:assertEquals(ids(check filtered({active: {eq: false}})), [3, 6]);
    test:assertEquals(ids(check filtered({id: {anyOf: []}})), []);
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testInvalidFilter() returns error? {
    check assertInvalid(filterPath({launchedOn: {eq: "2024-3-1"}}),
            "The field 'launchedOn' of the entity 'Product' is not an ISO 8601 date.");
    http:Response response = check send("GET", filterPath({supplier: {eq: "x"}}));
    test:assertEquals(response.statusCode, 400);
    response = check send("GET", filterPath({active: {lt: true}}));
    test:assertEquals(response.statusCode, 400);
}

@test:Config {dependsOn: [testCreateReturnsRequestOrder]}
function testFields() returns error? {
    json body = check get("/api/products?fields=name,note&size=2");
    test:assertEquals(body, {
        items: [{id: 1, name: "Apple pie", note: "50% off"}, {id: 2, name: "Hammer", note: ()}],
        hasMore: true
    });
    test:assertEquals(check get("/api/products/4?fields=launchedOn&fields=launchedOn"),
            {id: 4, launchedOn: "2023-12-01"});
    check assertInvalid("/api/products?fields=", "'fields' must name at least one field.");
    check assertInvalid("/api/products/4?fields=supplier",
            "The field 'supplier' of the entity 'Product' cannot be used in 'fields'.");
}

function product(int id, string name, decimal price, string category, string launchedOn, string? note,
        boolean active) returns map<json> =>
    {id, name, price, category, launchedOn, note, active};

function get(string path) returns json|error {
    http:Response response = check send("GET", path);
    test:assertEquals(response.statusCode, 200, check response.getTextPayload());
    return response.getJsonPayload();
}

function filtered(json filter) returns json|error => get(filterPath(filter));

function filterPath(json filter) returns string =>
    "/api/products?filter=" + checkpanic url:encode(filter.toJsonString(), "UTF-8");

function assertInvalid(string path, string message) returns error? {
    http:Response response = check send("GET", path);
    test:assertEquals(response.statusCode, 400);
    test:assertEquals(check response.getJsonPayload(), {code: "INVALID_INPUT", message});
}

function ids(json body) returns int[] {
    json[] items = <json[]>checkpanic body.items;
    return from json item in items select <int>checkpanic item.id;
}
