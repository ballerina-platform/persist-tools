import ballerina/http;
import ballerina/test;

@test:Config
function testInternalRelationField() returns error? {
    check created("/api/decks", [{id: 1, label: "A"}, {id: 2, label: "B"}]);
    check created("/api/cards", [{id: 1, title: "Dune", deckId: 1}]);
    test:assertEquals(check get("/api/cards/1"), {id: 1, title: "Dune", deckId: 1});
    test:assertEquals(check get("/api/decks/1/cards"), {
        items: [{id: 1, title: "Dune", deckId: 1}],
        hasMore: false
    });
    http:Response response = check send("GET", "/api/cards/1/deck");
    test:assertEquals(response.statusCode, 404);
    check assertInvalid("/api/cards?fields=deck",
            "The field 'deck' of the entity 'Card' cannot be used in 'fields'.");
    response = check send("PATCH", "/api/cards/1", {deckId: 2});
    test:assertEquals(response.statusCode, 400);
}

@test:Config
function testInternalForeignKey() returns error? {
    check created("/api/bins", [{id: 1, label: "Top"}]);
    check created("/api/items", [{id: 1, name: "Bolt"}]);
    test:assertEquals(check get("/api/items/1"), {id: 1, name: "Bolt"});
    http:Response response = check send("GET", "/api/items/1/bin");
    test:assertEquals(response.statusCode, 404);
    response = check send("GET", "/api/bins/1/items");
    test:assertEquals(response.statusCode, 404);
    response = check send("POST", "/api/items", [{id: 2, name: "Nut", binId: 1}]);
    test:assertEquals(response.statusCode, 400);
}
