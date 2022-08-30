import ballerina/sql;
import ballerinax/mysql;
import ballerina/time;
import ballerina/persist;
import foo/perist_generate_9 as entities;

client class MedicalNeedClient {

    private final string entityName = "MedicalNeed";
    private final sql:ParameterizedQuery tableName = `MedicalNeeds`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        needId: {columnName: "needId", 'type: int},
        itemId: {columnName: "itemId", 'type: int},
        beneficiaryId: {columnName: "beneficiaryId", 'type: int},
        period: {columnName: "period", 'type: time:Civil},
        urgency: {columnName: "urgency", 'type: string},
        quantity: {columnName: "quantity", 'type: int}
    };
    private string[] keyFields = ["needId", "itemId"];

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (self.entityName, self.tableName, self.fieldMetadata, self.keyFields, dbClient);
    }

    remote function create(entities:MedicalNeed value) returns any|error? {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        if result.lastInsertId is () {
            return [value.itemId, value.needId];
        }
        return result.lastInsertId;
    }

    remote function readByKey(int key0, int key1) returns entities:MedicalNeed|error {
        return (check self.persistClient.runReadByKeyQuery(entities:MedicalNeed, key0, key1)).cloneWithType(entities:MedicalNeed);
    }

    remote function read(map<anydata>? filter = ()) returns stream<entities:MedicalNeed, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(entities:MedicalNeed, filter);
        return new stream<entities:MedicalNeed, error?>(new MedicalNeedStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
    }

    remote function delete(map<anydata> filter) returns error? {
        _ = check self.persistClient.runDeleteQuery(filter);
    }

    function close() returns error? {
        return self.persistClient.close();
    }
}

public class MedicalNeedStream {

    private stream<anydata, error?> anydataStream;

    public isolated function init(stream<anydata, error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|entities:MedicalNeed value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|entities:MedicalNeed value;|} nextRecord = {value: check streamValue.value.cloneWithType(entities:MedicalNeed)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}
