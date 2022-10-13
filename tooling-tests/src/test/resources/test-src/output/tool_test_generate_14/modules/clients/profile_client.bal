import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;

public client class ProfileClient {

    private final string entityName = "Profile";
    private final sql:ParameterizedQuery tableName = `Profiles`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        "user.id": {columnName: "userId", 'type: int, relation: {entityName: "user", refTable: "Users", refField: "id"}},
        "user.name": {'type: string, relation: {entityName: "user", refTable: "Users", refField: "name"}}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {user: {entity: entities:User, fieldName: "user", refTable: "Users", refFields: ["id"], joinColumns: ["userId"]}};

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(entities:Profile value) returns entities:Profile|error {
        if value.user is entities:User {
            UserClient userClient = check new UserClient();
            boolean exists = check userClient->exists(<entities:User>value.user);
            if !exists {
                value.user = check userClient->create(<entities:User>value.user);
            }
        }
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, ProfileRelations[] include = []) returns entities:Profile|error {
        return <entities:Profile>check self.persistClient.runReadByKeyQuery(entities:Profile, key, include);
    }

    remote function read(map<anydata>? filter = (), ProfileRelations[] include = []) returns stream<entities:Profile, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(entities:Profile, filter, include);
        return new stream<entities:Profile, error?>(new ProfileStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
        if 'object["user"] is record {} {
            record {} userEntity = <record {}>'object["user"];
            UserClient userClient = check new UserClient();
            stream<entities:Profile, error?> profileStream = check self->read(filter, [UserEntity]);
            check from entities:Profile p in profileStream
                do {
                    if p.user is entities:User {
                        check userClient->update(userEntity, {"id": (<entities:User>p.user).id});
                    }
                };
        }
    }

    remote function delete(map<anydata> filter) returns error? {
        _ = check self.persistClient.runDeleteQuery(filter);
    }

    remote function exists(entities:Profile profile) returns boolean|error {
        entities:Profile|error result = self->readByKey(profile.id);
        if result is entities:Profile {
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

public enum ProfileRelations {
    UserEntity = "user"
}

public class ProfileStream {

    private stream<anydata, error?> anydataStream;

    public isolated function init(stream<anydata, error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|entities:Profile value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|entities:Profile value;|} nextRecord = {value: check streamValue.value.cloneWithType(entities:Profile)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}

