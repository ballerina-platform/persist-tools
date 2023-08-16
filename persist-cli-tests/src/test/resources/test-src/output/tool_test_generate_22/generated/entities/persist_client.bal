// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const MEDICAL_NEED = "medicalneeds";
const MEDICAL_ITEM = "medicalitems";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [MEDICAL_NEED] : {
            entityName: "MedicalNeed",
            tableName: "MedicalNeed",
            fieldMetadata: {
                'record: {columnName: "record"},
                itemItemId: {columnName: "itemItemId"},
                beneficiaryId: {columnName: "beneficiaryId"},
                'time: {columnName: "time"},
                urgency: {columnName: "urgency"},
                quantity: {columnName: "quantity"},
                "item.itemId": {relation: {entityName: "item", refField: "itemId"}},
                "item.string": {relation: {entityName: "item", refField: "string"}},
                "item.type": {relation: {entityName: "item", refField: "type"}},
                "item.unit": {relation: {entityName: "item", refField: "unit"}}
            },
            keyFields: ["record"],
            joinMetadata: {item: {entity: MedicalItem, fieldName: "item", refTable: "MedicalItem", refColumns: ["itemId"], joinColumns: ["itemItemId"], 'type: psql:ONE_TO_ONE}}
        },
        [MEDICAL_ITEM] : {
            entityName: "MedicalItem",
            tableName: "MedicalItem",
            fieldMetadata: {
                itemId: {columnName: "itemId"},
                'string: {columnName: "string"},
                'type: {columnName: "type"},
                unit: {columnName: "unit"},
                "medicalNeed.record": {relation: {entityName: "medicalNeed", refField: "record"}},
                "medicalNeed.itemItemId": {relation: {entityName: "medicalNeed", refField: "itemItemId"}},
                "medicalNeed.beneficiaryId": {relation: {entityName: "medicalNeed", refField: "beneficiaryId"}},
                "medicalNeed.time": {relation: {entityName: "medicalNeed", refField: "time"}},
                "medicalNeed.urgency": {relation: {entityName: "medicalNeed", refField: "urgency"}},
                "medicalNeed.quantity": {relation: {entityName: "medicalNeed", refField: "quantity"}}
            },
            keyFields: ["itemId"],
            joinMetadata: {medicalNeed: {entity: MedicalNeed, fieldName: "medicalNeed", refTable: "MedicalNeed", refColumns: ["itemItemId"], joinColumns: ["itemId"], 'type: psql:ONE_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [MEDICAL_NEED] : check new (dbClient, self.metadata.get(MEDICAL_NEED), psql:MYSQL_SPECIFICS),
            [MEDICAL_ITEM] : check new (dbClient, self.metadata.get(MEDICAL_ITEM), psql:MYSQL_SPECIFICS)
        };
    }

    isolated resource function get medicalneeds(MedicalNeedTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get medicalneeds/[int 'record](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.'record;
    }

    isolated resource function put medicalneeds/[int 'record](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runUpdateQuery('record, value);
        return self->/medicalneeds/['record].get();
    }

    isolated resource function delete medicalneeds/[int 'record]() returns MedicalNeed|persist:Error {
        MedicalNeed result = check self->/medicalneeds/['record].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runDeleteQuery('record);
        return result;
    }

    isolated resource function get medicalitems(MedicalItemTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get medicalitems/[int itemId](MedicalItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalitems(MedicalItemInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from MedicalItemInsert inserted in data
            select inserted.itemId;
    }

    isolated resource function put medicalitems/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_ITEM);
        }
        _ = check sqlClient.runUpdateQuery(itemId, value);
        return self->/medicalitems/[itemId].get();
    }

    isolated resource function delete medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem result = check self->/medicalitems/[itemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_ITEM);
        }
        _ = check sqlClient.runDeleteQuery(itemId);
        return result;
    }

    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns persist:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

