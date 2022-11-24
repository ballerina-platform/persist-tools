import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;

public client class ProfileClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Profile";
    private final sql:ParameterizedQuery tableName = `Profiles`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        isAdult: {columnName: "isAdult", 'type: boolean},
        salary: {columnName: "salary", 'type: float},
        age: {columnName: "age", 'type: decimal}
    };
    private string[] keyFields = ["id"];

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata);
    }

    remote function create(Profile value) returns Profile|persist:Error {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        if result.lastInsertId is () {
            return value;
        }
        return {id: <int>result.lastInsertId, name: value.name, isAdult: value.isAdult, salary: value.salary, age: value.age};
    }

    remote function readByKey(int key) returns Profile|persist:Error {
        return <Profile>check self.persistClient.runReadByKeyQuery(Profile, key);
    }

    remote function read() returns stream<Profile, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Profile);
        if result is persist:Error {
            return new stream<Profile, persist:Error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, persist:Error?>(new ProfileStream(result));
        }
    }

    remote function execute(sql:ParameterizedQuery filterClause) returns stream<Profile, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runExecuteQuery(filterClause, Profile);
        if result is persist:Error {
            return new stream<Profile, persist:Error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, persist:Error?>(new ProfileStream(result));
        }
    }

    remote function update(Profile value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
    }

    remote function delete(Profile value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Profile profile) returns boolean|persist:Error {
        Profile|persist:Error result = self->readByKey(profile.id);
        if result is Profile {
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

public class ProfileStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Profile value;|}|persist:Error? {
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
                record {|Profile value;|} nextRecord = {value: <Profile>streamValue.value};
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

