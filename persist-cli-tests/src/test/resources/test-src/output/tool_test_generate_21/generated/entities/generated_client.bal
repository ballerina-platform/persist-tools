// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerina/time;
import ballerinax/mysql;

const MEDICAL_NEED = "medicalneed";
const AID_PACKAGE_ORDER_ITEM = "aidpackageorderitem";

public client class EntitiesClient {
    *persist:AbstractPersistClient;

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
            medicalneed: check new (self.dbClient, self.metadata.get(MEDICAL_NEED)),
            aidpackageorderitem: check new (self.dbClient, self.metadata.get(AID_PACKAGE_ORDER_ITEM))
        };
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
        MedicalNeed|error result = (check self.persistClients.get(MEDICAL_NEED).runReadByKeyQuery(MedicalNeed, needId)).cloneWithType(MedicalNeed);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post medicalneed(MedicalNeedInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(MEDICAL_NEED).runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.needId;
    }

    isolated resource function put medicalneed/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        _ = check self.persistClients.get(MEDICAL_NEED).runUpdateQuery(needId, value);
        return self->/medicalneed/[needId].get();
    }

    isolated resource function delete medicalneed/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed result = check self->/medicalneed/[needId].get();
        _ = check self.persistClients.get(MEDICAL_NEED).runDeleteQuery(needId);
        return result;
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
        AidPackageOrderItem|error result = (check self.persistClients.get(AID_PACKAGE_ORDER_ITEM).runReadByKeyQuery(AidPackageOrderItem, id)).cloneWithType(AidPackageOrderItem);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post aidpackageorderitem(AidPackageOrderItemInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(AID_PACKAGE_ORDER_ITEM).runBatchInsertQuery(data);
        return from AidPackageOrderItemInsert inserted in data
            select inserted.id;
    }

    isolated resource function put aidpackageorderitem/[int id](AidPackageOrderItemUpdate value) returns AidPackageOrderItem|persist:Error {
        _ = check self.persistClients.get(AID_PACKAGE_ORDER_ITEM).runUpdateQuery(id, value);
        return self->/aidpackageorderitem/[id].get();
    }

    isolated resource function delete aidpackageorderitem/[int id]() returns AidPackageOrderItem|persist:Error {
        AidPackageOrderItem result = check self->/aidpackageorderitem/[id].get();
        _ = check self.persistClients.get(AID_PACKAGE_ORDER_ITEM).runDeleteQuery(id);
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

