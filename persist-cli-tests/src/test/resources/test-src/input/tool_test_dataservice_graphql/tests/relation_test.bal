import ballerina/test;

@test:Config
function testToOne() returns error? {
    json response = check query("{ assignments { items { seq employee { id name } } } }");
    test:assertEquals(response, {
        data: {assignments: {items: [{seq: 1, employee: {id: 1, name: "Ann"}}, {seq: 2, employee: {id: 3, name: "Cid"}}]}}
    });
}

@test:Config
function testToOneWithACompositeForeignKey() returns error? {
    json response = check query("{ timesheets { items { id assignment { projectCode seq hours } } } }");
    test:assertEquals(response, {
        data: {
            timesheets: {
                items: [
                    {id: 1, assignment: {projectCode: "ATLAS", seq: 2, hours: 4d}},
                    {id: 2, assignment: {projectCode: "ATLAS", seq: 1, hours: 12.50d}}
                ]
            }
        }
    });
}

@test:Config
function testOneToOneFromTheSideWithoutTheForeignKey() returns error? {
    json response = check query("{ employees(size: 3) { items { id badge { code } } } }");
    // An employee without a badge has a null badge and no error.
    test:assertEquals(response, {
        data: {employees: {items: [{id: 1, badge: {code: "B-1"}}, {id: 2, badge: ()}, {id: 3, badge: ()}]}}
    });
}

@test:Config
function testToManyWithArguments() returns error? {
    json response = check query("{ department(id: 1) { name employees(sort: [{name: DESC}], size: 1) " +
            "{ items { id name } hasMore } } }");
    test:assertEquals(response, {
        data: {department: {name: "Research", employees: {items: [{id: 3, name: "Cid"}], hasMore: true}}}
    });
}

@test:Config
function testToManyWithAFilter() returns error? {
    json response = check query("query($hours: Decimal) { employees(size: 3) { items { id " +
            "assignments(filter: {hours: {gt: $hours}}) { items { seq } hasMore } } } }", {hours: 4.5});
    test:assertEquals(response, {
        data: {
            employees: {
                items: [
                    {id: 1, assignments: {items: [{seq: 1}], hasMore: false}},
                    {id: 2, assignments: {items: [], hasMore: false}},
                    {id: 3, assignments: {items: [], hasMore: false}}
                ]
            }
        }
    });
}

@test:Config
function testInvalidRelationArgumentNullsOnlyTheField() returns error? {
    json response = check query("{ department(id: 1) { name employees(size: 0) { hasMore } } }");
    test:assertEquals(check response.data, {department: {name: "Research", employees: ()}});
    test:assertEquals(check errorsOf(response), [
        {
            message: "The page size must be at least 1.",
            path: ["department", "employees"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

@test:Config
function testRelationsAreOneHop() returns error? {
    json response = check query("{ assignments { items { employee { assignments { items { seq } } } } } }");
    map<json> body = check response.ensureType();
    test:assertFalse(body.hasKey("data"));
    test:assertEquals(check response.errors, [
        {
            message: "Cannot query field \"assignments\" on type \"EmployeeRelatedObject\".",
            locations: [{line: 1, column: 36}]
        }
    ]);
}

@test:Config
function testRelationInAFragment() returns error? {
    json response = check query("{ assignments { items { ...withEmployee } } } " +
            "fragment withEmployee on AssignmentObject { seq employee { name } }");
    test:assertEquals(response, {
        data: {assignments: {items: [{seq: 1, employee: {name: "Ann"}}, {seq: 2, employee: {name: "Cid"}}]}}
    });
}
