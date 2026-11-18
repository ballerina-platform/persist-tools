import ballerina/http;
import ballerina/test;

@test:Config
function testGetMany() returns error? {
    json response = check query("{ departments { items { id name } hasMore } " +
            "employees { items { id name email hiredOn departmentId } hasMore } " +
            "assignments { items { projectCode seq hours employeeId } hasMore } }");
    // The page size is 2, and the records are in key order.
    test:assertEquals(response, {
        data: {
            departments: {items: [{id: 1, name: "Research"}, {id: 2, name: "Sales"}], hasMore: false},
            employees: {
                items: [
                    {id: 1, name: "Ann", email: "ann@example.com", hiredOn: "2021-03-15", departmentId: 1},
                    {id: 2, name: "Bob", email: (), hiredOn: "2022-07-01", departmentId: 2}
                ],
                hasMore: true
            },
            assignments: {
                items: [
                    {projectCode: "ATLAS", seq: 1, hours: 12.50d, employeeId: 1},
                    {projectCode: "ATLAS", seq: 2, hours: 4d, employeeId: 3}
                ],
                hasMore: false
            }
        }
    });
}

@test:Config
function testInternalFieldIsNotInTheSchema() returns error? {
    json response = check query("{ employees { items { department { id } } } }");
    test:assertEquals(check response.errors, [
        {
            message: "Cannot query field \"department\" on type \"EmployeeObject\".",
            locations: [{line: 1, column: 23}]
        }
    ]);
}

@test:Config
function testRootFieldsInModelOrder() returns error? {
    json response = check query("{ __schema { queryType { fields { name } } mutationType { name } } }");
    test:assertEquals(response, {
        data: {
            __schema: {
                queryType: {
                    fields: [
                        {name: "departments"},
                        {name: "department"},
                        {name: "employees"},
                        {name: "employee"},
                        {name: "assignments"},
                        {name: "assignment"}
                    ]
                },
                mutationType: ()
            }
        }
    });
}

@test:Config
function testGraphiql() returns error? {
    http:Response response = check api->get("/graphiql");
    test:assertEquals(response.statusCode, 200);
    test:assertTrue((check response.getTextPayload()).includes("graphiql"));
}
