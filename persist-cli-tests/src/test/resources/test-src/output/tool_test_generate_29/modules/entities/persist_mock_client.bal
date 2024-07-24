// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/h2.driver as _;
import ballerinax/java.jdbc;
import ballerinax/persist.sql as psql;

const MEDICAL_NEED = "medicalneeds";

public isolated client class MockClient {
    *persist:AbstractPersistClient;

    private final jdbc:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [MEDICAL_NEED]: {
            entityName: "MedicalNeed",
            tableName: "MedicalNeed",
            fieldMetadata: {
                needId: {columnName: "needId"},
                itemId: {columnName: "itemId"},
                beneficiaryId: {columnName: "beneficiaryId"},
                period: {columnName: "period"},
                urgency: {columnName: "urgency"},
                quantity: {columnName: "quantity"}
            },
            keyFields: ["needId"]
        }
    };

    public isolated function init(string url, string? user = (), string? password = (), jdbc:Options? connectionOptions = ()) returns persist:Error? {
        jdbc:Client|error dbClient = new (url = url, user = user, password = password, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {[MEDICAL_NEED]: check new (dbClient, self.metadata.get(MEDICAL_NEED), psql:H2_SPECIFICS)};
    }

    isolated resource function get medicalneeds(MedicalNeedTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "query"
    } external;

    isolated resource function get medicalneeds/[int needId](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor",
        name: "queryOne"
    } external;

    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.needId;
    }

    isolated resource function put medicalneeds/[int needId](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runUpdateQuery(needId, value);
        return self->/medicalneeds/[needId].get();
    }

    isolated resource function delete medicalneeds/[int needId]() returns MedicalNeed|persist:Error {
        MedicalNeed result = check self->/medicalneeds/[needId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runDeleteQuery(needId);
        return result;
    }

    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor"
    } external;

    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.H2Processor"
    } external;

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

