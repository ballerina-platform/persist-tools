// AUTO-GENERATED FILE. DO NOT MODIFY.
// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.
import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.inmemory;

const MEDICAL_NEED = "medicalneeds";
const MEDICAL_ITEM = "medicalitems";
final isolated table<MedicalNeed> key(needId) medicalneedsTable = table [];
final isolated table<MedicalItem> key(itemId) medicalitemsTable = table [];

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final map<inmemory:InMemoryClient> persistClients;

    public isolated function init() returns persist:Error? {
        final map<inmemory:TableMetadata> metadata = {
            [MEDICAL_NEED]: {
                keyFields: ["needId"],
                query: queryMedicalneeds,
                queryOne: queryOneMedicalneeds
            },
            [MEDICAL_ITEM]: {
                keyFields: ["itemId"],
                query: queryMedicalitems,
                queryOne: queryOneMedicalitems
            }
        };
        self.persistClients = {
            [MEDICAL_NEED]: check new (metadata.get(MEDICAL_NEED).cloneReadOnly()),
            [MEDICAL_ITEM]: check new (metadata.get(MEDICAL_ITEM).cloneReadOnly())
        };
    }

    isolated resource function get medicalneeds(MedicalNeedTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get medicalneeds/[int needId](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach MedicalNeedInsert value in data {
            lock {
                if medicalneedsTable.hasKey(value.needId) {
                    return persist:getAlreadyExistsError("MedicalNeed", value.needId);
                }
                medicalneedsTable.put(value.clone());
            }
            keys.push(value.needId);
        }
        return keys;
    }

    isolated resource function put medicalneeds/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        lock {
            if !medicalneedsTable.hasKey(needId) {
                return persist:getNotFoundError("MedicalNeed", needId);
            }
            MedicalNeed medicalneed = medicalneedsTable.get(needId);
            foreach var [k, v] in value.clone().entries() {
                medicalneed[k] = v;
            }
            medicalneedsTable.put(medicalneed);
            return medicalneed.clone();
        }
    }

    isolated resource function delete medicalneeds/[int needId]() returns MedicalNeed|persist:Error {
        lock {
            if !medicalneedsTable.hasKey(needId) {
                return persist:getNotFoundError("MedicalNeed", needId);
            }
            return medicalneedsTable.remove(needId).clone();
        }
    }

    isolated resource function get medicalitems(MedicalItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get medicalitems/[int itemId](MedicalItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalitems(MedicalItemInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach MedicalItemInsert value in data {
            lock {
                if medicalitemsTable.hasKey(value.itemId) {
                    return persist:getAlreadyExistsError("MedicalItem", value.itemId);
                }
                medicalitemsTable.put(value.clone());
            }
            keys.push(value.itemId);
        }
        return keys;
    }

    isolated resource function put medicalitems/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        lock {
            if !medicalitemsTable.hasKey(itemId) {
                return persist:getNotFoundError("MedicalItem", itemId);
            }
            MedicalItem medicalitem = medicalitemsTable.get(itemId);
            foreach var [k, v] in value.clone().entries() {
                medicalitem[k] = v;
            }
            medicalitemsTable.put(medicalitem);
            return medicalitem.clone();
        }
    }

    isolated resource function delete medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        lock {
            if !medicalitemsTable.hasKey(itemId) {
                return persist:getNotFoundError("MedicalItem", itemId);
            }
            return medicalitemsTable.remove(itemId).clone();
        }
    }

    public isolated function close() returns persist:Error? {
        return ();
    }
}

isolated function queryMedicalneeds(string[] fields) returns stream<record {}, persist:Error?> {
    table<MedicalNeed> key(needId) medicalneedsClonedTable;
    lock {
        medicalneedsClonedTable = medicalneedsTable.clone();
    }
    return from record {} 'object in medicalneedsClonedTable
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryOneMedicalneeds(anydata key) returns record {}|persist:NotFoundError {
    table<MedicalNeed> key(needId) medicalneedsClonedTable;
    lock {
        medicalneedsClonedTable = medicalneedsTable.clone();
    }
    from record {} 'object in medicalneedsClonedTable
    where persist:getKey('object, ["needId"]) == key
    do {
        return {
            ...'object
        };
    };
    return persist:getNotFoundError("MedicalNeed", key);
}

isolated function queryMedicalitems(string[] fields) returns stream<record {}, persist:Error?> {
    table<MedicalItem> key(itemId) medicalitemsClonedTable;
    lock {
        medicalitemsClonedTable = medicalitemsTable.clone();
    }
    return from record {} 'object in medicalitemsClonedTable
        select persist:filterRecord({
            ...'object
        }, fields);
}

isolated function queryOneMedicalitems(anydata key) returns record {}|persist:NotFoundError {
    table<MedicalItem> key(itemId) medicalitemsClonedTable;
    lock {
        medicalitemsClonedTable = medicalitemsTable.clone();
    }
    from record {} 'object in medicalitemsClonedTable
    where persist:getKey('object, ["itemId"]) == key
    do {
        return {
            ...'object
        };
    };
    return persist:getNotFoundError("MedicalItem", key);
}

