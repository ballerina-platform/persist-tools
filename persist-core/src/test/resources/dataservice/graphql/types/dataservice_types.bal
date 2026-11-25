// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;
import ballerina/time;

# A reading, with a field of each type.
public isolated service class ReadingObject {
    private final readonly & Reading row;

    isolated function init(Reading row) {
        self.row = row.cloneReadOnly();
    }

    # The `id` field of `Reading`
    # + return - The value
    isolated resource function get id() returns int => self.row.id;

    # The `count` field of `Reading`
    # + return - The value
    isolated resource function get count() returns int => self.row.count;

    # The `ratio` field of `Reading`
    # + return - The value
    isolated resource function get ratio() returns float|error => dsGraphql:toFinite(self.row.ratio, "Reading", "ratio");

    # The `spare` field of `Reading`
    # + return - The value
    isolated resource function get spare() returns float?|error {
        float? value = self.row.spare;
        return value is () ? () : check dsGraphql:toFinite(value, "Reading", "spare");
    }

    # The `amount` field of `Reading`
    # + return - The value
    isolated resource function get amount() returns decimal => self.row.amount;

    # The `active` field of `Reading`
    # + return - The value
    isolated resource function get active() returns boolean => self.row.active;

    # The `day` field of `Reading`, as an ISO 8601 date
    # + return - The value
    isolated resource function get day() returns string => dataservice:dateToString(self.row.day);

    # The `at` field of `Reading`, as an ISO 8601 time
    # + return - The value
    isolated resource function get at() returns string => dataservice:timeOfDayToString(self.row.at);

    # The `localTime` field of `Reading`, as an ISO 8601 date-time without an offset
    # + return - The value
    isolated resource function get localTime() returns string => dataservice:civilToString(self.row.localTime);

    # The `instant` field of `Reading`, as an ISO 8601 date-time in UTC
    # + return - The value
    isolated resource function get instant() returns string => dataservice:utcToString(self.row.instant);

    # The `endDay` field of `Reading`, as an ISO 8601 date
    # + return - The value
    isolated resource function get endDay() returns string? {
        time:Date? value = self.row.endDay;
        return value is () ? () : dataservice:dateToString(value);
    }

    # The `payload` field of `Reading`, as a base64 string
    # + return - The value
    isolated resource function get payload() returns string => dataservice:bytesToString(self.row.payload);

    # The `extra` field of `Reading`, as a base64 string
    # + return - The value
    isolated resource function get extra() returns string? {
        byte[]? value = self.row.extra;
        return value is () ? () : dataservice:bytesToString(value);
    }

    # The `status` field of `Reading`
    # + return - The value
    isolated resource function get status() returns Status => self.row.status;

    # The `previous` field of `Reading`
    # + return - The value
    isolated resource function get previous() returns Status? => self.row.previous;
}

# A page of readings.
#
# + items - The readings
# + hasMore - Whether records exist after this page
public type ReadingList record {
    ReadingObject[] items;
    boolean hasMore;
};

# The conditions that readings match; every condition set must hold.
public type ReadingFilter record {|
    # The conditions on `id`
    dsGraphql:IntFilter? id;
    # The conditions on `count`
    dsGraphql:IntFilter? count;
    # The conditions on `ratio`
    dsGraphql:FloatFilter? ratio;
    # The conditions on `spare`
    dsGraphql:FloatNullableFilter? spare;
    # The conditions on `amount`
    dsGraphql:DecimalFilter? amount;
    # The conditions on `active`
    dsGraphql:BooleanFilter? active;
    # The conditions on `day`
    dsGraphql:DateFilter? day;
    # The conditions on `at`
    dsGraphql:TimeOfDayFilter? at;
    # The conditions on `localTime`
    dsGraphql:CivilFilter? localTime;
    # The conditions on `instant`
    dsGraphql:UtcFilter? instant;
    # The conditions on `endDay`
    dsGraphql:DateNullableFilter? endDay;
    # The conditions on `status`
    StatusFilter? status;
    # The conditions on `previous`
    StatusNullableFilter? previous;
|};

# One entry of the order of readings; set exactly one field.
public type ReadingSort record {|
    # Sorts by `id`
    dsGraphql:SortDirection? id;
    # Sorts by `count`
    dsGraphql:SortDirection? count;
    # Sorts by `ratio`
    dsGraphql:SortDirection? ratio;
    # Sorts by `spare`
    dsGraphql:SortDirection? spare;
    # Sorts by `amount`
    dsGraphql:SortDirection? amount;
    # Sorts by `active`
    dsGraphql:SortDirection? active;
    # Sorts by `day`
    dsGraphql:SortDirection? day;
    # Sorts by `at`
    dsGraphql:SortDirection? at;
    # Sorts by `localTime`
    dsGraphql:SortDirection? localTime;
    # Sorts by `instant`
    dsGraphql:SortDirection? instant;
    # Sorts by `endDay`
    dsGraphql:SortDirection? endDay;
    # Sorts by `status`
    dsGraphql:SortDirection? status;
    # Sorts by `previous`
    dsGraphql:SortDirection? previous;
|};

# The fields to create a reading with.
public type ReadingInsertInput record {|
    # The `id` field of `Reading`
    int id;
    # The `count` field of `Reading`
    int count;
    # The `ratio` field of `Reading`
    float ratio;
    # The `spare` field of `Reading`
    float? spare;
    # The `amount` field of `Reading`
    decimal amount;
    # The `active` field of `Reading`
    boolean active;
    # The `day` field of `Reading`, as an ISO 8601 date
    string day;
    # The `at` field of `Reading`, as an ISO 8601 time
    string at;
    # The `localTime` field of `Reading`, as an ISO 8601 date-time without an offset
    string localTime;
    # The `instant` field of `Reading`, as an ISO 8601 date-time in UTC
    string instant;
    # The `endDay` field of `Reading`, as an ISO 8601 date
    string? endDay;
    # The `payload` field of `Reading`, as a base64 string
    string payload;
    # The `extra` field of `Reading`, as a base64 string
    string? extra;
    # The `status` field of `Reading`
    Status status;
    # The `previous` field of `Reading`
    Status? previous;
|};

# The fields to change in a reading; a field left out or set to `null` is not changed.
public type ReadingUpdateInput record {|
    # The `count` field of `Reading`
    int? count;
    # The `ratio` field of `Reading`
    float? ratio;
    # The `spare` field of `Reading`
    float? spare;
    # The `amount` field of `Reading`
    decimal? amount;
    # The `active` field of `Reading`
    boolean? active;
    # The `day` field of `Reading`, as an ISO 8601 date
    string? day;
    # The `at` field of `Reading`, as an ISO 8601 time
    string? at;
    # The `localTime` field of `Reading`, as an ISO 8601 date-time without an offset
    string? localTime;
    # The `instant` field of `Reading`, as an ISO 8601 date-time in UTC
    string? instant;
    # The `endDay` field of `Reading`, as an ISO 8601 date
    string? endDay;
    # The `payload` field of `Reading`, as a base64 string
    string? payload;
    # The `extra` field of `Reading`, as a base64 string
    string? extra;
    # The `status` field of `Reading`
    Status? status;
    # The `previous` field of `Reading`
    Status? previous;
    # The fields to set to `null`
    ReadingUnset? unset;
|};

# The nullable fields to set to `null` in a reading.
public type ReadingUnset record {|
    # `true` to set `spare` to `null`
    boolean? spare;
    # `true` to set `endDay` to `null`
    boolean? endDay;
    # `true` to set `extra` to `null`
    boolean? extra;
    # `true` to set `previous` to `null`
    boolean? previous;
|};

# The fields of `Reading` that callers can see.
final readonly & dataservice:Field[] dataserviceReadingFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "count", column: "count", 'type: dataservice:INT},
    {name: "ratio", column: "ratio", 'type: dataservice:FLOAT},
    {name: "spare", column: "spare", 'type: dataservice:FLOAT, nullable: true},
    {name: "amount", column: "amount", 'type: dataservice:DECIMAL},
    {name: "active", column: "active", 'type: dataservice:BOOLEAN},
    {name: "day", column: "day", 'type: dataservice:DATE},
    {name: "at", column: "at", 'type: dataservice:TIME_OF_DAY},
    {name: "localTime", column: "localTime", 'type: dataservice:CIVIL},
    {name: "instant", column: "instant", 'type: dataservice:UTC},
    {name: "endDay", column: "endDay", 'type: dataservice:DATE, nullable: true},
    {name: "payload", column: "payload", 'type: dataservice:BYTES},
    {name: "extra", column: "extra", 'type: dataservice:BYTES, nullable: true},
    {name: "status", column: "status", 'type: dataservice:ENUM},
    {name: "previous", column: "previous", 'type: dataservice:ENUM, nullable: true}
];

# The description of `Reading` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadingEntity = {
    name: "Reading",
    collection: "readings",
    'resource: "readings",
    datastore: dataservice:H2,
    fields: dataserviceReadingFields
};

isolated function transformInputToReadingInsert(ReadingInsertInput value) returns ReadingInsert|dsGraphql:Error {
    check dsGraphql:checkFinite(value.ratio, "Reading", "ratio");
    check dsGraphql:checkFinite(value.spare ?: 0.0, "Reading", "spare");
    time:Date|dataservice:ConversionError day = dataservice:dateFromString(value.day);
    if day is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(day, "Reading", "day");
    }
    time:TimeOfDay|dataservice:ConversionError at = dataservice:timeOfDayFromString(value.at);
    if at is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(at, "Reading", "at");
    }
    time:Civil|dataservice:ConversionError localTime = dataservice:civilFromString(value.localTime);
    if localTime is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(localTime, "Reading", "localTime");
    }
    time:Utc|dataservice:ConversionError instant = dataservice:utcFromString(value.instant);
    if instant is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(instant, "Reading", "instant");
    }
    time:Date?|dataservice:ConversionError endDay = ();
    string? endDayText = value.endDay;
    if endDayText !is () {
        endDay = dataservice:dateFromString(endDayText);
    }
    if endDay is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(endDay, "Reading", "endDay");
    }
    byte[]|dataservice:ConversionError payload = dataservice:bytesFromString(value.payload);
    if payload is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(payload, "Reading", "payload");
    }
    byte[]?|dataservice:ConversionError extra = ();
    string? extraText = value.extra;
    if extraText !is () {
        extra = dataservice:bytesFromString(extraText);
    }
    if extra is dataservice:ConversionError {
        return dsGraphql:toInvalidInput(extra, "Reading", "extra");
    }
    return {id: value.id, count: value.count, ratio: value.ratio, spare: value.spare, amount: value.amount, active: value.active, day, at, localTime, instant, endDay, payload, extra, status: value.status, previous: value.previous};
}

isolated function transformInputToReadingUpdate(ReadingUpdateInput value) returns ReadingUpdate|dsGraphql:Error {
    ReadingUpdate update = {};
    int? count = value.count;
    if count !is () {
        update.count = count;
    }
    float? ratio = value.ratio;
    if ratio !is () {
        check dsGraphql:checkFinite(ratio, "Reading", "ratio");
        update.ratio = ratio;
    }
    float? spare = value.spare;
    if spare !is () {
        check dsGraphql:checkFinite(spare, "Reading", "spare");
        update.spare = spare;
    }
    decimal? amount = value.amount;
    if amount !is () {
        update.amount = amount;
    }
    boolean? active = value.active;
    if active !is () {
        update.active = active;
    }
    string? dayText = value.day;
    if dayText !is () {
        time:Date|dataservice:ConversionError day = dataservice:dateFromString(dayText);
        if day is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(day, "Reading", "day");
        }
        update.day = day;
    }
    string? atText = value.at;
    if atText !is () {
        time:TimeOfDay|dataservice:ConversionError at = dataservice:timeOfDayFromString(atText);
        if at is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(at, "Reading", "at");
        }
        update.at = at;
    }
    string? localTimeText = value.localTime;
    if localTimeText !is () {
        time:Civil|dataservice:ConversionError localTime = dataservice:civilFromString(localTimeText);
        if localTime is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(localTime, "Reading", "localTime");
        }
        update.localTime = localTime;
    }
    string? instantText = value.instant;
    if instantText !is () {
        time:Utc|dataservice:ConversionError instant = dataservice:utcFromString(instantText);
        if instant is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(instant, "Reading", "instant");
        }
        update.instant = instant;
    }
    string? endDayText = value.endDay;
    if endDayText !is () {
        time:Date|dataservice:ConversionError endDay = dataservice:dateFromString(endDayText);
        if endDay is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(endDay, "Reading", "endDay");
        }
        update.endDay = endDay;
    }
    string? payloadText = value.payload;
    if payloadText !is () {
        byte[]|dataservice:ConversionError payload = dataservice:bytesFromString(payloadText);
        if payload is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(payload, "Reading", "payload");
        }
        update.payload = payload;
    }
    string? extraText = value.extra;
    if extraText !is () {
        byte[]|dataservice:ConversionError extra = dataservice:bytesFromString(extraText);
        if extra is dataservice:ConversionError {
            return dsGraphql:toInvalidInput(extra, "Reading", "extra");
        }
        update.extra = extra;
    }
    Status? status = value.status;
    if status !is () {
        update.status = status;
    }
    Status? previous = value.previous;
    if previous !is () {
        update.previous = previous;
    }
    ReadingUnset? unset = value.unset;
    if unset !is () {
        if unset.spare == true {
            if update.hasKey("spare") {
                return dsGraphql:toSetAndUnsetError("Reading", "spare");
            }
            update.spare = ();
        }
        if unset.endDay == true {
            if update.hasKey("endDay") {
                return dsGraphql:toSetAndUnsetError("Reading", "endDay");
            }
            update.endDay = ();
        }
        if unset.extra == true {
            if update.hasKey("extra") {
                return dsGraphql:toSetAndUnsetError("Reading", "extra");
            }
            update.extra = ();
        }
        if unset.previous == true {
            if update.hasKey("previous") {
                return dsGraphql:toSetAndUnsetError("Reading", "previous");
            }
            update.previous = ();
        }
    }
    return update;
}

# A measure, keyed by a float.
public isolated service class MeasureObject {
    private final readonly & Measure row;

    isolated function init(Measure row) {
        self.row = row.cloneReadOnly();
    }

    # The `weight` field of `Measure`
    # + return - The value
    isolated resource function get weight() returns float|error => dsGraphql:toFinite(self.row.weight, "Measure", "weight");

    # The `label` field of `Measure`
    # + return - The value
    isolated resource function get label() returns string => self.row.label;
}

# A page of measures.
#
# + items - The measures
# + hasMore - Whether records exist after this page
public type MeasureList record {
    MeasureObject[] items;
    boolean hasMore;
};

# The conditions that measures match; every condition set must hold.
public type MeasureFilter record {|
    # The conditions on `weight`
    dsGraphql:FloatFilter? weight;
    # The conditions on `label`
    dsGraphql:StringFilter? label;
|};

# One entry of the order of measures; set exactly one field.
public type MeasureSort record {|
    # Sorts by `weight`
    dsGraphql:SortDirection? weight;
    # Sorts by `label`
    dsGraphql:SortDirection? label;
|};

# The fields to change in a measure; a field left out or set to `null` is not changed.
public type MeasureUpdateInput record {|
    # The `label` field of `Measure`
    string? label;
|};

# The fields of `Measure` that callers can see.
final readonly & dataservice:Field[] dataserviceMeasureFields = [
    {name: "weight", column: "weight", 'type: dataservice:FLOAT, key: true},
    {name: "label", column: "label", 'type: dataservice:STRING}
];

# The description of `Measure` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceMeasureEntity = {
    name: "Measure",
    collection: "measures",
    'resource: "measures",
    datastore: dataservice:H2,
    fields: dataserviceMeasureFields
};

isolated function transformInputToMeasureUpdate(MeasureUpdateInput value) returns MeasureUpdate|dsGraphql:Error {
    MeasureUpdate update = {};
    string? label = value.label;
    if label !is () {
        update.label = label;
    }
    return update;
}

# The conditions on a `Status` field; every condition set must hold.
public type StatusFilter record {|
    # Equal to the value
    Status? eq;
    # Not equal to the value
    Status? ne;
    # Equal to any of the values
    Status[]? anyOf;
|};

# The conditions on a nullable `Status` field. A null field matches only `isNull`.
public type StatusNullableFilter record {|
    # Equal to the value
    Status? eq;
    # Not equal to the value; a null field does not match
    Status? ne;
    # Equal to any of the values
    Status[]? anyOf;
    # `true` for null, `false` for not null
    boolean? isNull;
|};
