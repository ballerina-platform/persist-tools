import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;
import foo/tool_test_generate_15.foo as fooEntities;

public client class FooUserClient {

    private final string entityName = "User";
    private final sql:ParameterizedQuery tableName = `Users`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {};

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(fooEntities:User value) returns fooEntities:User|error {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        if result.lastInsertId is () {
            return value;
        }
        return {id: <int>result.lastInsertId, name: value.name};
    }

    remote function readByKey(int key) returns fooEntities:User|error {
        return (check self.persistClient.runReadByKeyQuery(fooEntities:User, key)).cloneWithType(fooEntities:User);
    }

    remote function read(map<anydata>? filter = ()) returns stream<fooEntities:User, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(fooEntities:User, filter);
        return new stream<fooEntities:User, error?>(new FooUserStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
    }

    remote function delete(map<anydata> filter) returns error? {
        _ = check self.persistClient.runDeleteQuery(filter);
    }

    remote function exists(fooEntities:User user) returns boolean|error {
        fooEntities:User|error result = self->readByKey(user.id);
        if result is fooEntities:User {
            return true;
        } else if result is persist:InvalidKey {
            return false;
        } else {
            return result;
        }
    }

    function close() returns error? {
        return self.persistClient.close();
    }
}

public class FooUserStream {

    private stream<anydata, error?> anydataStream;

    public isolated function init(stream<anydata, error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|fooEntities:User value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|fooEntities:User value;|} nextRecord = {value: check streamValue.value.cloneWithType(fooEntities:User)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}

