// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

public client class EntitiesClient {

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final map<persist:Metadata> metadata = {medicalneed: {entityName: "MedicalNeed", tableName: 'MedicalNeed ', needId: {columnName: "needId", 'type: int}, itemId: {columnName: "itemId", 'type: int}, beneficiaryId: {columnName: "beneficiaryId", 'type: int}, period: {columnName: "period", 'type: time:Civil}, urgency: {columnName: "urgency", 'type: string}, quantity: {columnName: "quantity", 'type: int} keyFields: ["needId"]}, medicalitem: {entityName: "MedicalItem", tableName: 'MedicalItem ', itemId: {columnName: "itemId", 'type: int}, name: {columnName: "name", 'type: string}, 'type: {columnName: "'type", 'type: string}, unit: {columnName: "unit", 'type: string} keyFields: ["itemId"]}};

    public function init() returns persist:Error? {
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {medicalneed: check new (self.dbClient, self.metadata.get("medicalneed").entityName, self.metadata.get("medicalneed").tableName, self.metadata.get("medicalneed").keyFields, self.metadata.get("medicalneed").fieldMetadata), medicalitem: check new (self.dbClient, self.metadata.get("medicalitem").entityName, self.metadata.get("medicalitem").tableName, self.metadata.get("medicalitem").keyFields, self.metadata.get("medicalitem").fieldMetadata)};
    }

    public function close() returns persist:Error? {
        sql:Error? e = self.dbClient.close();
        if e is sql:Error {
            return <persist:Error>error(e.message());
        }
    }

    isolated resource function get medicalneed() returns stream<MedicalNeed, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("medicalneed").runReadQuery(MedicalNeed);
        if result is persist:Error {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream((), result));
        } else {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream(result));
        }
    }
    isolated resource function get medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        return (check self.persistClients.get("medicalneed").runReadByKeyQuery(MedicalNeed, needId)).cloneWithType(MedicalNeed);
    }
    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("medicalneed").runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.needId;
    }
    isolated resource function put medicalneed/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        _ = check self.persistClients.get("medicalneed").runUpdateQuery({"needId": needId}, value);
        return self->/medicalneed/[needId].get();
    }
    isolated resource function delete medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed 'object = check self->/medicalneed/[needId].get();
        _ = check self.persistClients.get("medicalneed").runDeleteQuery({"needId": needId});
        return 'object;
    }

    isolated resource function get medicalitem() returns stream<MedicalItem, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClients.get("medicalitem").runReadQuery(MedicalItem);
        if result is persist:Error {
            return new stream<MedicalItem, persist:Error?>(new MedicalItemStream((), result));
        } else {
            return new stream<MedicalItem, persist:Error?>(new MedicalItemStream(result));
        }
    }
    isolated resource function get medicalitem/[int itemId]() returns MedicalItem|persist:Error {
        return (check self.persistClients.get("medicalitem").runReadByKeyQuery(MedicalItem, itemId)).cloneWithType(MedicalItem);
    }
    isolated resource function post medicalitem(MedicalItemInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get("medicalitem").runBatchInsertQuery(data);
        return from MedicalItemInsert inserted in data
            select inserted.itemId;
    }
    isolated resource function put medicalitem/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        _ = check self.persistClients.get("medicalitem").runUpdateQuery({"itemId": itemId}, value);
        return self->/medicalitem/[itemId].get();
    }
    isolated resource function delete medicalitem/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem 'object = check self->/medicalitem/[itemId].get();
        _ = check self.persistClients.get("medicalitem").runDeleteQuery({"itemId": itemId});
        return 'object;
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
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
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
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
    }
}

