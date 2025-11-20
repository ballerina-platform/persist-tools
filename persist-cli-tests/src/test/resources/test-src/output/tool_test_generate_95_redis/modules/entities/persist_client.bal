// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.redis as predis;
import ballerinax/redis;

const ALL_TYPES = "alltypes";
const STRING_ID_RECORD = "stringidrecords";
const INT_ID_RECORD = "intidrecords";
const FLOAT_ID_RECORD = "floatidrecords";
const DECIMAL_ID_RECORD = "decimalidrecords";
const BOOLEAN_ID_RECORD = "booleanidrecords";
const COMPOSITE_ASSOCIATION_RECORD = "compositeassociationrecords";
const ALL_TYPES_ID_RECORD = "alltypesidrecords";

# Redis persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final redis:Client dbClient;

    private final map<predis:RedisClient> persistClients;

    private final record {|predis:RedisMetadata...;|} & readonly metadata = {
        [ALL_TYPES]: {
            entityName: "AllTypes",
            collectionName: "AllTypes",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                booleanType: {fieldName: "booleanType", fieldDataType: predis:BOOLEAN},
                intType: {fieldName: "intType", fieldDataType: predis:INT},
                floatType: {fieldName: "floatType", fieldDataType: predis:FLOAT},
                decimalType: {fieldName: "decimalType", fieldDataType: predis:DECIMAL},
                stringType: {fieldName: "stringType", fieldDataType: predis:STRING},
                dateType: {fieldName: "dateType", fieldDataType: predis:DATE},
                timeOfDayType: {fieldName: "timeOfDayType", fieldDataType: predis:TIME_OF_DAY},
                utcType: {fieldName: "utcType", fieldDataType: predis:UTC},
                civilType: {fieldName: "civilType", fieldDataType: predis:CIVIL},
                enumType: {fieldName: "enumType", fieldDataType: predis:STRING},
                booleanTypeOptional: {fieldName: "booleanTypeOptional", fieldDataType: predis:BOOLEAN},
                intTypeOptional: {fieldName: "intTypeOptional", fieldDataType: predis:INT},
                floatTypeOptional: {fieldName: "floatTypeOptional", fieldDataType: predis:FLOAT},
                decimalTypeOptional: {fieldName: "decimalTypeOptional", fieldDataType: predis:DECIMAL},
                stringTypeOptional: {fieldName: "stringTypeOptional", fieldDataType: predis:STRING},
                dateTypeOptional: {fieldName: "dateTypeOptional", fieldDataType: predis:DATE},
                timeOfDayTypeOptional: {fieldName: "timeOfDayTypeOptional", fieldDataType: predis:TIME_OF_DAY},
                utcTypeOptional: {fieldName: "utcTypeOptional", fieldDataType: predis:UTC},
                civilTypeOptional: {fieldName: "civilTypeOptional", fieldDataType: predis:CIVIL},
                enumTypeOptional: {fieldName: "enumTypeOptional", fieldDataType: predis:STRING}
            },
            keyFields: ["id"]
        },
        [STRING_ID_RECORD]: {
            entityName: "StringIdRecord",
            collectionName: "StringIdRecord",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:STRING},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING}
            },
            keyFields: ["id"]
        },
        [INT_ID_RECORD]: {
            entityName: "IntIdRecord",
            collectionName: "IntIdRecord",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:INT},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING}
            },
            keyFields: ["id"]
        },
        [FLOAT_ID_RECORD]: {
            entityName: "FloatIdRecord",
            collectionName: "FloatIdRecord",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:FLOAT},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING}
            },
            keyFields: ["id"]
        },
        [DECIMAL_ID_RECORD]: {
            entityName: "DecimalIdRecord",
            collectionName: "DecimalIdRecord",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:DECIMAL},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING}
            },
            keyFields: ["id"]
        },
        [BOOLEAN_ID_RECORD]: {
            entityName: "BooleanIdRecord",
            collectionName: "BooleanIdRecord",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:BOOLEAN},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING}
            },
            keyFields: ["id"]
        },
        [COMPOSITE_ASSOCIATION_RECORD]: {
            entityName: "CompositeAssociationRecord",
            collectionName: "CompositeAssociationRecord",
            fieldMetadata: {
                id: {fieldName: "id", fieldDataType: predis:STRING},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING},
                alltypesidrecordBooleanType: {fieldName: "alltypesidrecordBooleanType", fieldDataType: predis:BOOLEAN},
                alltypesidrecordIntType: {fieldName: "alltypesidrecordIntType", fieldDataType: predis:INT},
                alltypesidrecordFloatType: {fieldName: "alltypesidrecordFloatType", fieldDataType: predis:FLOAT},
                alltypesidrecordDecimalType: {fieldName: "alltypesidrecordDecimalType", fieldDataType: predis:DECIMAL},
                alltypesidrecordStringType: {fieldName: "alltypesidrecordStringType", fieldDataType: predis:STRING},
                "allTypesIdRecord.booleanType": {relation: {entityName: "allTypesIdRecord", refField: "booleanType", refFieldDataType: predis:BOOLEAN}},
                "allTypesIdRecord.intType": {relation: {entityName: "allTypesIdRecord", refField: "intType", refFieldDataType: predis:INT}},
                "allTypesIdRecord.floatType": {relation: {entityName: "allTypesIdRecord", refField: "floatType", refFieldDataType: predis:FLOAT}},
                "allTypesIdRecord.decimalType": {relation: {entityName: "allTypesIdRecord", refField: "decimalType", refFieldDataType: predis:DECIMAL}},
                "allTypesIdRecord.stringType": {relation: {entityName: "allTypesIdRecord", refField: "stringType", refFieldDataType: predis:STRING}},
                "allTypesIdRecord.randomField": {relation: {entityName: "allTypesIdRecord", refField: "randomField", refFieldDataType: predis:STRING}}
            },
            keyFields: ["id"],
            refMetadata: {allTypesIdRecord: {entity: AllTypesIdRecord, fieldName: "allTypesIdRecord", refCollection: "AllTypesIdRecord", refMetaDataKey: "compositeAssociationRecord", refFields: ["booleanType", "intType", "floatType", "decimalType", "stringType"], joinFields: ["alltypesidrecordBooleanType", "alltypesidrecordIntType", "alltypesidrecordFloatType", "alltypesidrecordDecimalType", "alltypesidrecordStringType"], 'type: predis:ONE_TO_ONE}}
        },
        [ALL_TYPES_ID_RECORD]: {
            entityName: "AllTypesIdRecord",
            collectionName: "AllTypesIdRecord",
            fieldMetadata: {
                booleanType: {fieldName: "booleanType", fieldDataType: predis:BOOLEAN},
                intType: {fieldName: "intType", fieldDataType: predis:INT},
                floatType: {fieldName: "floatType", fieldDataType: predis:FLOAT},
                decimalType: {fieldName: "decimalType", fieldDataType: predis:DECIMAL},
                stringType: {fieldName: "stringType", fieldDataType: predis:STRING},
                randomField: {fieldName: "randomField", fieldDataType: predis:STRING},
                "compositeAssociationRecord.id": {relation: {entityName: "compositeAssociationRecord", refField: "id", refFieldDataType: predis:STRING}},
                "compositeAssociationRecord.randomField": {relation: {entityName: "compositeAssociationRecord", refField: "randomField", refFieldDataType: predis:STRING}},
                "compositeAssociationRecord.alltypesidrecordBooleanType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordBooleanType", refFieldDataType: predis:BOOLEAN}},
                "compositeAssociationRecord.alltypesidrecordIntType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordIntType", refFieldDataType: predis:INT}},
                "compositeAssociationRecord.alltypesidrecordFloatType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordFloatType", refFieldDataType: predis:FLOAT}},
                "compositeAssociationRecord.alltypesidrecordDecimalType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordDecimalType", refFieldDataType: predis:DECIMAL}},
                "compositeAssociationRecord.alltypesidrecordStringType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordStringType", refFieldDataType: predis:STRING}}
            },
            keyFields: ["booleanType", "intType", "floatType", "decimalType", "stringType"],
            refMetadata: {compositeAssociationRecord: {entity: CompositeAssociationRecord, fieldName: "compositeAssociationRecord", refCollection: "CompositeAssociationRecord", refFields: ["alltypesidrecordBooleanType", "alltypesidrecordIntType", "alltypesidrecordFloatType", "alltypesidrecordDecimalType", "alltypesidrecordStringType"], joinFields: ["booleanType", "intType", "floatType", "decimalType", "stringType"], 'type: predis:ONE_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        redis:Client|error dbClient = new (connectionConfig);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [ALL_TYPES]: check new (dbClient, self.metadata.get(ALL_TYPES), cacheConfig.maxAge),
            [STRING_ID_RECORD]: check new (dbClient, self.metadata.get(STRING_ID_RECORD), cacheConfig.maxAge),
            [INT_ID_RECORD]: check new (dbClient, self.metadata.get(INT_ID_RECORD), cacheConfig.maxAge),
            [FLOAT_ID_RECORD]: check new (dbClient, self.metadata.get(FLOAT_ID_RECORD), cacheConfig.maxAge),
            [DECIMAL_ID_RECORD]: check new (dbClient, self.metadata.get(DECIMAL_ID_RECORD), cacheConfig.maxAge),
            [BOOLEAN_ID_RECORD]: check new (dbClient, self.metadata.get(BOOLEAN_ID_RECORD), cacheConfig.maxAge),
            [COMPOSITE_ASSOCIATION_RECORD]: check new (dbClient, self.metadata.get(COMPOSITE_ASSOCIATION_RECORD), cacheConfig.maxAge),
            [ALL_TYPES_ID_RECORD]: check new (dbClient, self.metadata.get(ALL_TYPES_ID_RECORD), cacheConfig.maxAge)
        };
    }

    # Get rows from AllTypes key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get alltypes(AllTypesTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from AllTypes key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get alltypes/[int id](AllTypesTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into AllTypes key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post alltypes(AllTypesInsert[] data) returns int[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ALL_TYPES);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from AllTypesInsert inserted in data
            select inserted.id;
    }

    # Update row in AllTypes key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put alltypes/[int id](AllTypesUpdate value) returns AllTypes|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ALL_TYPES);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/alltypes/[id].get();
    }

    # Delete row from AllTypes key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete alltypes/[int id]() returns AllTypes|persist:Error {
        AllTypes result = check self->/alltypes/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ALL_TYPES);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from StringIdRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get stringidrecords(StringIdRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from StringIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get stringidrecords/[string id](StringIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into StringIdRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post stringidrecords(StringIdRecordInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(STRING_ID_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from StringIdRecordInsert inserted in data
            select inserted.id;
    }

    # Update row in StringIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put stringidrecords/[string id](StringIdRecordUpdate value) returns StringIdRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(STRING_ID_RECORD);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/stringidrecords/[id].get();
    }

    # Delete row from StringIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete stringidrecords/[string id]() returns StringIdRecord|persist:Error {
        StringIdRecord result = check self->/stringidrecords/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(STRING_ID_RECORD);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from IntIdRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get intidrecords(IntIdRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from IntIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get intidrecords/[int id](IntIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into IntIdRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post intidrecords(IntIdRecordInsert[] data) returns int[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(INT_ID_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from IntIdRecordInsert inserted in data
            select inserted.id;
    }

    # Update row in IntIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put intidrecords/[int id](IntIdRecordUpdate value) returns IntIdRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(INT_ID_RECORD);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/intidrecords/[id].get();
    }

    # Delete row from IntIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete intidrecords/[int id]() returns IntIdRecord|persist:Error {
        IntIdRecord result = check self->/intidrecords/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(INT_ID_RECORD);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from FloatIdRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get floatidrecords(FloatIdRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from FloatIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get floatidrecords/[float id](FloatIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into FloatIdRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post floatidrecords(FloatIdRecordInsert[] data) returns float[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(FLOAT_ID_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from FloatIdRecordInsert inserted in data
            select inserted.id;
    }

    # Update row in FloatIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put floatidrecords/[float id](FloatIdRecordUpdate value) returns FloatIdRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(FLOAT_ID_RECORD);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/floatidrecords/[id].get();
    }

    # Delete row from FloatIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete floatidrecords/[float id]() returns FloatIdRecord|persist:Error {
        FloatIdRecord result = check self->/floatidrecords/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(FLOAT_ID_RECORD);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from DecimalIdRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get decimalidrecords(DecimalIdRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from DecimalIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get decimalidrecords/[decimal id](DecimalIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into DecimalIdRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post decimalidrecords(DecimalIdRecordInsert[] data) returns decimal[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DECIMAL_ID_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from DecimalIdRecordInsert inserted in data
            select inserted.id;
    }

    # Update row in DecimalIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put decimalidrecords/[decimal id](DecimalIdRecordUpdate value) returns DecimalIdRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DECIMAL_ID_RECORD);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/decimalidrecords/[id].get();
    }

    # Delete row from DecimalIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete decimalidrecords/[decimal id]() returns DecimalIdRecord|persist:Error {
        DecimalIdRecord result = check self->/decimalidrecords/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(DECIMAL_ID_RECORD);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from BooleanIdRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get booleanidrecords(BooleanIdRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from BooleanIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get booleanidrecords/[boolean id](BooleanIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into BooleanIdRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post booleanidrecords(BooleanIdRecordInsert[] data) returns boolean[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BOOLEAN_ID_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from BooleanIdRecordInsert inserted in data
            select inserted.id;
    }

    # Update row in BooleanIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put booleanidrecords/[boolean id](BooleanIdRecordUpdate value) returns BooleanIdRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BOOLEAN_ID_RECORD);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/booleanidrecords/[id].get();
    }

    # Delete row from BooleanIdRecord key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete booleanidrecords/[boolean id]() returns BooleanIdRecord|persist:Error {
        BooleanIdRecord result = check self->/booleanidrecords/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(BOOLEAN_ID_RECORD);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from CompositeAssociationRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get compositeassociationrecords(CompositeAssociationRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from CompositeAssociationRecord key space.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get compositeassociationrecords/[string id](CompositeAssociationRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into CompositeAssociationRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post compositeassociationrecords(CompositeAssociationRecordInsert[] data) returns string[]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(COMPOSITE_ASSOCIATION_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from CompositeAssociationRecordInsert inserted in data
            select inserted.id;
    }

    # Update row in CompositeAssociationRecord key space.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put compositeassociationrecords/[string id](CompositeAssociationRecordUpdate value) returns CompositeAssociationRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(COMPOSITE_ASSOCIATION_RECORD);
        }
        _ = check redisClient.runUpdateQuery(id, value);
        return self->/compositeassociationrecords/[id].get();
    }

    # Delete row from CompositeAssociationRecord key space.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete compositeassociationrecords/[string id]() returns CompositeAssociationRecord|persist:Error {
        CompositeAssociationRecord result = check self->/compositeassociationrecords/[id].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(COMPOSITE_ASSOCIATION_RECORD);
        }
        _ = check redisClient.runDeleteQuery(id);
        return result;
    }

    # Get rows from AllTypesIdRecord key space.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get alltypesidrecords(AllTypesIdRecordTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "query"
    } external;

    # Get row from AllTypesIdRecord key space.
    #
    # + booleanType - The value of the primary key field booleanType
    # + intType - The value of the primary key field intType
    # + floatType - The value of the primary key field floatType
    # + decimalType - The value of the primary key field decimalType
    # + stringType - The value of the primary key field stringType
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get alltypesidrecords/[boolean booleanType]/[int intType]/[float floatType]/[decimal decimalType]/[string stringType](AllTypesIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.redis.datastore.RedisProcessor",
        name: "queryOne"
    } external;

    # Insert rows into AllTypesIdRecord key space.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post alltypesidrecords(AllTypesIdRecordInsert[] data) returns [boolean, int, float, decimal, string][]|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ALL_TYPES_ID_RECORD);
        }
        _ = check redisClient.runBatchInsertQuery(data);
        return from AllTypesIdRecordInsert inserted in data
            select [inserted.booleanType, inserted.intType, inserted.floatType, inserted.decimalType, inserted.stringType];
    }

    # Update row in AllTypesIdRecord key space.
    #
    # + booleanType - The value of the primary key field booleanType
    # + intType - The value of the primary key field intType
    # + floatType - The value of the primary key field floatType
    # + decimalType - The value of the primary key field decimalType
    # + stringType - The value of the primary key field stringType
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put alltypesidrecords/[boolean booleanType]/[int intType]/[float floatType]/[decimal decimalType]/[string stringType](AllTypesIdRecordUpdate value) returns AllTypesIdRecord|persist:Error {
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ALL_TYPES_ID_RECORD);
        }
        _ = check redisClient.runUpdateQuery({"booleanType": booleanType, "intType": intType, "floatType": floatType, "decimalType": decimalType, "stringType": stringType}, value);
        return self->/alltypesidrecords/[booleanType]/[intType]/[floatType]/[decimalType]/[stringType].get();
    }

    # Delete row from AllTypesIdRecord key space.
    #
    # + booleanType - The value of the primary key field booleanType
    # + intType - The value of the primary key field intType
    # + floatType - The value of the primary key field floatType
    # + decimalType - The value of the primary key field decimalType
    # + stringType - The value of the primary key field stringType
    # + return - The deleted record or an error
    isolated resource function delete alltypesidrecords/[boolean booleanType]/[int intType]/[float floatType]/[decimal decimalType]/[string stringType]() returns AllTypesIdRecord|persist:Error {
        AllTypesIdRecord result = check self->/alltypesidrecords/[booleanType]/[intType]/[floatType]/[decimalType]/[stringType].get();
        predis:RedisClient redisClient;
        lock {
            redisClient = self.persistClients.get(ALL_TYPES_ID_RECORD);
        }
        _ = check redisClient.runDeleteQuery({"booleanType": booleanType, "intType": intType, "floatType": floatType, "decimalType": decimalType, "stringType": stringType});
        return result;
    }

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

