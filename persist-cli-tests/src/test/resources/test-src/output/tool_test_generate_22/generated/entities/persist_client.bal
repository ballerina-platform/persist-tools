// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

const MEDICAL_NEED = "medicalneeds";
const MEDICAL_ITEM = "medicalitems";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients = {};

    private final record {|persist:SQLMetadata...;|} metadata = {
        [MEDICAL_NEED] : {
            entityName: "MedicalNeed",
            tableName: `MedicalNeed`,
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
            joinMetadata: {item: {entity: MedicalItem, fieldName: "item", refTable: "MedicalItem", refColumns: ["itemId"], joinColumns: ["itemItemId"], 'type: persist:ONE_TO_ONE}}
        },
        [MEDICAL_ITEM] : {
            entityName: "MedicalItem",
            tableName: `MedicalItem`,
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
            joinMetadata: {medicalNeed: {entity: MedicalNeed, fieldName: "medicalNeed", refTable: "MedicalNeed", refColumns: ["itemItemId"], joinColumns: ["itemId"], 'type: persist:ONE_TO_ONE}}
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        lock {
            self.persistClients[MEDICAL_NEED] = check new (self.dbClient, self.metadata.get(MEDICAL_NEED));
            self.persistClients[MEDICAL_ITEM] = check new (self.dbClient, self.metadata.get(MEDICAL_ITEM));
        }
    }

    isolated resource function get medicalneeds(MedicalNeedTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get medicalneeds/[int 'record](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        lock {
            _ = check self.persistClients.get(MEDICAL_NEED).runBatchInsertQuery(data.clone());
        }
        return from MedicalNeedInsert inserted in data
            select inserted.'record;
    }

    isolated resource function put medicalneeds/[int 'record](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        lock {
            _ = check self.persistClients.get(MEDICAL_NEED).runUpdateQuery('record, value.clone());
        }
        return self->/medicalneeds/['record].get();
    }

    isolated resource function delete medicalneeds/[int 'record]() returns MedicalNeed|persist:Error {
        MedicalNeed result = check self->/medicalneeds/['record].get();
        lock {
            _ = check self.persistClients.get(MEDICAL_NEED).runDeleteQuery('record);
        }
        return result;
    }

    isolated resource function get medicalitems(MedicalItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get medicalitems/[int itemId](MedicalItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalitems(MedicalItemInsert[] data) returns int[]|persist:Error {
        lock {
            _ = check self.persistClients.get(MEDICAL_ITEM).runBatchInsertQuery(data.clone());
        }
        return from MedicalItemInsert inserted in data
            select inserted.itemId;
    }

    isolated resource function put medicalitems/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        lock {
            _ = check self.persistClients.get(MEDICAL_ITEM).runUpdateQuery(itemId, value.clone());
        }
        return self->/medicalitems/[itemId].get();
    }

    isolated resource function delete medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem result = check self->/medicalitems/[itemId].get();
        lock {
            _ = check self.persistClients.get(MEDICAL_ITEM).runDeleteQuery(itemId);
        }
        return result;
    }

    public function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

