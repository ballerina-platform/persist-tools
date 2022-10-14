import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;

public client class MultipleAssociationsClient {

    private final string entityName = "MultipleAssociations";
    private final sql:ParameterizedQuery tableName = `MultipleAssociations`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        "profile.id": {columnName: "profileId", 'type: int, relation: {entityName: "profile", refTable: "Profiles", refField: "id"}},
        "profile.name": {'type: string, relation: {entityName: "profile", refTable: "Profiles", refField: "name"}},
        "user.id": {columnName: "userId", 'type: int, relation: {entityName: "user", refTable: "Users", refField: "id"}},
        "user.name": {'type: string, relation: {entityName: "user", refTable: "Users", refField: "name"}}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {
        profile: {entity: Profile, fieldName: "profile", refTable: "Profiles", refFields: ["id"], joinColumns: ["profileId"]},
        user: {entity: User, fieldName: "user", refTable: "Users", refFields: ["id"], joinColumns: ["userId"]}
    };

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(MultipleAssociations value) returns MultipleAssociations|error {
        if value["profile"] is Profile {
            ProfileClient profileClient = check new ProfileClient();
            boolean exists = check profileClient->exists(<Profile>value.profile);
            if !exists {
                value.profile = check profileClient->create(<Profile>value.profile);
            }
        }
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

    remote function readByKey(int key, MultipleAssociationsRelations[] include = []) returns MultipleAssociations|error {
        return <MultipleAssociations>check self.persistClient.runReadByKeyQuery(MultipleAssociations, key, include);
    }

    remote function read(MultipleAssociationsRelations[] include = []) returns stream<MultipleAssociations, error?> {
        stream<anydata, error?>|error result = self.persistClient.runReadQuery(MultipleAssociations, (), include);
        if result is error {
            return new stream<MultipleAssociations, error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, error?>(new MultipleAssociationsStream(result));
        }
    }

    remote function execute(sql:ParameterizedQuery filterClause) returns stream<MultipleAssociations, error?> {
        stream<anydata, error?>|error result = self.persistClient.runExecuteQuery(filterClause, MultipleAssociations);
        if result is error {
            return new stream<MultipleAssociations, error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, error?>(new MultipleAssociationsStream(result));
        }
    }

    remote function update(MultipleAssociations value) returns error? {
        map<anydata> filter = {"id": value.id};
        _ = check self.persistClient.runUpdateQuery(value, filter);
        if value["profile"] is record {} {
            record {} profileEntity = <record {}>value["profile"];
            ProfileClient profileClient = check new ProfileClient();
            stream<MultipleAssociations, error?> multipleAssociationsStream = self->read([ProfileEntity]);
            check from MultipleAssociations p in multipleAssociationsStream
                do {
                    if p.profile is Profile {
                        check profileClient->update(<Profile>profileEntity);
                    }
                };
        }
        if value["user"] is record {} {
            record {} userEntity = <record {}>value["user"];
            UserClient userClient = check new UserClient();
            stream<MultipleAssociations, error?> multipleAssociationsStream = self->read([UserEntity]);
            check from MultipleAssociations p in multipleAssociationsStream
                do {
                    if p.user is User {
                        check userClient->update(<User>userEntity);
                    }
                };
        }
    }

    remote function delete(MultipleAssociations value) returns error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(MultipleAssociations multipleAssociations) returns boolean|error {
        MultipleAssociations|error result = self->readByKey(multipleAssociations.id);
        if result is MultipleAssociations {
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

public enum MultipleAssociationsRelations {
    ProfileEntity = "profile",
    UserEntity = "user"
}

public class MultipleAssociationsStream {

    private stream<anydata, error?>? anydataStream;
    private error? err;

    public isolated function init(stream<anydata, error?>? anydataStream, error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|MultipleAssociations value;|}|error? {
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
                record {|MultipleAssociations value;|} nextRecord = {value: check streamValue.value.cloneWithType(MultipleAssociations)};
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

