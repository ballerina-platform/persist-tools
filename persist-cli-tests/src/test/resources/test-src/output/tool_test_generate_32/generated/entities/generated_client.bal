// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const MEDICAL_ITEM = "medicalitem";

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "medicalitem": {
            entityName: "MedicalItem",
            tableName: `MedicalItem`,
            fieldMetadata: {
                itemId: {columnName: "itemId", 'type: int},
                name: {columnName: "name", 'type: string},
                'type: {columnName: "type", 'type: string},
                unit: {columnName: "unit", 'type: string}
            },
            keyFields: ["itemId"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {medicalitem: check new (self.dbClient, self.metadata.get(MEDICAL_ITEM))};
    }

    isolated resource function get medicalitems() returns stream<MedicalItem, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(MEDICAL_ITEM).runReadQuery(MedicalItem);
        if result is persist:Error {
            return new stream<MedicalItem, persist:Error?>(new MedicalItemStream((), result));
        } else {
            return new stream<MedicalItem, persist:Error?>(new MedicalItemStream(result));
        }
    }

    isolated resource function get medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem|error result = (check self.persistClients.get(MEDICAL_ITEM).runReadByKeyQuery(MedicalItem, itemId)).cloneWithType(MedicalItem);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post medicalitems(MedicalItemInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(MEDICAL_ITEM).runBatchInsertQuery(data);
        return from MedicalItemInsert inserted in data
            select inserted.itemId;
    }

    isolated resource function put medicalitems/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        _ = check self.persistClients.get(MEDICAL_ITEM).runUpdateQuery(itemId, value);
        return self->/medicalitems/[itemId].get();
    }

    isolated resource function delete medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem result = check self->/medicalitems/[itemId].get();
        _ = check self.persistClients.get(MEDICAL_ITEM).runDeleteQuery(itemId);
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

public class MedicalItemStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|MedicalItem value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                MedicalItem|error value = streamValue.value.cloneWithType(MedicalItem);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|MedicalItem value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

