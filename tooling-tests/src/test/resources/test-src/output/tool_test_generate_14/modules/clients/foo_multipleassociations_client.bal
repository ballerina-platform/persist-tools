import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;
import foo/tool_test_generate_14.foo as fooEntities;
import foo/tool_test_generate_14 as entities;

public client class FooMultipleAssociationsClient {

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
        user: {entity: entities:User, fieldName: "user", refTable: "Users", refFields: ["id"], joinColumns: ["userId"]}
    };

    private persist:SQLClient persistClient;

    public function init() returns error? {
        mysql:Client dbClient = check new (host = host, user = user, password = password, database = database, port = port);
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(fooEntities:MultipleAssociations value) returns fooEntities:MultipleAssociations|error {
        if value.profile is entities:Profile {
            ProfileClient profileClient = check new ProfileClient();
            boolean exists = check profileClient->exists(<entities:Profile>value.profile);
            if !exists {
                value.profile = check profileClient->create(<entities:Profile>value.profile);
            }
        }
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

    remote function readByKey(MultipleAssociationsRelations[] include = []) returns MultipleAssociations|error {
        return <MultipleAssociations>check self.persistClient.runReadByKeyQuery(MultipleAssociations, key, include);
    }

    remote function read(MultipleAssociationsRelations[] include = []) returns stream<MultipleAssociations, error?>|error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(MultipleAssociations, (), include);
        return new stream<MultipleAssociations, error?>(new FooMultipleAssociationsStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
        if 'object["profile"] is record {} {
            record {} profileEntity = <record {}>'object["profile"];
            ProfileClient profileClient = check new ProfileClient();
            stream<MultipleAssociations, error?> fooMultipleAssociationsStream = check self->read(filter, [ProfileEntity]);
            check from MultipleAssociations p in fooMultipleAssociationsStream
                do {
                    if p.profile is entities:Profile {
                        check profileClient->update(profileEntity, {"id": (<entities:Profile>p.profile).id});
                    }
                };
        }
        if 'object["user"] is record {} {
            record {} userEntity = <record {}>'object["user"];
            UserClient userClient = check new UserClient();
            stream<MultipleAssociations, error?> fooMultipleAssociationsStream = check self->read(filter, [UserEntity]);
            check from MultipleAssociations p in fooMultipleAssociationsStream
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

    remote function exists(MultipleAssociations multipleAssociations) returns boolean|error {
        MultipleAssociations|error result = self->readByKey(multipleAssociations.id);
        if result is MultipleAssociations {
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

public enum FooMultipleAssociationsRelations {
    ProfileEntity = "profile",
    UserEntity = "user"
}

public class FooMultipleAssociationsStream {

    private stream<anydata, error?> anydataStream;

    public isolated function init(stream<anydata, error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|MultipleAssociations value;|}|error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|MultipleAssociations value;|} nextRecord = {value: check streamValue.value.cloneWithType(MultipleAssociations)};
            return nextRecord;
        }
    }

    public isolated function close() returns error? {
        return self.anydataStream.close();
    }
}

