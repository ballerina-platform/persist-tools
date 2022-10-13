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

    private final map<persist:JoinMetadata> joinMetadata = {user: {entity: User, fieldName: "user", refTable: "Users", refFields: ["id"], joinColumns: ["userId"]}};

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(Profile value) returns Profile|error {
        if value["user"] is User {
            UserClient userClient = check new UserClient();
            boolean exists = check userClient->exists(<User>value.user);
            if !exists {
                value.user = check userClient->create(<User>value.user);
            }
        }
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, ProfileRelations[] include = []) returns Profile|error {
        return <Profile>check self.persistClient.runReadByKeyQuery(Profile, key, include);
    }

    remote function read(ProfileRelations[] include = []) returns stream<Profile, error?> {
        stream<anydata, error?>|error result = self.persistClient.runReadQuery(Profile, (), include);
        if result is error {
            return new stream<Profile, error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, error?>(new ProfileStream(result));
        }
    }

    remote function execute(sql:ParameterizedQuery filterClause) returns stream<Profile, error?> {
        stream<anydata, error?>|error result = self.persistClient.runExecuteQuery(filterClause, Profile);
        if result is error {
            return new stream<Profile, error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, error?>(new ProfileStream(result));
        }
    }

    remote function update(Profile value) returns error? {
        map<anydata> filter = {"id": value.id};
        _ = check self.persistClient.runUpdateQuery(value, filter);
        if value["user"] is record {} {
            record {} userEntity = <record {}>value["user"];
            UserClient userClient = check new UserClient();
            stream<Profile, error?> profileStream = self->read([UserEntity]);
            check from Profile p in profileStream
                do {
                    if p.user is User {
                        check userClient->update(<User>userEntity);
                    }
                };
        }
    }

    remote function delete(Profile value) returns error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Profile profile) returns boolean|error {
        Profile|error result = self->readByKey(profile.id);
        if result is Profile {
            return true;
        } else if result is persist:InvalidKeyError {
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

    private stream<anydata, error?>? anydataStream;
    private error? err;

    public isolated function init(stream<anydata, error?>? anydataStream, error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Profile value;|}|error? {
        if self.err is error {
            return <error>self.err;
        } else if self.anydataStream is stream<anydata, error?> {
            var anydataStream = <stream<anydata, error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is error) {
                return streamValue;
            } else {
                record {|Profile value;|} nextRecord = {value: check streamValue.value.cloneWithType(Profile)};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns error? {
        if self.anydataStream is stream<anydata, error?> {
            var anydataStream = <stream<anydata, error?>>self.anydataStream;
            return anydataStream.close();
        }
    }
}

