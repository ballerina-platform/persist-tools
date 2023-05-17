// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;

const MEDICAL_NEED = "medicalneeds";
const MEDICAL_ITEM = "medicalitems";
table<MedicalNeed> key(needId) medicalneeds = table [];
table<MedicalItem> key(itemId) medicalitems = table [];

public client class Client {
    *persist:AbstractPersistClient;

    private final map<persist:InMemoryClient> persistClients;

    table<MedicalNeed> key(needId) medicalneeds = medicalneeds;
    table<MedicalItem> key(itemId) medicalitems = medicalitems;

    public function init() returns persist:Error? {
        final map<persist:TableMetadata> metadata = {
            [MEDICAL_NEED] : {
                keyFields: ["needId"],
                query: self.queryMedicalneeds,
                queryOne: self.queryOneMedicalneeds
            },
            [MEDICAL_ITEM] : {
                keyFields: ["itemId"],
                query: self.queryMedicalitems,
                queryOne: self.queryOneMedicalitems
            }
        };
        self.persistClients = {
            [MEDICAL_NEED] : check new (metadata.get(MEDICAL_NEED)),
            [MEDICAL_ITEM] : check new (metadata.get(MEDICAL_ITEM))
        };
    }

    isolated resource function get medicalneeds(MedicalNeedTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get medicalneeds/[int needId](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach MedicalNeedInsert value in data {
            if self.medicalneeds.hasKey(value.needId) {
                return <persist:AlreadyExistsError>error("Duplicate key: " + value.needId.toString());
            }
            self.medicalneeds.put(value.clone());
            keys.push(value.needId);
        }
        return keys;
    }

    isolated resource function put medicalneeds/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        if !self.medicalneeds.hasKey(needId) {
            return <persist:NotFoundError>error("Not found: " + needId.toString());
        }
        MedicalNeed medicalneed = self.medicalneeds.get(needId);
        foreach var [k, v] in value.entries() {
            medicalneed[k] = v;
        }
        self.medicalneeds.put(medicalneed);
        return medicalneed;
    }

    isolated resource function delete medicalneeds/[int needId]() returns MedicalNeed|persist:Error {
        if !self.medicalneeds.hasKey(needId) {
            return <persist:NotFoundError>error("Not found: " + needId.toString());
        }
        return self.medicalneeds.remove(needId);
    }

    private function queryMedicalneeds(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.medicalneeds
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneMedicalneeds(anydata key) returns record {}|persist:NotFoundError {
        from record {} 'object in self.medicalneeds
        where self.persistClients.get(MEDICAL_NEED).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    isolated resource function get medicalitems(MedicalItemTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "query"
    } external;

    isolated resource function get medicalitems/[int itemId](MedicalItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    isolated resource function post medicalitems(MedicalItemInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach MedicalItemInsert value in data {
            if self.medicalitems.hasKey(value.itemId) {
                return <persist:AlreadyExistsError>error("Duplicate key: " + value.itemId.toString());
            }
            self.medicalitems.put(value.clone());
            keys.push(value.itemId);
        }
        return keys;
    }

    isolated resource function put medicalitems/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        if !self.medicalitems.hasKey(itemId) {
            return <persist:NotFoundError>error("Not found: " + itemId.toString());
        }
        MedicalItem medicalitem = self.medicalitems.get(itemId);
        foreach var [k, v] in value.entries() {
            medicalitem[k] = v;
        }
        self.medicalitems.put(medicalitem);
        return medicalitem;
    }

    isolated resource function delete medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        if !self.medicalitems.hasKey(itemId) {
            return <persist:NotFoundError>error("Not found: " + itemId.toString());
        }
        return self.medicalitems.remove(itemId);
    }

    private function queryMedicalitems(string[] fields) returns stream<record {}, persist:Error?> {
        return from record {} 'object in self.medicalitems
            select persist:filterRecord({
                ...'object
            }, fields);
    }

    private function queryOneMedicalitems(anydata key) returns record {}|persist:NotFoundError {
        from record {} 'object in self.medicalitems
        where self.persistClients.get(MEDICAL_ITEM).getKey('object) == key
        do {
            return {
                ...'object
            };
        };
        return <persist:NotFoundError>error("Invalid key: " + key.toString());
    }

    public function close() returns persist:Error? {
        return ();
    }
}
