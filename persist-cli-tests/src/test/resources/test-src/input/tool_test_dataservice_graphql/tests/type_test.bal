import ballerina/test;

const READING_FIELDS = "id count ratio spare amount active day at localTime instant payload extra status previous";

// A decimal and a 64-bit int are sent as variables, which keep their value.
@test:Config
function testRoundTripOfEveryType() returns error? {
    json response = check query("mutation($reading: ReadingInsertInput!) { createReading(input: $reading) { " +
            READING_FIELDS + " } }", {
        reading: {
            id: 1, count: 9007199254740993, ratio: 0.25, spare: (), amount: 1.50, active: true, day: "2024-01-02",
            at: "10:15:30", localTime: "2024-01-02T10:15:30", instant: "2024-01-02T15:45:30+05:30",
            payload: "AAEC+v8=", extra: (), status: "ACTIVE", previous: "INACTIVE"
        }
    });
    // An offset is converted to UTC.
    test:assertEquals(response, {
        data: {
            createReading: {
                id: 1, count: 9007199254740993, ratio: 0.25d, spare: (), amount: 1.50d, active: true,
                day: "2024-01-02", at: "10:15:30", localTime: "2024-01-02T10:15:30", instant: "2024-01-02T10:15:30Z",
                payload: "AAEC+v8=", extra: (), status: "ACTIVE", previous: "INACTIVE"
            }
        }
    });
}

@test:Config {dependsOn: [testRoundTripOfEveryType]}
function testEnumFilter() returns error? {
    json response = check query("{ active: readings(filter: {status: {eq: ACTIVE}, previous: {isNull: false}}) " +
            "{ items { id } } none: readings(filter: {status: {anyOf: [INACTIVE]}}) { items { id } } }");
    test:assertEquals(response, {data: {active: {items: [{id: 1}]}, none: {items: []}}});
}

@test:Config {dataProvider: invalidValues}
function testValueThatDoesNotParse(string fieldName, json value, string format) returns error? {
    map<json> reading = {
        id: 2, count: 1, ratio: 1.0, amount: 1, active: false, day: "2024-01-02", at: "10:15:30",
        localTime: "2024-01-02T10:15:30", instant: "2024-01-02T10:15:30Z", payload: "AA==", status: "ACTIVE"
    };
    reading[fieldName] = value;
    json response = check query("mutation($reading: ReadingInsertInput!) { createReading(input: $reading) { id } }",
            {reading});
    test:assertEquals(check errorsOf(response), [
        {
            message: string `The field '${fieldName}' of the entity 'Reading' is not ${format}.`,
            path: ["createReading"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

function invalidValues() returns [string, json, string][] => [
    ["day", "2024-13-01", "an ISO 8601 date"],
    ["at", "25:00", "an ISO 8601 time"],
    ["localTime", "2024-01-02 10:15", "an ISO 8601 date-time"],
    ["instant", "yesterday", "an ISO 8601 date-time"],
    ["payload", "not base64!", "a base64 string"]
];

@test:Config
function testFloatThatIsNotFinite() returns error? {
    json response = check query("mutation { createReading(input: {id: 3, count: 1, ratio: 1e400, amount: 1, " +
            "active: true, day: \"2024-01-02\", at: \"10:15:30\", localTime: \"2024-01-02T10:15:30\", " +
            "instant: \"2024-01-02T10:15:30Z\", payload: \"AA==\", status: ACTIVE}) { id } }");
    test:assertEquals(check errorsOf(response), [
        {
            message: "The field 'ratio' of the entity 'Reading' is not a finite number.",
            path: ["createReading"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

@test:Config {dependsOn: [testEnumFilter]}
function testStoredFloatThatIsNotFinite() returns error? {
    _ = check dataserviceClient->executeNativeSQL(`UPDATE "Reading" SET "ratio" = CAST('NaN' AS DOUBLE PRECISION)`);
    json|error response = query("{ reading(id: 1) { id ratio } measures { items { label } } }");
    _ = check dataserviceClient->executeNativeSQL(`UPDATE "Reading" SET "ratio" = 0.25`);
    json body = check response;
    // ratio is non-null, so the error makes the reading null; the other root field keeps its data.
    test:assertEquals(check body.data, {reading: (), measures: {items: [{label: "small"}, {label: "large"}]}});
    test:assertEquals(check errorsOf(body), [
        {
            message: "The field 'ratio' of the entity 'Reading' holds a value that is not a finite number.",
            path: ["reading", "ratio"],
            extensions: {code: "INTERNAL"}
        }
    ]);
}

@test:Config
function testFloatKey() returns error? {
    json response = check query("{ measure(weight: 1.5) { weight label } infinite: measure(weight: 1e400) { label } }");
    test:assertEquals(check response.data, {measure: {weight: 1.5d, label: "small"}, infinite: ()});
    test:assertEquals(check errorsOf(response), [
        {
            message: "The key 'Infinity' of the entity 'Measure' is not a finite number.",
            path: ["infinite"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}

@test:Config {dependsOn: [testFloatKey, testStoredFloatThatIsNotFinite]}
function testFloatKeyOnWrites() returns error? {
    json response = check query("mutation { updateMeasure(weight: 2.5, input: {label: \"big\"}) { label } " +
            "infinite: deleteMeasure(weight: 1e400) { label } }");
    test:assertEquals(check response.data, {updateMeasure: {label: "big"}, infinite: ()});
    test:assertEquals(check errorsOf(response), [
        {
            message: "The key 'Infinity' of the entity 'Measure' is not a finite number.",
            path: ["infinite"],
            extensions: {code: "INVALID_INPUT"}
        }
    ]);
}
