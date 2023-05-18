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
                needId: {columnName: "needId"},
                itemId: {columnName: "itemId"},
                name: {columnName: "name"},
                beneficiaryId: {columnName: "beneficiaryId"},
                period: {columnName: "period"},
                urgency: {columnName: "urgency"},
                quantity: {columnName: "quantity"}
            },
            keyFields: ["needId"]
        },
        [MEDICAL_ITEM] : {
            entityName: "MedicalItem",
            tableName: `MedicalItem`,
            fieldMetadata: {
                itemId: {columnName: "itemId"},
                name: {columnName: "name"},
                'type: {columnName: "type"},
                unit: {columnName: "unit"}
            },
            keyFields: ["itemId"]
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

    isolated resource function get medicalneeds/[int needId](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        lock {
            _ = check self.persistClients.get(MEDICAL_NEED).runBatchInsertQuery(data.clone());
        }
        return from MedicalNeedInsert inserted in data
            select inserted.needId;
    }

    isolated resource function put medicalneeds/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        lock {
            _ = check self.persistClients.get(MEDICAL_NEED).runUpdateQuery(needId, value.clone());
        }
        return self->/medicalneeds/[needId].get();
    }

    isolated resource function delete medicalneeds/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed result = check self->/medicalneeds/[needId].get();
        lock {
            _ = check self.persistClients.get(MEDICAL_NEED).runDeleteQuery(needId);
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

