// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const MEDICAL_NEED = "MedicalNeed";
const AID_PACKAGE_ORDER_ITEM = "AidPackageOrderItem";

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
        self.dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClients = {
            medicalneed: check new (self.dbClient, self.metadata.get(MEDICAL_NEED),
            aidpackageorderitem: check new (self.dbClient, self.metadata.get(AID_PACKAGE_ORDER_ITEM)        };
    }

    isolated resource function get medicalneed() returns stream<MedicalNeed, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(MEDICAL_NEED).runReadQuery(MedicalNeed);
        if result is persist:Error {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream((), result));
        } else {
            return new stream<MedicalNeed, persist:Error?>(new MedicalNeedStream(result));
        }
    }
    isolated resource function get medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        return (check self.persistClients.get(MEDICAL_NEED).runReadByKeyQuery(MedicalNeed, needId)).cloneWithType(MedicalNeed);
    }
    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns [int][]|persist:Error {
        _ = check self.persistClients.get("medicalneed").runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select [inserted.needId];
    }
    isolated resource function put medicalneed/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        _ = check self.persistClients.get("medicalneed").runUpdateQuery({"needId": needId, }, data);
        return self->/medicalneed/[needId].get();
    }
    isolated resource function delete medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed 'object = check self->/medicalneed/[needId].get();
        _ = check self.persistClients.get("medicalneed").runDeleteQuery({"needId": needId, });
        return 'object;
    }

    isolated resource function get aidpackageorderitem() returns stream<AidPackageOrderItem, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(AID_PACKAGE_ORDER_ITEM).runReadQuery(AidPackageOrderItem);
        if result is persist:Error {
            return new stream<AidPackageOrderItem, persist:Error?>(new AidPackageOrderItemStream((), result));
        } else {
            return new stream<AidPackageOrderItem, persist:Error?>(new AidPackageOrderItemStream(result));
        }
    }
    isolated resource function get aidpackageorderitem/[int id]() returns AidPackageOrderItem|persist:Error {
        return (check self.persistClients.get(AID_PACKAGE_ORDER_ITEM).runReadByKeyQuery(AidPackageOrderItem, id)).cloneWithType(AidPackageOrderItem);
    }
    isolated resource function post aidpackageorderitem(AidPackageOrderItemInsert[] data) returns [int][]|persist:Error {
        _ = check self.persistClients.get("aidpackageorderitem").runBatchInsertQuery(data);
        return from AidPackageOrderItemInsert inserted in data
            select [inserted.id];
    }
    isolated resource function put aidpackageorderitem/[int id](AidPackageOrderItemUpdate value) returns AidPackageOrderItem|persist:Error {
        _ = check self.persistClients.get("aidpackageorderitem").runUpdateQuery({"id": id, }, data);
        return self->/aidpackageorderitem/[id].get();
    }
    isolated resource function delete aidpackageorderitem/[int id]() returns AidPackageOrderItem|persist:Error {
        AidPackageOrderItem 'object = check self->/aidpackageorderitem/[id].get();
        _ = check self.persistClients.get("aidpackageorderitem").runDeleteQuery({"id": id, });
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
                record {|AidPackageOrderItem value;|} nextRecord = {value: check streamValue.value.cloneWithType(AidPackageOrderItem)};
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

