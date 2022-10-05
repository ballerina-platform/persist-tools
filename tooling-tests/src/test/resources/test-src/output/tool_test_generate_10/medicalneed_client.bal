import ballerina/sql;
import ballerinax/mysql;
import ballerina/time;
import ballerina/persist;

public client class MedicalNeedClient {

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
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata);
    }

    remote function create(MedicalNeed value) returns MedicalNeed|error? {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(record {|int itemId; int needId;|} key) returns MedicalNeed|error {
        return (check self.persistClient.runReadByKeyQuery(MedicalNeed, key)).cloneWithType(MedicalNeed);
    }

    remote function read(map<anydata>? filter = ()) returns stream<MedicalNeed, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(MedicalNeed, filter);
        return new stream<MedicalNeed, error?>(new MedicalNeedStream(result));
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

    public isolated function next() returns record {|MedicalNeed value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|MedicalNeed value;|} nextRecord = {value: check streamValue.value.cloneWithType(MedicalNeed)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}

