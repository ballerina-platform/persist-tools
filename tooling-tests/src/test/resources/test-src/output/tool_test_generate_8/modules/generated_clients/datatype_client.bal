import ballerina/sql;
import ballerinax/mysql;
import ballerina/time;
import ballerina/persist;

client class DataTypeClient {

    private final string entityName = "DataType";
    private final sql:ParameterizedQuery tableName = `DataTypes`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        a: {columnName: "a", 'type: int, autoGenerated: true},
        b1: {columnName: "b1", 'type: string},
        b2: {columnName: "b2", 'type: string?},
        c1: {columnName: "c1", 'type: int},
        c2: {columnName: "c2", 'type: int?},
        d1: {columnName: "d1", 'type: boolean},
        d2: {columnName: "d2", 'type: boolean?},
        e1: {columnName: "e1", 'type: float},
        e2: {columnName: "e2", 'type: float?},
        f1: {columnName: "f1", 'type: decimal},
        f2: {columnName: "f2", 'type: decimal?},
        g1: {columnName: "g1", 'type: byte},
        g2: {columnName: "g2", 'type: byte?},
        h1: {columnName: "h1", 'type: xml},
        h2: {columnName: "h2", 'type: xml?},
        i1: {columnName: "i1", 'type: record},
        i2: {columnName: "i2", 'type: record?},
        j1: {columnName: "j1", 'type: time:Utc},
        j2: {columnName: "j2", 'type: time:Utc?},
        k1: {columnName: "k1", 'type: time:Civil},
        k2: {columnName: "k2", 'type: time:Civil?},
        l1: {columnName: "l1", 'type: time:Date},
        l2: {columnName: "l2", 'type: time:Date?},
        m1: {columnName: "m1", 'type: time:TimeOfDay},
        m2: {columnName: "m2", 'type: time:TimeOfDay?},
        n1: {columnName: "n1", 'type: time:Civil[]},
        n2: {columnName: "n2", 'type: time:Civil[]?},
        o1: {columnName: "o1", 'type: time:TimeOfDay[]},
        o2: {columnName: "o2", 'type: time:TimeOfDay[]?},
        p1: {columnName: "p1", 'type: string[]},
        p2: {columnName: "p2", 'type: string?[]},
        p3: {columnName: "p3", 'type: string[]?},
        p3: {columnName: "p4", 'type: string?[]?},
        q1: {columnName: "q1", 'type: int[]},
        q2: {columnName: "q2", 'type: int?[]},
        q3: {columnName: "q3", 'type: int[]?},
        q3: {columnName: "q4", 'type: int?[]?},
        r1: {columnName: "r1", 'type: boolean[]},
        r2: {columnName: "r2", 'type: boolean?[]},
        r3: {columnName: "r3", 'type: boolean[]?},
        r3: {columnName: "r4", 'type: boolean?[]?},
        s1: {columnName: "s1", 'type: float[]},
        s2: {columnName: "s2", 'type: float?[]},
        s3: {columnName: "s3", 'type: float[]?},
        s3: {columnName: "s4", 'type: float?[]?},
        t1: {columnName: "t1", 'type: decimal[]},
        t2: {columnName: "t2", 'type: decimal?[]},
        t3: {columnName: "t3", 'type: decimal[]?},
        t3: {columnName: "t4", 'type: decimal?[]?},
        u1: {columnName: "u1", 'type: byte[][]},
        u2: {columnName: "u2", 'type: byte[]?[]},
        u3: {columnName: "u3", 'type: byte[][]?},
        u3: {columnName: "u4", 'type: byte[]?[]?},
        v1: {columnName: "v1", 'type: anydata},
        v2: {columnName: "v2", 'type: anydata?},
        w1: {columnName: "w1", 'type: object {}},
        x1: {columnName: "x1", 'type: anydata[]},
        x2: {columnName: "x2", 'type: anydata[]?},
        y1: {columnName: "y1", 'type: object {}[]?}
    };

    private string[] keyFields = ["needId"];

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client dbClient = check new (host = HOST, user = USER, password = PASSWORD, database = DATABASE, port = PORT);
        self.persistClient = check new (self.entityName, self.tableName, self.fieldMetadata, self.keyFields, dbClient);
    }

    remote function create(DataType value) returns int|persist:Error? {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        return <int>result.lastInsertId;
    }

    remote function readByKey(int key) returns DataType|persist:Error {
        return (check self.persistClient.runReadByKeyQuery(key)).cloneWithType(DataType);
    }

    remote function read(map<anydata>? filter = ()) returns stream<DataType, persist:Error?>|persist:Error {
        stream<anydata, error?> result = check self.persistClient.runReadQuery(filter);
        return new stream<DataType, error?>(new DataTypeStream(result));
    }

    remote function update(record {} 'object, map<anydata> filter) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery('object, filter);
    }

    remote function delete(map<anydata> filter) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(filter);
    }

    function close() returns persist:Error? {
        return self.persistClient.close();
    }
}

public class DataTypeStream {

    private stream<anydata, persist:Error?> anydataStream;

    public isolated function init(stream<anydata, persist:Error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|DataType value;|}|persist:Error? {
        var streamValue = self.anydataStream.next();
        if streamValue is () {
            return streamValue;
        } else if (streamValue is error) {
            return streamValue;
        } else {
            record {|DataType value;|} nextRecord = {value: check streamValue.value.cloneWithType(DataType)};
            return nextRecord;
        }
    }

    public isolated function close() returns persist:Error? {
        return self.anydataStream.close();
    }
}
