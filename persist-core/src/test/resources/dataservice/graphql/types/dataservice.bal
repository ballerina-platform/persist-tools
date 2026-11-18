// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/graphql;
import ballerina/persist;

# The persist client of the data service.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener graphql:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@graphql:ServiceConfig {
    graphiql: {enabled: true},
    cors: dataserviceConfig.serviceConfig.cors,
    maxQueryDepth: dataserviceConfig.serviceConfig.maxQueryDepth,
    queryComplexityConfig: {maxComplexity: dataserviceConfig.serviceConfig.maxQueryComplexity},
    validation: false
}
service graphql:Service /graphql on dataserviceListener {

    # Checks the query limits, so that a value out of range stops the program.
    #
    # + return - An error naming the limit that is out of range
    function init() returns error? {
        check dsGraphql:checkLimits(dataserviceConfig.serviceConfig);
    }

    # Gets a page of readings.
    #
    # A reading, with a field of each type.
    #
    # + filter - The conditions that the readings match
    # + sort - The order of the readings; each entry names one field
    # + size - The number of readings in the page
    # + offset - The number of readings to skip
    # + return - A page of readings, or the error
    resource function get readings(ReadingFilter? filter = (), ReadingSort[]? sort = (),
            int? size = (), int? offset = ()) returns ReadingList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceReadingEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Reading, persist:Error?> rows = dataserviceClient->/readings(Reading,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Reading[]|persist:Error items = from Reading item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Reading");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Reading item in items
                select new ReadingObject(item), hasMore};
    }

    # Gets a reading by its key.
    #
    # A reading, with a field of each type.
    #
    # + id - The id of the reading
    # + return - The reading, or the error
    resource function get reading(int id) returns ReadingObject?|error {
        Reading|persist:Error item = dataserviceClient->/readings/[id];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Reading", [id]);
        }
        return new (item);
    }

    # Creates a reading.
    #
    # A reading, with a field of each type.
    #
    # + input - The reading to create
    # + return - The created reading, or the error
    remote function createReading(ReadingInsertInput input) returns ReadingObject?|error =>
        (check dataserviceCreateReading([input]))[0];

    # Creates readings.
    #
    # A reading, with a field of each type.
    #
    # + input - The readings to create
    # + return - The created readings, or the error
    remote function createReadings(ReadingInsertInput[] input) returns ReadingList?|error =>
        {items: check dataserviceCreateReading(input), hasMore: false};

    # Updates a reading by its key.
    #
    # A reading, with a field of each type.
    #
    # + id - The id of the reading
    # + input - The fields to change
    # + return - The updated reading, `()` when it cannot be returned, or the error
    remote function updateReading(int id, ReadingUpdateInput input)
            returns ReadingObject?|error {
        ReadingUpdate value = check transformInputToReadingUpdate(input);
        Reading|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/readings/[id];
        } else {
            item = dataserviceClient->/readings/[id].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Reading", [id]);
        }
        return new (item);
    }

    # Deletes a reading by its key.
    #
    # A reading, with a field of each type.
    #
    # + id - The id of the reading
    # + return - The deleted reading, or the error
    remote function deleteReading(int id) returns ReadingObject?|error {
        Reading|persist:Error item = dataserviceClient->/readings/[id].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Reading", [id]);
        }
        return new (item);
    }

    # Gets a page of measures.
    #
    # A measure, keyed by a float.
    #
    # + filter - The conditions that the measures match
    # + sort - The order of the measures; each entry names one field
    # + size - The number of measures in the page
    # + offset - The number of measures to skip
    # + return - A page of measures, or the error
    resource function get measures(MeasureFilter? filter = (), MeasureSort[]? sort = (),
            int? size = (), int? offset = ()) returns MeasureList?|error {
        dataservice:Query query = check dsGraphql:toQuery(dataserviceMeasureEntity, dataservicePageConfig,
                filter, sort, size, offset);
        stream<Measure, persist:Error?> rows = dataserviceClient->/measures(Measure,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Measure[]|persist:Error items = from Measure item in rows
            select item;
        if items is persist:Error {
            return dsGraphql:toListError(items, "Measure");
        }
        boolean hasMore = dataservice:trimToPage(items, query);
        return {items: from Measure item in items
                select new MeasureObject(item), hasMore};
    }

    # Gets a measure by its key.
    #
    # A measure, keyed by a float.
    #
    # + weight - The weight of the measure
    # + return - The measure, or the error
    resource function get measure(float weight) returns MeasureObject?|error {
        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey("Measure", [weight]);
        if invalidKey is dataservice:DataserviceError {
            return dsGraphql:toError(invalidKey);
        }
        Measure|persist:Error item = dataserviceClient->/measures/[weight];
        if item is persist:Error {
            return dsGraphql:toGetError(item, "Measure", [weight]);
        }
        return new (item);
    }

    # Updates a measure by its key.
    #
    # A measure, keyed by a float.
    #
    # + weight - The weight of the measure
    # + input - The fields to change
    # + return - The updated measure, `()` when it cannot be returned, or the error
    remote function updateMeasure(float weight, MeasureUpdateInput input)
            returns MeasureObject?|error {
        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey("Measure", [weight]);
        if invalidKey is dataservice:DataserviceError {
            return dsGraphql:toError(invalidKey);
        }
        MeasureUpdate value = check transformInputToMeasureUpdate(input);
        Measure|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/measures/[weight];
        } else {
            item = dataserviceClient->/measures/[weight].put(value);
        }
        if item is persist:Error {
            return dsGraphql:toUpdateError(item, "Measure", [weight]);
        }
        return new (item);
    }

    # Deletes a measure by its key.
    #
    # A measure, keyed by a float.
    #
    # + weight - The weight of the measure
    # + return - The deleted measure, or the error
    remote function deleteMeasure(float weight) returns MeasureObject?|error {
        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey("Measure", [weight]);
        if invalidKey is dataservice:DataserviceError {
            return dsGraphql:toError(invalidKey);
        }
        Measure|persist:Error item = dataserviceClient->/measures/[weight].delete();
        if item is persist:Error {
            return dsGraphql:toDeleteError(item, "Measure", [weight]);
        }
        return new (item);
    }
}

isolated function dataserviceCreateReading(ReadingInsertInput[] input) returns ReadingObject[]|error {
    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), dataservicePageConfig);
    if tooMany is dataservice:DataserviceError {
        return dsGraphql:toError(tooMany);
    }
    if input.length() == 0 {
        return [];
    }
    ReadingInsert[] values = [];
    foreach ReadingInsertInput entry in input {
        values.push(check transformInputToReadingInsert(entry));
    }
    do {
        transaction {
            int[] keys = check dataserviceClient->/readings.post(values);
            dataservice:KeyValue[][] keyValues = from int key in keys
                select [key];
            dataservice:Query query = dataservice:toKeysQuery(dataserviceReadingEntity, keyValues);
            stream<Reading, persist:Error?> stored = dataserviceClient->/readings(Reading,
                whereClause = query.whereClause, orderByClause = query.orderByClause
            );
            Reading[] rows = check from Reading item in stored
                select item;
            check dataservice:orderByKeys(rows, dataserviceReadingEntity, keyValues);
            check commit;
            return from Reading item in rows
                select new ReadingObject(item);
        }
    } on fail error err {
        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, "Reading");
    }
}
