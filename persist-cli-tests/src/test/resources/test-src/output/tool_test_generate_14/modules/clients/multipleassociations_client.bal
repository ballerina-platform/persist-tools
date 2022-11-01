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

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(MultipleAssociations value) returns MultipleAssociations|persist:Error {
        if value.profile is Profile {
            ProfileClient profileClient = check new ProfileClient();
            boolean exists = check profileClient->exists(<Profile>value.profile);
            if !exists {
                value.profile = check profileClient->create(<Profile>value.profile);
            }
        }
        if value.user is User {
            UserClient userClient = check new UserClient();
            boolean exists = check userClient->exists(<User>value.user);
            if !exists {
                value.user = check userClient->create(<User>value.user);
            }
        }
        _ = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, MultipleAssociationsRelations[] include = []) returns MultipleAssociations|persist:Error {
        return <MultipleAssociations>check self.persistClient.runReadByKeyQuery(MultipleAssociations, key, include);
    }

    remote function read(MultipleAssociationsRelations[] include = []) returns stream<MultipleAssociations, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(MultipleAssociations, include);
        if result is persist:Error {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream(result));
        }
    }

    remote function execute(sql:ParameterizedQuery filterClause) returns stream<MultipleAssociations, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runExecuteQuery(filterClause, MultipleAssociations);
        if result is persist:Error {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream(result));
        }
    }

    remote function update(MultipleAssociations value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
        if value.profile is record {} {
            Profile profileEntity = <Profile>value.profile;
            ProfileClient profileClient = check new ProfileClient();
            check profileClient->update(profileEntity);
        }
        if value.user is record {} {
            User userEntity = <User>value.user;
            UserClient userClient = check new UserClient();
            check userClient->update(userEntity);
        }
    }

    remote function delete(MultipleAssociations value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(MultipleAssociations multipleAssociations) returns boolean|persist:Error {
        MultipleAssociations|persist:Error result = self->readByKey(multipleAssociations.id);
        if result is MultipleAssociations {
            return true;
        } else if result is persist:InvalidKeyError {
            return false;
        } else {
            return result;
        }
    }

    public function close() returns persist:Error? {
        return self.persistClient.close();
    }
}

public enum MultipleAssociationsRelations {
    ProfileEntity = "profile", UserEntity = "user"
}

public class MultipleAssociationsStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|MultipleAssociations value;|}|persist:Error? {
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
                record {|MultipleAssociations value;|} nextRecord = {value: <MultipleAssociations>streamValue.value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
    }
}

