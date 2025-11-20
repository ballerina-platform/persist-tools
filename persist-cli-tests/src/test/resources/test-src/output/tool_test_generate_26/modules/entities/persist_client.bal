// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;
import ballerinax/persist.sql as psql;

const MEDICAL_NEED = "medicalneeds";
const MEDICAL_ITEM = "medicalitems";

# MySQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [MEDICAL_NEED]: {
            entityName: "MedicalNeed",
            tableName: "MedicalNeed",
            fieldMetadata: {
                'record: {columnName: "record"},
                itemItemId: {columnName: "itemItemId"},
                beneficiaryId: {columnName: "beneficiaryId"},
                'time: {columnName: "time"},
                urgency: {columnName: "urgency"},
                quantity: {columnName: "quantity"},
                "item.itemId": {relation: {entityName: "item", refField: "itemId"}},
                "item.string": {relation: {entityName: "item", refField: "string"}},
                "item.type": {relation: {entityName: "item", refField: "type"}},
                "item.unit": {relation: {entityName: "item", refField: "unit"}}
            },
            keyFields: ["record"],
            joinMetadata: {item: {entity: MedicalItem, fieldName: "item", refTable: "MedicalItem", refColumns: ["itemId"], joinColumns: ["itemItemId"], 'type: psql:ONE_TO_ONE}}
        },
        [MEDICAL_ITEM]: {
            entityName: "MedicalItem",
            tableName: "MedicalItem",
            fieldMetadata: {
                itemId: {columnName: "itemId"},
                'string: {columnName: "string"},
                'type: {columnName: "type"},
                unit: {columnName: "unit"},
                "need.record": {relation: {entityName: "need", refField: "record"}},
                "need.itemItemId": {relation: {entityName: "need", refField: "itemItemId"}},
                "need.beneficiaryId": {relation: {entityName: "need", refField: "beneficiaryId"}},
                "need.time": {relation: {entityName: "need", refField: "time"}},
                "need.urgency": {relation: {entityName: "need", refField: "urgency"}},
                "need.quantity": {relation: {entityName: "need", refField: "quantity"}}
            },
            keyFields: ["itemId"],
            joinMetadata: {need: {entity: MedicalNeed, fieldName: "need", refTable: "MedicalNeed", refColumns: ["itemItemId"], joinColumns: ["itemId"], 'type: psql:ONE_TO_ONE}}
        }
    };

    public isolated function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [MEDICAL_NEED]: check new (dbClient, self.metadata.get(MEDICAL_NEED), psql:MYSQL_SPECIFICS),
            [MEDICAL_ITEM]: check new (dbClient, self.metadata.get(MEDICAL_ITEM), psql:MYSQL_SPECIFICS)
        };
    }

    # Get rows from MedicalNeed table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get medicalneeds(MedicalNeedTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from MedicalNeed table.
    #
    # + 'record - The value of the primary key field 'record
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get medicalneeds/[int 'record](MedicalNeedTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into MedicalNeed table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post medicalneeds(MedicalNeedInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from MedicalNeedInsert inserted in data
            select inserted.'record;
    }

    # Update row in MedicalNeed table.
    #
    # + 'record - The value of the primary key field 'record
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put medicalneeds/[int 'record](MedicalNeedUpdate value) returns MedicalNeed|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runUpdateQuery('record, value);
        return self->/medicalneeds/['record].get();
    }

    # Delete row from MedicalNeed table.
    #
    # + 'record - The value of the primary key field 'record
    # + return - The deleted record or an error
    isolated resource function delete medicalneeds/[int 'record]() returns MedicalNeed|persist:Error {
        MedicalNeed result = check self->/medicalneeds/['record].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_NEED);
        }
        _ = check sqlClient.runDeleteQuery('record);
        return result;
    }

    # Get rows from MedicalItem table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get medicalitems(MedicalItemTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "query"
    } external;

    # Get row from MedicalItem table.
    #
    # + itemId - The value of the primary key field itemId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get medicalitems/[int itemId](MedicalItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into MedicalItem table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post medicalitems(MedicalItemInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from MedicalItemInsert inserted in data
            select inserted.itemId;
    }

    # Update row in MedicalItem table.
    #
    # + itemId - The value of the primary key field itemId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put medicalitems/[int itemId](MedicalItemUpdate value) returns MedicalItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_ITEM);
        }
        _ = check sqlClient.runUpdateQuery(itemId, value);
        return self->/medicalitems/[itemId].get();
    }

    # Delete row from MedicalItem table.
    #
    # + itemId - The value of the primary key field itemId
    # + return - The deleted record or an error
    isolated resource function delete medicalitems/[int itemId]() returns MedicalItem|persist:Error {
        MedicalItem result = check self->/medicalitems/[itemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(MEDICAL_ITEM);
        }
        _ = check sqlClient.runDeleteQuery(itemId);
        return result;
    }

    # Execute a custom SQL query and return results.
    #
    # + sqlQuery - The SQL query to execute
    # + rowType - Defines the structure of the result rows
    # + return - A collection of result rows or an error
    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    # Execute a custom SQL command (INSERT, UPDATE, DELETE, etc.).
    #
    # + sqlQuery - The SQL command to execute
    # + return - The execution result or an error
    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MySQLProcessor"
    } external;

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

