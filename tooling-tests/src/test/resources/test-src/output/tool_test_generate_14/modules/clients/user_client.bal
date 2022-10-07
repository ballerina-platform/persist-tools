import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;
import foo/tool_test_generate_14 as entities;

public client class UserClient {

    private final string entityName = "User";
    private final sql:ParameterizedQuery tableName = `Users`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        "profile.id": {'type: int, relation: {entityName: "profile", refTable: "Profiles", refField: "id"}},
        "profile.name": {'type: string, relation: {entityName: "profile", refTable: "Profiles", refField: "name"}}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {profile: {entity: entities:Profile, fieldName: "profile", refTable: "Profiles", refFields: [""], joinColumns: [""]}};

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(entities:User value) returns entities:User|error {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, UserRelations[] include = []) returns entities:User|error {
        return <entities:User>check self.persistClient.runReadByKeyQuery(entities:User, key, include);
    }

    remote function read(map<anydata>? filter = (), UserRelations[] include = []) returns stream<entities:User, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(entities:User, filter, include);
        return new stream<entities:User, error?>(new UserStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
    }

    remote function delete(map<anydata> filter) returns error? {
        _ = check self.persistClient.runDeleteQuery(filter);
    }

    remote function exists(entities:User user) returns boolean|error {
        entities:User|error result = self->readByKey(user.id);
        if result is entities:User {
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

public enum UserRelations {
    ProfileEntity = "profile"
}

public class UserStream {

    private stream<anydata, error?> anydataStream;

    public isolated function init(stream<anydata, error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|entities:User value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|entities:User value;|} nextRecord = {value: check streamValue.value.cloneWithType(entities:User)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}

