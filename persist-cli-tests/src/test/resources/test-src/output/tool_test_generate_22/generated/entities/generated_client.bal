// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const MEDICALNEED = "MedicalNeed";
const MEDICALITEM = "MedicalItem";

public client class EntitiesClient {

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "medicalneed": {
            entityName: "MedicalNeed",
            tableName: `MedicalNeed`,
            'record: {columnName: "'record", 'type: int},
            medicalitemItemId: {columnName: "medicalitemItemId", 'type: int},
            beneficiaryId: {columnName: "beneficiaryId", 'type: int},
            'time: {columnName: "'time", 'type: time:Civil},
            urgency: {columnName: "urgency", 'type: string},
            quantity: {columnName: "quantity", 'type: int},
            keyFields: ["'record"]
        },
        "medicalitem": {
            entityName: "MedicalItem",
            tableName: `MedicalItem`,
            itemId: {columnName: "itemId", 'type: int},
            'string: {columnName: "'string", 'type: string},
            'type: {columnName: "'type", 'type: string},
            unit: {columnName: "unit", 'type: string},
            keyFields: ["itemId"]
        }
    };

    public function init() returns persist:Error? {
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {
            medicalneed: check new (self.dbClient, self.metadata.get(MEDICALNEED),
            medicalitem: check new (self.dbClient, self.metadata.get(MEDICALITEM)        };
    }

    isolated resource function get medicalneed() returns stream<MedicalNeed, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(MEDICALNEED).runReadQuery(MedicalNeed);
        if result is persist:Error {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream((), result));
        } else {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream(result));
        }
    }
    isolated resource function get medicalneed/[int 'record]() returns MedicalNeed|persist:Error {
        return (check self.persistClients.get(MEDICALNEED).runReadByKeyQuery(MedicalNeed, 'record)).cloneWithType(MedicalNeed);
    }
    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(MEDICALNEED).runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.'record;
    }
    isolated resource function put medicalneed/[int 'record](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        _ = check self.persistClients.get(MEDICALNEED).runUpdateQuery({"'record": 'record}, value);
        return self->/medicalneed/['record].get();
    }
    isolated resource function delete medicalneed/[int 'record]() returns MedicalNeed|persist:Error {
        MedicalNeed 'object = check self->/medicalneed/['record].get();
        _ = check self.persistClients.get(MEDICALNEED).runDeleteQuery({"'record": 'record});
        return 'object;
    }

    isolated resource function get medicalitem() returns stream<MedicalItem, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(MEDICALITEM).runReadQuery(MedicalItem);
        if result is persist:Error {
            return new stream<MedicalItem, persist:Error?>(new MedicalItemStream((), result));
        } else {
            return new stream<MedicalItem, persist:Error?>(new MedicalItemStream(result));
        }
    }
    isolated resource function get medicalitem/[int itemId]() returns MedicalItem|persist:Error {
        return (check self.persistClients.get(MEDICALITEM).runReadByKeyQuery(MedicalItem, itemId)).cloneWithType(MedicalItem);
    }
    isolated resource function post medicalitem(MedicalItemInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(MEDICALITEM).runBatchInsertQuery(data);
        return from MedicalItemInsert inserted in data
            select inserted.itemId;
    }
    isolated resource function put medicalitem/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        _ = check self.persistClients.get(MEDICALITEM).runUpdateQuery({"itemId": itemId}, value);
        return self->/medicalitem/[itemId].get();
    }
    isolated resource function delete medicalitem/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem 'object = check self->/medicalitem/[itemId].get();
        _ = check self.persistClients.get(MEDICALITEM).runDeleteQuery({"itemId": itemId});
        return 'object;
    }

    public function close() returns persist:Error? {
        _ = check self.dbClient.close();
    }
}

public class MedicalNeedStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|MedicalNeed value;|}|persist:Error? {
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
                record {|MedicalNeed value;|} nextRecord = {value: check streamValue.value.cloneWithType(MedicalNeed)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check closeEntityStream(self.anydataStream);
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
                record {|MedicalItem value;|} nextRecord = {value: check streamValue.value.cloneWithType(MedicalItem)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check closeEntityStream(self.anydataStream);
    }
}

