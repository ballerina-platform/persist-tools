import ballerina/test;

const TICKET_FIELDS = "id title status note openedOn dueAt departmentId";

@test:Config
function testCreateOne() returns error? {
    json response = check query("mutation { createTicket(input: {id: 1, title: \"Printer\", note: null, " +
            "openedOn: \"2024-01-02\", dueAt: \"2024-01-09T17:00:00\", departmentId: 1}) { " + TICKET_FIELDS +
            " department { name } } }");
    // status is ReadOnly, so it takes the value of its annotation; the relation is read under the mutation.
    test:assertEquals(response, {
        data: {
            createTicket: {
                id: 1, title: "Printer", status: "open", note: (), openedOn: "2024-01-02",
                dueAt: "2024-01-09T17:00:00", departmentId: 1, department: {name: "Research"}
            }
        }
    });
}

@test:Config
function testCreateMany() returns error? {
    json response = check query("mutation { createTickets(input: [" +
            "{id: 2, title: \"Laptop\", openedOn: \"2024-02-01\", departmentId: 2}, " +
            "{id: 3, title: \"Desk\", note: \"blue\", openedOn: \"2024-02-02\", departmentId: 1}]) " +
            "{ items { id title note dueAt } hasMore } }");
    // A nullable field left out of the input is created as null.
    test:assertEquals(response, {
        data: {
            createTickets: {
                items: [{id: 2, title: "Laptop", note: (), dueAt: ()}, {id: 3, title: "Desk", note: "blue", dueAt: ()}],
                hasMore: false
            }
        }
    });
}

@test:Config
function testCreateNone() returns error? {
    json response = check query("mutation { createTickets(input: []) { items { id } hasMore } }");
    test:assertEquals(response, {data: {createTickets: {items: [], hasMore: false}}});
}

@test:Config
function testCreateTooMany() returns error? {
    json response = check query("mutation { createNotes(input: [{region: \"a\", seq: 1, text: \"x\"}, " +
            "{region: \"a\", seq: 2, text: \"x\"}, {region: \"a\", seq: 3, text: \"x\"}, " +
            "{region: \"a\", seq: 4, text: \"x\"}]) { items { region } } }");
    test:assertEquals(check errorsOf(response), [
        {message: "A create can hold at most 3 records.", path: ["createNotes"], extensions: {code: "INVALID_INPUT"}}
    ]);
}

@test:Config {dependsOn: [testCreateOne]}
function testCreateIsAtomic() returns error? {
    json response = check query("mutation { createTickets(input: [" +
            "{id: 50, title: \"New\", openedOn: \"2024-03-01\", departmentId: 1}, " +
            "{id: 1, title: \"Duplicate\", openedOn: \"2024-03-01\", departmentId: 1}]) { items { id } } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "A record that conflicts with an existing record of the entity 'Ticket' already exists.",
            path: ["createTickets"],
            extensions: {code: "CONFLICT"}
        }
    ]);
    json created = check query("{ ticket(id: 50) { id } }");
    test:assertEquals(check created.data, {ticket: ()});
}

@test:Config
function testCreateConstraintViolation() returns error? {
    json response = check query("mutation { createTicket(input: {id: 60, title: \"Orphan\", " +
            "openedOn: \"2024-03-01\", departmentId: 42}) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The operation violates a constraint of the entity 'Ticket'.",
            path: ["createTicket"],
            extensions: {code: "CONSTRAINT_VIOLATION"}
        }
    ]);
}

@test:Config
function testCreateWithAValueThatDoesNotParse() returns error? {
    json response = check query("mutation { createTicket(input: {id: 61, title: \"Late\", openedOn: \"01/03/2024\", " +
            "departmentId: 1}) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The field 'openedOn' of the entity 'Ticket' is not an ISO 8601 date.",
            path: ["createTicket"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

@test:Config
function testReadOnlyAndImmutableFieldsAreRejected() returns error? {
    json readOnly = check query("mutation { createTicket(input: {id: 62, title: \"x\", status: \"closed\", " +
            "openedOn: \"2024-03-01\", departmentId: 1}) { id } }");
    json immutable = check query("mutation { updateTicket(id: 1, input: {title: \"y\"}) { id } }");
    foreach [json, string] [response, name] in [[readOnly, "status"], [immutable, "title"]] {
        map<json> body = check response.ensureType();
        test:assertFalse(body.hasKey("data"), body.toJsonString());
        json[] errors = check errorsOf(response);
        test:assertTrue((check errors[0].message).toString().includes(string `"${name}"`), errors.toJsonString());
    }
}

@test:Config {dependsOn: [testGetMany]}
function testCreateWithAGeneratedKey() returns error? {
    json response = check query("mutation { createDepartment(input: {name: \"Support\"}) { id name } }");
    test:assertEquals(response, {data: {createDepartment: {id: 3, name: "Support"}}});
}

@test:Config {dependsOn: [testCreateMany]}
function testUpdateIsPartial() returns error? {
    json response = check query("mutation { updateTicket(id: 3, input: {dueAt: \"2024-05-01T09:30:00\"}) " +
            "{ id title note dueAt } }");
    test:assertEquals(response, {
        data: {updateTicket: {id: 3, title: "Desk", note: "blue", dueAt: "2024-05-01T09:30:00"}}
    });
}

@test:Config {dependsOn: [testUpdateIsPartial]}
function testUpdateSetsNull() returns error? {
    json response = check query("mutation { updateTicket(id: 3, input: {note: null, dueAt: null}) { note dueAt } }");
    test:assertEquals(response, {data: {updateTicket: {note: (), dueAt: ()}}});
}

@test:Config {dependsOn: [testCreateMany]}
function testUpdateNullOnAFieldThatNeedsAValue() returns error? {
    json response = check query("mutation { updateTicket(id: 2, input: {openedOn: null}) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The field 'openedOn' of the entity 'Ticket' cannot be null.",
            path: ["updateTicket"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

@test:Config {dependsOn: [testCreateMany]}
function testEmptyPatchReads() returns error? {
    json response = check query("mutation { updateTicket(id: 2, input: {}) { id title } " +
            "missing: updateTicket(id: 99, input: {}) { id } }");
    test:assertEquals(check response.data, {updateTicket: {id: 2, title: "Laptop"}, missing: ()});
    test:assertEquals(check errorsOf(response), [
        {
            message: "A record with the key '99' does not exist for the entity 'Ticket'.",
            path: ["missing"],
            extensions: {code: "NOT_FOUND"}
        }
    ]);
}

@test:Config
function testUpdateMissing() returns error? {
    json response = check query("mutation { updateTicket(id: 99, input: {note: \"x\"}) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "A record with the key '99' does not exist for the entity 'Ticket'.",
            path: ["updateTicket"],
            extensions: {code: "NOT_FOUND"}
        }
    ]);
}

@test:Config {dependsOn: [testCreateMany, testUpdateNullOnAFieldThatNeedsAValue, testEmptyPatchReads]}
function testDelete() returns error? {
    json response = check query("mutation { deleteTicket(id: 2) { id title } }");
    test:assertEquals(response, {data: {deleteTicket: {id: 2, title: "Laptop"}}});
    json missing = check query("mutation { deleteTicket(id: 2) { id } }");
    test:assertEquals(check errorsOf(missing), [
        {
            message: "A record with the key '2' does not exist for the entity 'Ticket'.",
            path: ["deleteTicket"],
            extensions: {code: "NOT_FOUND"}
        }
    ]);
}

@test:Config
function testDeleteConstraintViolation() returns error? {
    // Employee 1 has assignments.
    json response = check query("mutation { deleteEmployee(id: 1) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The operation violates a constraint of the entity 'Employee'.",
            path: ["deleteEmployee"],
            extensions: {code: "CONSTRAINT_VIOLATION"}
        }
    ]);
}

@test:Config
function testWithoutReadPatchAndDeleteReturnTrue() returns error? {
    json created = check query("mutation { createNote(input: {region: \"b\", seq: 1, text: \"hello\"}) " +
            "{ region seq text } }");
    test:assertEquals(created, {data: {createNote: {region: "b", seq: 1, text: "hello"}}});
    json updated = check query("mutation { updateNote(region: \"b\", seq: 1, input: {text: \"bye\"}) " +
            "empty: updateNote(region: \"b\", seq: 1, input: {}) }");
    test:assertEquals(updated, {data: {updateNote: true, empty: true}});
    json deleted = check query("mutation { deleteNote(region: \"b\", seq: 1) }");
    test:assertEquals(deleted, {data: {deleteNote: true}});
    json missing = check query("mutation { deleteNote(region: \"b\", seq: 1) }");
    test:assertEquals(check errorsOf(missing), [
        {
            message: "A record with the key 'b/1' does not exist for the entity 'Note'.",
            path: ["deleteNote"],
            extensions: {code: "NOT_FOUND"}
        }
    ]);
}

@test:Config
function testMutationsRunInOrderEachInItsOwnTransaction() returns error? {
    json response = check query("mutation { first: createNote(input: {region: \"c\", seq: 1, text: \"1\"}) { seq } " +
            "again: createNote(input: {region: \"c\", seq: 1, text: \"2\"}) { seq } " +
            "second: createNote(input: {region: \"c\", seq: 2, text: \"3\"}) { seq } }");
    test:assertEquals(check response.data, {first: {seq: 1}, again: (), second: {seq: 2}});
    test:assertEquals(check errorsOf(response), [
        {
            message: "A record that conflicts with an existing record of the entity 'Note' already exists.",
            path: ["again"],
            extensions: {code: "CONFLICT"}
        }
    ]);
}
