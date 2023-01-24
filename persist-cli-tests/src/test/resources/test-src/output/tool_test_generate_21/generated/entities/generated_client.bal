// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerina/time;
import ballerinax/mysql;

const MEDICALNEED = "MedicalNeed";
const AIDPACKAGEORDERITEM = "AidPackageOrderItem";

public client class EntitiesClient {

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "medicalneed": {
            entityName: "MedicalNeed",
            tableName: `MedicalNeed`,
            fieldMetadata: {
                needId: {columnName: "needId", 'type: int},
                beneficiaryId: {columnName: "beneficiaryId", 'type: int},
                period: {columnName: "period", 'type: time:Civil},
                urgency: {columnName: "urgency", 'type: string},
                quantity: {columnName: "quantity", 'type: int},
                aidpackageorderitemId: {columnName: "aidpackageorderitemId", 'type: int}
            },
            keyFields: ["needId"]
        },
        "aidpackageorderitem": {
            entityName: "AidPackageOrderItem",
            tableName: `AidPackageOrderItem`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                quantity: {columnName: "quantity", 'type: int},
                totalAmount: {columnName: "totalAmount", 'type: int}
            },
            keyFields: ["id"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            medicalneed: check new (self.dbClient, self.metadata.get(MEDICALNEED)),
            aidpackageorderitem: check new (self.dbClient, self.metadata.get(AIDPACKAGEORDERITEM))
        };
    }

    isolated resource function get medicalneed() returns stream<MedicalNeed, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(MEDICALNEED).runReadQuery(MedicalNeed);
        if result is persist:Error {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream((), result));
        } else {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream(result));
        }
    }
    isolated resource function get medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed|error result = (check self.persistClients.get(MEDICALNEED).runReadByKeyQuery(MedicalNeed, needId)).cloneWithType(MedicalNeed);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(MEDICALNEED).runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.needId;
    }
    isolated resource function put medicalneed/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        _ = check self.persistClients.get(MEDICALNEED).runUpdateQuery({"needId": needId}, value);
        return self->/medicalneed/[needId].get();
    }
    isolated resource function delete medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed 'object = check self->/medicalneed/[needId].get();
        _ = check self.persistClients.get(MEDICALNEED).runDeleteQuery({"needId": needId});
        return 'object;
    }

    isolated resource function get aidpackageorderitem() returns stream<AidPackageOrderItem, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(AIDPACKAGEORDERITEM).runReadQuery(AidPackageOrderItem);
        if result is persist:Error {
            return new stream<AidPackageOrderItem, persist:Error?>(new AidPackageOrderItemStream((), result));
        } else {
            return new stream<AidPackageOrderItem, persist:Error?>(new AidPackageOrderItemStream(result));
        }
    }
    isolated resource function get aidpackageorderitem/[int id]() returns AidPackageOrderItem|persist:Error {
        AidPackageOrderItem|error result = (check self.persistClients.get(AIDPACKAGEORDERITEM).runReadByKeyQuery(AidPackageOrderItem, id)).cloneWithType(AidPackageOrderItem);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
    isolated resource function post aidpackageorderitem(AidPackageOrderItemInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(AIDPACKAGEORDERITEM).runBatchInsertQuery(data);
        return from AidPackageOrderItemInsert inserted in data
            select inserted.id;
    }
    isolated resource function put aidpackageorderitem/[int id](AidPackageOrderItemUpdate value) returns AidPackageOrderItem|persist:Error {
        _ = check self.persistClients.get(AIDPACKAGEORDERITEM).runUpdateQuery({"id": id}, value);
        return self->/aidpackageorderitem/[id].get();
    }
    isolated resource function delete aidpackageorderitem/[int id]() returns AidPackageOrderItem|persist:Error {
        AidPackageOrderItem 'object = check self->/aidpackageorderitem/[id].get();
        _ = check self.persistClients.get(AIDPACKAGEORDERITEM).runDeleteQuery({"id": id});
        return 'object;
    }

    public function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
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
                MedicalNeed|error value = streamValue.value.cloneWithType(MedicalNeed);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|MedicalNeed value;|} nextRecord = {value: value};
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

public class AidPackageOrderItemStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|AidPackageOrderItem value;|}|persist:Error? {
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
                AidPackageOrderItem|error value = streamValue.value.cloneWithType(AidPackageOrderItem);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|AidPackageOrderItem value;|} nextRecord = {value: value};
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

