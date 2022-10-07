import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;
import foo/tool_test_generate_15 as entities;
import foo/tool_test_generate_15.foo as fooEntities;

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
        profile: {entity: entities:Profile, fieldName: "profile", refTable: "Profiles", refFields: ["id"], joinColumns: ["profileId"]},
        user: {entity: fooEntities:User, fieldName: "user", refTable: "Users", refFields: ["id"], joinColumns: ["userId"]}
    };

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(entities:MultipleAssociations value) returns entities:MultipleAssociations|error {
        if value.profile is entities:Profile {
            ProfileClient profileClient = check new ProfileClient();
            boolean exists = check profileClient->exists(<entities:Profile>value.profile);
            if !exists {
                value.profile = check profileClient->create(<entities:Profile>value.profile);
            }
        }
        if value.user is fooEntities:User {
            FooUserClient fooUserClient = check new FooUserClient();
            boolean exists = check fooUserClient->exists(<fooEntities:User>value.user);
            if !exists {
                value.user = check fooUserClient->create(<fooEntities:User>value.user);
            }
        }
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, MultipleAssociationsRelations[] include = []) returns entities:MultipleAssociations|error {
        return <entities:MultipleAssociations>check self.persistClient.runReadByKeyQuery(entities:MultipleAssociations, key, include);
    }

    remote function read(map<anydata>? filter = (), MultipleAssociationsRelations[] include = []) returns stream<entities:MultipleAssociations, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(entities:MultipleAssociations, filter, include);
        return new stream<entities:MultipleAssociations, error?>(new MultipleAssociationsStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
        if 'object["profile"] is record {} {
            record {} profileEntity = <record {}>'object["profile"];
            ProfileClient profileClient = check new ProfileClient();
            stream<entities:MultipleAssociations, error?> multipleAssociationsStream = check self->read(filter, [ProfileEntity]);
            check from entities:MultipleAssociations p in multipleAssociationsStream
                do {
                    if p.profile is entities:Profile {
                        check profileClient->update(profileEntity, {"id": (<entities:Profile>p.profile).id});
                    }
                };
        }
        if 'object["user"] is record {} {
            record {} userEntity = <record {}>'object["user"];
            FooUserClient fooUserClient = check new FooUserClient();
            stream<entities:MultipleAssociations, error?> multipleAssociationsStream = check self->read(filter, [UserEntity]);
            check from entities:MultipleAssociations p in multipleAssociationsStream
                do {
                    if p.user is fooEntities:User {
                        check fooUserClient->update(userEntity, {"id": (<fooEntities:User>p.user).id});
                    }
                };
        }
    }

    remote function delete(map<anydata> filter) returns error? {
        _ = check self.persistClient.runDeleteQuery(filter);
    }

    remote function exists(entities:MultipleAssociations multipleAssociations) returns boolean|error {
        entities:MultipleAssociations|error result = self->readByKey(multipleAssociations.id);
        if result is entities:MultipleAssociations {
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

public enum MultipleAssociationsRelations {
    ProfileEntity = "profile",
    UserEntity = "user"
}

public class MultipleAssociationsStream {

    private stream<anydata, error?> anydataStream;

    public isolated function init(stream<anydata, error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|entities:MultipleAssociations value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|entities:MultipleAssociations value;|} nextRecord = {value: check streamValue.value.cloneWithType(entities:MultipleAssociations)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}

