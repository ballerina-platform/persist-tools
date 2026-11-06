import ballerina/http;
import ballerina/test;

@test:Config
function testCreateTeams() returns error? {
    check created("/api/teams", [{id: 1, name: "Core"}, {id: 2, name: "Empty"}]);
    check created("/api/members", [
        {code: "A", seq: 1, name: "Ann", teamId: 1},
        {code: "B", seq: 1, name: "Bob", teamId: 1},
        {code: "C", seq: 2, name: "Cy", teamId: 1}
    ]);
    check created("/api/tasks", [
        {id: 1, title: "Plan", due: "2024-01-05", memberCode: "A", memberSeq: 1},
        {id: 2, title: "Build", due: "2024-02-05", memberCode: "A", memberSeq: 1},
        {id: 3, title: "Test", due: "2024-03-05", memberCode: "C", memberSeq: 2}
    ]);
    check created("/api/charters", [{id: 1, text: "Ship it", teamId: 1}]);
}

@test:Config {dependsOn: [testCreateTeams]}
function testGetRelatedToOne() returns error? {
    test:assertEquals(check get("/api/members/A/1/team"), {id: 1, name: "Core"});
    test:assertEquals(check get("/api/members/A/1/team?fields=name"), {id: 1, name: "Core"});
    test:assertEquals(check get("/api/tasks/3/member"), {code: "C", seq: 2, name: "Cy", teamId: 1});
    test:assertEquals(check get("/api/teams/1/charter"), {id: 1, text: "Ship it", teamId: 1});
}

@test:Config {dependsOn: [testCreateTeams]}
function testGetRelatedToOneMissing() returns error? {
    http:Response response = check send("GET", "/api/teams/2/charter");
    test:assertEquals(response.statusCode, 404);
    test:assertEquals(check response.getJsonPayload(), {
        code: "NOT_FOUND",
        message: "The record with the key '2' of the entity 'Team' has no 'charter'."
    });
    response = check send("GET", "/api/members/Z/9/team");
    test:assertEquals(response.statusCode, 404);
    test:assertEquals(check response.getJsonPayload(), {
        code: "NOT_FOUND",
        message: "A record with the key 'Z/9' does not exist for the entity 'Member'."
    });
}

@test:Config {dependsOn: [testCreateTeams]}
function testGetRelatedToMany() returns error? {
    json body = check get("/api/teams/1/members?sort=-name&size=2");
    test:assertEquals(body, {
        items: [{code: "C", seq: 2, name: "Cy", teamId: 1}, {code: "B", seq: 1, name: "Bob", teamId: 1}],
        hasMore: true
    });
    test:assertEquals(check get("/api/members/A/1/tasks?fields=due"), {
        items: [{id: 1, due: "2024-01-05"}, {id: 2, due: "2024-02-05"}],
        hasMore: false
    });
    test:assertEquals(check get("/api/teams/2/members"), {items: [], hasMore: false});
    http:Response response = check send("GET", "/api/teams/9/members");
    test:assertEquals(response.statusCode, 404);
    check assertInvalid("/api/teams/1/members?sort=salary",
            "The field 'salary' of the entity 'Member' cannot be used in 'sort'.");
    check assertInvalid("/api/teams/1/members?fields=tasks",
            "The field 'tasks' of the entity 'Member' cannot be used in 'fields'.");
}

@test:Config {dependsOn: [testCreateTeams]}
function testRelationsInFields() returns error? {
    test:assertEquals(check get("/api/members?fields=name,team.name&size=2"), {
        items: [
            {code: "A", seq: 1, name: "Ann", team: {id: 1, name: "Core"}},
            {code: "B", seq: 1, name: "Bob", team: {id: 1, name: "Core"}}
        ],
        hasMore: true
    });
    test:assertEquals(check get("/api/teams?fields=charter"), {
        items: [{id: 1, charter: {id: 1, text: "Ship it", teamId: 1}}, {id: 2, charter: ()}],
        hasMore: false
    });
    test:assertEquals(check get("/api/members/A/1?fields=tasks.title"), {
        code: "A",
        seq: 1,
        tasks: {items: [{id: 1, title: "Plan"}, {id: 2, title: "Build"}], hasMore: false}
    });
    test:assertEquals(check get("/api/teams/1?fields=members.name,members"), {
        id: 1,
        members: {
            items: [
                {code: "A", seq: 1, name: "Ann", teamId: 1},
                {code: "B", seq: 1, name: "Bob", teamId: 1},
                {code: "C", seq: 2, name: "Cy", teamId: 1}
            ],
            hasMore: false
        }
    });
    check assertInvalid("/api/members?fields=team.members",
            "The field 'team.members' of the entity 'Member' cannot be used in 'fields'.");
}

@test:Config {dependsOn: [testGetRelatedToMany, testRelationsInFields]}
function testIncludedToManyIsCapped() returns error? {
    check created("/api/members", [
        {code: "D", seq: 1, name: "Di", teamId: 2},
        {code: "E", seq: 1, name: "Ed", teamId: 2},
        {code: "F", seq: 1, name: "Flo", teamId: 2},
        {code: "G", seq: 1, name: "Gus", teamId: 2},
        {code: "H", seq: 1, name: "Hal", teamId: 2}
    ]);
    check created("/api/members", [{code: "I", seq: 1, name: "Ivy", teamId: 2}]);
    json body = check get("/api/teams/2?fields=members.name");
    json[] items = <json[]>check body.members.items;
    test:assertEquals(items.length(), 5);
    test:assertEquals(check body.members.hasMore, true);
}

function created(string path, json body) returns error? {
    http:Response response = check send("POST", path, body);
    test:assertEquals(response.statusCode, 201, check response.getTextPayload());
}
